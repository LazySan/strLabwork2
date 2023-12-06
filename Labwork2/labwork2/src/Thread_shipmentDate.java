import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

public class Thread_shipmentDate extends Thread {

    Pallet[][] matrixPallets;
    Mechanism mechanism;
    Semaphore startFlashSemaphore;
    Semaphore stopFlashSemaphore;

    public Thread_shipmentDate(Pallet[][] matrixPallets, Mechanism mechanism, Semaphore startFlashSemaphore,
            Semaphore stopFlashSemaphore) {
        this.matrixPallets = matrixPallets;
        this.mechanism = mechanism;
        this.startFlashSemaphore = startFlashSemaphore;
        this.stopFlashSemaphore = stopFlashSemaphore;
    }

    @Override
    public void run() {
        LocalDateTime now;
        int i, j;
        Pallet pallet;
        boolean hasToShip;
        while (true) {
            now = LocalDateTime.now();
            hasToShip = false;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    pallet = matrixPallets[i][j];
                    if (pallet != null) {
                        if (now.isAfter(pallet.shipmentDate)) {
                            {
                                hasToShip = true;
                            }
                        }
                    }
                }
                if (hasToShip) {
                    if (startFlashSemaphore.availablePermits() <= 0)
                        startFlashSemaphore.release();
                } else {
                    if (stopFlashSemaphore.availablePermits() <= 0) {
                        stopFlashSemaphore.release();
                    }
                }
            }
        }
    }
}
