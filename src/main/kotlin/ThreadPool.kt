////package main.kotlin
////
////import java.util.*
////
////class ThreadPool(countThread: Int) {
////
////    @Volatile
////    private var isRunning = true
////    private val queueTask: Queue<Runnable> = LinkedList()
////
////    init {
////        for (i in 1..countThread) {
////            Thread(TaskWorker()).start()
////        }
////    }
////
////    fun execute(command: Runnable) {
////        synchronized(queueTask) {
////            if (!queueTask.add(command)) {
////                println("Task can not be added in the queue")
////            }
////        }
////    }
////
////    fun shutDown() {
////        isRunning = false
////    }
////
////
////    private inner class TaskWorker : Runnable {
////
////        override fun run() {
////
////            while (isRunning) {
////                try {
////                    synchronized(queueTask) { queueTask.poll() }!!.run() // synchronized(queueTask) { queueTask.poll() }?.run()
////
////                } catch (e: RuntimeException) {
////
////                }
////            }
////        }
////    }
////}
////
//
//package main.kotlin
//
//import java.util.*
//
//class ThreadPool(private var countThread: Int) {
//
//    @Volatile
//    private var isRunning = true
//    @Volatile
//    private var currentCount = 0
//    private val queueTask: Queue<Runnable> = LinkedList()
//
//    private fun createThread() {
//        Thread(TaskWorker()).start()
//    }
//
//
//    fun execute(command: Runnable) {
//        synchronized(this) {
//            if (currentCount < countThread) {
//                createThread()
//                currentCount++
//                //println(currentCount)
//            }
//            if (!queueTask.offer(command)) {
//                println("Task can not be added in the queue")
//            }
//        }
//    }
//
//
//    fun shutDown() {
//        isRunning = false
//    }
//
//
//    private inner class TaskWorker : Runnable {
//
//        //        override fun run() {
////            while (isRunning) {
////                var task: Runnable? = synchronized(queueTask){queueTask.poll()}
////                if (task != null) {
////                    task.run()
////                } else {
////                    synchronized(currentCount){
////                        currentCount--
////                    }
////                    return
////                }
////            }
////        }
//        override fun run() {
//            while (isRunning) {
//                if (queueTask.isNotEmpty()) {
//                    var nextTask: Runnable? = null
//                    synchronized(queueTask) {
//                        nextTask = queueTask.poll()
//                    }
//                    try {
//                        nextTask!!.run()
//                    } catch (e: RuntimeException) {
//                        synchronized(currentCount) {
//                            currentCount--
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}

package main.kotlin

import java.lang.Thread.currentThread
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ThreadPool(private var countThread: Int) {

    @Volatile
    private var isRunning = true
    private val queueTask: Queue<Runnable> = LinkedList()
    private val currentCount = AtomicInteger(0)

//    init {
//        for (i in 1..countThread) {
//            Thread(TaskWorker()).start()
//        }
//    }

    private fun createThread() {
        Thread(TaskWorker()).start()
    }

    fun execute(command: Runnable) {
        synchronized(queueTask) {
            if (currentCount.get() < countThread) {
                createThread()
                currentCount.incrementAndGet()
                //println(currentCount)
            }
            if (!queueTask.add(command)) {
                println("Task can not be added in the queue")
            }
        }
    }

    fun shutDown() {
        isRunning = false
    }


    private inner class TaskWorker : Runnable {

        override fun run() {
            while (isRunning) {
                var nextTask: Runnable? = null
                        synchronized(queueTask) {
                            nextTask = queueTask.poll()
                    }



                    try {
                        nextTask!!.run()
                    } catch (e: NullPointerException) {
                        currentCount.getAndDecrement()
//                    currentThread().interrupt()
//                        println(currentCount)
                        return
                    }
            }
        }
    }
}
