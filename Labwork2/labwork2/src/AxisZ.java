public class AxisZ implements Axis {
    public void moveForward() {
        Storage.moveZUp();
    }

    public void moveBackward() {
        Storage.moveZDown();
    }

    public void stop() {
        Storage.stopZ();
    }

    public int getPos() {
        return Storage.getZPos();
    }

    public void gotoPos(int pos) {
        switch (pos) {
            case 1:
                moveBackward();
                break;
            case 2:
                if (getPos() < pos) {
                    moveForward();
                } else if (getPos() > pos) {
                    moveBackward();
                }
                break;
            case 3:
                moveForward();
                break;
            case 10:
                if (getPos() != 1) {
                    System.out.println("ERRO, TENTATIVA DO MOVIMENTO INVÁLIDO(A)");
                    return;
                }
            case 20:
                if (getPos() != 2) {
                    System.out.println("ERRO, TENTATIVA DO MOVIMENTO INVÁLIDO(A)");
                    return;
                }
            case 30:
                if (getPos() != 3) {
                    System.out.println("ERRO, TENTATIVA DO MOVIMENTO INVÁLIDO(A)");
                    return;
                }
        }
        while (getPos() != pos) {
            continue;
        }
        stop();
    }
}
