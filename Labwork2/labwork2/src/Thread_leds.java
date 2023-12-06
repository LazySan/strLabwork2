import java.util.concurrent.Semaphore;

public class Thread_leds extends Thread {

    Mechanism mechanism;
    Semaphore startFlashSemaphore;
    Semaphore stopFlashSemaphore;

    public Thread_leds(Mechanism mechanism, Semaphore startFlashSemaphore, Semaphore stopFlashSemaphore) {
        this.mechanism = mechanism;
        this.startFlashSemaphore = startFlashSemaphore;
        this.stopFlashSemaphore = stopFlashSemaphore;
    }

    @Override
    public void run() {
        while (true) {
            SemaphoreTake(startFlashSemaphore);
            while (!stopFlashSemaphore.tryAcquire()) {
                mechanism.ledOn(1);
                Sleep(2);
                mechanism.ledsOff();
                Sleep(2);
            }
            startFlashSemaphore.drainPermits();
            stopFlashSemaphore.drainPermits();
        }
    }

    private void Sleep(int seconds) {
        try {
            sleep(seconds * 1000);
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
