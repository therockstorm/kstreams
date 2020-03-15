#!/bin/bash

prefix="example"
in="$prefix.in"
out1="$prefix.out.1"
out2="$prefix.out.2"
out3="$prefix.out.3"

quiet() {
  "$@" >/dev/null 2>&1
}

start() {
  quiet brew ls --versions kafka || brew install kafka
  zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &
  kafka-server-start /usr/local/etc/kafka/server.properties
}

stop() {
  kafka-server-stop
  zookeeper-server-stop
}

createTopics() {
  createTopic="kafka-topics --zookeeper localhost:2181 --create --partitions 1 --replication-factor 1 --topic"
  $createTopic $in
  $createTopic $out1
  $createTopic $out2
  $createTopic $out3
}

produce() {
  kafka-console-producer --broker-list localhost:9092 --topic $in
}

consumeOut1() {
  kafka-console-consumer --bootstrap-server localhost:9092 --topic $out1 --from-beginning --property print.key=true
}

consumeOut2() {
  kafka-console-consumer --bootstrap-server localhost:9092 --topic $out2 --from-beginning --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
}

consumeOut3() {
  kafka-console-consumer --bootstrap-server localhost:9092 --topic $out3 --from-beginning --property print.key=true
}

deleteTopics() {
  deleteTopic="kafka-topics --zookeeper localhost:2181 --delete --topic"
  $deleteTopic $in
  $deleteTopic $out1
  $deleteTopic $out2
  $deleteTopic $out3
}

reset() {
  kafka-streams-application-reset --application-id kstreams
  deleteTopics
  createTopics
}

list() {
  kafka-topics --zookeeper localhost:2181 --list
}

$1
