# forage

Squelette Spring MVC déployable en WAR sur Tomcat, avec Thymeleaf, JPA et PostgreSQL.

## Prérequis

- Java 21
- Maven 3.9+
- Tomcat 10.1+ pour le déploiement
- Docker pour PostgreSQL local

## Démarrage de la base

```bash
docker compose up -d postgres
```

## Compilation

```bash
mvn clean package
```

Le fichier généré est `target/forage.war`.

## Déploiement Tomcat

Copie `target/forage.war` dans le dossier `webapps` de Tomcat, puis démarre Tomcat.

L'application est accessible sur `http://localhost:8080/forage/`.

## Configuration PostgreSQL

Par défaut, l'application utilise les variables suivantes :

- `APP_DATASOURCE_URL`
- `APP_DATASOURCE_USERNAME`
- `APP_DATASOURCE_PASSWORD`

Si elles ne sont pas définies, elle se connecte au conteneur PostgreSQL local configuré dans `docker-compose.yml`.
