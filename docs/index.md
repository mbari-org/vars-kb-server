# VARS Knowlegebase Server (aka vars-kb-server)

![MBARI logo](assets/images/logo-mbari-3b.png)

The VARS Knowlegebase server provides a naming service that can be used by a variety of applications. The fastest way to get up and running is to use the [m3-quickstart](https://github.com/mbari-media-management/m3-quickstart) project which will bring up a full suite of services, including a database server. If you don't need all those services, it is possible to just run the vars-kb-server.

## Quickstart

### System Diagram

```mermaid
flowchart LR
  A[(Database)]---|JDBC|B(vars-kb-server)
  A---|JDBC|C(vars-kb UI)
  B---[REST/JSON|D(Your applications)
```

### Docker

If you already have a database server that you'd like to use for the knowledgebase. Out-of-the box the vars-kb-server includes drivers for Oracle, SQL Server, Postgresql, and Apache Derby. Create a database user with read/write and create schema permissions. When the vars-kb-server is configured with that user, it can create the tables it needs the first time you run it. The vars-kb-server is distributed as a [docker image](https://hub.docker.com/repository/docker/mbari/vars-kb-server). Here's an example command to start it. The environment parameters will vary depending on your database configuration.

```bash
docker run -d \
    -p 8080:8080 \
    -e DATABASE_ENVIRONMENT=production \
    -e DATABASE_LOG_LEVEL=INFO \
    -e HTTP_CONTEXT_PATH="/kb" \
    -e LOGBACK_LEVEL=WARN \
    -e ORG_MBARI_VARS_KBSERVER_DATABASE_HIKARI_TEST="SELECT 1" \
    -e ORG_MBARI_VARS_KBSERVER_DATABASE_PRODUCTION_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
    -e ORG_MBARI_VARS_KBSERVER_DATABASE_PRODUCTION_NAME=SQLServer \
    -e ORG_MBARI_VARS_KBSERVER_DATABASE_PRODUCTION_PASSWORD="super_secret_password" \
    -e ORG_MBARI_VARS_KBSERVER_DATABASE_PRODUCTION_URL="jdbc:sqlserver://my.database.org:1433;databaseName=VARS_KB" \
    -e ORG_MBARI_VARS_KBSERVER_DATABASE_PRODUCTION_USER=varsuser \
    --name=vars-kb-server \
    --restart unless-stopped \
    mbari/vars-kb-server
```

## UI

The knowledgebase can be edited using the [vars-kb](https://github.com/mbari-media-management/vars-kb) application.
