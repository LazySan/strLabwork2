public interface Axis {
    public void moveForward();

    public void moveBackward();

    public void stop();

    public int getPos();

    public void gotoPos(int pos);
}