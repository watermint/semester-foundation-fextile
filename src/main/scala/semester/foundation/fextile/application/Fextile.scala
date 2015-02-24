package semester.foundation.fextile.application

import javafx.{application => fxa}

import akka.actor._
import semester.foundation.fextile.event.{WindowHidden, Event, UIEvent, ApplicationWillLaunch}

class Fextile extends Actor with Stash {
  private var appActor: Option[ActorRef] = None
  implicit val executor = Fextile.system.dispatcher

  def receive: Receive = {
    case launcher: ApplicationLauncher =>
      launcher.app.props foreach {
        props =>
          val actor = Fextile.system.actorOf(props)
          appActor = Some(actor)
          actor ! ApplicationWillLaunch(launcher.app, launcher.args)
      }
      launcher.launch()
      unstashAll()

    case e: UIEvent[_] =>
      e.source.currentActor ! e
      e match {
        case w: WindowHidden =>
          if (!w.fxEvent.isConsumed) {
            Fextile.shutdown()
          }
        case _ =>
      }

    case e: Event =>
      appActor match {
        case Some(app) => app ! e
        case None => stash()
      }
  }
}

object Fextile {
  def shutdown() = {
    system.shutdown()
    fxa.Platform.exit()
  }

  val system = ActorSystem("fextile")

  val ref = system.actorOf(Props[Fextile])

  val appDefault = system.actorOf(Props[ApplicationDefault])
}
