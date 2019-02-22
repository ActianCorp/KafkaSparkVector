package com.actian.KafkaSparkVector

/*
 Copyright 2019 Actian Corporation

   Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

import kafka.serializer.StringDecoder

import org.apache.spark.sql._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.kafka010.KafkaUtils

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe



import com.actian._



object SimpleApp {
  def main(args: Array[String]) {

    // Init Spark context
    val sparkConf  = new SparkConf().setAppName("Simple Application")
    val sc         = new SparkContext(sparkConf)

    // Init SQL context
    val sqlContext = new SQLContext(sc) 
    import sqlContext.implicits._
    
    // Change connection properties to fit your demo environment
    var rs  =     sqlContext.sql("""CREATE TEMPORARY VIEW kafka
                              USING com.actian.spark_vector.sql.DefaultSource
                              OPTIONS (
                              host "localhost",
                              instance "V5",
                              database "dbt3",
                              table "kafka" )""")
    
    
    // Init Kafka spark streaming
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val interval : Duration = Seconds(2)
    val ssc = new StreamingContext(sc, interval )

    val topics = Array("test")
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )


    val m = messages.map(record => (record.key, record.value))

    m.foreachRDD(msg => {
      if (msg.count() > 0) {
        val df = msg.toDF().registerTempTable("kafka_temp")
        
        rs = sqlContext.sql("select * from kafka_temp")
        rs.show()                                                  // Display received data
        
        rs = sqlContext.sql("insert into table kafka select * from kafka_temp")
        rs = sqlContext.sql("select count(*) from kafka")
        rs.show()                                                  // Count total data inserted into Vector[H]
        }
    } )
    
    ssc.start()
    ssc.awaitTermination()
      
  }

}
