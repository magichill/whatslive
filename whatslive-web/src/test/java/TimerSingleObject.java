import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangjian7 on 2015/8/26.
 */
public class TimerSingleObject {
    private static TimerSingleObject timerSingleObject = new TimerSingleObject();
    public static TimerSingleObject getInstance(){
        return timerSingleObject;
    }
    public void schedule(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("run Timer count :"+System.currentTimeMillis());
            }
        }, 100);
    }

}
