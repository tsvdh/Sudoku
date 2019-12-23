package utilities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static gui.Sudoku.getSettingsHandler;

public class LinkedGridSolver extends GridSolver {

    private ScheduledExecutorService executor;
    private Square currentSquare;
    private Square previousSquare;

    public LinkedGridSolver(Grid grid) {
        super(grid);
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public Square getCurrentSquare() {
        return currentSquare;
    }

    public Square getPreviousSquare() {
        return previousSquare;
    }

    @Override
    void updateNextSquare() {
        if (isSolved()) {
            stop();

        } else {
            if (getIterator().hasNext()) {
                Square square = getIterator().next();

                if (square.hasNoValue()) {
                    previousSquare = currentSquare;
                    currentSquare = square;
                    setChanged();
                    notifyObservers("working");

                    optimizeSquare(square);
                }

                else {
                    updateNextSquare();
                }
            } else {
                if (stuck()) {
                    setChanged();
                    notifyObservers("magic");

                    try {
                        Thread.sleep(getSettingsHandler().getPause());
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    determinePair();

                    boolean success = getSuccess();

                    if (success) {
                        resetIterator();
                    } else {
                        stop();
                    }
                } else {
                    resetIterator();
                }
            }
        }
    }

    @Override
    public void solve() {
        int interval = getSettingsHandler().getInterval();
        executor.scheduleWithFixedDelay(this :: updateNextSquare, 1000, interval, TimeUnit.MILLISECONDS);
    }

    private void stop() {
        executor.shutdown();

        setChanged();
        notifyObservers("done");
    }

    public void togglePause() {
        if (executor.isShutdown()) {
            executor = Executors.newSingleThreadScheduledExecutor();

            int interval = getSettingsHandler().getInterval();
            executor.scheduleWithFixedDelay(this :: updateNextSquare, 0, interval, TimeUnit.MILLISECONDS);
        } else {
            executor.shutdown();
        }
    }
}
