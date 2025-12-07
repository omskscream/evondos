# Patient measurements service

This service is responsible the medical data (blood pressure, heartbeat) of 
the patients using our system. It collects, saves and shows (to the dedicated caretaker) the collected data.

## API docs 
Should be available at http://localhost:8080/swagger-ui.html and http://localhost:8080/v3/api-docs for OpenApi specification.

## Requirements
- docker engine to build/run
- JDK 21 to build/run
- internet connection to download dependencies

## Building and running tests note
Unfortunately, I didn't manage to run tests with testcontainers because it was incompatible with docker installed on my machine.
I did not manage a way to fix it in appropriate time. Error is `Status 400: {"message":"client version 1.44 is too new. Maximum supported API version is 1.43"}`, 
and one workaround from StackOverflow did not help but instead repeatedly crushed Docker daemon, so I didn't dare to try downgrading docker any other way.

So, if you're lucky, the tests using testcontainers will run great. Otherwise, you can comment `companion object` and `@Testcontainers` annotation in the test files 
and run them using DB deployed from docker compose.
App config in test resources should support it, and I ran both tests this way.

## Manual testing
There's a `manual_testing.http` file in the root of the project with examples of requests that I used to check API manually.
You can use it too, but start the service on the localhost first (or run `Dockerfile` with port mapping).

## AWS deploy
- needs new Spring profile that specifies DB parameters (url, schema, user, password) for cloud DB (example in application-prod.properties)
- could be deployed according to the company standards and best practises.
  E.g. wrap service in docker image (example in Dockerfile), push image to AWS ECR, and deploy it as a service in ECS.

## Further steps
Of course this is not production-ready. Missing things include (but not limited to):
- authentication (and users as a concept in general)
- logging
- proper javadocs (not really necessary for this small scale)
- proper error handling (e.g. for duplicates (patientId, timestamp))
- well-thought use cases (e.g. parameterize high pressure markers in SQL query)
- project structure might be a bit more thoroughly designed if service gets bigger

Also check out attached mind board that I started the task with https://excalidraw.com/#json=gmj_5sM0p8Zoz_yTzywdi,aJ03f9xlZncGEO1OtCYIaQ


## Task definition
Design, develop, and document a basic cloud-based microservice using Kotlin and Spring Boot that handles periodic medical data for multiple patients, collects the data and provides a simple API for reading the collected data.


Requirements:

- Implement the microservice using Kotlin, use Spring Boot for the web framework.
- Integrate with a PostgreSQL database.
- Periodically receive medical data containing patient ID, blood pressure measurement, and heartbeat rate.
- Develop and document a simple API for reading the collected medical data.
- Provide basic documentation for the microservice. Include setup instructions and API documentation.

Deliverables:

1. Complete source code.
2. Basic documentation covering the setup and usage of the microservice. API documentation with examples.
3. Step-by-step instructions to deploy the microservice on AWS.
4. Any tests to verify the functionality of the microservice.
5. Next steps to complete the assignment
6. Optionally, source code and documentation for the UI.
