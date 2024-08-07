import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ServerWorkThreadTest {

    @Test
    void run() {
    }

    @Test
    void requestNickname() {
//        try {
//            Method method = ServerWorkThread.class.getDeclaredMethod("requestNickname", null);
//            method.setAccessible(true);
//            assertEquals(method.invoke());
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    @Test
    void getSocket() {
    }

    @Test
    void getIn() {
    }

    @Test
    void getOut() {

    }

    @Test
    void getNickname() {
        String nickname = "qwerty";
        assertEquals("qwerty", nickname.toString());
    }
}