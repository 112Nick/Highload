import java.util.*

object HttpHeader {

    fun ok(length: Int, contentType: String): String {
        return "HTTP/1.1 200 OK" + "\r\n" +
                "Date: " + Date().toString() + "\r\n" +
                "Server: Kurkin \r\n" +
                "Content-Length: " + length + "\r\n" +
                "Content-type: " + contentType + "\r\n" +
                "Connection: " + "Closed" + "\r\n\r\n"
    }

    fun serverOK(): String {
        return "HTTP/1.1 200 OK " + "\r\n" +
                "Date: " + Date().toString() + "\r\n" +
                "Server: Kurkin \r\n" +
                "Connection: " + "Closed" + "\r\n\r\n"
    }

    fun forbidden(): String {

        return "HTTP/1.1 403 Forbidden" + "\r\n" +
                "Date: " + Date().toString() + "\r\n" +
                "Server: Kurkin \r\n" +
                "Connection: " + "Closed" + "\r\n\r\n"
    }

    fun notFound(): String {
        return "HTTP/1.1 404 Not Found" + "\r\n" +
                "Date: " + Date().toString() + "\r\n" +
                "Server: Kurkin \r\n" +
                "Connection: " + "Closed" + "\r\n\r\n"
    }

    fun notAllowed(): String {
        return "HTTP/1.1 405 Method Not Allowed" + "\r\n" +
                "Date: " + Date().toString() + "\r\n" +
                "Server: Kurkin \r\n" +
                "Connection: " + "Closed" + "\r\n\r\n"
    }

}
