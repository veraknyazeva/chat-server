import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    public static LinkedList<ServerWorkThread> connectedSocketList = new LinkedList<>(); // список всех нитей
    public static final FileOutputStream writer;

    static {
        try {
            writer = new FileOutputStream("logs/server.log", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(ServerConfig.PORT)) {
            System.out.println("Сервер чата запущен!"); // хорошо бы серверу объявить о своем запуске

            while (true) {
                try {
                    Socket clientSocket = server.accept();// accept() будет ждать пока кто-нибудь не захочет подключиться
                    ServerWorkThread workThread = new ServerWorkThread(clientSocket);
                    connectedSocketList.add(workThread);
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}