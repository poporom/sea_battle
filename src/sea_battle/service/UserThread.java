package sea_battle.service;

import sea_battle.SeaBattle;
import sea_battle.models.Cell;
import sea_battle.models.Ship;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Exchanger;

public class UserThread implements Runnable {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final Exchanger<String> exchanger;
    private final List<Ship> ships;
    private final List<Ship> userShips;

    public UserThread(Exchanger<String> exchanger, List<Ship> ships, List<Ship> userShips) {
        this.exchanger = exchanger;
        this.ships = ships;
        this.userShips = userShips;
    }

    public void run() {
        String message = "";
        String strStrike = "";
        boolean finished = false;

        while (true) {

            try {
                message = exchanger.exchange("");
                if (message.length() == 2) {
                    System.out.println("Computer move is : " + message);
                }
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
                break;
            }

            if (message.length() == 2) {
                if (message.matches(SeaBattle.CELL_REGEX)) {

                    int y = Character.getNumericValue(message.charAt(0));
                    char x = message.charAt(1);

                    Cell tmpCompCell = new Cell();
                    tmpCompCell.setDigit(y);
                    tmpCompCell.setLetter(x);

                    strStrike = "Miss";

                    for (Ship ship : userShips) {
                        if (ship.getShipCells().contains(tmpCompCell)) {
                            Strike strike = new Strike(ship);
                            strStrike = strike.getStrike(tmpCompCell);
                            break;
                        }
                    }

                    System.out.println(strStrike);

                } else {
                    System.out.println("Out of field!");
                }

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
                    exchanger.exchange("finish");
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                    break;
                }
                break;
            }

            if (line != null && line.matches(SeaBattle.CELL_REGEX)) {

                int y = Character.getNumericValue(line.charAt(0));
                char x = line.charAt(1);

                Cell tmpUserCell = new Cell();
                tmpUserCell.setDigit(y);
                tmpUserCell.setLetter(x);

                strStrike = "Miss";

                for (Ship ship : ships) {
                    if (ship.getShipCells().contains(tmpUserCell)) {
                        Strike strike = new Strike(ship);
                        strStrike = strike.getStrike(tmpUserCell);
                        break;
                    }
                }

                System.out.println(strStrike);

                if (SeaBattle.testMode) {
                    System.out.println("Computer ships:");
                    for (Ship ship : ships) {
                        System.out.println(ship);
                    }
                }

            } else {
                System.out.println("Out of field!");
                continue;
            }

            long userRemainder = userShips.stream()
                    .flatMap(e -> e.getShipCells().stream())
                    .filter(e -> !e.isBeaten())
                    .count();

            long computerRemainder = ships.stream()
                    .flatMap(e -> e.getShipCells().stream())
                    .filter(e -> !e.isBeaten())
                    .count();

            if (computerRemainder == 0 || userRemainder == 0) {
                finished = true;
                String loser = computerRemainder == 0 ? "COMPUTER" : "USER";
                System.out.println(loser + " ships are sunk! Game over");
            }

            if (finished) {
                try {
                    exchanger.exchange("finished");
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                    break;
                }
                break;
            } else {
                try {
                    exchanger.exchange("computerTurn");
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                    break;
                }
            }
        }
    }
}
