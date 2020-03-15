# kstreams

[Kafka Streams](https://kafka.apache.org/documentation/streams/) with [CloudSpec](https://cloudevents.io/)-compatible events serialized with [protocol buffers](https://developers.google.com/protocol-buffers/docs/proto3).

## Running Locally

1. Install/start Zookeeper and Kafka, `./build.sh start`
1. Create test topics, `./build.sh createTopics`
1. Start the app, `sbt run`
1. Start a Kafka Producer and write input in Protobuf format,
    ```shell script
    ./build.sh produce
    >a: "hi, kafka"<ENTER>
    >a: "bye, kafka"<ENTER>
    ```
1. Start Kafka Consumers in new teminals to view results,
    ```shell script
    ./build.sh consumeOut1
    ./build.sh consumeOut2
    ./build.sh consumeOut3
    ```
1. Once you're done, `./build.sh stop`

If you send a bad message and/or start getting exceptions, `./build.sh reset`.
