
package sea_battle;

import java.io.IOException;

import sea_battle.models.Ship;
import sea_battle.service.ShipCreator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import sea_battle.models.Cell;
import sea_battle.service.Strike;


public class Sea_battle {
    public static final int fieldSize = 10;

    public static void main(String[] args) throws IOException {

        List<Ship> ships = new ArrayList<>();
        List<Ship> userShips = new ArrayList<>();
        Random random = new Random();
        Exchanger<String> ex = new Exchanger<String>();

        List<Integer> vert = IntStream.iterate(0, i -> i + 1)
                .limit(fieldSize)
                .boxed()
                .collect(Collectors.toList());

        List<Character> hor = IntStream.iterate(0, i -> i + 1)
                .limit(fieldSize)
                .boxed()
                .map(e -> (char) (e + 'a'))
                .collect(Collectors.toList());


        System.out.println("Field:");
        System.out.println("Integer coordinates: " + vert.toString());
        System.out.println("Character coordinates: " + hor.toString());

        ShipCreator shipCreator = new ShipCreator();
        ships.add(shipCreator.createShip(4));
        ships.add(shipCreator.createShip(3));
        ships.add(shipCreator.createShip(3));
        ships.add(shipCreator.createShip(2));
        ships.add(shipCreator.createShip(2));
        ships.add(shipCreator.createShip(2));
        ships.add(shipCreator.createShip(1));
        ships.add(shipCreator.createShip(1));
        ships.add(shipCreator.createShip(1));
        ships.add(shipCreator.createShip(1));

        System.out.println("Computer ships:");
        for (Ship ship : ships) {
            System.out.println(ship);
        }

        ShipCreator userShipCreator = new ShipCreator();
        userShips.add(userShipCreator.createShip(4));
        userShips.add(userShipCreator.createShip(3));
        userShips.add(userShipCreator.createShip(3));
        userShips.add(userShipCreator.createShip(2));
        userShips.add(userShipCreator.createShip(2));
        userShips.add(userShipCreator.createShip(2));
        userShips.add(userShipCreator.createShip(1));
        userShips.add(userShipCreator.createShip(1));
        userShips.add(userShipCreator.createShip(1));
        userShips.add(userShipCreator.createShip(1));

        System.out.println("User ships:");

        for (Ship ship : userShips) {
            System.out.println(ship);
        }

        class ComputerThread implements Runnable {

            Exchanger<String> exchanger;
            String message = "";
            List<Cell> usedCells = new ArrayList<>();

            ComputerThread(Exchanger<String> ex) {
                this.exchanger = ex;
            }

            public void run() {

                while (true) {

                    try {
                        message = exchanger.exchange(message);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }

                    if ("computerTurn".equals(message)) {

                        Cell cell = new Cell();
                        cell.setDigit(random.nextInt(fieldSize));
                        cell.setLetter((char) (random.nextInt(fieldSize) + 'a'));
                        if (!usedCells.contains(cell)) {
                            message = String.valueOf(cell.getDigit()) + String.valueOf(cell.getLetter());
                            try {
                                message = exchanger.exchange(message);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            usedCells.add(cell);
                        }
                    } else if ("finish".equals(message)) {
                        break;
                    }
                }
            }
        }

        class UserThread implements Runnable {
            private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Exchanger<String> exchanger;
            String message = "";

            UserThread(Exchanger<String> ex) {
                this.exchanger = ex;
            }

            public void run() {

                String strStrike = "";
                boolean finished = false;

                while (true) {

                    try {
                        message = exchanger.exchange("");
                        if (message.length() == 2) {
                            System.out.println("computer move is : " + message);
                        }
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }

                    if (message.length() == 2) {

                        char ch = message.charAt(1);
                        if (!hor.contains(ch)) {
                            System.out.println("Out of field!");
                        }
                        String stringInt = message.substring(0, 1);
                        int i = Integer.valueOf(stringInt);
                        if (!vert.contains(i)) {
                            System.out.println("Out of field!");
                        }

                        Cell cell1 = new Cell();
                        cell1.setDigit(i);
                        cell1.setLetter(ch);

                        strStrike = "Miss";

                        for (Ship ship : userShips) {
                            if (ship.getShipCells().contains(cell1)) {
                                Strike strike = new Strike(ship);
                                strStrike = strike.getStrike(cell1);
                                break;
                            }
                        }

                        System.out.println(strStrike);

                        System.out.println("User ships:");

                        for (Ship ship : userShips) {
                            System.out.println(ship);
                        }
                    }

                    System.out.println("Enter the cell in format [int+char]: ");
                    String line = null;
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                    if ("exit".equals(line)) {
                        try {
                            message = exchanger.exchange("finish");
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        break;
                    }

                    String stringChar = line.substring(1, 2);
                    char ch = stringChar.charAt(0);
                    if (!hor.contains(ch)) {
                        System.out.println("Out of field!");
                        continue;
                    }
                    String stringInt = line.substring(0, 1);
                    int i = Integer.valueOf(stringInt);
                    if (!vert.contains(i)) {
                        System.out.println("Out of field!");
                        continue;
                    }
                    Cell cell = new Cell();
                    cell.setDigit(i);
                    cell.setLetter(ch);

                    strStrike = "Miss";

                    for (Ship ship : ships) {
                        if (ship.getShipCells().contains(cell)) {
                            Strike strike = new Strike(ship);
                            strStrike = strike.getStrike(cell);
                            break;
                        }
                    }

                    System.out.println(strStrike);

                    System.out.println("Computer ships:");
                    for (Ship ship : ships) {
                        System.out.println(ship);
                    }

                    finished = true;
                    for (Cell tmp : shipCreator.getShipCells()) {
                        if (!tmp.isBeaten()) {
                            finished = false;
                        }
                    }

                    if (finished) {
                        System.out.println("All ships are sunk! Game over");
                        try {
                            message = exchanger.exchange("finish");
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else {
                        try {
                            message = exchanger.exchange("computerTurn");
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
        }

        new Thread(new ComputerThread(ex)).start();
        new Thread(new UserThread(ex)).start();

    }

}
