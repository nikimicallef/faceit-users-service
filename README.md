# FACEIT Interview Question - User Service

The original interview question can be found in the file named `BE_Tech_Test_-_Java.pdf`.  

## Running the Application

- Run the application by executing `docker compose up -d` in the command line. NOTE: The first execution might take a few minutes because all images and dependencies need to be download.
  - Execute HTTP requests against the `http://localhost:8080/users` endpoint. 
  - Health endpoint can be reached by executing a GET request against `http://localhost:8080/actuator/health`.
- API documentation can be found under `src/main/resources/openapi.yaml`.
  - A postman collection with sample requests has been provided at `src/main/resources/FACEIT_Users_Collection.postman_collection.json`.
  - A quick end-to-end test can be performed by running the entire postman collection. You can find more information about this [here](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/).
- Unit tests can be run by running `mvn test` in the command line.

## Design Decisions

- Implemented using Java and Spring Framework (Data, Web, Kafka, Actuator). Build and dependencies managed through maven.
- REST API preferred over gRPC due to previous familiarity.
- API contract defined using the OpenAPI 3 specification.
  - Request and response objects for clear separation between the request and response bodies.
    - `password` only specified on the request body but never returned on the response body while the `id` is only specified in the response body and not the request body.
- OpenAPI Spring Generator used to ensure consistency between the contact and the implementation.
  - Continuous code generation is used rather than a scaffolding approach in order to not have generated code committed in git and as part of future syntax analysis.
- MongoDB used for data storage.
  - Better for rapid prototyping and eventual changes.
  - `password` is encrypted so that it can not be leaked easily.
  - `createdAt` and `updatedAt` are managed using Spring Data.
- Single node Kafka cluster with a single topic named `users-topic`.
  - Topic only has a single partition however a key is sent alongside the message so all messages pertaining to the same user are pushed on the same partition once Kafka is scaled.
- Docker and Docker Compose used for packaging and execution.
- Health endpoint implemented via Spring Actuator.
- Tested service, mapper and validators using JUnit tests. Other classes did not have much business logic and thus they did not need dedicated tests.
  - End-to-end tests can be executed using the Postman runner although these are not part of the build.

## Improvements

- It might happen that the DB insert/update/delete succeeds but the message does not get sent because Kafka is unreachable. This results in the API returning in a 500 response even though the database has been updated. In order to solve this, a decision needs to be made with regard to the consistency of the data between the database and the messages.
  - A strict consistency approach would require a transaction between the database and sending of the message such that if the message can't be sent then the database change is not committed.
  - A more resilient and scalable approach would be to totally delegate sending of the message to a different process and retry if the message fails to be sent. If after a certain number of retries the sending still fails, then a log of this message is saved within a data store which will be used to re-triggered the failed messages once Kafka recovers. This may result in out of order messages and consistency issues with the data of third-party systems.
  - Either way, both approaches were deemed to be too complex for the initial test, and I felt it was fine to have this minor inconsistency if for some reason Kafka is not available.
- While the application can be run as Kubernetes pods and scaled where necessary, the CQRS pattern can be adopted which effectively splits the service into Query and a Command services thus providing the ability to scale only parts of the system as necessary.
- Similarly, a serverless approach could be considered rather than creating a Spring Boot deployable which, when run on one of the major cloud platform, would nullify the scaling effort since the cloud platforms would automatically scale the functions as required.
- MongoDB favors consistency and partition tolerance while sacrificing availability. If availability is considered more important than partition tolerance then MongoDB would need to be changed for a CA storage.
- The Docker Compose approach works for small-scale testing however, dedicated multi-node instances of MongoDB, Kafka and Zookeeper will increase scalability. There are many cloud providers which offer these infrastructures as a service which would reduce the set-up time and reduce maintenance time/cost.
- Multiple partitions for the `users-topic` will allow for higher throughput.
- Message contracts should be available as a dependency in a programming language agnostic specification language (like Protobuf) so both the producers and the consumers can depend on a single version of the contract and the consumer can easily update to new message versions.
- The MongoDB encryption key should not be bundled with the application. It should be injected as part of the CICD pipeline with different values for different environments.
- Improved testing via the creation of integration and end-to-end tests leveraging Spring Boot, Cucumber and/or Karate.
- Deployment can be done via a CICD pipeline where all tests are executed on various environments before the code is deployed and running on production.
  - The current Postman tests can also be run in this pipeline by using [Newman](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/).
