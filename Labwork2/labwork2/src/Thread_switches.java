import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Thread_switches extends Thread {

    Mechanism mechanism;
    LinkedBlockingQueue<int[]> movementQueue;
    Pallet[][] matrixPallets;
    LinkedBlockingQueue<Integer> deliverWithDelayQueue;
    Thread_menu menu;
    Semaphore enterEmergencySemaphore;
    Semaphore exitEmergencySemaphore;

    public Thread_switches(Thread_menu menu, Pallet[][] matrixPallets, Mechanism mechanism,
            LinkedBlockingQueue<int[]> movementQueue, LinkedBlockingQueue<Integer> deliverWithDelayQueue,
            Semaphore enterEmergencySemaphore, Semaphore exitEmergencySemaphore) {
        this.mechanism = mechanism;
        this.movementQueue = movementQueue;
        this.matrixPallets = matrixPallets;
        this.deliverWithDelayQueue = deliverWithDelayQueue;
        this.menu = menu;
        this.enterEmergencySemaphore = enterEmergencySemaphore;
        this.exitEmergencySemaphore = exitEmergencySemaphore;
    }

    @Override
    public void run() {
        int i, j;
        Pallet pallet;
        LocalDateTime now;
        int p2 = 0;
        while (true) {
            if (mechanism.bothSwitchesPressed()) {

                this.interrupt();
                menu.interrupt();

                p2 = mechanism.GetP2();
                mechanism.ModifyP2(0);

                enterEmergencySemaphore.release();

                System.out.println("\n==[MODO EMERGENCIA]==");
                while (mechanism.bothSwitchesPressed()) {

                }

            } else if (mechanism.switch1Pressed()) {
                if (!this.isInterrupted()) {
                    now = LocalDateTime.now();
                    for (i = 0; i < 3; i++) {
                        for (j = 0; j < 3; j++) {
                            pallet = matrixPallets[i][j];
                            if (pallet != null) {
                                if (now.isAfter(pallet.shipmentDate)) {
                                    movementQueue.add(new int[] { 2, i + 1, j + 1 });
                                    deliverWithDelayQueue.add(1);
                                }
                            }
                        }
                    }
                } else {

                    Thread_switches.interrupted();
                    Thread_menu.interrupted();
                    mechanism.ModifyP2(p2);
                    exitEmergencySemaphore.release();

                    System.out.println("==[PROGRAMA RETOMADO]==\n");
                }
                while (mechanism.switch1Pressed()) {

                }

            } else if (mechanism.switch2Pressed()) {
            }
        }
    }
}
