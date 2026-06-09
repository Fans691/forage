<?php
$apiUrl = 'http://localhost:8080/forage/api/demandes';
if (!empty($_GET['api'])) {
    $apiUrl = $_GET['api'];
}

$dbHost = getenv('APP_DB_HOST') ?: 'localhost';
$dbPort = getenv('APP_DB_PORT') ?: '5432';
$dbName = getenv('APP_DB_NAME') ?: 'forage';
$dbUser = getenv('APP_DB_USER') ?: 'fans';
$dbPassword = getenv('APP_DB_PASSWORD') ?: '123';

function fetchJson($url) {
    $context = stream_context_create([
        'http' => [
            'method' => 'GET',
            'timeout' => 10,
            'header' => "Accept: application/json\r\n",
        ]
    ]);

    $raw = @file_get_contents($url, false, $context);
    if ($raw === false) {
        $error = error_get_last();
        $detail = $error && !empty($error['message']) ? $error['message'] : 'Erreur inconnue';
        return [
            'error' => 'Impossible de contacter l API: ' . $url . ' (' . $detail . ')',
            'data' => null,
        ];
    }

    $data = json_decode($raw, true);
    if (!is_array($data)) {
        return [
            'error' => 'Reponse JSON invalide.',
            'data' => null,
        ];
    }

    return [
        'error' => null,
        'data' => $data,
    ];
}

