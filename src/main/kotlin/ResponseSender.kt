import java.io.*
import java.net.Socket
import java.net.URLDecoder

class ResponseSender(private val socket: Socket) : Runnable {

    private val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val outputStream: OutputStream = BufferedOutputStream(socket.getOutputStream())
    private val out: Writer = OutputStreamWriter(outputStream)
    private var parsedRequestArray: Array<String> = emptyArray()
    private var flagIndex = false

    private fun getFile(posQuestion: Int, fileName: String): File {
        val path = root + fileName
        return if (posQuestion == -1) {
            File(path)
        } else {
            File(path.substring(0, root.length + posQuestion))
        }
    }

    @Throws(IOException::class)
    private fun response() {
        val method = parsedRequestArray[0]
        if (method.toUpperCase() == "GET" || method.toUpperCase() == "HEAD") {
            var fileName = URLDecoder.decode(parsedRequestArray[1], "UTF-8")
            val posLastDot = fileName.lastIndexOf('.')

            val contentType: String
            if (posLastDot != -1) {
                try {
                    contentType = fileTypes[fileName.substring(posLastDot + 1)] ?: ""
                } catch (e: Exception) {
                    println("Unnsuported type=" + fileName.substring(posLastDot + 1))
                    sendHeader(HttpHeader.notAllowed())
                    return
                }

            } else {
                if (fileName.endsWith("/")) {
                    flagIndex = true
                    fileName += indexFileName
                    contentType = "html"
                } else {
                    sendHeader(HttpHeader.forbidden())
                    return
                }
            }

            val posQuestion = fileName.indexOf('?')
            val theFile = getFile(posQuestion, fileName)
            sendResponse(theFile, method, contentType)

        } else {
            sendHeader(HttpHeader.notAllowed())
        }
    }

    private fun sendFile(theFile: File) {
        try {
            FileInputStream(theFile).use { ios ->
                val buffer = ByteArray(1024)
                var read = ios.read(buffer)
                while (read != -1) {
                    outputStream.write(buffer, 0, read)
                    outputStream.flush()
                    read = ios.read(buffer)
                }
            }
        } catch (e: IOException) {
            sendHeader(HttpHeader.notFound())
        }

    }

    @Throws(IOException::class)
    private fun sendResponse(theFile: File, method: String, contentType: String) {
        if (theFile.canRead() && theFile.canonicalPath.startsWith(root)) {
            sendHeader(HttpHeader.ok(theFile.length().toInt(), contentType))
            if (method.toUpperCase() == "GET") {
                sendFile(theFile)
            }

        } else {
            if (parsedRequestArray[1] == "/httptest/") {
                sendHeader(HttpHeader.serverOK())
            } else {
                if (flagIndex) {
                    sendHeader(HttpHeader.forbidden())
                } else {
                    sendHeader(HttpHeader.notFound())
                }
            }

        }
    }

    private fun parseRequest() {
        parsedRequestArray = readRequest().split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    private fun readRequest(): String {
        val requestLine = StringBuilder()

        while (true) {
            val line = bufferedReader.readLine()
            if (line == null || line.isEmpty()) {
                break
            }
            requestLine.append(line)
        }
        return requestLine.toString()
    }

    override fun run() {
        try {
            parseRequest()
            if (parsedRequestArray.size >= 3) {
                response()
            }
        } catch (e: Exception) {
            println("bufferedReader run")
            e.printStackTrace()
        } finally {
            finish()
        }
    }

    private fun finish() {
        try {
            bufferedReader.close()
            socket.close()
        } catch (e: IOException) {
            println("bufferedReader finish")
            e.printStackTrace()
        }

    }


    private fun sendHeader(responseHeader: String) {
        try {
            out.write(responseHeader)
            out.flush()
        } catch (e: Exception) {
//            println("bufferedReader sendHeader" + e.message)
        }

    }

    companion object {
        val fileTypes = mapOf(
                "html" to "text/html",
                "css" to "text/css",
                "js" to "text/javascript",
                "jpg" to "image/jpeg",
                "jpeg" to "image/jpeg",
                "png" to "image/png",
                "gif" to "image/gif",
                "swf" to "application/x-shockwave-flash")
        var root = "/home/nick/Desktop/HighLoad/" //httptest";

        private const val indexFileName = "index.html"
    }
}
