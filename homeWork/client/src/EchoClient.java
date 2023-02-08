import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final String host;
    private final int port;

    private EchoClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public static EchoClient connectTo(int port){
        var localHost = "127.0.0.1";
        return new EchoClient(localHost,port);
    }


    public void run(){
        System.out.println("Print 'bye' to exit");
        try (var socket = new Socket(host,port)){
            var scanner = new Scanner(System.in);
            var output = socket.getOutputStream();
            var writer = new PrintWriter(output);
            try (scanner;writer){
                while (true){
                    String message = scanner.nextLine();
                    writer.write(message);
                    writer.write(System.lineSeparator());
                    writer.flush();
                    read();
                    if(message.toLowerCase().equals("bye")){
                        return;
                    }
                }
            }
        } catch (NoSuchElementException exception){
            System.out.println("Connection dropped!");
        } catch (IOException exception){
            var msg = "Can not connect to %s:%s! %n";
            System.out.printf(msg,host,port);
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
                return;
            }
        } catch (NoSuchElementException exception){
            System.out.println("Client dropped the connection!");
        }
    }

    private void read(){
        try (var server = new ServerSocket(8081)) {
            try (var clientSocket = server.accept()){
                handle(clientSocket);
            }
        } catch (IOException exception){
            var formatMsg = "The port %s is busy %n";
            System.out.printf(formatMsg,8081);
            exception.printStackTrace();
        }
    }
}
