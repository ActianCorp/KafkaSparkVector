# KafkaSparkVector

The package "com.actian.KafkaSparkVector" aims to help to start with Kafka/Spark/Vector streaming.
In this example we provides a very simple code  which streams Kafka messages into Actian Vector[H].
Scheduling, intermediate transformations and parallelization of streams into Vector[H] is insured by 
a Spark streaming job.

In this demo, we write through th Kafka producer described in the Quick start at : http://kafka.apache.org. 
The Kafka message is gathered and processed into Spark SQL before to be loaded into Vector in a parallel 
streaming fashion.


A) Check demo environment is ready (see Pre-build steps below)


B) Build the demo :


	$ export ACTIAN_HOME=/opt/Actian && export DEMO=$ACTIAN_HOME/KafkaSparkVector 

	$ export M2_REPOS=$HOME/.m2

	$ cd $ACTIAN_HOME

	$ git clone https://github.com/ActianCorp/KafkaSparkVector.git


	$ cd $DEMO

	$ vi src/main/scala/com/actian/KafkaSparkVector/SimpleApp.scala

		// Change connection properties to fit your demo environment 
		var rs  =     sqlContext.sql("""CREATE TEMPORARY TABLE kafka
						USING com.actian.spark_vector.sql.DefaultSource
						OPTIONS (
						host "localhost",
						instance "V5",
						database "dbt3",
						table "kafka" )""")
    
    
		// Init Kafka spark streaming
		val interval : Duration = Seconds(2)
		val topics   : String = "test"                       // A comma separated list of Kafka topics
		val brokers  : String = "localhost:9092"             // The kafka brocker


	$ mvn package


C) Run the demo

	$ export ACTIAN_HOME=/opt/Actian 
	$ export SPARK_VECTOR=$ACTIAN_HOME/spark-vector
        $ export SPARK_HOME=$ACTIAN_HOME/spark-1.6.1
        $ export DEMO=$ACTIAN_HOME/KafkaSparkVector

	$ export M2_REPO=$HOME/.m2/repository       ## Path to you maven repository
	
	$ $SPARK_HOME/bin/spark-submit --jars $SPARK_VECTOR/target/spark_vector-assembly-1.0-SNAPSHOT.jar,$M2_REPO/com/yammer/metrics/metrics-core/2.2.0/metrics-core-2.2.0.jar,$M2_REPO/org/apache/kafka/kafka_2.10/0.8.2.1/kafka_2.10-0.8.2.1.jar,$SPARK_HOME/external/kafka/target/spark-streaming-kafka_2.10-1.6.1.jar --class "com.actian.KafkaSparkVector.SimpleApp" --master "local[4]" $DEMO/target/KafkaSparkVector-1.0-SNAPSHOT.jar


	=> Send message using the Kafka producer describe below 



Pre-build steps :
---------------------------------------------------------------------------------

Required pre-steps to make the demo running.


1 - Install Kafka from Quick start at "http://kafka.apache.org" 

	Ex: 

	$ export ACTIAN_HOME=/opt/Actian && export KAFKA_HOME=$ACTIAN_HOME/kafka

	$ mkdir -p $ACTIAN_HOME
	
	$ wget http://wwwftp.ciril.fr/pub/apache/kafka/0.9.0.1/kafka_2.10-0.9.0.1.tgz

	$ tar xzf kafka_2.10-0.9.0.1.tgz -C$ACTIAN_HOME

	$ ln -s $KAFKA_HOME/kafka_2.10-0.9.0.1 $ACTIAN_HOME/kafka

	$ mkdir -p $KAFKA_HOME/logs
       
        $ cd $KAFKA_HOME

	$ bin/zookeeper-server-start.sh config/zookeeper.properties 1>$KAFKA_HOME/logs/zookeeper.log 1>&1 &     ## Starts Zookeper

	$ bin/kafka-server-start.sh config/server.properties 1>$KAFKA_HOME/logs/kafka-server.log 2>&1 &         ## Start Kafka server

	$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test  ## Create topic "test"

	$ bin/kafka-topics.sh --list --zookeeper localhost:2181                                                       ## Check topic is created

		test

        $ bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning                      ## Start a consummer

       

        ## Open new terminal session, start a producer, write a message in the producer windows

        ....

        $ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
           This is test 
           => If config is correct message is broadcasted and display in the above consumer windows



2 - Install Spark 1.6.1 from https://spark.apache.org/

	Ex:
	
	$ export ACTIAN_HOME=/opt/Actian && export SPARK_HOME=$ACTIAN_HOME/spark-1.6.1

	$ wget -c http://d3kbcqa49mib13.cloudfront.net/spark-1.6.1.tgz

	$ tar xf spark-1.6.1.tgz  -C $ACTIAN_HOME



3 - Install Spark-Vector from https://github.com/ActianCorp/spark-vector

	Ex:

	$ export ACTIAN_HOME=/opt/Actian && export SPARK_VECTOR=$ACTIAN_HOME/spark-vector

	$ cd $ACTIAN_HOME

	$ git clone https://github.com/ActianCorp/spark-vector

	$ cd $SPARK_VECTOR$  

	$ export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"

        $  build/mvn -DskipTests clean package
	
	$ sbt assembly



  
4 - Create a table to store Kafka messages into your Actian-Vector[H] database

	$ sql test
		create table kafka ( a varchar32 )\g



	





