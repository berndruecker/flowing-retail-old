# flowing-retail-example-kafka
The "flowing retail example" 

# Get Started

* Start & configure channel (see below)
* Start all modules, as easiest use the Starter class:

```starter/io.flowing.retail.command.SimpleStarter

## Channel Kafka

* Install Kafka
* Start Kafka
* Create Topic "flowing-retail"


```kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flowing-retail

You can query all topics by: 

```kafka-topics.sh --list --zookeeper localhost:2181


# Process implemented with Camunda BPM

## Visibility via cockpit

* Download Camunda Distribution of your choice
* Configure Datasource to connect to: jdbc:h2:tcp://localhost:8092/mem:camunda
* Best: Do not start job executor
* Run it and you can use cockpit normally