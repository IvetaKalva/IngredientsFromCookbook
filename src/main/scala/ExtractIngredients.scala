import java.io.{File, PrintWriter}
import scala.io.Source

object ExtractIngredients extends App {

  val url = "https://www.gutenberg.org/ebooks/13177.txt.utf-8"
  val bufferedSource = Source.fromURL(url)
  val txtLines = bufferedSource.getLines.toArray
  bufferedSource.close()

  val firstTextCleaning = txtLines.slice(0, txtLines.indexOf("INDEX TO RECIPES")) //remove everything after "INDEX TO RECIPES"
    .filterNot(line => line.isEmpty) //discard empty lines
    .collect {
      case line if line.matches("^\\s{4}.*") && line.toUpperCase != line => line //keep lines starting with 4 whitespaces
      case line if line.matches("^[A-Z]{3}.*") => line //keep uppercase lines and uppercase lines with special condition
    }

  var secondTextCleaning: Array[String] = Array()
  var previousLine = ""
  firstTextCleaning.foreach(line => {
    if (line.matches("^\\s{5}.*")) previousLine = previousLine + line.replaceAll("\\s+", " ")
    else {
      if (previousLine.nonEmpty) secondTextCleaning = secondTextCleaning :+ previousLine
      previousLine = line
    }
  }) //lines that started with five or more whitespaces are added to the previous line, extra whitespaces are removed
  secondTextCleaning = secondTextCleaning :+ firstTextCleaning.last //add last line to complete an array

  var thirdTextCleaning: Array[String] = Array()
  var allCapsLine = ""
  secondTextCleaning.foreach(line => {
    if (line.matches("^[A-Z]{3}.*")) allCapsLine = line
    else {
      if (allCapsLine.nonEmpty) thirdTextCleaning = thirdTextCleaning :+ "\n\n" + allCapsLine + "\n"
      thirdTextCleaning = thirdTextCleaning :+ line
      allCapsLine = ""
    }
  }) //get rid of unnecessary uppercase lines that are not recipe titles, the titles stored in the array are separated by blank lines for better readability in the .txt file
  thirdTextCleaning.foreach(println)

  val savePath = "src/result/Recipes_titles_and_ingredients_from_the_cookbook.txt"
  def saveTitlesAndIngredients(thirdTextCleaning: Array[String], destinationPath: String, separator: String = "\n"): Unit = {
    val pw = new PrintWriter(new File(destinationPath))
    val txt = thirdTextCleaning.mkString(separator)
    pw.write(txt)
    pw.close()
  }
  saveTitlesAndIngredients(thirdTextCleaning, savePath)
}
