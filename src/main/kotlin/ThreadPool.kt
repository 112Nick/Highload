import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class ThreadPool(private var countThread: Int) : Executor {

    @Volatile
    private var isRunning = true
//    private var currentCount = 0
//    private val queueTask = LinkedBlockingQueue<Runnable>()
    private val currentCount = AtomicInteger(0)
    private val queueTask = ConcurrentLinkedQueue<Runnable>()

    private fun createThread() {
        Thread(TaskWorker()).start()
    }


    override fun execute(command: Runnable) {
        synchronized(this) {
            if (currentCount.get() < countThread) {
                createThread()
                currentCount.incrementAndGet()
                //println(currentCount)
            }
            if (!queueTask.offer(command)) {
                println("Task can not be added in the queue")
            }
        }
    }

//    override fun execute(command: Runnable) {
//            if (currentCount.toInt() < countThread) {
//                createThread()
//                currentCount.getAndIncrement()
////                println(currentCount)
//            }
//            if (!queueTask.offer(command)) {
//                println("Task can not be added in the queue")
//            }
//    }

    fun shutDown() {
        isRunning = false
    }


//    private inner class TaskWorker : Runnable {
//
//        override fun run() {
//            while (isRunning) {
//
//                val task: Runnable? = synchronized(this) {
//                    queueTask.poll()
//                }
//                if (task != null) {
//                    task.run()
//                } else {
//                    synchronized(this) {
//                        currentCount--
//                        return
//                    }
//                }
//            }
//        }
//    }

    private inner class TaskWorker : Runnable {

        override fun run() {
            while (isRunning) {
                val task: Runnable? = queueTask.poll()
                if (task != null) {
                    task.run()
                } else {
                    currentCount.getAndDecrement()
                    return
                }
            }
        }
    }

}
