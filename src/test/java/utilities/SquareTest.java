package utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @ParameterizedTest
    @CsvSource({"0, 1, 1",
                "10, 2, 2",
                "3, 0, 3",
                "4, 10, 4",
                "5, 5, 0",
                "6, 6, 10"
    })
    void invalidSquare(Integer value, int xPosition, int yPosition) {
        boolean error = false;

        try {
            new Square(value, xPosition, yPosition);
        } catch (AssertionError e) {
            error = true;
        }

        assertTrue(error);
    }

    @ParameterizedTest
    @CsvSource({"1, 1, 1",
                "9, 2, 2",
                "3, 1, 3",
                "4, 9, 4",
                "5, 5, 1",
                "6, 6, 9"
    })
    void validSquare(Integer value, int xPosition, int yPosition) {
        boolean error = false;

        try {
            new Square(value, xPosition, yPosition);
        } catch (AssertionError e) {
            error = true;
        }

        assertFalse(error);
    }

    @Test
    void noValue() {
        Square square = new Square(null, 1, 1);

        assertNull(square.getValue());
    }

    @Test
    void equals() {
        Square square1 = new Square(null, 2, 5);
        Square square2 = new Square(null, 2, 5);
        Square square3 = new Square(null, 2, 6);

        assertEquals(square1, square2);

        assertNotEquals(square1, square3);
    }

    @Test
    void hasValue() {
        Square square1 = new Square(null, 1, 1);
        Square square2 = new Square(3, 1, 1);

        assertFalse(square1.hasValue());
        assertTrue(square2.hasValue());
    }
}
