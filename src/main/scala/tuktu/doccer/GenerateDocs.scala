package tuktu.doccer

import java.io.BufferedWriter
import java.io.File
import scala.io.Codec.string2codec
import scala.io.Source
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.io.FileOutputStream
import java.io.OutputStreamWriter

object GenerateDocs {
    /**
     * Reads a single parameter and recurses for the others
     */
    def readParameter(json: JsObject, writer: BufferedWriter, nestDepth: Int): Unit = {
        // Get fields we need
        val name = (json \ "name").asOpt[String] match {
            case None | Some("") => "[UNNAMED]"
            case Some(s) => s
        }
        val dataType = (json \ "type").as[String]
        val required = (json \ "required").asOpt[Boolean]
        val default = (json \ "default").asOpt[JsValue] match {
            case None => ""
            case Some(dflt) => ", default = " + dflt.toString
        }
        val parameters = (json \ "parameters").asOpt[List[JsObject]]
        val description = (json \ "description").asOpt[String]

        // Generate output and recurse for all parameters
        writer.write(
            (for (i <- 1 to nestDepth * 2) yield " ").mkString("") +
                "* **" + name + "** *(type: " + dataType + ")* `[" + {
                    if (required.getOrElse(false))
                        "Required"
                    else
                        "Optional" + default
                } +
                "]`")

        writer.newLine
        description match {
            case None => {}
            case Some(descr) => {
                writer.write((for (i <- 1 to nestDepth * 2) yield " ").mkString("") + "- " + descr)
                writer.newLine
            }
        }
        writer.newLine

        // Recurse
        parameters match {
            case None => {}
            case Some(params) => params.foreach(parameter => readParameter(parameter, writer, nestDepth + 1))
        }
    }

    /**
     * Bootstraps a documentation
     */
    def readTransformer(json: JsObject, writer: BufferedWriter): Unit = {
        // Get fields
        val name = (json \ "name").as[String]
        val className = (json \ "class").as[String]
        val description = (json \ "description").asOpt[String]
        val parameters = (json \ "parameters").asOpt[List[JsObject]]

        // Write out main output
        writer.write("### " + className)
        writer.newLine
        writer.write(description.getOrElse("No description present."))
        writer.newLine
        writer.newLine

        // Process all parameters
        parameters match {
            case None => {
                writer.write("No parameters.")
                writer.newLine
                writer.newLine
            }
            case Some(params) => params.foreach(parameter => readParameter(parameter, writer, 1))
        }
    }

    /**
     * Generates a doc for a single file
     */
    def generateDoc(file: File, outputFolder: String, typeName: String) = {
        println("Processing file " + file.getAbsoluteFile)

        // Open file for reading
        val content = Source.fromFile(file)("utf-8").getLines.mkString

        // Parse the JSON
        val json = Json.parse(content).asInstanceOf[JsObject]

        // Create dirs
        val fileName = file.getAbsolutePath
        val outputFile = new File(outputFolder + "/" + fileName.drop(fileName.indexOf(typeName) + typeName.size + 1) + ".md")
        new File(outputFile.getParent).mkdirs
        // Invoke the writer
        val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))
        readTransformer(json, writer)
        writer.close
    }

    /**
     * Generates documentation for all files
     */
    def generateAllDocs(folder: File, outputFolder: String, typeName: String): Unit = {
        // Get all files in this folder
        val files = folder.listFiles
        files.foreach(file => file.isDirectory match {
            case true => generateAllDocs(file, outputFolder, typeName)
            case false => {
                // Process the file
                generateDoc(file, outputFolder, typeName)
            }
        })
    }

    def main(args: Array[String]) {
        if (args.size != 2) {
            println("Not enough arguments found!")
            println
            println("Please specify:")
            println("  modeller meta folder")
            println("  documentation output folder")
            sys.exit(0)
        }

        // Get args
        val metaFolder = args(0)
        val outputFolder = args(1)

        // Remove the output folder first
        val out = new File(outputFolder)
        out.delete
        out.mkdir

        // Bootstrap generators and processors
        val generatorsFolder = new File(metaFolder + "/generators")
        generateAllDocs(generatorsFolder, outputFolder + "/generators/", "generators")
        val processorsFolder = new File(metaFolder + "/processors")
        generateAllDocs(processorsFolder, outputFolder + "/processors/", "processors")
    }
}