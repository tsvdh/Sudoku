package utilities;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GridSolver extends Observable {

    private Grid grid;
    private ScheduledExecutorService executor;
    private Iterator<Square> iterator;

    public GridSolver(Grid grid) {
        this.grid = grid;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        resetIterator();
    }

    public void solve() {
        executor.scheduleWithFixedDelay(this :: updateNextSquare, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void updateNextSquare() {
        if (isSolved()) {
            executor.shutdown();

            setChanged();
            notifyObservers();

        } else {
            if (iterator.hasNext()) {
                Square square = iterator.next();
                
                if (square.hasNoValue()) {
                    removeDeprecatedOptions(square);
                } else {
                    updateNextSquare();
                }
            } else {
                resetIterator();
            }
        }
    }

    private void resetIterator() {
        this.iterator = grid.getSquareList().iterator();
    }

    private void removeDeprecatedOptions(Square thisSquare) {
        List<Section> sections = grid.getSectionsContaining(thisSquare);

        for (Section section : sections) {
            for (Square otherSquare : section.getSquareList()) {
                if (otherSquare.hasValue() && thisSquare.hasNoValue()) {

                    thisSquare.removeOption(otherSquare.getValue());
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
}
