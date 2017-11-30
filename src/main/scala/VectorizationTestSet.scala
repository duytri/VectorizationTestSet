package main.scala

import java.io.File
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object VectorizationTestSet {
  def main(args: Array[String]): Unit = {
    try {
      if (args.length < 6 || args.length == 0 || args(0).equals("-h") || args(0).equals("--help"))
        printHelp
      else {
        // input arguments
        println("Getting user parameters...")
        val params = new ParamsHelper
        val inputPath = params.checkAndGetInput(args, "-i", "--input", ParamsHelperType.STRING).asInstanceOf[String]
        val dictionaryPath = params.checkAndGetInput(args, "-d", "--dictionary", ParamsHelperType.STRING).asInstanceOf[String]
        val outputPath = params.checkAndGetInput(args, "-o", "--output", ParamsHelperType.STRING).asInstanceOf[String]
        if (inputPath == null || dictionaryPath == null || outputPath == null) throw new Exception("ERROR: You must declare input, dictionary and output")
        // Processing
        println("Processing...")

        println("Input path: " + inputPath)
        println("Output path: " + outputPath)

        // Analyzing
        println("Start analyze ...")
        val dictionary = Source.fromFile(dictionaryPath)("UTF-8").getLines().toArray
        val listTestFiles = (new File(inputPath)).listFiles()
        var topicVectors = Map[String, ArrayBuffer[(String, Double)]]()

        //~~~~~~~~~~ Get input files ~~~~~~~~~~
        val vectorsFile = new File(outputPath)
        val bwVectors = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vectorsFile, true), "UTF-8"))
        bwVectors.flush()
        listTestFiles.foreach(file => {
          var vector = file.getName.replaceAll("[0-9]", "") //get topic label from file name
          vector = vector.slice(0, vector.length - 1)
          val vocabulary = Source.fromFile(file)("UTF-8").getLines().toArray.map(x => x.toLowerCase())
          dictionary.foreach(word => {
            if (vocabulary.contains(word))
              vector += ", 1"
            else vector += ", 0"
          })
          bwVectors.write(vector + "\n")
        })
        bwVectors.close()

        println("Finished!!!")
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        printHelp()
      }
    }
  }

  def printHelp() = {
    println("Usage: VectorizationTestSet [Arguments]")
    println("       Arguments:")
    println("              -i --input      [path]  : Path of corpus folder")
    println("              -d --dictionary [file]  : Path of corpus folder")
    println("              -o --output     [file]  : Output file path")
    println("              -h --help               : Print this help")
  }
}