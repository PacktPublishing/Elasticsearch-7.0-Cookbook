package com.packtpub

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.circe._
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions

object DeepLearning4s extends App with LazyLogging {


  lazy val client: ElasticClient = {
    ElasticClient(ElasticProperties("http://127.0.0.1:9200"))
  }

  lazy val indexName = "iris"

  case class Iris(label: Int, f1: Double, f2: Double, f3: Double, f4: Double)

  implicitly[Indexable[Iris]]
  val response = client.execute {
    search(indexName).size(1000)
  }.await

  val hits = response.result.to[Iris].toArray

  //Convert the iris data into 150x4 matrix
  val irisMatrix: Array[Array[Double]] = hits.map(r => Array(r.f1, r.f2, r.f3, r.f4))
  //Now do the same for the label data
  val labelMatrix: Array[Array[Double]] = hits.map { r =>
    r.label match {
      case 0 => Array(1.0, 0.0, 0.0)
      case 1 => Array(0.0, 1.0, 0.0)
      case 2 => Array(0.0, 0.0, 1.0)
    }
  }

  val training = Nd4j.create(irisMatrix)
  val labels = Nd4j.create(labelMatrix)

  val allData = new DataSet(training, labels)

  allData.shuffle()
  val testAndTrain = allData.splitTestAndTrain(0.65) //Use 65% of data for training

  val trainingData = testAndTrain.getTrain
  val testData = testAndTrain.getTest

  //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
  val normalizer = new NormalizerStandardize
  normalizer.fit(trainingData) //Collect the statistics (mean/stdev) from the training data. This does not modify the input data

  normalizer.transform(trainingData) //Apply normalization to the training data

  normalizer.transform(testData) //Apply normalization to the test data. This is using statistics calculated from the *training* set

  val numInputs = 4
  val outputNum = 3
  val seed = 6

  logger.info("Build model....")
  val conf = new NeuralNetConfiguration.Builder()
    .seed(seed)
    .activation(Activation.TANH)
    .weightInit(WeightInit.XAVIER)
    .updater(new Sgd(0.1))
    .l2(1e-4)
    .list
    .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(3).build)
    .layer(1, new DenseLayer.Builder().nIn(3).nOut(3).build)
    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .activation(Activation.SOFTMAX).nIn(3).nOut(outputNum).build)
    .backprop(true)
    .pretrain(false)
    .build

  //run the model
  val model = new MultiLayerNetwork(conf)
  model.init()
  model.setListeners(new ScoreIterationListener(100))

  0.to(1000).foreach{ _ => model.fit(trainingData)}

  //evaluate the model on the test set
  val eval = new Evaluation(3)
  val output = model.output(testData.getFeatures)
  eval.eval(testData.getLabels, output)
  logger.info(eval.stats)


  client.close()
}
