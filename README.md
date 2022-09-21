# Temporal Spring Boot Integration Demo

## Links

* [Temporal Java SDK](https://github.com/temporalio/sdk-java)
* [Spring Boot Integration package](https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha)

## How to use
* Start Temporal Server
  * Easiest to follow instructions [here](https://github.com/tsurdilo/my-temporal-dockercompose#deploying-without-auto-setup)
  as you would be able to check out metrics and tracing out of box.
* Start app
        
      mvn clean install spring-boot:run

* [Start onboarding](http://localhost:3030/)

## DSL 

Demo also includes integration with CNCF Serverless Workflow DSL

* [Start onboarding via DSL](http://localhost:3030/dsl.html)