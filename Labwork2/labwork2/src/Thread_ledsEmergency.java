import java.util.concurrent.Semaphore;

public class Thread_ledsEmergency extends Thread {

    Mechanism mechanism;
    Semaphore startFlashSemaphore;
    Semaphore stopFlashSemaphore;

    public Thread_ledsEmergency(Mechanism mechanism, Semaphore startFlashSemaphore, Semaphore stopFlashSemaphore) {
        this.mechanism = mechanism;
        this.startFlashSemaphore = startFlashSemaphore;
        this.stopFlashSemaphore = stopFlashSemaphore;
    }

    @Override
    public void run() {
        while (true) {
            SemaphoreTake(startFlashSemaphore);
            while (!stopFlashSemaphore.tryAcquire()) {
                mechanism.ledOn(2);
                Sleep(250);
                mechanism.ledsOff();
                Sleep(250);

            }
            startFlashSemaphore.drainPermits();
            stopFlashSemaphore.drainPermits();
        }
    }

    private void Sleep(int miliSeconds) {
        try {
            sleep(miliSeconds);
        } catch (InterruptedException e) {
        }
    }

    private void SemaphoreTake(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
        }
    }
}
