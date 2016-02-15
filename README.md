# MorMessages - JEE based sandbox for Websockets experimentation

## Getting Started

Assumes JEE7 container, tested on Wildfly 9 and 10.

1. Build runs arquillian tests by default.
        
        mvn clean package
        
1. Authentication API

        http://localhost:8080/mormessages/api/rest/auth
        
1. Form API

        http://localhost:8080/mormessages/api/rest/forum

1. Subscription API

        http://localhost:8080/mormessages/api/rest/subscription
        
1. Websocket API

        http://localhost:8080/mormessages/api/websocket
