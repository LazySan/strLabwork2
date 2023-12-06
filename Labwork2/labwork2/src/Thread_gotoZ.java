import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Thread_gotoZ extends Thread {

    private Semaphore sync;
    private LinkedBlockingQueue<Integer> queue = null;
    private Axis axis = null;

    public Thread_gotoZ(Axis axis, LinkedBlockingQueue<Integer> queue,Semaphore sync) {
        this.axis = axis;
        this.queue = queue;
        this.sync = sync;
    }

    @Override
    public void run() {
        while (true) {

            int pos;
            try {
                pos = queue.take();
                axis.gotoPos(pos);
                sync.release();
            } catch (Exception e) {
            }
        }
    }
}