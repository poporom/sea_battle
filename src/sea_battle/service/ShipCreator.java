/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sea_battle.service;
import sea_battle.Sea_battle;
import sea_battle.models.Cell;
import sea_battle.models.Ship;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShipCreator {

    private List<Cell> inaccessibleCells = new ArrayList<>();
    private List<Cell> shipCells = new ArrayList<>();
    private Random random = new Random();

    public Ship createShip(int shipLength) {

        Ship ship= new Ship(shipLength);
        boolean added;
        do {
            Cell cell = new Cell();
            do {
                cell.setDigit(getNextNumber());
                cell.setLetter(getNextChar());

            } while(!ship.checkIfEligible(cell) || inaccessibleCells.contains(cell));

            added = ship.addCell(cell);
            if (added) {
                inaccessibleCells.add(cell);
                shipCells.add(cell);
            }
        } while(added);

        //One cell between ships

        for (Cell shipCell : ship.getShipCells()) {
            int digit = shipCell.getDigit();
            int letter = shipCell.getLetter();

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    Cell tempCell = new Cell((char) (letter+i), digit + j);
                    if (!shipCells.contains(tempCell)) {
                        inaccessibleCells.add(tempCell);
                    }
                }
            }
        }

        return ship;
    }

    public List<Cell> getShipCells() {
        return shipCells;
    }

    private int getNextNumber() {
        return random.nextInt(Sea_battle.fieldSize);
   }
    
    private char getNextChar() {
        return (char)(random.nextInt(Sea_battle.fieldSize) + 'a');
    }    
}
