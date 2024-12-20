FROM --platform=linux/amd64 openjdk:21-jdk

WORKDIR webapp/

ADD bootstrap/target/marketplace-indexer.jar .
ADD docker-start.sh .

RUN curl -L -o dd-java-agent.jar https://dtdg.co/latest-java-tracer

ENTRYPOINT ["./docker-start.sh", "marketplace-indexer.jar"]
