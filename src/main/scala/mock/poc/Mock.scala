package mock.poc

import akka.actor.{Actor, Props}
import semester.foundation.fextile.application.FextileApp
import semester.foundation.fextile.event.WindowEvent
import semester.foundation.fextile.stage.PrimaryStage

class Mock extends Actor {
  override def receive: Receive = {
    case e: WindowEvent =>
  }
}

object Mock extends FextileApp {

  stage = new PrimaryStage {
    title = "Mock"
    width = 800
    height = 600
  }

  override def props: Props = Props[Mock]
}
