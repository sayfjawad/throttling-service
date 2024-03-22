# Throttling-service & OSDBK

The Throttling Service acts as an intermediary between the JMS Consumer and EbMS Core. The Throttling Service receives a REST call from the JMS Consumer that holds a receiver oin. 
The Throttling Service then queries the EbMS database and returns a boolean to signal if the afnemer should be throttled. 

The Throttling Service is part of OSDBK (Open Source Dienst Beveiligd Koppelvlak). OSDBK consists of seven applications:

- EbMS Core / EbMS Admin
- JMS Producer
- JMS Consumer
- Apache ActiveMQ
- CPA Service
- OSDBK Admin Console
- Throttling Service (optional)

The Throttling Service is responsible for determining the amount of tasks that is currently being processed per afnemer.
This is determined by adding the amount of messages that is ready to be sent per CPA and the amount of messages already sent in the last second per CPA. 
This then gives you an indication of how busy the receiving party linked to the CPA is and if this receiving party is capable of accepting more traffic.
If not, the JMS Consumer will rollback the message to the queue and will retry to send it in the next second.

### EBMS Database Configuration
The Throttling Service needs to query the EBMS database to determine the load that is being processed per CPA.
URL and credentials can be configured in the application.yml.

~~~
spring:
  datasource:
    url: jdbc:postgresql://ebms-database:5432/ebms
    username: ebms
    password: ebms
~~~

### Throttled Afnemers Configuration
The Throttling Service will only query the database if the afnemer of the message is configured for throttling. For each afnemer that needs
to be throttled, an item consisting of the `oin` of the afnemer and the `throttleValue` (the amount of messages per second the afnemer can receive) must be 
added to the `throttling.afnemers` list.

~~~
throttling:
  afnemers:
    -
      oin: 11111111111111111111
      throttleValue: 2
    -
      oin: 22222222222222222222
      throttleValue: 10
~~~

### How to run this application locally
An example docker-compose has been provided. This can be run to test this application.

However, the following variables need to be populated by means of a `.env` file:
~~~
${OSDBK_IMAGE_REPOSITORY} -- location of your Docker repository where you are housing your built OSDBK docker images
${ACTIVEMQ_IMAGE_TAG} -- the tag of your built ActiveMQ docker image
${ACTIVEMQ_POSTGRESQL_USERNAME} -- the username used to connect to the ActiveMQ database
${ACTIVEMQ_POSTGRESQL_PASSWORD} -- the password used to connect to the ActiveMQ database
${DGL_STUB_IMAGE_TAG}  -- the tag of your built stub application docker image, to simulate interactions with an external party
${DGL_STUB_MESSAGE_SENDER_OIN_AFNEMER} -- the OIN of your stubbed application
${EBMS_JDBC_USERNAME} -- the username used to connect to the EBMS database
${EBMS_JDBC_PASSWORD} -- the password used to connect to the EBMS database
${EBMS_JMS_BROKER_USERNAME} -- the username used to connect the ebms application to the ActiveMQ instance
${EBMS_JMS_BROKER_PASSWORD} -- the password used to connect the ebms application to the ActiveMQ instance
${EBMS_POSTGRESQL_IMAGE_TAG} -- the tag of your built EBMS PostgresQL docker image
${EBMS_STUB_IMAGE_TAG} -- the tag of your built stub application docker image, to simulate interactions with an external party
${JMS_CONSUMER_IMAGE_TAG} -- the tag of your built jms-consumer docker image
${JMS_CONSUMER_ACTIVEMQ_USER} -- the username used to connect the jms-consumer application to the ActiveMQ instance
${JMS_CONSUMER_ACTIVEMQ_PASSWORD} -- the password used to connect the jms-consumer application to the ActiveMQ instance
${JMS_PRODUCER_IMAGE_TAG} -- the tag of your built jms-producer docker image
${JMS_PRODUCER_ACTIVEMQ_USER} -- the username used to connect the jms-producer application to the ActiveMQ instance
${JMS_PRODUCER_ACTIVEMQ_PASSWORD} -- the password used to connect the jms-producer application to the ActiveMQ instance
${THROTTLING_SERVICE_IMAGE_TAG} -- the tag of your built throttling docker image
${CPA_SERVICE_IMAGE_TAG} -- the tag of your built cpa-service docker image
${OSDBK_ADMIN_CONSOLE_IMAGE_TAG} -- the tag of your built OSDBK Admin console docker image
${CPA_JDBC_USERNAME} -- the username used to connect to the CPA database
${CPA_JDBC_PASSWORD} -- the password used to connect to the CPA database
${CPA_SERVICE_ACTIVEMQ_USER} -- the username used to connect the cpa-service application to the ActiveMQ instance
${CPA_SERVICE_ACTIVEMQ_PASSWORD} -- the password used to connect the cpa-service application to the ActiveMQ instance
${CPA_DATABASE_IMAGE_TAG} -- the tag of your built CPA PostgresQL docker image
~~~

Example command to bring up the application:
~~~
docker-compose --env-file path/to/env/file/.env up
~~~

### How to test this application locally
A GET request can be invoked via Postman to: `http://localhost:8080/throttling/{afnemerOin}`. The endpoint is protected 
by basic authentication. Basic Authentication can be configured via:
~~~
spring:
  security:
    user:
      name: changeit
      password: changeit
~~~

## Release notes
See [NOTES][NOTES] for latest.

[NOTES]: templates/NOTES.txt

### Older release notes collated below:

V Up to 1.12.3
- Various improvements to:
    - container technology
    - use of base-images
    - GITLAB Security scan framework implemented
    - Improved Open Source build process and container releases
    - Test improvements via docker-compose
    - Dependency upgrades
