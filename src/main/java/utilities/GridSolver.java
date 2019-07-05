package utilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GridSolver extends Observable {

    private Grid grid;
    private ScheduledExecutorService executor;
    private Iterator<Square> iterator;
    private boolean gridChanged;

    public GridSolver(Grid grid) {
        this.grid = grid;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        resetIterator();
        this.gridChanged = false;
    }

    public void solve() {
        executor.scheduleWithFixedDelay(this :: updateNextSquare, 0, 20, TimeUnit.MILLISECONDS);
    }

    private void updateNextSquare() {
        if (isSolved()) {
            stop();

        } else {
            if (iterator.hasNext()) {
                Square square = iterator.next();
                
                if (square.hasNoValue()) {

                    removeDeprecatedOptions(square);
                    checkOptionOccursOnce(square);

                } else {
                    updateNextSquare();
                }
            } else {
                if (stuck()) {
                    stop();
                } else {
                    resetIterator();
                }
            }
        }
    }

    private void stop() {
        executor.shutdown();

        setChanged();
        notifyObservers();
    }

    private void resetIterator() {
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

    private boolean isSolved() {
        for (Square square : grid.getSquareList()) {
            if (square.hasNoValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean stuck() {
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
}
