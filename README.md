# MorMessages

## Overview

Simple forum/messaging webservice.

## Status

[![Build Status](https://travis-ci.org/morbrian/mormessages.svg?branch=master)](https://travis-ci.org/morbrian/mormessages)

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
