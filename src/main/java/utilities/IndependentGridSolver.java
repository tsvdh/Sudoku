package utilities;

public class IndependentGridSolver extends GridSolver {

    public IndependentGridSolver(Grid grid) {
        super(grid);
    }

    @Override
    public void solve() {
        updateNextSquare();
    }

    @Override
    void updateNextSquare() {
        if (!isSolved()) {

            if (getIterator().hasNext()) {
                Square square = getIterator().next();

                if (square.hasNoValue()) {
                    optimizeSquare(square);
                }

                updateNextSquare();
            }

            else if (!stuck()) {
                resetIterator();
                updateNextSquare();
            }
        }
    }
}
