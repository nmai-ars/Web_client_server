package WebServer;


import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class WebServer {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Web Server dang chay!");
            System.out.println("Truy cap: http://localhost:" + port);

            while (true) {
                Socket client = serverSocket.accept();

                new Thread(() -> {
                    try (
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            client.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );
                            OutputStream out = client.getOutputStream()
                    ) {
                        String request = in.readLine();
                        System.out.println("Request: " + request);

                        String line;
                        while ((line = in.readLine()) != null
                                && !line.isEmpty()) {
                        }

                        String html =
                                "<!DOCTYPE html>" +
                                        "<html lang='vi'><head>" +
                                        "<meta charset='UTF-8'>" +
                                        "<title>Java Web Server</title>" +
                                        "<style>body{font-family:Arial;" +
                                        "text-align:center;background:#eef4ff;" +
                                        "padding:50px}h1{color:#1769aa}" +
                                        "</style></head><body>" +
                                        "<h1>JAVA WEB SERVER</h1>" +
                                        "<h2>Chao mung ban!</h2>" +
                                        "<p>Web Server Java dang hoat dong.</p>" +
                                        "<p>Ket noi thanh cong qua mang LAN.</p>" +
                                        "</body></html>";

                        byte[] body = html.getBytes(
                                StandardCharsets.UTF_8
                        );

                        String header =
                                "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: text/html; charset=UTF-8\r\n" +
                                        "Content-Length: " + body.length + "\r\n" +
                                        "Connection: close\r\n\r\n";

                        out.write(header.getBytes(
                                StandardCharsets.UTF_8
                        ));
                        out.write(body);
                        out.flush();

                    } catch (IOException e) {
                        System.out.println("Loi: " + e.getMessage());
                    } finally {
                        try {
                            client.close();
                        } catch (IOException e) {
                            // Dong ket noi
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Khong the khoi dong server: "
                    + e.getMessage());
        }
    }
}

