package utilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

abstract class GridSolver extends Observable {

    private Grid grid;
    private Iterator<Square> iterator;
    private boolean gridChanged;

    GridSolver(Grid grid) {
        this.grid = grid;
        this.gridChanged = false;
        resetIterator();
    }

    Grid getGrid() {
        return grid;
    }

    Iterator<Square> getIterator() {
        return iterator;
    }

    public abstract void solve();

    abstract void updateNextSquare();


    void resetIterator() {
        this.iterator = grid.getSquareList().iterator();
        gridChanged = false;
    }

    private void removeDeprecatedOptions(Square thisSquare) {
        List<Section> sections = grid.getSectionsContaining(thisSquare);

        for (Section section : sections) {
            for (Square otherSquare : section.getSquareList()) {
                if (otherSquare.hasValue() && thisSquare.hasNoValue()) {

                    removeOption(thisSquare, otherSquare.getValue());
                }
            }
        }
    }

    boolean isSolved() {
        for (Square square : grid.getSquareList()) {
            if (square.hasNoValue()) {
                return false;
            }
        }
        return true;
    }

    boolean stuck() {
        return !gridChanged;
    }

    private void removeOption(Square square, Integer value) {
        boolean changed = square.removeOption(value);
        if (changed) {
            gridChanged = true;
        }
    }

    private void checkOptionOccursOnce(Square thisSquare) {

        if (thisSquare.hasValue()) {
            return;
        }

        List<Section> sections = grid.getSectionsContaining(thisSquare);
        List<Integer> allOptions = new LinkedList<>();

        for (Section section : sections) {

            for (Square otherSquare : section.getSquareList()) {
                if (!otherSquare.equals(thisSquare) && otherSquare.hasNoValue()) {

                    allOptions.addAll(otherSquare.getOptions());
                }
            }

            for (Integer integer : thisSquare.getOptions()) {
                if (!allOptions.contains(integer)) {

                    List<Integer> optionsToRemove = new LinkedList<>(thisSquare.getOptions());
                    optionsToRemove.remove(integer);

                    for (Integer integerToRemove : optionsToRemove) {

                        removeOption(thisSquare, integerToRemove);
                    }

                    return;
                }
            }

            allOptions.clear();
        }
    }

    void optimizeSquare(Square thisSquare) {
        removeDeprecatedOptions(thisSquare);
        checkOptionOccursOnce(thisSquare);
    }
}
