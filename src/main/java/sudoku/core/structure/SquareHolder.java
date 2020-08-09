package sudoku.core.structure;

import java.util.LinkedList;
import java.util.List;

class SquareHolder {

    private List<Square> squareList;

    SquareHolder() {
        reset();
    }

    void reset() {
        this.squareList = new LinkedList<>();
    }

    void addSquare(Square square) {
        squareList.add(square);
    }

    public List<Square> getSquareList() {
        return squareList;
    }

    boolean contains(Square square) {
        return squareList.contains(square);
    }
}
