package utilities;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @ParameterizedTest
    @CsvSource({"11, 4, 2",
                "3, -2, 2"
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
    @CsvSource({"1, 4, 2",
                "3, 9, 2"
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
}
