import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {
    private final int port;
    private EchoServer(int port){
        this.port = port;
    }

    static EchoServer bindToPort(int port){
        return new EchoServer(port);
    }

    public void run(){
        try (var server = new ServerSocket(port)) {
            try (var clientSocket = server.accept()){
                handle(clientSocket);
            }
        } catch (IOException exception){
            var formatMsg = "The port %s is busy %n";
            System.out.printf(formatMsg,port);
            exception.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException{
        var input = socket.getInputStream();
        var inputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
        var scanner = new Scanner(inputStreamReader);
        try (scanner){
            while (true){
                var message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n",message);
                printBack(message);
                if(message.toLowerCase().equals("bye")){
                    return;
                }
            }
        } catch (NoSuchElementException exception){
            System.out.println("Client dropped the connection!");
        }
    }

    private String reverse (String str){
        char[] arr = str.toCharArray();
        StringBuilder reversedMessage = new StringBuilder();
        for (int i = 1; i < arr.length + 1; i++){
            reversedMessage.append(arr[arr.length - i]);
        }
        return reversedMessage.toString();
    }

    private void printBack(String message){
        var localHost = "127.0.0.1";
        try (var socket = new Socket(localHost,8081)){
            var output = socket.getOutputStream();
            var writer = new PrintWriter(output);
            while (true){
                try (writer){
                    writer.write(reverse(message));
                    writer.flush();
                    return;
                }
            }
        } catch (NoSuchElementException exception){
            System.out.println("Connection dropped!");
        } catch (IOException exception){
            var msg = "Can not connect to %s:%s! %n";
            System.out.printf(msg,localHost,port);
            exception.printStackTrace();
        }
    }
}
