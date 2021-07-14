# mpservice

A Clojure service that is deployed on Google Cloud Run

## Instructions

This service is based on this article https://hannuhartikainen.fi/blog/clojure-cloud-run/

main files

- project.clj
- src/mpservice/main.clj

## Running locally

`export PORT=3030` to set environment variable for port and then `lein run`

Do in one line as `export PORT=3030 && lein run`

## Deploying to Google Cloud Run

Compile and generate a pom file: `lein do compile :all, pom`

Build with Jib and upload image to Google Cloud: `mvn compile jib:build `

Note: if you are running it for the first time you will need to run `gcloud auth configure-docker` before uploading image to Google Cloud