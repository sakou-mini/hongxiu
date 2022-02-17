package produce;

import com.donglai.gate.app.GateApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GateApp.class)
public class ProducerTest {


    @Test
    public void test11() {
        System.out.println("21312");
    }
}
