package CoreFiles


object Simulation:

    val reader = DataReader() //creates reader
    val parameters = Environment() //initializes environment object

    //creates boids and passes number of boids to GUI
    def initialTesting() =
        val inputs =  reader.read()
        val boidNumber = inputs(0)(0)
        val weights = Vector[Double](inputs(0)(1), inputs(0)(2), inputs(0)(3))
        for x <- Range(0, boidNumber) do //creates X (read from file) randomly located and facing boids
            parameters.addRandomBoid()
        parameters.setWeights(weights(0), weights(1), weights(2)) //updates weights based on file information


        for x <- inputs(1) do //adds obstacles that have been read from text file to the environment object called parameters
            parameters.addObstacle(x)


    def update() =
        //calls each boids' velocity updating and postion modifying method
        for boid <- parameters.boids do
            boid.synchronize() //makes sure view radius and max speed are up to date based on slider modifications
            boid.orient() //recalculates boid velocities

            //checks if boid is near edge and teleports the boid to the other side of the world if so, wrapping the world around
            val pos = boid.getPosition
            if pos.getX < parameters.edgeJumpConst && boid.getVelocity.getX < 0 then //adjust for X axis outside of screen
                boid.setPosition(pos.add(Vector2(parameters.windowWidth - 2 * parameters.edgeJumpConst, 0)))
            else if pos.getX > parameters.windowWidth -parameters.edgeJumpConst && boid.getVelocity.getX > 0 then
                boid.setPosition(pos.add(Vector2(-1 * parameters.windowWidth + 2 * parameters.edgeJumpConst, 0)))
            if pos.getY < parameters.edgeJumpConst && boid.getVelocity.getY < 0 then //adjust for Y axis outside of screen
                boid.setPosition(pos.add(Vector2(0, parameters.windowHeight- 2 * parameters.edgeJumpConst)))
            else if pos.getY > parameters.windowHeight -parameters.edgeJumpConst && boid.getVelocity.getY > 0 then
                boid.setPosition(pos.add(Vector2(0, -1 * parameters.windowHeight+2 * parameters.edgeJumpConst)))
        //after velocities have been calculated for all the boids, then the boids are moved
        for boid<- parameters.boids do
            boid.move()

end Simulation
