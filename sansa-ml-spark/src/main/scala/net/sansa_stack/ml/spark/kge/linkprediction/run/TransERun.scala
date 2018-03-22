package net.sansa_stack.ml.spark.kge.linkprediction.run

/**
 * Created by lpfgarcia on 14/11/2017.
 */

import org.apache.spark.sql._
import org.apache.log4j.Logger
import org.apache.log4j.Level

import net.sansa_stack.ml.spark.kge.linkprediction.dataframe.Triples
import net.sansa_stack.ml.spark.kge.linkprediction.convertor.ByIndex
import net.sansa_stack.ml.spark.kge.linkprediction.crossvalidation.Holdout
import net.sansa_stack.ml.spark.kge.linkprediction.models.TransE

object TransERun {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val sk = SparkSession.builder.master("local")
    .appName("Tensor").getOrCreate

  def main(args: Array[String]) = {

    val table = new Triples("kge", "/home/hamed/workspace/TransE/DataSets/FB15k/freebase_mtr100_mte100-train.txt", sk)
    println(table.triples.show())
    val data = new ByIndex(table.triples, sk).df

    println(data.show())

    val (train, test) = new Holdout(data, 0.6f).crossValidation()

    println(train.show())
    println(test.show())

    var model = new TransE(train, 100, 20, 1, "L1", sk)
    model.run()

    val predict = new net.sansa_stack.ml.spark.kge.linkprediction.prediction.TransE(model, test, sk)
    println(predict)

  }

}