package programming_scala_2nd_edition

object Sample_32_group {
    def group_list = {
        /** groupBy(p)方法会把一个集合根据你提供的方法切分成一个 map。Map中 p=true 对应的 value 是所有使 p 返回 true 的集
          * 合元素组成的新集合，p=flase 对应的 value 是所有使 p 返回 false 的元素组成的另一个集合。*/
        val l = List(15, 10, 5, 8, 20, 12)
        println(l.groupBy(_ > 10))  // Map(false -> List(10, 5, 8), true -> List(15, 20, 12))
    }
}
