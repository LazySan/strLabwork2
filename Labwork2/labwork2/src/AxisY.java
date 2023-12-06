public class AxisY implements Axis {
    public void moveForward() {
        Storage.moveYInside();
    }

    public void moveBackward() {
        Storage.moveYOutside();
    }

    public void stop() {
        Storage.stopY();
    }

    public int getPos() {
        return Storage.getYPos();
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
