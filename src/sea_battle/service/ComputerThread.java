package sea_battle.service;

import sea_battle.SeaBattle;
import sea_battle.models.Cell;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;

public class ComputerThread implements Runnable {
    private final Exchanger<String> exchanger;
    private String message = "";
    private final List<Cell> usedCells = new ArrayList<>();
    private final Random random = new Random();

    public ComputerThread(Exchanger<String> ex) {
        this.exchanger = ex;
    }

    public void run() {

        while (true) {

            try {
                message = exchanger.exchange(message);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
                break;
            }

            if ("computerTurn".equals(message)) {

                Cell cell = new Cell();
                cell.setDigit(random.nextInt(SeaBattle.FIELD_SIZE));
                cell.setLetter((char) (random.nextInt(SeaBattle.FIELD_SIZE) + 'a'));
                if (!usedCells.contains(cell)) {
                    message = String.valueOf(cell.getDigit()) + String.valueOf(cell.getLetter());
                    try {
                        message = exchanger.exchange(message);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                    usedCells.add(cell);
                }
            } else if ("finished".equals(message)) {
                break;
            }
        }
    }

}
