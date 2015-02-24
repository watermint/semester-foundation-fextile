package semester.foundation.fextile.application

import java.util.concurrent.{Executors, Executor, Semaphore}
import javafx.{stage => fxs, application => fxa}

import scala.concurrent.{ExecutionContext, Future}

private[fextile]
class ApplicationHelper
  extends fxa.Application {

  def start(stage: fxs.Stage): Unit = {
    ApplicationHelper.stage = Some(stage)
    ApplicationHelper.launchLock.release(Integer.MAX_VALUE)
    ApplicationHelper.launcher.foreach {
      launcher =>
        stage.show()
    }
  }
}

object ApplicationHelper {
  private[fextile] var launcher: Option[ApplicationLauncher] = None
  private[fextile] var stage: Option[fxs.Stage] = None
  private val launchLock: Semaphore = {
    val semaphore = new Semaphore(Integer.MAX_VALUE)
    semaphore.drainPermits()
    semaphore
  }

  val enqueueExecutionContext = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def fxExecutionContext: ExecutionContext = {
    ExecutionContext.fromExecutor(
      new Executor {
        override def execute(command: Runnable): Unit = {
          Future {
            if (ApplicationHelper.launchLock.availablePermits() < 1) {
              ApplicationHelper.launchLock.acquire()
            }
            fxa.Platform.runLater(command)
          }(enqueueExecutionContext)
        }
      }
    )
  }

}
