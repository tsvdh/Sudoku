package utilities;

import java.util.List;

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
