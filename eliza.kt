/* */

import java.io.File
import java.util.Random
import java.io.InputStream

fun readReplies(): Pair<List<String> ,Map<String, Set<String>>>{
   val lines = File("replies.txt").readLines()
   val topicLines = lines.mapIndexedNotNull { i, s -> i.takeIf { s.matches(Regex("""\w+(\|\w+)*:""")) } }
    val ranges = topicLines.zipWithNext { a, b -> a to b } + (topicLines.last() to lines.size)

    val topics = lines.filterIndexed { i, _ -> i in topicLines }.map { it.removeSuffix(":") }.flatMap { it.split("|") }

    val questions = ranges.map { (first, last) ->
                                val subTopics = lines[first].removeSuffix(":").split("|")
                                val subQuestions = lines.subList(first + 1, last).toSet()
                                subTopics.map { it to subQuestions }.toMap()
                               }.reduce { acc, map -> acc + map }

    return topics to questions
}

fun sanitize(s: String): String {
    var str: String = s.toLowerCase()  
    val re = Regex("[^a-z\\s]")
    str = re.replace(str, "")
    str = str.replace("\\s+".toRegex(), " ")
    str = str + " "
    var space = " "
    space = space + str
    return space
}
fun Random.nextInt(range: IntRange): Int {
    return range.start + nextInt(range.last - range.start)
}


 fun findKeyword(s: String, kwds: List<String>): String {
  
    var mutable = kwds.toMutableList()
    val conjugations = mapOf("are" to "am",
		       	 "were" to "was",
		       	 "i" to "you",
		       	 "im" to "you are",
		       	 "my" to "your",
		       	 "me" to "you",
		       	 "ive" to "you've",
		       	 "you" to "I",
		       	 "your" to "my",
		       	 "myself" to "yourself",
		       	 "yourself" to "myself")
                
    for (i in mutable) {
        for ((k, v) in conjugations) {
            if (i == k) {
              mutable[mutable.indexOf(i)] = v
            }
        }
    }
    
    
    val keys = readReplies()
    for (i in mutable) {
        for (j in keys.first) {
            if (i == j) {
                val random = Random()
                
                 var aki = random.nextInt(0..keys.second[j]?.size!!)
                return keys.second[j]?.elementAt(aki)!!
            }
        }
    }
   
    return "I do not know"
 }

 
fun main(args: Array<String>) {
    var prevMess: String = ""
    var curMess: String = ""
    println("Hi! I'm Eliza, what is your problem?")
    while(true) {
        prevMess = curMess
        val msg = readLine()
        curMess = sanitize(msg.toString())
        if(curMess == prevMess) {
            println("Please don't repeat yourself!")
            continue
        }
        if (curMess == " shut up ") {
            break
        }
        var result: List<String> = curMess.split(" ").map { it.trim() }
        println(findKeyword(curMess, result))
    }    
}

