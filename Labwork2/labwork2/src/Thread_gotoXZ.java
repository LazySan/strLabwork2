import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Thread_gotoXZ extends Thread {

    private LinkedBlockingQueue<int[]> queuePos = null;

    private LinkedBlockingQueue<Integer> queueX = null;
    private LinkedBlockingQueue<Integer> queueZ = null;

    private Semaphore semaphoreX;
    private Semaphore semaphoreZ;

    private Semaphore movementCompletedSemaphore;

    public Thread_gotoXZ(Axis axisX, Axis axisZ, LinkedBlockingQueue<int[]> queue, Semaphore completed) {
        this.queuePos = queue;

        this.queueX = new LinkedBlockingQueue<Integer>();
        this.queueZ = new LinkedBlockingQueue<Integer>();

        semaphoreX = new Semaphore(0);
        semaphoreZ = new Semaphore(0);
        this.movementCompletedSemaphore = completed;

        Thread threadX = new Thread_gotoX(axisX, queueX, semaphoreX);
        Thread threadZ = new Thread_gotoZ(axisZ, queueZ, semaphoreZ);

        threadX.start();
        threadZ.start();
    }

    @Override
    public void run() {
        while (true) {
            int[] pos = null;
            try {
                pos = queuePos.take();

                queueX.add(pos[0]);
                queueZ.add(pos[1]);

                semaphoreX.acquire();
                semaphoreZ.acquire();
                movementCompletedSemaphore.release();
            } catch (Exception e) {
            }
        }
    }
}