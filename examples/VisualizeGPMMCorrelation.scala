//> using scala "3"
//> using lib "ch.unibas.cs.gravis::gingr:0.1.0-SNAPSHOT"
//> using lib "ch.unibas.cs.gravis::scalismo-ui:0.91.2"
import DemoHelper.DemoDatasetLoader
import DemoHelper.CallBackFunctions.{visualLogger}
import gingr.api.gpmm.GPMMTriangleMesh3D
import scalismo.ui.api.ScalismoUI

@main def VisualizeGPMMCorrelation() =
  val (model, lms) = DemoDatasetLoader.armadillo.modelGauss(Some(10000))

  // Choose landmark to compute correlation
  val refLm = lms.get.find(f => f.id == "J").get
  val refId = model.reference.pointSet.findClosestPoint(refLm.point).id

  val gpmmHelp = GPMMTriangleMesh3D(model.reference, relativeTolerance = 0.0)
  val color = gpmmHelp.computeDistanceAbsMesh(model, refId)

  val ui = ScalismoUI()
  ui.show(color, "Correlation")
  ui.show(refLm, "landmarks")