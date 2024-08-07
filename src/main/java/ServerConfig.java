import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerConfig {
    public static final int PORT = initialize();
    public static final int DEFAULT_PORT = 4004;

    private static int initialize() {
        AtomicInteger serverPort = new AtomicInteger(DEFAULT_PORT);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("settings.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));) {
            reader.lines().forEach(configString -> {
                String[] splited = configString.split("=");
                if (splited.length == 2) {
                    String key = splited[0];
                    if (key.equals("server.port")) {
                        serverPort.set(Integer.parseInt(splited[1]));
                    }
                }
            });
            return serverPort.get();
        } catch (Exception ex) {
            ex.printStackTrace();
            return DEFAULT_PORT;
        }
    }
}
