package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_25_Monad.FunctorSample._
import programming_scala_2nd_edition.Sample_25_Monad.MonadSample.TestMonad

class Sample_25_Monad_test extends FunSuite{
    test("Functor") {
        TestFunctor()
    }

    test("Monda") {
        TestMonad()
    }

    test("State Monad") {
        import programming_scala_2nd_edition.Sample_25_Monad.RandomState._
        rollDie(5)
        rollDieMap(5)
    }
}
