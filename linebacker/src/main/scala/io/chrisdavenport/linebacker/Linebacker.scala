package io.chrisdavenport.linebacker

import cats.effect._
import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

trait Linebacker[F[_]] {

  def blockingContext: ExecutionContext

  /**
   * Attempts to Run the Given `F[A]` on the blocking pool.
   * Then shifts back to the F to the Context Shift
   * Requires Implicit ContextShift Available
   */
  final def blockContextShift[A](fa: F[A])(implicit cs: ContextShift[F]): F[A] =
    cs.evalOn(blockingContext)(fa)

  /**
   * Same Method as blockContextShift but significantly shorter.
    **/
  final def blockCS[A](fa: F[A])(implicit cs: ContextShift[F]): F[A] =
    blockContextShift(fa)
}

object Linebacker {
  def apply[F[_]](implicit ev: Linebacker[F]): Linebacker[F] = ev

  def fromExecutorService[F[_]](es: ExecutorService): Linebacker[F] = new Linebacker[F] {
    def blockingContext = ExecutionContext.fromExecutorService(es)
  }
  def fromExecutionContext[F[_]](ec: ExecutionContext): Linebacker[F] = new Linebacker[F] {
    def blockingContext = ec
  }
}
