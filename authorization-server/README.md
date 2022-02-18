# README

## Introduction

This repository contains the source code for an example OAuth 2.0 Authorization server. The service is a Java based Spring Boot application.

It provides access tokens via client credentials flow.

![OAuth2 client credentials flow](https://developer.okta.com/assets-jekyll/blog/client-credentials-spring-security/client-credentials-sequence-7fee4525b7b3e50e56ab635711468599b17126e8a8393986c572fffc2c4883b3.png)
_Source: https://developer.okta.com_

## Requirements

You need to install the following:

- Java 17
- Docker CE
- Gradle 7.4

## Run application locally

To start the application locally, the following preparatory actions are necessary:

1. Run `docker-compose up -d --no-recreate` from the root directory of the project. This starts a postgres database that is needed locally to run the application.

2. Define the data source connection details in file `application.yml`:
    - `spring.datasource.username` (you have to use user `postgres`)
    - `spring.datasource.password` (password from `docker-compose.yml`)
    - `spring.datasource.url` (`jdbc:postgresql://localhost:5432/authorization-server-db`, the database name must match `POSTGRES_DB` of service `auth-db` from `docker-compose.yml`)

3. Define the `client-id` and `client-secret` for oauth clients `client-one` and `client-two`. Choose any value you want.

4. Deposit the private key and public key of your generated rsa key pair.
    - `security.authorization-server-private-key`
    - `security.authorization-server-public-key`

It is also possible to define all mentioned connection details and secrets as environment variables. In this case no variables in `application.yml` need to be changed. The names of the environment variables are already in the `application.yml` file. You can define the environment variables for example within a Run Configuration in IntelliJ (other IDEs have similar possibilities).

In local setup it is also possible to simply define empty environment variables with name `AUTHORIZATION_SERVER_PRIVATE_KEY` and `AUTHORIZATION_SERVER_PUBLIC_KEY`. The key pair is then generated automatically when the application is started.

## Generate Key Pair manually

Generate an RSA key pair:

```
openssl genrsa -out keypair.pem 2048
```

Extract the public key:

```
openssl rsa -in keypair.pem -pubout -out public_key.crt
```

Extract the private key:

```
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private_key.key
```

From these files the actual keys have to be extracted without the first and last line (the `---` parts) and without the line breaks and put into the corresponding environment variable.

## Start the application

via gradle
- Execute command `./gradlew bootRun` in root directory of project

via your IDE
- Execute main class `rocks.danielw.AuthorizationServerApplication`
