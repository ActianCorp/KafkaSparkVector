package com.actian.KafkaSparkVector

/*
 Copyright 2015 Actian Corporation

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
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.kafka.KafkaUtils


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
    
    val topicsSet   = topics.split(",").toSet
    
    val ssc         = new StreamingContext(sc, interval )
    
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
        
    // Receives Kafka message
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
        ssc, kafkaParams, topicsSet)

    val m = messages.map(_._2)   // We keep value of the message
    
    
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