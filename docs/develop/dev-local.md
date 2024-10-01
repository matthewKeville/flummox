
ReBoggled is built in the spring boot ecosystem and uses MariaDB as a db back-end.
The front-end is built using Functional React & gets trans-piled and bundled using webpack.

# Initializing the database

## Local DB credentials

database = reboggled_local_dev_db
username = reboggled_local_dev
password = elggob

## Accessing mariadb without account

```sh
sudo mariadb -u root
```

## Create application database

```sql
create database reboggled_local_dev_db;
```

> build tables

```sh
sudo mariadb -u root reboggled_local_dev_db < src/main/resources/schema.sql
```

### Sanity Check

```sql
show databases;
```

> should list newly created db : reboggled_local_dev_db

***Nuking The DB***

If you must wipe the database clean and start from scratch you can re-execute the schema script.

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

> this runner will populate sample data to the database then terminate the runtime.
> it will add duplicate data if ran multiple times.

# Running locally

## Launch

mvn spring-boot:run # launch spring runtime
npm run watch       # transpile jsx & bundle into built.js triggered on file changes

## Access

Spring Boot MVC will launch tomcat and serve the website at http://localhost:8080.
Note `http` as at this point in development, SSL Certificates are not present.

# Developing Locally

With the above processes mentioned in Launch, you should be able to modifications
to the source code reflected soon after writing them to file. Spring boot has been configured
to hot reload and the `watch` task from node will rebuild the bundle if it detects changes.
