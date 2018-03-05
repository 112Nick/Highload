
class ThreadPool(private var countThread: Int)  {

    @Volatile
    private var isRunning = true
    private var currentCount = 0
    private val queueTask = ArrayList<Runnable>()

    private fun createThread() {
        Thread(TaskWorker()).start()
    }

     fun execute(command: Runnable) {
         synchronized(queueTask) {
             if (currentCount < countThread) {
                 createThread()
                 currentCount++
//                println(currentCount)
             }
             if (!queueTask!!.add(command)) {
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
                if (queueTask.isNotEmpty()) {
                    var task: Runnable? = null
                    synchronized(queueTask) {
                        if (queueTask.isNotEmpty()) {
                            task = queueTask.get(0)
                            queueTask.removeAt(0)
                        }
                    }
                    if (task != null) {
                        task!!.run()
                    } else {
                        synchronized(this) {
                            currentCount--
                            return
                        }
                    }
                }
            }
        }
    }



}