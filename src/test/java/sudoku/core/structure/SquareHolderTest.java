package sudoku.core.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareHolderTest {

    @Test
    void contains() {
        SquareHolder squareHolder = new SquareHolder();
        Square square1 = new Square(null, 1, 1);
        Square square2 = new Square(null, 1, 1);
        squareHolder.addSquare(square1);
        squareHolder.addSquare(square2);

        assertTrue(squareHolder.getSquareList().contains(square1));
    }
}
