package semester.foundation.fextile.scene

import javafx.{scene => fxs}

import semester.foundation.fextile.boundary.FextileDelegate

import scala.concurrent.Future

class Parent extends FextileDelegate[fxs.Parent] {
  override val delegate: Future[fxs.Parent] = _
}
