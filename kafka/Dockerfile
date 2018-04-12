FROM openjdk:8-8-jre-alpine3.7
ENV KAFKA_HOME=/app/kafka_2.11-1.1.0
ENV ZK=localhost:2181
RUN mkdir /app
RUN wget -O /app/kafka_2.11-1.1.0.tgz http://www-us.apache.org/dist/kafka/1.1.0/kafka_2.11-1.1.0.tgz
RUN tar -xvzf /app/kafka_2.11-1.1.0.tgz -C /app/
COPY findbrokerid.py ${KAFKA_HOME}/findbrokerid.py
COPY startkafka.sh ${KAFKA_HOME}/startkafka.sh
ENTRYPOINT  bash ${KAFKA_HOME}/startkafka.sh
# ENTRYPOINT /app/kafka_2.11-1.1.0/bin/kafka-server-start.sh /app/kafka_2.11-1.1.0/config/server.properties --override zookeeper.connect=${ZK}