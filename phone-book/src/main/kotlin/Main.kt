package org.example

import java.io.File
import java.io.IOException
import kotlin.math.*

fun linearSearch(array: List<String>, value: String): Boolean {
    for (line in array) {
        val fullName = line.substringAfter(' ')
        if (fullName == value) return true
    }
    return false
}

fun jumpSearch(array: List<String>, value: String): Boolean {
    if (array.isEmpty()) return false

    var curr = 0
    var prev = 0
    val last = array.lastIndex
    val step = sqrt(array.size.toDouble()).toInt()

    while (array[curr].substringAfter(' ') < value) {
        if (curr == last) return false
        prev = curr
        curr = min(curr + step, last)
    }

    while (array[curr].substringAfter(' ') > value) {
        curr--
        if (curr <= prev) return false
    }

    return array[curr].substringAfter(' ') == value
}

fun bubbleSort(array: List<String>): List<String> {
    val list = array.toMutableList()
    var swapped = true

    while (swapped) {
        swapped = false
        for (i in 1..list.lastIndex) {
            if (list[i - 1].substringAfter(' ') > list[i].substringAfter(' ')) {
                list.swap(i, i - 1)
                swapped = true
            }
        }
    }
    return list
}

fun partition(array: MutableList<String>, left: Int, right: Int): Int {
    var l = left
    var r = right
    val pivot = array[(l + r) / 2].substringAfter(' ')
    while (l <= r) {
        while (array[l].substringAfter(' ') < pivot) l++
        while (array[r].substringAfter(' ') > pivot) r--
        if (l <= r) {
            array.swap(l, r)
            l++
            r--
        }
    }
    return l
}

fun quickSort(array: MutableList<String>, start: Int, end: Int) {
    if (start >= end) return
    val rightStart = partition(array, start, end)
    quickSort(array, start, rightStart - 1)
    quickSort(array, rightStart, end)
}

fun quickSort(array: List<String>): MutableList<String> {
    val list = array.toMutableList()
    quickSort(list, 0, array.size - 1)
    return list
}

fun binarySearch(array: MutableList<String>, value: String): Boolean {
    var left = 0
    var right = array.size - 1
    while (left <= right) {
        val mid = (left + right) / 2

        if (array[mid].substringAfter(' ') == value) return true
        else if (array[mid].substringAfter(' ') > value) right = mid - 1
        else left = mid + 1
    }
    return false
}

fun createHashTable(array: List<String>): HashMap<String, String> {
    val phoneBookMap = HashMap<String, String>()
    for (entry in array) {
        val parts = entry.split(" ", limit = 2)
        val number = parts[0]
        val name = parts[1]
        phoneBookMap[name] = number
    }
    return phoneBookMap
}

fun <T> MutableList<T>.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}

fun formatTime(ms: Long): String {
    val minutes = ms / 60_000
    val seconds = (ms % 60_000) / 1000
    val millis = ms % 1000
    return "$minutes min. $seconds sec. $millis ms."
}

inline fun measure(block: () -> Unit): String {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    return formatTime(end - start)
}

inline fun <T> measureResult(block: () -> T): Pair<T, String> {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    return result to formatTime(end - start)
}

fun readLinesOrNull(path: String): List<String>? = try {
    File(path).readLines()
} catch (e: IOException) {
    println("Failed to read $path: ${e.message}")
    null
}

fun runLinearSearch(directory: List<String>, find: List<String>) {
    var found = 0

    println("Start searching (linear search)...")
    val timeLinearSearch = measure {
        for (el in find) {
            if (linearSearch(directory, el)) found++
        }
    }

    println("Found $found / ${find.size} entries. Time taken: $timeLinearSearch\n")
}

fun runJumpSearch(directory: List<String>, find: List<String>) {
    var  found = 0
    println("Start searching (bubble sort + jump search)...")

    val begin = System.currentTimeMillis()
    val (resultBubbleSort, timeBubbleSort) = measureResult {
        bubbleSort(directory)
    }

    val timeJumpSearch = measure {
        for (el in find) {
            if (jumpSearch(resultBubbleSort, el)) found++
        }
    }
    val end = System.currentTimeMillis()

    println("Found $found / ${find.size} entries. Time taken: ${formatTime(end - begin)}")

    println("Sorting time: $timeBubbleSort")
    println("Searching time: $timeJumpSearch\n")
}

fun runBinarySearch(directory: List<String>, find: List<String>) {
    var found = 0
    println("Start searching (quick sort + binary search)...")

    val begin = System.currentTimeMillis()
    val (resultQuickSort, timeQuickSort) = measureResult {
        quickSort(directory)
    }

    val timeBinarySearch = measure {
        for (el in find) {
            if (binarySearch(resultQuickSort, el)) found++
        }
    }
    val end = System.currentTimeMillis()

    println("Found $found / ${find.size} entries. Time taken: ${formatTime(end - begin)}")

    println("Sorting time: $timeQuickSort")
    println("Searching time: $timeBinarySearch\n")
}

fun runHashTable(directory: List<String>, find: List<String>) {
    var found = 0
    println("Start searching (hash table)...")

    val begin = System.currentTimeMillis()

    val (phoneBookMap, creationTime) = measureResult {
        createHashTable(directory)
    }

    val timeHashTableSearch = measure {
        for (name in find) {
            if (phoneBookMap.containsKey(name)) {
                found++
            }
        }
    }

    val end = System.currentTimeMillis()

    println("Found $found / ${find.size} entries. Time taken: ${formatTime(end - begin)}")

    println("Creating time: $creationTime")
    println("Searching time: $timeHashTableSearch")

}

fun main() {
    val directory = readLinesOrNull("src/directory.txt") ?: return
    val find = readLinesOrNull("src/find.txt") ?: return

    runLinearSearch(directory, find)
    runJumpSearch(directory, find)
    runBinarySearch(directory, find)
    runHashTable(directory, find)
}