package programming_scala_2nd_edition.sample_10

object Sample_10_seq_match extends App{
    val nonEmptySeq = Seq(1, 2, 3, 4, 5)
    val emptySeq = Seq.empty[Int]
    val nonEmptyList = List(1, 2, 3, 4, 5)
    val emptyList = Nil
    val nonEmptyVector = Vector(1, 2, 3, 4, 5)
    val emptyVector = Vector.empty[Int]
    val nonEmptyMap = Map("one" -> 1, "two" -> 2, "three" -> 3)
    val emptyMap = Map.empty[String,Int]

    def seqToString[T](seq: Seq[T]): String = seq match {
        case head +: tail =>                  // 覆盖含值的情况。这里的 head 和 tail 都是变量，匹配的实际上是 "+:" 运算模式。
            s"$head +: " + seqToString(tail)
        case Nil => "Nil"                     // 覆盖不含值的情况
    }

    // for 推导式
    for (seq <- Seq(   // 构建一个新的 Seq 的 Seq（二维）
        nonEmptySeq, emptySeq, nonEmptyList, emptyList,
        nonEmptyVector, emptyVector, nonEmptyMap.toSeq, emptyMap.toSeq)) {
        println(seqToString(seq))  // 打印成 String
    }
}
