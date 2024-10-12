## flyway usage notes

To run postgres from docker-compose use file in scripts dir:

```
docker-compose -f scripts/postgres-docker-compose.yml up [-d]
```

Compose file include pg admin for db UI found at `http://localhost:5050`. Connect to running db with connection
`http://localhost:5432/jcdraftutility`

Ensure `jcdraftutility` database exists before running flyway scripts. Can connect with running pgadmin or with psql cli
tool.

```
psql -h localhost -p 5432 -U postgres -c 'create database jcdraftutility;' jcdraftutility
```

Flyway tasks to run migration scripts (note: changes to migration scripts won't take affect unless you rebuild):

```
./gradlew build
./gradlew flywayMigrate -p data
./gradlew flywayInfo -p data
```

Subsequent flyway scripts should just follow the pattern `V#_init_whatever.sql` with the version sequentially increasing
and the first word being `init`.