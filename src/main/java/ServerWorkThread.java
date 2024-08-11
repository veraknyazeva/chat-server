import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class ServerWorkThread extends Thread {
    private static final String WELCOME_FORMAT = "Добро пожаловать в чат, %s! (Чтобы покинуть чат напишите '/exit')\n";
    private static final String NICKNAME_REQUEST = "Укажите Ваш никнейм:\n";
    private static final String MESSAGE_FORMAT = "[%s] %s: %s";
    private String nickname;
    private final Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private final BufferedReader in; // поток чтения из сокета
    private final BufferedWriter out; // поток записи в сокет

    public ServerWorkThread(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        // если потоку ввода/вывода приведут к генерированию исключения, оно пробросится дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
    }

    private void sendWelcomeMessage() {
        try {
            out.write(String.format(WELCOME_FORMAT, nickname));
            out.flush();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        nickname = requestNickname();
        sendWelcomeMessage();

        String messege;
        try {
            while (true) {
                messege = in.readLine();
                if (messege != null) {
                    if (messege.equals("/exit")) {
                        break;
                    }
                    String serverMessageForHistory = String.format(MESSAGE_FORMAT, LocalDateTime.now(), nickname, messege);
                    saveMessageForHistory(serverMessageForHistory);
                    for (ServerWorkThread otherConnection : Server.connectedSocketList) {
                        if (!otherConnection.equals(this)) {
                            send(serverMessageForHistory, otherConnection); // отослать принятое сообщение с привязанного клиента всем остальным
                        }
                    }
                }
            }
            in.close();
            out.close();
            Server.connectedSocketList.remove(this);
            socket.close();
        } catch (IOException e) {
            String errorMessage = String.format(MESSAGE_FORMAT, LocalDateTime.now(), "server", e.getMessage());
            try {
                out.write(errorMessage);
                out.flush();
                out.close();
                in.close();
                Server.connectedSocketList.remove(this);
                socket.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private void saveMessageForHistory(String serverMessageForHistory) {
        synchronized (Server.writer) {
            String messageForLog = serverMessageForHistory + "\n";
            try {
                Server.writer.write(messageForLog.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                System.out.println("Данные не сохранились");
            }
        }
    }

    private String requestNickname() {
        try {
            out.write(NICKNAME_REQUEST);
            out.flush();
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(String msg, ServerWorkThread otherConnection) {
        try {
            otherConnection.getOut().write(msg + "\n");
            otherConnection.getOut().write(" \n");
            otherConnection.getOut().flush();
        } catch (IOException ignored) {
        }
    }

    public BufferedWriter getOut() {
        return out;
    }
}
