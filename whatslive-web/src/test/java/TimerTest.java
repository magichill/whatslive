import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangjian7 on 2015/8/26.
 */
public class TimerTest {
    public static void main(String[] args) throws InterruptedException {
        for(int i =0; i<1000; i++){
            TimerSingleObject timerSingleObject = TimerSingleObject.getInstance();
            timerSingleObject.schedule();
            Thread.sleep(500L);
        }
    }
}
