package core.solving;

import core.misc.SettingsHandler;
import javafx.scene.control.Button;
import core.structure.Grid;
import core.structure.Square;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LinkedGridSolver extends GridSolver {

    private ScheduledExecutorService executor;
    private Square currentSquare;
    private Square previousSquare;
    private Button pauseButton;

    private static final SettingsHandler settingsHandler = SettingsHandler.getInstance();

    public LinkedGridSolver(Grid grid, Button pauseButton) {
        super(grid);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.pauseButton = pauseButton;
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

                    pauseButton.setDisable(true);
                    try {
                        Thread.sleep(settingsHandler.getPause());
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    pauseButton.setDisable(false);

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
        int interval = settingsHandler.getInterval();
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

            int interval = settingsHandler.getInterval();
            executor.scheduleWithFixedDelay(this :: updateNextSquare, 0, interval, TimeUnit.MILLISECONDS);
        } else {
            updateNextSquare();
            executor.shutdown();
        }
    }
}
