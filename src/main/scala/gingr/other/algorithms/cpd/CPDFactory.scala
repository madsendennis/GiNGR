/*
 * Copyright 2022 University of Basel, Graphics and Vision Research Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gingr.other.algorithms.cpd

import gingr.api.registration.utils.PointSequenceConverter
import breeze.linalg.DenseMatrix
import scalismo.common.Vectorizer
import scalismo.geometry.{Point, _3D}

/*
 Implementation of Point Set Registration: Coherent Point Drift
 In this script, only the non-rigid algorithm is implemented. Paper: https://arxiv.org/pdf/0905.2635.pdf
 A python implementation already exists from where parts of the implementation is from: https://github.com/siavashk/pycpd
 */
class CPDFactory(
    val templatePoints: Seq[Point[_3D]],
    val lambda: Double = 2,
    val beta: Double = 2,
    val w: Double = 0
)(implicit
    val vectorizer: Vectorizer[Point[_3D]]
) {
  val M: Int                        = templatePoints.length // num of reference points
  val dim: Int                      = vectorizer.dim        // dimension
  val G: DenseMatrix[Double]        = initializeKernelMatrixG(templatePoints, beta)
  val template: DenseMatrix[Double] = PointSequenceConverter.toMatrix(templatePoints)

  require(0.0 <= w && w <= 1.0)
  require(beta > 0)
  require(lambda > 0)

  /** Initialize G matrix - formula in paper fig. 4
    *
    * @param points
    * @param beta
    * @return
    */
  private def initializeKernelMatrixG(
      points: Seq[Point[_3D]],
      beta: Double
  ): DenseMatrix[Double] = {
    val M                      = points.length
    val G: DenseMatrix[Double] = DenseMatrix.zeros[Double](M, M)
    (0 until M).map { i =>
      (0 until M).map { j =>
        G(i, j) = math.exp(-(points(i) - points(j)).norm2 / (2 * math.pow(beta, 2)))
      }
    }
    G
  }

  def registerRigidly(targetPoints: Seq[Point[_3D]]): RigidCPD = {
    new RigidCPD(targetPoints, this)
  }

  def registerNonRigidly(targetPoints: Seq[Point[_3D]]): RigidCPD = {
    new NonRigidCPD(targetPoints, this)
  }

  def registerAffine(targetPoints: Seq[Point[_3D]]): RigidCPD = {
    new AffineCPD(targetPoints, this)
  }

}
