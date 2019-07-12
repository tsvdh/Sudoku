package utilities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LinkedGridSolver extends GridSolver {

    private ScheduledExecutorService executor;

    public LinkedGridSolver(Grid grid) {
        super(grid);
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    void updateNextSquare() {
        if (isSolved()) {
            stop();

        } else {
            if (getIterator().hasNext()) {
                Square square = getIterator().next();

                if (square.hasNoValue()) {
                    optimizeSquare(square);
                }

                else {
                    updateNextSquare();
                }
            } else {
                if (stuck()) {
                    Grid grid = getGrid();
                    for (Square square : grid.getSquareList()) {
                        if (square.isPair()) {
                            System.out.println(square);
                            break;
                        }
                    }
                    stop();
                } else {
                    resetIterator();
                }
            }
        }
    }

    @Override
    public void solve() {
        int delay = new SettingsHandler().getDelay();
        executor.scheduleWithFixedDelay(this :: updateNextSquare, 0, delay, TimeUnit.MILLISECONDS);
    }

    private void stop() {
        executor.shutdown();

        setChanged();
        notifyObservers();
    }
}
