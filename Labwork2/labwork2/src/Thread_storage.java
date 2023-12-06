import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Thread_storage extends Thread {

    Pallet[][] matrixPallets;
    LinkedBlockingQueue<int[]> updateMatrixQueue;
    Semaphore stopFlashSemaphore;

    public Thread_storage(Pallet[][] matrixPallets, LinkedBlockingQueue<int[]> updateMatrixSemaphore,
            Semaphore stopFlashSemaphore) {
        this.matrixPallets = matrixPallets;
        this.updateMatrixQueue = updateMatrixSemaphore;
        this.stopFlashSemaphore = stopFlashSemaphore;
    }

    public Pallet GetPallet(int x, int z) {
        return matrixPallets[x][z];
    }

    public void InsertPalletMatrix(Pallet pallet, int x, int z) {
        matrixPallets[x][z] = pallet;
    }

    public void MovePalletMatrix(int oldx, int oldz, int x, int z) {
        matrixPallets[x][z] = matrixPallets[oldx][oldz];
        matrixPallets[oldx][oldz] = null;
    }

    public void DeliverPalletMatrix(int x, int z) {
        matrixPallets[x][z] = null;
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
                pos = updateMatrixQueue.take();
                matrixPallets[pos[0] - 1][pos[1] - 1] = null;
                stopFlashSemaphore.release();
            } catch (InterruptedException e) {
            }
        }
    }

}