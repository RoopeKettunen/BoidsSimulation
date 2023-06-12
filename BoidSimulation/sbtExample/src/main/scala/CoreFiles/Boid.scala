package CoreFiles

class Boid(startLocation: Vector2, initialFacing: Double, environment: Environment) extends Entity(startLocation):
  private val mass:Double = 5
  private val max_Force:Double = 0.25
  private val glide_Speed: Double = 0.5 //minimum speed the boid will stay at
  private val view_Angle = 300

  private var max_Speed:Double = 2
  private var view_Radius = 50
  private var facing = initialFacing
  private var velocity: Vector2 = Vector2(math.cos(initialFacing) * max_Speed,math.sin(initialFacing) *  max_Speed)

  def getSpeed: Double = max_Speed
  def getFacing: Double = facing
  def getViewRadius: Double = view_Radius
  def setMaxSpeed(speed: Double) = max_Speed = speed
  def setViewRadius(view: Int): Unit = view_Radius = view
  def getViewAngle: Double = view_Angle
  def getVelocity: Vector2 = velocity
  def x: Double = getPosition.getX
  def y: Double = getPosition.getY
  //updates velocity based on posiitons of nearby boids

  def synchronize() =
    if environment.speed != this.max_Speed then
      this.max_Speed = environment.speed
    if this.view_Radius != environment.viewRadius then
      this.view_Radius = environment.viewRadius

  def orient(): Unit =
    var numberOfNeighbors = 0
    var sepForce = Vector2(0, 0)
    var alignForce = Vector2(0, 0)
    var cohForce = Vector2(0, 0)
    var avoidForce = Vector2(0, 0)//obstacle avoidance force which is not affected by separation weightings

    //this section accounts for the edge wrapping of the world where the boids fly,
    // and attempts to account for boids that are on the opposite side of the map
    val edgeChecks = scala.collection.mutable.Map[String, Boolean]("top" -> false, "bottom"-> false, "right" -> false, "left" -> false)
    def applyNeighbor(pos: Vector2, b: Boid): Boolean =
      val distance = this.position.dist(pos)
      if distance < view_Radius then //if the other boid is within view radius, then adjust all three orientation vectors
        numberOfNeighbors += 1
        sepForce = sepForce.add(this.position.add(pos.multiplyConst(-1)).normalize.multiplyConst(20/distance))
        cohForce = cohForce.add(b.getPosition.add(this.position.multiplyConst(-1)))
        alignForce = alignForce.add(b.getVelocity)
        true
      else
        false

    //various edge checking
    if this.y - view_Radius < 0 then
      edgeChecks("top") = true // and in loop check y-windowHeight
    else if this.y + view_Radius > environment.windowHeight then
      edgeChecks("bottom") = true// and in loop check y+windowHeight of each boid
    if this.x - view_Radius < 0 then
      edgeChecks("left") = true
    else if this.x + view_Radius > environment.windowWidth then
      edgeChecks("right") = true
    var checkEdges = false
    if edgeChecks.filter(_._2).toVector.nonEmpty then
      checkEdges = true

    //loop through all of the boids and compare this boids position to other boids, taking into account the edge wrapping of the world
    for boid <- environment.boids do
      if boid != this then
        if !applyNeighbor(boid.getPosition, boid) && checkEdges then
          for pair <- edgeChecks do
            if pair._2 then
              if pair._1 == "top" then
                applyNeighbor(boid.getPosition.add(Vector2(0, -1*environment.windowHeight)), boid)
              if pair._1 == "bottom" then
                applyNeighbor(boid.getPosition.add(Vector2(0, environment.windowHeight)), boid)
              if pair._1 == "left" then
                applyNeighbor(boid.getPosition.add(Vector2(-1*environment.windowWidth, 0)), boid)
              if pair._1 == "right" then
                applyNeighbor(boid.getPosition.add(Vector2(environment.windowWidth, 0)), boid)

    //averages the alignment and cohesion forces based on number of nearby boids
    alignForce = alignForce.multiplyConst(1.0 / numberOfNeighbors)
    cohForce = cohForce.multiplyConst(1.0 / numberOfNeighbors)

    //calculates the avoidForce based on nearby obstacles
    for obstacle <- environment.obstacles do
        val distance = this.position.dist(obstacle.getPosition)
        if distance < view_Radius then
          avoidForce = avoidForce.add(this.position.add(obstacle.getPosition.multiplyConst(-1)).normalize.multiplyConst(50/distance))

    //obstacle avoidance force applied
    if avoidForce.magnitude!=0 && avoidForce.magnitude.isFinite then
      velocity = velocity.add( avoidForce.multiplyConst(max_Force/2))

    //Separation vector applied to velocity
    if sepForce.magnitude != 0 && sepForce.magnitude.isFinite then
      velocity = velocity.add( sepForce.multiplyConst(max_Force/2).multiplyConst(environment.separationWeight))

    //Alignment vector calculated and added to velocity

    if alignForce.getX.isFinite && alignForce.getY.isFinite then
      velocity = velocity.add(alignForce.normalize.multiplyConst(max_Force/ mass).multiplyConst(environment.alignmentWeight))

    //Cohesion vector calculated and added to velocity
    if cohForce.getX.isFinite && cohForce.getY.isFinite then
      velocity = velocity.add(cohForce.normalize.multiplyConst(max_Force/ mass).multiplyConst(environment.cohesionWeight))

    //Makes sure velocity stays above glide speed
    if velocity.magnitude < glide_Speed then
      velocity = velocity.multiplyConst(1.05)
    //Makes sure velocity stays within maximum speed
    if velocity.magnitude > max_Speed then
      velocity = velocity.normalize.multiplyConst(max_Speed)


  //applied velocity to position and adjust facing accordingly
  def move(): Unit =
    position = position.add(velocity)
    facing = velocity.direction

end Boid


