# reactivekafkawebsocket
Demo application to show how to pipe messages from kafka to a websocket endpoint in a reactive manner.

This application uses project reactors reactive-kafka as well as spring-webflux. Both libraries work together to provide a completely reactive stack, providing access to a kafka topic hosted on confluent cloud.

## How to run

To run this application you need to enter your confluent cloud credential into ccloud_example.properties and rename it to ccloud.properties.

Now you can run it with `mvn spring-boot:run`
