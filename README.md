# KafkaSparkVector

The package "com.actian.KafkaSparkVector" aims to provide a quick start with Kafka/Spark/Vector streaming.
In this example we provide some simple code  which streams Kafka messages into a singleActian Vector/VectorH database table.
Scheduling, intermediate transformations and parallelization of streams into Vector/VectorH is insured by 
a Spark streaming job.

In this demo, we write through th Kafka producer described in the Quick start at : http://kafka.apache.org. 
The Kafka message is gathered and processed into Spark SQL before being loaded into Vector in a parallel 
streaming fashion through Actian's Spark-Vector connector.


A) Check demo environment is ready (see Pre-build steps below)


B) Build the demo :


	$ export ACTIAN_HOME=/opt/Actian && export DEMO=$ACTIAN_HOME/KafkaSparkVector

	$ export M2_REPOS=$HOME/.m2

	$ cd $ACTIAN_HOME

	$ git clone https://github.com/ActianCorp/KafkaSparkVector.git


	$ cd $DEMO

	$ vi src/main/scala/com/actian/KafkaSparkVector/SimpleApp.scala # change the settings as necessary

	$ mvn package


C) Run the demo

	$ export ACTIAN_HOME=/opt/Actian 
	$ export SPARK_VECTOR=$ACTIAN_HOME/spark-vector
    $ export SPARK_HOME=$ACTIAN_HOME/spark
    $ export DEMO=$ACTIAN_HOME/KafkaSparkVector

	$ export M2_REPO=$HOME/.m2/repository       ## Path to your maven repository
	
	$ $SPARK_HOME/bin/spark-submit --jars $SPARK_VECTOR/target/spark-vector-assembly-2.1-SNAPSHOT.jar,$M2_REPO/com/yammer/metrics/metrics-core/2.2.0/metrics-core-2.2.0.jar,$M2_REPO/org/apache/kafka/kafka_2.11/0.10.0.1/kafka_2.11-0.10.-.1.jar,$SPARK_HOME/external/kafka-0-10/target/spark-streaming-kafka-0-10_2.11-2.2.0.jar --class "com.actian.KafkaSparkVector.SimpleApp" --master "local[4]" $DEMO/target/KafkaSparkVector-1.0-SNAPSHOT.jar


	=> Send message using the Kafka producer describe below 



Pre-build steps :
---------------------------------------------------------------------------------

Required pre-steps to make the demo running.


1 - Install Kafka from Quick start at "http://kafka.apache.org" 

	Example: 

	$ export ACTIAN_HOME=/opt/Actian && export KAFKA_HOME=$ACTIAN_HOME/kafka

	$ mkdir -p $ACTIAN_HOME
	
	Download kafka tgz file from the 'net

	$ tar xzf kafka_kafka_2.12-2.1.1.tgz -C $ACTIAN_HOME

	$ ln -s $KAFKA_HOME/kafka_ $ACTIAN_HOME/kafka

	$ mkdir -p $KAFKA_HOME/logs
       
    $ cd $KAFKA_HOME

	$ bin/zookeeper-server-start.sh config/zookeeper.properties 1>$KAFKA_HOME/logs/zookeeper.log 1>&1 &     ## Starts Zookeper

	$ bin/kafka-server-start.sh config/server.properties 1>$KAFKA_HOME/logs/kafka-server.log 2>&1 &         ## Start Kafka server

	$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test &  ## Create topic "test"

	$ bin/kafka-topics.sh --list --zookeeper localhost:2181                                                         ## Check topic is created

		test

    $ bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning                      ## Start a consummer

       

        ## Open new terminal session, start a producer, write a message in the producer windows

        ....

        $ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
           This is test 
           => If config is correct message is broadcasted and display in the above consumer windows



2 - Install Spark 2.2.0 from https://spark.apache.org/

	Example:
	
	$ export ACTIAN_HOME=/opt/Actian && export SPARK_HOME=$ACTIAN_HOME/spark-2.2.0

	Download spark-2.2.0 from the 'net

	$ tar xf spark-2.2.0.tgz  -C $ACTIAN_HOME



3 - Install Spark-Vector from https://github.com/ActianCorp/spark-vector

	Example:

	$ export ACTIAN_HOME=/opt/Actian && export SPARK_VECTOR=$ACTIAN_HOME/spark-vector

	$ cd $ACTIAN_HOME

	$ git clone https://github.com/ActianCorp/spark-vector

	$ cd $SPARK_VECTOR$  

	$ export MAVEN_OPTS="-Xmx2g -XX:ReservedCodeCacheSize=512m"

    $ build/mvn -DskipTests clean package
	
	$ sbt assembly



  
4 - Create a table to store Kafka messages into your Actian Vector/VectorH database

	$ sql test
		create table kafka ( a varchar32 )\g



	





