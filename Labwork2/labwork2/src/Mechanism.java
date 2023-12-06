public class Mechanism {
    public void ledOn(int ledNumber) {
        Storage.ledon(ledNumber);
    }

    public void ledsOff() {
        Storage.ledsOff();
    }

    public boolean switch1Pressed() {
        if (Storage.getSwitch1() == 1)
            return true;
        return false;
    }

    public boolean switch2Pressed() {
        if (Storage.getSwitch2() == 1)
            return true;
        return false;
    }

    public boolean bothSwitchesPressed() {
        if (Storage.getSwitch1_2() == 1)
            return true;
        return false;
    }

    public boolean hasPallet() {
        if (Storage.hasPallet() == 1)
            return true;
        return false;
    }

    public void putPartInCell() {
        Storage.moveZUp();
        while (Storage.getZPos() <= 3) {
            continue;
        }
        Storage.stopZ();

        Storage.moveYInside();
        while (Storage.getYPos() != 3) {
            continue;
        }
        Storage.stopY();

        Storage.moveZDown();
        while (!(Storage.getZPos() <= 3 && Storage.getZPos() >= 1)) {

            continue;
        }
        Storage.stopZ();

        Storage.moveYOutside();
        while (Storage.getYPos() != 2) {
            continue;
        }
        Storage.stopY();
    }

    public void takePartFromCell() {
        Storage.moveYInside();
        while (Storage.getYPos() != 3) {
            continue;
        }
        Storage.stopY();

        Storage.moveZUp();
        while (Storage.getZPos() <= 3) {
            continue;
        }
        Storage.stopZ();

        Storage.moveYOutside();
        while (Storage.getYPos() != 2) {
            continue;
        }
        Storage.stopY();

        Storage.moveZDown();
        while (!(Storage.getZPos() <= 3 && Storage.getZPos() >= 1)) {

            continue;
        }
        Storage.stopZ();
    }

    public int GetP2() {
        return Storage.getP2();
    }

    public void ModifyP2(int p) {
        Storage.modifyP2(p);
    }
}