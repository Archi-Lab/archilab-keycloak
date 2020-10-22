# ArchiLab Keycloak

Setup scripts and configuration files for ArchiLabs
[Keycloak](https://www.keycloak.org/) server.

## Admin account

Keycloak requires an initial admin account to login into the master realm for
Keycloak configuration. The admin and database credentials are stored in
ArchiLab Vault and have to be created as Docker secrets before. For simplicity
you may use
[vault-to-docker-secret](https://github.com/Archi-Lab/vault-to-docker-secret).

## Themes

There are some customizations of the original Keycloak themes in the themes
folder of the Docker context. Documentation can be found
[here](https://www.keycloak.org/docs/latest/server_development/#_themes).

## Setup

To start Keycloak in production mode run:

```bash
$ run.sh
```

If you want to develop locally you should run:

```bash
$ run-dev.sh
```

This disables caching and does not set a fixed frontend URL.

To shut down Keycloak run:

```bash
$ stop.sh
```

## Backup and restore

To backup the keycloak folder and database of a running server run:

```bash
$ backup/backup.sh <DB_USER>
```

The keycloak folder is backed up as recommended in the
[official documentation](https://www.keycloak.org/docs/latest/upgrading/#_prep_migration)
but is actually never used since we don't use any special configuration.

To restore the backup of the database make sure Keycloak is shut down and run
only the database:

```bash
$ start-db-only.sh
```

Then restore the backup with:

```bash
$ restore-db.sh <SQL_BACKUP_FILE> <DB_USER>
```

Shut down the database and start the whole stack again:

```bash
$ stop.sh
$ run.sh
```
