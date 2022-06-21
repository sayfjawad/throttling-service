# Throttling-service & OSDBK

The Throttling Service acts as an intermediary between the JMS Consumer and EbMS Core. The Throttling Service receives a REST call from the JMS Consumer that holds a receiver oin. The Throttling Service then queries the EbMS database and returns a. 
The jms-consumer will be the main consumer of this service.

The Throttling Service is part of OSDBK (Open Source Dienst Beveiligd Koppelvlak). OSDBK consists of five applications:

- EbMS Core / EbMS Admin
- JMS Producer
- JMS Consumer
- Apache ActiveMQ
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

The queries are configured via:

~~~
throttling:
  sql:
    ready-to-send: "select COUNT(*) from ebms_message em where em.to_party_id = ? and status = 10"
    already-sent: "select COUNT(*) from ebms_message em where em.to_party_id = ? and status in (11, 12, 13) and status_time BETWEEN now() - (interval '1s') and now()"
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

### Task Executor Configuration
The Throttling Service makes use of a ThreadPoolTaskExecutor to determine the amount of pending tasks per CPA in an asynchronous manner.
Based on performance and resource requirements this task executor can be configured in the application.yml.

The following properties can be configured:
- corePoolSize - Set the ThreadPoolExecutor's core pool size.
- maxPoolSize - Set the ThreadPoolExecutor's maximum pool size.
- queueSize - Set the capacity for the ThreadPoolExecutor's BlockingQueue.

~~~
spring:
  task-executor:
    corePoolSize: 10
    maxPoolSize: 25
    queueSize: 100
~~~

### How to run this application locally
An example docker-compose has been provided. This can be run to test this application.
However the following variables need to be populated by means of a `.env` file:
~~~

${EBMS_JDBC_USERNAME} -- the username used to connect to the EBMS database
${EBMS_JDBC_PASSWORD} -- the password used to connect to the EBMS database
${EBMS_POSTGRESQL_IMAGE_TAG} -- the tag of your built EBMS PostgresQL docker image
~~~

Example command to bring up the application:
~~~
docker-compose --env-file path/to/env/file/.env up
~~~

### How to test this application locally
A GET request can be invoked via Postman to: `http://localhost:8080/throttling/{cpaId}/pendingTasks` 
