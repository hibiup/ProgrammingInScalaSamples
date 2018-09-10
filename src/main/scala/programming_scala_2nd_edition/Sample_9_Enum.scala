package programming_scala_2nd_edition.sample_9

object Breed extends Enumeration {
    type Breed = Value
    val doberman = Value("Doberman Pinscher")
    val yorkie = Value("Yorkshire Terrier")
    val scottie = Value("Scottish Terrier")
    val dane = Value("Great Dane")
    val portie = Value("Portuguese Water Dog")
}

object WeekDay extends Enumeration {
    type WeekDay = Value
    val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}

object Sample_9_Enum extends App{
    print_dogs()
    print_weekday()

    def print_dogs() {
        import Breed._
        // 打印所有犬种及其ID列表
        println("ID\tBreed")
        for (breed <- Breed.values) println(s"${breed.id}\t$breed")

        // 打印犬列表
        println("\nJust Terriers:")
        Breed.values filter (_.toString.endsWith("Terrier")) foreach println

        def isTerrier(b: Breed) = b.toString.endsWith("Terrier")

        println("\nTerriers Again??")
        Breed.values filter isTerrier foreach println
    }

    def print_weekday(): Unit = {
        import WeekDay._
        def isWorkingDay(d: WeekDay) = ! (d == Sat || d == Sun)
        WeekDay.values filter isWorkingDay foreach println
    }
}
