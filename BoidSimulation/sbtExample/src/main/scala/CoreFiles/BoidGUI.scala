package CoreFiles

import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{GridPane, HBox, Pane, VBox}
import scalafx.scene.paint.Color.*
import scalafx.scene.paint.*
import scalafx.scene.text.*
import scalafx.scene.shape.*
import scalafx.scene.control.*
import scalafx.animation.AnimationTimer
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.input.MouseEvent

object BoidGUI extends JFXApp3:
  val simulation = Simulation
  val settings = simulation.parameters
  simulation.initialTesting()

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "MainStage"
      width = 900 //initial window size
      height = 800

    val root = GridPane() // grid pane that houses all of the gui components
    root.setHgap(1)
    root.gridLinesVisible = true

    val scene = new Scene(parent = root) // Scene acts as a container for the scene graph
    {
      onMouseClicked = (click: MouseEvent) => {
      if click.eventType == javafx.scene.input.MouseEvent.MOUSE_CLICKED then
        if click.x > 50 && click.x < 650 && click.y > 50 && click.y < 650 then
          settings.addObstacle(Obstacle(Vector2(click.x-50, click.y-50)))
      }
    }

    stage.scene = scene // Assigning the new scene as the current scene for the stage


    val sliderBox = new VBox //creates a box that will house all of the sliders and other interactables

    //creates a box for file information text
    val bottomFileText = new HBox
    bottomFileText.setMinSize(800, 50)
    val fileInformation = Label("Number of Boids: " + settings.boids.length + "\nClick on simulation area to add obstacles!")
    fileInformation.setFont(Font.font("verdana", 16))
    bottomFileText.children += fileInformation

    var pane = paneUpdate(false) //creating the first pane of the simulation using the paneUpdate method



    //buffers in the GUI layout to improve clarity
    val leftBuffer = new VBox
    leftBuffer.setMinSize(50, simulation.parameters.windowHeight- 50)
    val topBuffer = new HBox
    topBuffer.setMinSize(simulation.parameters.windowWidth + 50, 50)


    //creation of all slider and their correspoding labels
    var sliders = Vector[Slider]()

    def addSlider(start: Double, end: Double, value :Double, name: String, bigTick: Double, smallTick: Double) =
      val toReturn = Slider(start, end, value)
      toReturn.setShowTickLabels(true)
      toReturn.setShowTickMarks(true)
      toReturn.setMajorTickUnit(bigTick)
      toReturn.setBlockIncrement(smallTick)
      sliderBox.children += Label(name)
      sliderBox.children += toReturn
      sliders = sliders :+ toReturn

    addSlider(0.0, 5.0, settings.separationWeight, "Separation Weight", 0.5, 0.1)
    addSlider(0.0, 5.0, settings.cohesionWeight, "Cohesion Weight", 0.5, 0.1)
    addSlider(0.0, 5.0, settings.alignmentWeight, "Alignment Weight", 0.5, 0.1)
    addSlider(10, 150, settings.viewRadius, "View Radius", 10, 1)
    addSlider(1, 10, settings.speed, "Boid Speed", 3, 1)
    addSlider(0, 500, settings.boids.length, "Number of Boids", 10, 1)

    //creation of button section
    val buttonBox = GridPane()
    buttonBox.setAlignment(Pos.CENTER)
    sliderBox.children += buttonBox
    val viewCircleToggleButton = ToggleButton("ToggleViewCircles")


    //creation of boid adding and removing buttons
    val boidBox = new GridPane()
    val addBoidButton = Button("+1 Boid")
    val removeBoidButton = Button("-1 Boid")
    addBoidButton.onAction = (event: ActionEvent) => {
      settings.addRandomBoid()
      sliders(5).setValue(sliders(5).getValue + 1)
      fileInformation.setText("Number of Boids: " + settings.boids.length + "\nClick on simulation area to add obstacles!")
    }
    removeBoidButton.onAction  = (event: ActionEvent) =>{
      settings.removeRandomBoid()
      sliders(5).setValue(sliders(5).getValue - 1)
      fileInformation.setText("Number of Boids: " + settings.boids.length + "\nClick on simulation area to add obstacles!")
    }
    boidBox.add(addBoidButton, 1, 0)
    boidBox.add(removeBoidButton, 0, 0)


    //creation of the pause and obstacle clearning buttons
    val pauseButton = ToggleButton("Pause/Resume")
    val obstacleClearer = Button("Clear Obstacles")
    obstacleClearer.onAction = (event: ActionEvent) => {
      settings.clearObstacles()
    }
    //adding all buttons to the button grid pane
    buttonBox.add(viewCircleToggleButton, 1, 0)
    buttonBox.add(boidBox, 1, 2)
    buttonBox.add(pauseButton, 1, 3)
    buttonBox.add(obstacleClearer, 1, 4)
    //adding all compoenents to the gui
    root.add(topBuffer, 0, 0, 2, 1)
    root.add(leftBuffer, 0, 1, 1, 2)
    root.add(pane, 1, 1)
    root.add(sliderBox, 2, 1, 1, 2)
    root.add(bottomFileText, 1, 2, 2, 1)


    /* Defines an AnimationTimer that loops and runs the entire updating sequence.
    *  This involves calling the update method of the simulation,
    *  along with creating a new pane with updated locations of boids
    */

    var tick = 0 //used for fps calculations in console
    var secStart = System.nanoTime()

    val animateTimer = AnimationTimer {t =>
      if !pauseButton.isSelected then
        simulation.update() //updating the simulation if not paused

      //refreshing the pane
      root.getChildren.remove(pane)
      pane = paneUpdate(viewCircleToggleButton.isSelected)
      root.add(pane, 1, 1)

      //updating all slider modifyable constants in environment object(called settings here) if changes detected
      if (math rint sliders(0).getValue * 100) / 100 != settings.separationWeight then
        settings.separationWeight = (math rint sliders(0).getValue * 100) / 100
      else if (math rint sliders(1).getValue * 100) / 100 != settings.cohesionWeight then
        settings.cohesionWeight = (math rint sliders(1).getValue * 100) / 100
      else if (math rint sliders(2).getValue * 100) / 100 != settings.alignmentWeight then
        settings.alignmentWeight = (math rint sliders(2).getValue * 100) / 100
      else if (math rint sliders(3).getValue * 100) / 100 != settings.viewRadius then
        settings.viewRadius = sliders(3).getValue.toInt
      else if (math rint sliders(4).getValue * 100) / 100 != settings.speed then
        settings.speed = sliders(4).getValue

      if (math rint sliders(5).getValue * 100) / 100 > settings.boids.length-1 then
        while (math rint sliders(5).getValue * 100) / 100 > settings.boids.length do
          settings.addRandomBoid()
      else if (math rint sliders(5).getValue * 100) / 100 < settings.boids.length-1 then
        while (math rint sliders(5).getValue * 100) / 100 < settings.boids.length do
          settings.removeRandomBoid()
      fileInformation.setText("Number of Boids: " + settings.boids.length + "\nClick on simulation area to add obstacles!")

      //fps calculating
      tick += 1
      if (System.nanoTime - secStart) / 1e9d > 1 then
        println("fps: " + tick)
        secStart = System.nanoTime()
        tick = 0

    }

    animateTimer.start()


  //large method that creates a pane with boids drawn with upated positional information
  def paneUpdate(showViews: Boolean): Pane =
    //creation of new pane and redefining the size, paddings, and settings the background
    val pane = new Pane()
    pane.setMaxSize(100, 100)
    pane.setMinSize(pane.maxHeight.toDouble, pane.maxWidth.toDouble)
    pane.setPadding(new javafx.geometry.Insets(0, 25, 25, 0))
    pane.setMinSize(settings.windowWidth, settings.windowHeight)
    val rectangle = Rectangle(0, 0, settings.windowWidth, settings.windowHeight)
    pane.children += rectangle

    //draws all boids as four sided polygons, using boid position information from settings
    val sizeConstant = 10 // adjusts the size of the boids and obstacles
    for bird <- settings.boids do
      val pos = bird.getPosition
      if bird.x < settings.windowWidth + settings.edgeJumpConst && bird.x > -settings.edgeJumpConst
        && bird.y > -settings.edgeJumpConst && bird.y < settings.windowHeight + settings.edgeJumpConst then
        val dir = bird.getFacing
        val triangle = Polygon(
          pos.getX+(2*sizeConstant*math.cos(dir)), pos.getY+(2*sizeConstant*math.sin(dir)),
          pos.getX+sizeConstant*(math.cos(dir+(3 * math.Pi/4))), pos.getY+sizeConstant * math.sin(dir+(3 * math.Pi/4)),
          pos.getX, pos.getY,
          pos.getX+sizeConstant*math.cos(dir+((5 * math.Pi/4))), pos.getY+sizeConstant * math.sin(dir+(5 * math.Pi/4)))
        triangle.setFill(Color.Navy)
        triangle.translateX = -0.7 * sizeConstant*math.cos(dir)
        triangle.translateY = -0.7 * sizeConstant*math.sin(dir)
        pane.children += triangle

         //if view circles are toggled, draws them for each boid
        if showViews then
          val viewCircle = Circle(bird.x, bird.y, bird.getViewRadius)
          viewCircle.setFill(null)
          viewCircle.setStroke(Color.Red)
          viewCircle.setStrokeWidth(0.5)
          pane.children += viewCircle

    //draws all obstacles as purple circles of radius 10
    for obstacle <- simulation.parameters.obstacles do
      val pos = obstacle.getPosition
      val circle = Circle(pos.getX, pos.getY, sizeConstant)
      circle.setFill(Color.Purple)
      pane.children += circle
    rectangle.setFill(Color.DarkGrey)

    pane

end BoidGUI

