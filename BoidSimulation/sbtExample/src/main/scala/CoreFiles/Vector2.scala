package CoreFiles

class Vector2(initialX: Double, initialY: Double):
  private var xComp:Double = initialX
  private var yComp:Double = initialY
  def getX: Double = xComp
  def getY: Double = yComp
  def normalize: Vector2 = Vector2(this.xComp / this.magnitude, this.yComp / this.magnitude)
  def direction: Double =
    if xComp == 0.0 && yComp == 0.0 then
      0.0
    else if xComp == 0 then
      if yComp > 0 then
        math.Pi/2
      else
        3 * math.Pi/2
    else
      var angleF = math.atan(yComp/xComp)
      if xComp < 0 then
        angleF + math.Pi
      else if yComp < 0 then
        angleF + math.Pi*2
      else
        angleF

  def magnitude: Double = math.sqrt(math.pow(xComp, 2)+ math.pow(yComp, 2))
  def add(v: Vector2): Vector2 = Vector2(this.xComp + v.xComp, this.yComp + v.yComp)
  def multiplyConst(const: Double): Vector2 = Vector2(this.xComp * const, this.yComp * const)
  override def toString = "X: " + xComp + " Y: " + yComp
  def dist(other: Vector2): Double = math.sqrt(math.pow((xComp - other.getX), 2) + math.pow(yComp - other.getY, 2))
end Vector2
