package CoreFiles
import scala.collection.mutable.Buffer

class Environment():
  //GUI constants
  val windowWidth = 600
  val windowHeight = 600
  val edgeJumpConst = -5

  //Simulation variables
  var separationWeight:Double = 1.05
  var alignmentWeight:Double = 0.8
  var cohesionWeight:Double = 1.0
  var viewRadius = 70
  var speed = 2.0
  //Entity holders
  var boids: Buffer[Boid] = Buffer[Boid]()
  var obstacles: Buffer[Obstacle] = Buffer[Obstacle]()
  def entities = boids ++ obstacles
  //sets all three weights for all boids
  def setWeights(sep: Double, align: Double, coh: Double) =
    separationWeight = sep
    alignmentWeight = align
    cohesionWeight = coh
  //adds boid to boid list
  def addBoid(b: Boid) =
    boids += b
  //adds obstacle to obstacle list
  def addObstacle(o: Obstacle) =
    obstacles += o

  def addRandomBoid() =
    val bob = Boid(Vector2(50 + math.random() * 500, 50 + math.random() * 500), (math.random() * 2 * math.Pi), this)
    bob.setViewRadius(viewRadius)
    bob.setMaxSpeed(speed)
    boids += bob

  def removeRandomBoid() =
    if boids.nonEmpty then
      val ind = (math.random() * boids.length).toInt
      boids.remove(ind)

  def clearObstacles() =
    obstacles = Buffer[Obstacle]()
end Environment