function openDbConnection($host, $port, $dbName, $dbUser, $dbPassword) {
    $dsn = sprintf('pgsql:host=%s;port=%s;dbname=%s', $host, $port, $dbName);
    $pdo = new PDO($dsn, $dbUser, $dbPassword, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
    return $pdo;
}

function buildPlaceholders($count) {
    return implode(',', array_fill(0, $count, '?'));
}

function computeDtBetween(array $entries, $id1, $id2) {
    $pos1 = null;
    $pos2 = null;

    $count = count($entries);
    for ($i = 0; $i < $count; $i++) {
        if ($entries[$i]['statutId'] == $id1) {
            $pos1 = $i;
            break;
        }
    }

    if ($pos1 === null) {
        return null;
    }

    for ($i = $pos1 + 1; $i < $count; $i++) {
        if ($entries[$i]['statutId'] == $id2) {
            $pos2 = $i;
            break;
        }
    }

    if ($pos2 === null) {
        return null;
    }

    $start = new DateTimeImmutable($entries[$pos1]['date']);
    $end = new DateTimeImmutable($entries[$pos2]['date']);

    if ($end < $start) {
        return null;
    }

    $totalMinutes = 0;
    $workStart = 8;
    $workEnd = 16;

    $currentDate = $start->setTime(0, 0);
    $lastDate = $end->setTime(0, 0);

    while ($currentDate <= $lastDate) {
        $dayOfWeek = (int) $currentDate->format('N');
        if ($dayOfWeek < 6) {
            $dayStart = $currentDate->setTime($workStart, 0);
            $dayEnd = $currentDate->setTime($workEnd, 0);

            $rangeStart = $start > $dayStart ? $start : $dayStart;
            $rangeEnd = $end < $dayEnd ? $end : $dayEnd;

            if ($rangeEnd > $rangeStart) {
                $totalMinutes += (int) (($rangeEnd->getTimestamp() - $rangeStart->getTimestamp()) / 60);
            }
        }

        $currentDate = $currentDate->modify('+1 day');
    }

    return $totalMinutes;
}

function pickColorForDt(array $configs, $dtValue) {
    if ($dtValue === null) {
        return null;
    }

    $matchingColor = null;
    foreach ($configs as $config) {
        $limit = (float) $config['dt'];
        if ($dtValue > $limit) {
            $matchingColor = $config['code_couleur'];
        }
    }

    return $matchingColor;
}

function formatHours($value) {
    if ($value === null || $value === '') {
        return '';
    }

    return rtrim(rtrim(number_format((float) $value, 2, '.', ''), '0'), '.') . ' h';
}

$result = fetchJson($apiUrl);
$error = $result['error'];
$demandes = $result['data'];
$dbError = null;
$configRules = [];
$configByPair = [];
$statutMap = [];
$demandeStatutHistory = [];
$dureeTravailleTotalByDemande = [];

if (!$error && !empty($demandes)) {
    try {
        $pdo = openDbConnection($dbHost, $dbPort, $dbName, $dbUser, $dbPassword);

        $statutRows = $pdo->query('select id, libelle from statut order by id')->fetchAll();
        foreach ($statutRows as $row) {
            $statutMap[(int) $row['id']] = $row['libelle'];
        }

        $configRules = $pdo->query('select id1, id2, dt, code_couleur from config order by id1, id2, dt')
            ->fetchAll();
        foreach ($configRules as $rule) {
            $key = $rule['id1'] . '-' . $rule['id2'];
            if (!isset($configByPair[$key])) {
                $configByPair[$key] = [];
            }
            $configByPair[$key][] = $rule;
        }

        $demandeIds = array_values(array_filter(array_map(function ($demande) {
            return isset($demande['id']) ? (int) $demande['id'] : null;
        }, $demandes)));

        if (!empty($demandeIds)) {
            $placeholders = buildPlaceholders(count($demandeIds));
            $stmt = $pdo->prepare(
                'select id_demande, id_statut, date, dt, duree_travaille_total from demande_statut where id_demande in (' . $placeholders . ') order by id_demande, date'
            );
            $stmt->execute($demandeIds);
            $rows = $stmt->fetchAll();

            foreach ($rows as $row) {
                $demandeId = (int) $row['id_demande'];
                if (!isset($demandeStatutHistory[$demandeId])) {
                    $demandeStatutHistory[$demandeId] = [];
                }
                $demandeStatutHistory[$demandeId][] = [
                    'statutId' => (int) $row['id_statut'],
                    'date' => $row['date'],
                    'dt' => $row['dt'],
                ];

                if ($row['duree_travaille_total'] !== null) {
                    $dureeTravailleTotalByDemande[$demandeId] = $row['duree_travaille_total'];
                }
            }
        }
    } catch (Throwable $ex) {
        $dbError = $ex->getMessage();
    }
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demandes - Forage</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f2f2f2; }
        .error { color: #b00020; margin-bottom: 16px; }
        .rules { margin: 0; padding-left: 18px; }
        .rules li { margin: 4px 0; }
    </style>
</head>
<body>
<h1>Liste des demandes | ETU004043</h1>

<?php if ($error): ?>
    <div class="error"><?php echo htmlspecialchars($error, ENT_QUOTES, 'UTF-8'); ?></div>
<?php elseif ($dbError): ?>
    <div class="error">Erreur BD: <?php echo htmlspecialchars($dbError, ENT_QUOTES, 'UTF-8'); ?></div>
<?php else: ?>
    <?php if (empty($demandes)): ?>
        <p>Aucune demande.</p>
    <?php else: ?>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Commune</th>
                    <th>Date</th>
                    <th>Lieu</th>
                    <th>Statut</th>
                    <th>Duree travail total</th>
                    <th>Regles</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($demandes as $demande): ?>
                    <?php
                        $demandeId = isset($demande['id']) ? (int) $demande['id'] : null;
                        $history = $demandeId !== null && isset($demandeStatutHistory[$demandeId])
                            ? $demandeStatutHistory[$demandeId]
                            : [];
                        $dureeTravailleTotal = $demandeId !== null && isset($dureeTravailleTotalByDemande[$demandeId])
                            ? $dureeTravailleTotalByDemande[$demandeId]
                            : null;
                        $ruleItems = [];

                        foreach ($configByPair as $pairKey => $rules) {
                            $parts = explode('-', $pairKey, 2);
                            $id1 = (int) $parts[0];
                            $id2 = (int) $parts[1];
                            $dtCalc = computeDtBetween($history, $id1, $id2);
                            $color = pickColorForDt($rules, $dtCalc);
                            if ($color === null) {
                                continue;
                            }

                            $label1 = $statutMap[$id1] ?? ('statut ' . $id1);
                            $label2 = $statutMap[$id2] ?? ('statut ' . $id2);
                            $ruleItems[] = sprintf(
                                '[%s -> %s] => %s',
                                $label1,
                                $label2,
                                $color
                            );
                        }
                    ?>
                    <tr>
                        <td><?php echo htmlspecialchars($demande['id'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['client']['nom'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['commune']['libelle'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['dateDemande'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['lieu'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['statutActuelLibelle'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars(formatHours($dureeTravailleTotal), ENT_QUOTES, 'UTF-8'); ?></td>
                        <td>
                            <?php if (empty($ruleItems)): ?>
                                <span>Aucune configuration.</span>
                            <?php else: ?>
                                <ul class="rules">
                                    <?php foreach ($ruleItems as $item): ?>
                                        <li><?php echo htmlspecialchars($item, ENT_QUOTES, 'UTF-8'); ?></li>
                                    <?php endforeach; ?>
                                </ul>
                            <?php endif; ?>
                        </td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    <?php endif; ?>
<?php endif; ?>
</body>
</html>
