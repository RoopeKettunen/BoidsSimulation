package CoreFiles

trait Entity(startPosition: Vector2):
  protected var position = startPosition
  def getPosition:Vector2 = position
  def setPosition(pos: Vector2): Unit = position = pos

end Entity

