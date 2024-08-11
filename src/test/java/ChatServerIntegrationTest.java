import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatServerIntegrationTest {

    public static final Path PATH = Paths.get("logs/server.log");
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static final String TEST_NICKNAME = "klava";

    @BeforeAll
    public static void before_all() throws Exception {
        Thread serverWorkFlowThread = new Thread(() -> {
            ServerConfig serverConfig = new ServerConfig();
            Server server = new Server();
            Server.main(null);
        });
        serverWorkFlowThread.start();
        Thread.sleep(3000);
        clientSocket = new Socket("localhost", ServerConfig.PORT);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    @AfterAll
    public static void after_all() throws InterruptedException {
        Thread.sleep(3000);
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }

    @Test
    @Order(1)
    public void test_connection() {
        assertEquals(1, Server.connectedSocketList.size());
    }

    @Test
    @Order(2)
    void server_send_nickname_message() throws IOException {
        String message = in.readLine();
        assertEquals("Укажите Ваш никнейм:", message);
    }

    @Test
    @Order(3)
    void send_nickname_and_receive_welcome_message() throws IOException {
        out.write(TEST_NICKNAME + "\n");
        out.flush();
        String welcomeMessage = in.readLine();
        assertTrue(welcomeMessage.contains(TEST_NICKNAME));
    }

    @Test
    @Order(4)
    void read_file_log() throws IOException, InterruptedException {
        List<String> allLinesBefore = Files.readAllLines(PATH);
        int sizeLogBefore = allLinesBefore.size();
        out.write("hello" + "\n");
        out.flush();
        Thread.sleep(2000);
        List<String> allLinesAfter = Files.readAllLines(PATH);
        int sizeLogAfter = allLinesAfter.size();
        assertTrue(sizeLogAfter > sizeLogBefore);
    }

    @Test
    @Order(5)
    void exit() throws IOException, InterruptedException {
        String exit = "/exit";
        out.write(exit + "\n");
        out.flush();
        Thread.sleep(2000);
        assertEquals(0, Server.connectedSocketList.size());
    }
}