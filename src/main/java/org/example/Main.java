package org.example;

public class Main {
    private static final int TIME_GREEN_MAIN = 30000; // 30 segundos para a avenida principal
    private static final int TIME_GREEN_SIDE = 15000; // 15 segundos para as avenidas laterais
    private static final int TIME_GREEN_PEDESTRIAN = 10000; // 10 segundos para o semáforo de pedestres
    private static final int TIME_RED = 1000; // 1 segundo de buffer entre trocas

    private static boolean mainRoadGreen = true; // Estado inicial do semáforo da avenida principal
    private static boolean pedestrianCrossingGreen = false; // Estado inicial do semáforo de pedestres

    public static void main(String[] args) {
        // Criando as threads para os semáforos
        Thread mainRoadLight = new Thread(new MainRoadTrafficLight());
        Thread sideRoadLeftLight = new Thread(new SideRoadTrafficLight("esquerda"));
        Thread sideRoadRightLight = new Thread(new SideRoadTrafficLight("direita"));
        Thread pedestrianLight = new Thread(new PedestrianTrafficLight());

        // Iniciando as threads
        mainRoadLight.start();
        sideRoadLeftLight.start();
        sideRoadRightLight.start();
        pedestrianLight.start();
    }

    // Semáforo da avenida principal
    static class MainRoadTrafficLight implements Runnable {
        public void run() {
            try {
                while (true) {
                    synchronized (Main.class) {
                        mainRoadGreen = true;
                        pedestrianCrossingGreen = false;
                        System.out.println("Semáforo da avenida principal está VERDE.");
                        Main.class.notifyAll(); // Notifica os outros semáforos para que se ajustem
                    }
                    Thread.sleep(TIME_GREEN_MAIN);

                    synchronized (Main.class) {
                        mainRoadGreen = false;
                        pedestrianCrossingGreen = true; // Permitir que o semáforo de pedestres fique verde
                        System.out.println("Semáforo da avenida principal está VERMELHO.");
                        Main.class.notifyAll(); // Notifica os outros semáforos para que se ajustem
                    }
                    Thread.sleep(TIME_RED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Semáforos das avenidas laterais (esquerda e direita)
    static class SideRoadTrafficLight implements Runnable {
        private String side;

        public SideRoadTrafficLight(String side) {
            this.side = side;
        }

        public void run() {
            try {
                while (true) {
                    synchronized (Main.class) {
                        while (mainRoadGreen) {
                            Main.class.wait(); // Espera até que o semáforo da avenida principal esteja vermelho
                        }
                        System.out.println("Semáforo da avenida " + side + " está VERDE.");
                    }
                    Thread.sleep(TIME_GREEN_SIDE);

                    synchronized (Main.class) {
                        System.out.println("Semáforo da avenida " + side + " está VERMELHO.");
                        Main.class.notifyAll(); // Notifica os outros semáforos para que se ajustem
                    }
                    Thread.sleep(TIME_RED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Semáforo para pedestres
    static class PedestrianTrafficLight implements Runnable {
        public void run() {
            try {
                while (true) {
                    synchronized (Main.class) {
                        while (!pedestrianCrossingGreen) {
                            Main.class.wait(); // Espera até que seja o momento para o semáforo de pedestres ficar verde
                        }
                        System.out.println("Semáforo de pedestre está VERDE.");
                    }
                    Thread.sleep(TIME_GREEN_PEDESTRIAN);

                    synchronized (Main.class) {
                        pedestrianCrossingGreen = false;
                        System.out.println("Semáforo de pedestre está VERMELHO.");
                        Main.class.notifyAll(); // Notifica os outros semáforos para que se ajustem
                    }
                    Thread.sleep(TIME_RED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
