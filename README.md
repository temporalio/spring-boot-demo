# Temporal Spring Boot Integration Demo

## Links

* [Temporal Java SDK](https://github.com/temporalio/sdk-java)
* [Spring Boot Integration package](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha)
* [Temporal Spring Boot Thymeleaf UI](https://github.com/tsurdilo/temporal-springboot-web-ui)

## Intro

This demo showcases Temporal Java SDK integration with Spring Boot.
It uses the [Java SDK Spring Boot AutoConfig](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha).

Shown features:
* [Connection Setup](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#connection-setup)
* [Data converter auto-discovery](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#data-converter)
* [Worker auto-discovery](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#auto-discovery)
* [Integration with Metrics (Prometheus)](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#metrics)
* [Integration with Tracing (Jaeger)](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#tracing)
* [Testing](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#testing)
* (todo) [mTLS](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha#mtls)

## Run Demo

1. First we need to set up Temporal Server. To showcase the metrics
and tracing capabilities it's easiest to use [this] https://github.com/tsurdilo/my-temporal-dockercompose) repo
which has it all built in and ready to go. If you already have Temporal
server running on Docker locally you can clean and prune and then run:
   

    git clone https://github.com/tsurdilo/my-temporal-dockercompose
    cd my-temporal-dockercompose
    docker network create temporal-network
    docker compose -f docker-compose-postgres.yml -f docker-compose-services.yml up

2. This demo depends on the [Temporal Spring Boot Thymeleaf UI](https://github.com/tsurdilo/temporal-springboot-web-ui)
project for the web part of it.
So first we have to fetch and compile it:


    git clone git@github.com:tsurdilo/temporal-springboot-web-ui.git
    cd temporal-springboot-web-ui
    mvn clean install
   
3. Now lets build the demo


    git clone git@github.com:tsurdilo/temporal-springboot-demo.git
    cd temporal-springboot-demo
    mvn clean install spring-boot:run

## Use Demo

Once your demo has started up you can access the ui via
[localhost:3030](http://localhost:3030/)

<p align="center">
<img src="img/start.png" width="500px" />
</p>






