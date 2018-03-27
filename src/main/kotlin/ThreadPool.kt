package main.kotlin

import java.util.*

class ThreadPool(countThread: Int) {

    @Volatile
    private var isRunning = true
    private val queueTask: Queue<Runnable> = LinkedList()

    init {
        for (i in 1..countThread) {
            Thread(TaskWorker()).start()
        }
    }

    fun execute(command: Runnable) {
        synchronized(queueTask) {
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
                synchronized(queueTask) { queueTask.poll() }?.run()
            }
        }
    }
}
