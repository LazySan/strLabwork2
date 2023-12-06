import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Thread_movements extends Thread {
    private Semaphore movementCompletedSemaphore;
    private LinkedBlockingQueue<int[]> gotoXZQueue;
    private LinkedBlockingQueue<int[]> movementQueue;
    private Mechanism mechanism;
    LinkedBlockingQueue<int[]> updateMatrixQueue;
    LinkedBlockingQueue<Integer> deliverWithDelayQueue;
    private Axis axisY;

    public Thread_movements(LinkedBlockingQueue<int[]> movementQueue, LinkedBlockingQueue<int[]> gotoXZQueue,
            Mechanism mechanism, Semaphore movementCompletedQueue, Axis axisY,
            LinkedBlockingQueue<Integer> deliverWithDelayQueue,
            LinkedBlockingQueue<int[]> updateMatrixQueue) {
        this.movementCompletedSemaphore = movementCompletedQueue;
        this.movementQueue = movementQueue;
        this.gotoXZQueue = gotoXZQueue;
        this.mechanism = mechanism;
        this.axisY = axisY;
        this.deliverWithDelayQueue = deliverWithDelayQueue;
        this.updateMatrixQueue = updateMatrixQueue;
    }
    // Movement Queue tem:
    // 0 -> Insert
    // 1 -> Move
    // 2 -> Deliver

    @Override
    public void run() {
        while (true) {
            int[] pos;
            try {
                pos = movementQueue.take();
                switch (pos[0]) {
                    case 0:
                        InsertPallet(new int[] { pos[1], pos[2] });
                        break;
                    case 1:
                        MovePallet(new int[] { pos[1], pos[2] }, new int[] { pos[3], pos[4] });
                        break;
                    case 2:
                        DeliverPallet(new int[] { pos[1], pos[2] });
                        if (deliverWithDelayQueue.take() == 1) {
                            updateMatrixQueue.add(new int[] { pos[1], pos[2] });
                        }
                        break;
                }
            } catch (Exception e) {

            }
        }
    }

    private void InsertPallet(int[] pos) throws InterruptedException {
        axisY.gotoPos(2);
        gotoXZQueue.add(new int[] { 1, 1 });

        SemaphoreTake(movementCompletedSemaphore);
        while (true) {
            axisY.gotoPos(1);
            sleep(2000);
            axisY.gotoPos(2);
            if (mechanism.hasPallet())
                break;
        }

        axisY.gotoPos(2);
        gotoXZQueue.add(pos);

        SemaphoreTake(movementCompletedSemaphore);

        mechanism.putPartInCell();
    }

    private void MovePallet(int[] oldPos, int[] newPos) {
        axisY.gotoPos(2);
        gotoXZQueue.add(oldPos);

        SemaphoreTake(movementCompletedSemaphore);
        mechanism.takePartFromCell();

        axisY.gotoPos(2);
        gotoXZQueue.add(newPos);

        SemaphoreTake(movementCompletedSemaphore);
        mechanism.putPartInCell();
    }

    private void DeliverPallet(int[] pos) throws InterruptedException {
        while (true) {
            axisY.gotoPos(2);
            if (!mechanism.hasPallet())
                break;
            axisY.gotoPos(1);
            sleep(2000);
        }
        axisY.gotoPos(2);
        gotoXZQueue.add(pos);

        SemaphoreTake(movementCompletedSemaphore);
        mechanism.takePartFromCell();

        axisY.gotoPos(2);
        gotoXZQueue.add(new int[] { 1, 1 });
        SemaphoreTake(movementCompletedSemaphore);
        axisY.gotoPos(1);
    }

    private void SemaphoreTake(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
        }
    }
}