## Description

integration jira and redmine by REST.

### Build and deploy image

- Export rep location like:

``
export DOCKER_REP="docker.finch.fm:5000/gosloto/news888-app/jira-redmine-rest-integration"
``

- build jar and image

``
mvn clean package
``

- push to repository like:

``
docker push docker.finch.fm:5000/gosloto/news888-app/jira-redmine-rest-integration:0.0.1-SNAPSHOT
``

-  Next need delivery image to server. You can use docker-compose file. 

## Prepare DB  

you need put jira and redmine keys to db 