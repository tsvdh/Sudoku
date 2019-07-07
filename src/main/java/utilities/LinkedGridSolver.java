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

    @Override
    public void solve() {
        executor.scheduleWithFixedDelay(this :: updateNextSquare, 0, 20, TimeUnit.MILLISECONDS);
    }

    private void stop() {
        executor.shutdown();

        setChanged();
        notifyObservers();
    }
}
