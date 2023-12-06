
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Thread_menu extends Thread {

    LinkedBlockingQueue<int[]> movementQueue = new LinkedBlockingQueue<int[]>();
    Thread_storage thread_storage;
    LinkedBlockingQueue<Integer> deliverWithDelayQueue;

    @Override
    public void run() {

        Storage.initializeHardwarePorts();

        Axis axisX = new AxisX();
        Axis axisY = new AxisY();
        Axis axisZ = new AxisZ();

        Mechanism mechanism = new Mechanism();

        Pallet[][] matrixPallets = new Pallet[3][3];

        Scanner inputScanner = new Scanner(System.in);

        Semaphore movementCompletedSemaphore = new Semaphore(0);

        Semaphore startFlashSemaphore = new Semaphore(0);
        Semaphore stopFlashSemaphore = new Semaphore(0);

        Semaphore enterEmergencySemaphore = new Semaphore(0);
        Semaphore exitEmergencySemaphore = new Semaphore(0);

        deliverWithDelayQueue = new LinkedBlockingQueue<Integer>();
        LinkedBlockingQueue<int[]> updateMatrixQueue = new LinkedBlockingQueue<int[]>();

        LinkedBlockingQueue<int[]> gotoXZQueue = new LinkedBlockingQueue<int[]>();

        Thread thread_gotoXZ = new Thread_gotoXZ(axisX, axisZ, gotoXZQueue, movementCompletedSemaphore);
        Thread thread_movement = new Thread_movements(movementQueue, gotoXZQueue, mechanism,
                movementCompletedSemaphore, axisY, deliverWithDelayQueue, updateMatrixQueue);
        Thread thread_shipmentDate = new Thread_shipmentDate(matrixPallets, mechanism, startFlashSemaphore,
                stopFlashSemaphore);
        Thread thread_leds = new Thread_leds(mechanism, startFlashSemaphore, stopFlashSemaphore);
        Thread thread_switches = new Thread_switches(this, matrixPallets, mechanism, movementQueue,
                deliverWithDelayQueue, enterEmergencySemaphore, exitEmergencySemaphore);
        thread_storage = new Thread_storage(matrixPallets, updateMatrixQueue, stopFlashSemaphore);
        Thread thread_ledsEmergency = new Thread_ledsEmergency(mechanism, enterEmergencySemaphore,
                exitEmergencySemaphore);

        Calibrate(inputScanner, axisX, axisY, axisZ);

        thread_gotoXZ.start();
        thread_movement.start();
        thread_shipmentDate.start();
        thread_switches.start();
        thread_leds.start();
        thread_storage.start();
        thread_ledsEmergency.start();

        int op = -1;
        while (op != 0) {
            ClearConsole();
            System.out.println("========[MENU]========\n");
            System.out.println("1 - Storage Manager");
            System.out.println("2 - Info Manager");

            op = inputScanner.nextInt();
            inputScanner.nextLine();
            switch (op) {
                case 0:
                    System.out.println("A desligar o programa...");
                    System.exit(1);
                    break;
                case 1:
                    try {
                        StorageManager(inputScanner, mechanism, axisX, axisZ);
                    } catch (InterruptedException e) {
                    }
                    break;

                case 2:
                    InfoManager(inputScanner);
                    break;

                default:
                    break;
            }
        }
    }

    private void StorageManager(Scanner inputScanner, Mechanism mechanism,
            Axis axisX, Axis axisZ) throws InterruptedException {
        String name;
        String strAux;
        long referenceCode;
        LocalDateTime shipmentDate;
        int x, z;
        Pallet pallet;
        while (true) {
            int op = 0;
            ClearConsole();
            System.out.println("=======[STORAGE]=======\n");
            System.out.println("1 - Inserir Pallet");
            System.out.println("2 - Mover Pallet");
            System.out.println("3 - Deliver Pallet (com posicao)");
            System.out.println("4 - Deliver Pallet (com referencia)");

            inputScanner = new Scanner(System.in);

            op = inputScanner.nextInt();

            CleanInput(inputScanner);
            switch (op) {
                case 0:
                    return;
                case 1:
                    System.out.println("Onde é que a quer inserir?");
                    System.out.println("X Z");
                    x = inputScanner.nextInt();
                    z = inputScanner.nextInt();

                    CleanInput(inputScanner);
                    if (thread_storage.GetPallet(x - 1, z - 1) != null) {
                        System.out.println("Essa célula não está vazia");
                        break;
                    }

                    System.out.println("Introduza o nome da pallet");
                    name = inputScanner.nextLine();

                    System.out.println("Introduza a referencia da pallet");
                    strAux = inputScanner.nextLine();
                    referenceCode = StringToIntArray(strAux);

                    System.out.println("Introduza a data de entrega da pallet");
                    System.out.println("dd/MM/YYYY hh:mm:ss");

                    strAux = inputScanner.nextLine();
                    shipmentDate = StringToDate(strAux);

                    pallet = CreatePallet(name, referenceCode, shipmentDate);
                    System.out.println("Pallet criada com sucesso");

                    thread_storage.InsertPalletMatrix(pallet, x - 1, z - 1);
                    movementQueue.add(new int[] { 0, x, z });

                    break;
                case 2:
                    int oldx, oldz;

                    System.out.println("Onde é que a quer tirar?");
                    System.out.println("X Z");
                    oldx = inputScanner.nextInt();
                    oldz = inputScanner.nextInt();
                    CleanInput(inputScanner);

                    if (thread_storage.GetPallet(oldx - 1, oldz - 1) == null) {
                        System.err.println("Essa célula está vazia");
                        break;
                    }

                    System.out.println("Onde é que a quer inserir?");
                    System.out.println("X Z");
                    x = inputScanner.nextInt();
                    z = inputScanner.nextInt();
                    CleanInput(inputScanner);

                    if (thread_storage.GetPallet(x - 1, z - 1) != null) {
                        System.out.println("Essa célula não está vazia");
                        break;
                    }

                    thread_storage.MovePalletMatrix(oldx - 1, oldz - 1, x - 1, z - 1);
                    movementQueue.add(new int[] { 1, oldx, oldz, x, z });
                    break;
                case 3:
                    System.out.println("Onde é que a quer tirar?");
                    System.out.println("X Z");
                    x = inputScanner.nextInt();
                    z = inputScanner.nextInt();
                    CleanInput(inputScanner);

                    if (thread_storage.GetPallet(x - 1, z - 1) == null) {
                        System.out.println("Essa célula está vazia");
                        break;
                    }

                    thread_storage.DeliverPalletMatrix(x - 1, z - 1);
                    deliverWithDelayQueue.add(0);
                    movementQueue.add(new int[] { 2, x, z });

                    break;
                case 4:
                    System.out.println("Qual é a referencia da pallet?");
                    referenceCode = inputScanner.nextLong();
                    CleanInput(inputScanner);

                    for (int i = 0; i <= 2; i++) {

                        for (int j = 0; j <= 2; j++) {

                            pallet = thread_storage.GetPallet(i, j);
                            if (pallet != null) {
                                if (referenceCode == pallet.referenceCode) {
                                    x = i + 1;
                                    z = j + 1;

                                    thread_storage.DeliverPalletMatrix(x - 1, z - 1);
                                    deliverWithDelayQueue.add(0);
                                    movementQueue.add(new int[] { 2, x, z });
                                }
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void InfoManager(Scanner inputScanner) {
        Pallet pallet;

        long[] rep = new long[9];
        long referencia;
        int y;

        while (true) {
            int op = -1;
            boolean referenciaRepetida;

            ClearConsole();
            System.out.println("\n\n========[INFO]========\n");
            System.out.println("1 - Listagem");
            System.out.println("2 - Procurar pallet");
            System.out.println("3 - Informação do sistema");

            op = inputScanner.nextInt();
            CleanInput(inputScanner);

            switch (op) {
                case 0:
                    return;
                case 1:
                    for (int i = 0; i < rep.length; i++) {
                        rep[i] = -1;
                    }
                    for (int x = 0; x <= 2; x++) {
                        for (int z = 0; z <= 2; z++) {
                            referenciaRepetida = false;
                            pallet = thread_storage.GetPallet(x, z);
                            if (pallet != null) {
                                for (y = 0; y < rep.length; y++) {
                                    if (rep[y] == pallet.referenceCode) {
                                        referenciaRepetida = true;
                                        break;
                                    }
                                }
                                if (!referenciaRepetida) {
                                    for (y = 0; y < rep.length; y++) {
                                        if (rep[y] == -1) {
                                            rep[y] = pallet.referenceCode;
                                        }
                                    }
                                    System.out.println("\nNome: " + pallet.name);
                                    System.out.println("Referencia: " + pallet.referenceCode);
                                    System.out.println("Data: " + pallet.shipmentDate);
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    System.out.println("Introduza uma referencia: ");
                    referencia = inputScanner.nextLong();
                    CleanInput(inputScanner);

                    for (int x = 0; x <= 2; x++) {
                        for (int z = 0; z <= 2; z++) {
                            pallet = thread_storage.GetPallet(x, z);
                            if (pallet != null) {
                                if (referencia == pallet.referenceCode) {
                                    System.out.println("\nData: " + pallet.shipmentDate);
                                    System.out.println("Coordenadas X:" + (x + 1));
                                    System.out.println("Coordenadas Z:" + (z + 1));

                                }
                            }
                        }
                    }
                    break;
                case 3:
                    System.out.println("+---+---+---+");
                    DrawLineInfo(2);
                    System.out.println("|");
                    System.out.println("+---+---+---+");
                    DrawLineInfo(1);
                    System.out.println("|");
                    System.out.println("+---+---+---+");
                    DrawLineInfo(0);
                    System.out.println("|");
                    System.out.println("+---+---+---+");

                    int free = 0;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            pallet = thread_storage.GetPallet(i, j);
                            if (pallet == null) {
                                free++;
                            }
                        }
                    }
                    System.out.println("Células ocupadas: " + (9 - free));
                    System.out.println("Células livres: " + free);
            }
            do {
                System.out.println("\nPara sair introduza 0");
                op = inputScanner.nextInt();
            } while (op != 0);
        }
    }

    private void Calibrate(Scanner inputScanner, Axis axisX, Axis axisY, Axis axisZ) {
        System.out.println("Calibração iniciada");
        while (axisY.getPos() == -1) {
            axisY.moveBackward();
        }
        axisY.stop();
        System.out.println("Eixo Y calibrado");
        axisY.gotoPos(2);// Senão os outros n movem

        while (axisX.getPos() == -1) {
            axisX.moveBackward();
        }
        axisX.stop();
        System.out.println("Eixo X calibrado");

        while (axisZ.getPos() == -1) {
            axisZ.moveBackward();
        }
        axisZ.stop();
        System.out.println("Eixo Z calibrado");

        System.out.println("Todos os eixos foram calibrados, controlo manual ativado");
        boolean calibrado = axisX.getPos() == 1 && axisZ.getPos() == 1 && axisY.getPos() == 2;
        char op = 0;
        while (!calibrado) {
            System.out.println("\nControlos:");
            System.out.println("W - Cima");
            System.out.println("A - Esquerda");
            System.out.println("S - Baixo");
            System.out.println("D - Direita");

            op = inputScanner.next().charAt(0);
            switch (op) {
                case 'W':
                    if (axisZ.getPos() != 30) {
                        axisZ.moveForward();
                        while (axisZ.getPos() != -1) {
                        }
                        while (axisZ.getPos() == -1) {
                        }
                        axisZ.stop();
                    }
                    break;
                case 'A':
                    if (axisX.getPos() != 1) {
                        axisX.moveBackward();
                        while (axisX.getPos() != -1) {
                        }
                        while (axisX.getPos() == -1) {
                        }
                        axisX.stop();
                    }
                    break;
                case 'S':
                    if (axisZ.getPos() != 1) {
                        axisZ.moveBackward();
                        while (axisZ.getPos() != -1) {
                        }
                        while (axisZ.getPos() == -1) {
                        }
                        axisZ.stop();
                    }
                    break;
                case 'D':
                    if (axisX.getPos() != 3) {
                        axisX.moveForward();
                        while (axisX.getPos() != -1) {
                        }
                        while (axisX.getPos() == -1) {
                        }
                        axisX.stop();
                    }
                    break;

                default:
                    System.out.println("Comando nao reconhecido");
                    break;
            }
            calibrado = axisX.getPos() == 1 && axisZ.getPos() == 1 && axisY.getPos() == 2;
        }
        System.out.println("Calibração concluida, iniciando o programa...");
    }

    /****************************/
    // AUXILIARES
    /****************************/
    private long StringToIntArray(String str) {
        return Long.parseLong(str);
    }

    private LocalDateTime StringToDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        return LocalDateTime.parse(str, formatter);
    }

    private Pallet CreatePallet(String name, long referenceCode, LocalDateTime shipmentDate) {
        Pallet pallet = new Pallet();
        pallet.name = name;
        pallet.referenceCode = referenceCode;
        pallet.shipmentDate = shipmentDate;
        return pallet;
    }

    private void CleanInput(Scanner sc) {
        if (sc.hasNextLine()) {
            sc.nextLine();
        }
    }

    private void ClearConsole() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    private void DrawLineInfo(int line) {
        Pallet pallet;
        for (int i = 0; i < 3; i++) {
            System.out.printf("| ");
            pallet = thread_storage.GetPallet(i, line);
            if (pallet != null) {
                System.out.printf("X ");
            } else {
                System.out.printf("  ");
            }
        }
    }
}