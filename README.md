# FACEIT Interview Question - User Service

## Running the Application

- Run the application by executing `docker compose up -d` in the command line. NOTE: The first execution might take a few minutes because all images and dependencies need to be download.
  - Execute HTTP requests against the `http://localhost:8080/users` endpoint. 
  - Health endpoint can be reached by executing a GET request against `http://localhost:8080/actuator/health`.
- API documentation can be found under `src/main/resources/openapi.yaml`.
  - A postman collection with sample requests has been provided at `src/main/resources/FACEIT_Users_Collection.postman_collection.json`.
  - A quick end-to-end test by running the entire postman collection. You can find more information on this process [here](https://learning.postman.com/docs/running-collections/intro-to-collection-runs/).
- Unit tests can be run by running `mvn test` in the command line.

## Design Decisions

- Implemented using Java and Spring Framework (Data, Web, Kafka, Actuator). Build and dependencies managed through maven.
- API contract defines using the OpenAPI specification.
  - Request and response objects for clear separation between the request and response bodies.
    - `password` only specified on the request body but never returned on the response body while the `id` is only specified in the response body and not the request body.
- OpenAPI Spring Generator to ensure consistency between the contact and the implementation.
  - Continuous code generation is used rather than a scaffolding approach in order to not have generated code as part of git and syntax analysis.
- MongoDB used for data storage.
  - Better for rapid prototyping and eventual changes.
  - `password` is encrypted so they can not be leaked easily.
  - `createdAt` and `updatedAt` are managed using Spring Data.
- Single node Kafka cluster with a single topic named `users-topic`.
  - Topic only has a single partition however a key is sent alongside the message so all messages pertaining to the same user are pushed on the same partition when Kafka is scaled.
- Docker and Docker Compose used for packaging and ease of execution.
- Health endpoint implemented via Spring Actuator.
- Tested service, mapper and validators using unit tests. Other classes did not have much business logic and thus they did not need dedicated tests.
  - End-to-end tests can be executed using the Postman runner.

## Improvements

- While the application can be run as Kubernetes pods and scaled where necessary, the CQRS pattern can be adopted which effectively splits the service into a Query and a Command services in order to have the ability to scale only parts of the system.
- Similarly, a serverless approach could be considered rather than creating a Spring Boot deployable which, when run on one of the major cloud platform, would nullify the scaling effort since the cloud platforms would automatically scale the functions as required.
- MongoDB favors consistency and partition tolerance, while sacrificing availability. If availability is considered more important than partition tolerance then the database would need to be changed to a CA storage.
- The Docker Compose approach works for small-scale testing however dedicated multi-node instances of MongoDB, Kafka and Zookeeper will increase potential scalability. There are many cloud providers which offer this infrastructure as a service which would reduce the set-up time and reduce maintenance time/cost.
- Multiple partitions for the `users-topic` will allow for higher throughput for message consumption.
- Message contract should be available as a dependency in a programming language agnostic specification language (like Protobuf) so both the producers and the consumers can depend on a single version of the contract and the consumer can easily update to new message versions.
- The MongoDB encryption key should not be bundled with the application. It should be injected as part of the CICD pipeline with different values for different environments.
- Improved testing via the creation of integration and end-to-end tests leveraging Spring Boot, Cucumber and/or Karate.
- Deployment can be done via a CICD pipeline where all tests are executed on various environments before the code is deployed and running on production.