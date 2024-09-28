# Database initialization (local)

database - reboggled_local_dev_db
username - reboggled_local_dev
password - elggob

```sh
sudo mariadb -u root
```

## Create application database

```sql
create database reboggled_local_dev_db;
```

build tables

```sh
sudo mariadb -u root reboggled_local_dev_db < src/main/resources/schema.sql
```

### Sanity Check

```sql
show databases;
```

should list newly created db

## Create application user

```sql
CREATE USER 'reboggled_local_dev'@localhost IDENTIFIED BY 'elggob';
```

```sql
GRANT ALL PRIVILEGES ON reboggled_local_dev_db.* TO 'reboggled_local_dev'@localhost IDENTIFIED BY 'elggob';
```

### Sanity Check

```sql
select user from mysql.user;
```

```sql
SHOW GRANTS FOR 'reboggled_local_dev'@localhost;
```
## Create application data

```sh
mvn spring-boot:run -Dspring-boot.run.arguments=--create-dev-data=true
```

note, this runner simply adds new entities to the db. Ran multiple times will create duplicate users
and lobbies. To get a fresh database with new data, rerun 'schema.sql' against the db before executing the runner.

# Running locally

mvn spring-boot:run # launch spring runtime
npm run watch       # transpile jsx & bundle into built.js triggered on file changes

spring should start listening on https://localhost:8080
this should should redirect to the '/lobby' page (it's not working atm). So
head there to view/join lobbies.
