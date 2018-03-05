import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket


object Server {

    private const val PATH_TO_CONFIG = "./httpd.config"

    private var PORT = 8080
    private var MAX_THREADS = 2
    
    @Throws(IOException::class)
    private fun readConfig() {
        val fstream = FileInputStream(PATH_TO_CONFIG)
        val br = BufferedReader(InputStreamReader(fstream))

        var strLine: String

        //Reading config file by lines
        while (true) {
            try {
                strLine = br.readLine()
            } catch (e: Exception) {
                break
            }
            val maps = strLine.split(":")
            if (maps[0] == "Listen") {
                PORT = Integer.parseInt(maps[1])
                println(PORT)
            }
            if (maps[0] == "threads_max") {
                println(Integer.parseInt(maps[1]))
                MAX_THREADS = Integer.parseInt(maps[1])
            }
            if (maps[0] == "document_root") {
                ResponseSender.root = maps[1]
            }
            println(strLine)

        }
        fstream.close()
        br.close()
    }


    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        readConfig()
        val s = ServerSocket(PORT)
        val threadPool = ThreadPool(MAX_THREADS)
        while (true) {
            val socket = s.accept()
            val rs = ResponseSender(socket)
            threadPool.execute(rs)
        }
    }
}
