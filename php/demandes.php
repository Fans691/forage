<?php
$apiUrl = 'http://localhost:8080/forage/api/demandes';
if (!empty($_GET['api'])) {
    $apiUrl = $_GET['api'];
}

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

$result = fetchJson($apiUrl);
$error = $result['error'];
$demandes = $result['data'];
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
    </style>
</head>
<body>
<h1>Liste des demandes</h1>

<?php if ($error): ?>
    <div class="error"><?php echo htmlspecialchars($error, ENT_QUOTES, 'UTF-8'); ?></div>
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
                </tr>
            </thead>
            <tbody>
                <?php foreach ($demandes as $demande): ?>
                    <tr>
                        <td><?php echo htmlspecialchars($demande['id'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['client']['nom'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['commune']['libelle'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['dateDemande'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['lieu'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($demande['statutActuelLibelle'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    <?php endif; ?>
<?php endif; ?>
</body>
</html>
