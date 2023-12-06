public class AxisX implements Axis {
    public void moveForward() {
        Storage.moveXRight();
    }

    public void moveBackward() {
        Storage.moveXLeft();
    }

    public void stop() {
        Storage.stopX();
    }

    public int getPos() {
        return Storage.getXPos();
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
        }
        while (getPos() != pos) {
            continue;
        }
        stop();
    }
}
