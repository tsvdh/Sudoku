package utilities;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GridSolver {

    private Grid grid;

    public GridSolver(Grid grid) {
        this.grid = grid;
    }

    public void solve() {
        while (!isSolved()) {
            for (Square square : grid.getSquareList()) {
                removeDeprecatedOptions(square);
            }
        }
    }

    private void removeDeprecatedOptions(Square thisSquare) {
        List<Section> sections = grid.getSectionsContaining(thisSquare);

        for (Section section : sections) {
            for (Square otherSquare : section.getSquareList()) {
                if (otherSquare.hasValue() && thisSquare.hasNoValue()) {

                    /*ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

                    scheduledExecutorService.schedule(() -> {
                        square.removeOption(otherSquare.getValue());
                    }, 333, TimeUnit.MILLISECONDS);*/
                    //System.out.println("test");
                    thisSquare.removeOption(otherSquare.getValue());
                    /*try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }*/
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
