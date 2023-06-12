
import CoreFiles.{Simulation, 
  Boid, 
  Vector2
}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._


class gateTest extends AnyFlatSpec with Matchers:
    "orient" should "change the boids position accordingly" in {
      val sim = Simulation
      val alan = Boid(Vector2(300, 300), 0, sim.parameters) //first boid facing right
      val bob = Boid(Vector2(280, 280), math.Pi/2, sim.parameters) //second boid facing downward and at a 135 degree angle from the first
      sim.parameters.addBoid(alan)//alan will have an initial velocity of (2, 0)
      sim.parameters.addBoid(bob)// bob will have an initial velocity of (0, 2)
      sim.update()
      val aVel = alan.getVelocity
      val bVel = bob.getVelocity
      val bExpected = Vector2(0.0118, 1.9757) //expected velocities of the second boid bob after one update cycle
      val aExpected = Vector2(1.9988, 0.0692) //expected velocities of the first boid alan after one update cycle

            withClue("When checking the X velocty of bob ") {
              (math rint (bVel.getX*10000))/10000 shouldBe(bExpected.getX)
            }
            withClue("When checking the Y velocty of bob ") {
              (math rint (bVel.getY*10000))/10000 shouldBe(bExpected.getY)
            }
            withClue("When checking the X velocty of alan ") {
              (math rint (aVel.getX*10000))/10000 shouldBe(aExpected.getX)
            }
            withClue("When checking the Y velocty of alan ") {
              (math rint (aVel.getY*10000))/10000 shouldBe(aExpected.getY)
            }
    }

