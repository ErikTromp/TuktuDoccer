package tuktu.doccer

import java.io._
import scala.io.Source
import play.api.libs.json.Json

object GenerateDocs {
    def generateDoc(file: File) = {
        // Open file for reading
        val content = Source.fromFile(file)("utf-8").getLines.mkString
        
        // Parse the JSON
        val json = Json.parse(content)
    }
    
    def main(args: Array[String]) = {
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
        
        // Iterate over files in meta folder
        val files = new File(metaFolder).listFiles
    }
}