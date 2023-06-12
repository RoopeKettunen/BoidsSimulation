package CoreFiles
import scala.collection.mutable.Buffer
import java.io.{
  FileReader,
  FileNotFoundException,
  BufferedReader,
  IOException,
  FileWriter,
  BufferedWriter
}

class DataReader():
  private var fileName: String = "BoidData.txt"

  //Creates a file reader and buffered file reader to read the file with name fileName, and returns important parameters
  def read():((Int, Double, Double, Double), Seq[Obstacle]) =
    var seq = Seq[String]()
    try
      println(getClass.getClassLoader.getResource(fileName).getPath)
      val fileIn = FileReader(getClass.getClassLoader.getResource(fileName).getPath)
      val linesIn = BufferedReader(fileIn)
      try
        val data = fileOperation(linesIn)
        seq = data(0)
        ((seq.head.toInt, seq(1).toDouble, seq(2).toDouble, seq(3).toDouble), data(1))
      finally
        fileIn.close()
        linesIn.close()

    //catches numerous tested errors that could occur from incorrect data input format within the file
    catch
      case notFound: FileNotFoundException =>
        println("File not found")
        ((50, 1.2, 3.0, 3.0), Seq[Obstacle]())
      case  io: IOException =>
        println("Reading finished with error")
        ((50, 1.2, 3.0, 3.0), Seq[Obstacle]())
      case bob: NullPointerException =>
        println("File not found, using default values instead")
        ((50, 1.2, 3.0, 3.0), Seq[Obstacle]())
      case num: NumberFormatException =>
        println("Problem with reading weightings from text file, try checking formatting again.")
        ((50, 1.2, 3.0, 3.0), Seq[Obstacle]())
      case ind: IndexOutOfBoundsException =>
        println("Problem with reading weightings from text file, make sure all three weights are mentioned.")
        ((50, 1.2, 3.0, 3.0), Seq[Obstacle]())

  /* given a line reader with text file assigned, loops through file and using keywords such as "Boids",
  finds all of the important intial parameters and returns them in a sequence */
  def fileOperation(lineReader: BufferedReader): (Seq[String], Seq[Obstacle]) =
    var resList = Seq[String]()
    var obstacleList = Seq[Obstacle]()
    var oneLine: String = lineReader.readLine().trim

    while oneLine != null do
      if !oneLine(0).equals('#') then
        if oneLine.contains("Boids") then
          resList = resList :+ oneLine.substring(14)
        else if oneLine.contains("Separation") then
          resList = resList :+ oneLine.substring(17)
        else if oneLine.contains("Alignment") then
          resList = resList :+ oneLine.substring(16)
        else if oneLine.contains("Cohesion") then
          resList = resList :+ oneLine.substring(15)
        else if oneLine.contains("Obstacle") then
          val extract = "\\d+".r.findAllIn(oneLine).toVector
          obstacleList = obstacleList :+ Obstacle(Vector2(extract(0).toInt, extract(1).toInt))
      oneLine = lineReader.readLine()
    (resList, obstacleList)

end DataReader
