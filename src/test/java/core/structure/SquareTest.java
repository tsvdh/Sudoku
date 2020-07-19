package core.structure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import core.structure.Square;

import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest
    @CsvSource({"2, 3",
                "3, 2",
                "3, 4",
                "4, 3"
    })
    void neighbours(int xPosition, int yPosition) {
        Square square1 = new Square(null, 3, 3);
        Square square2 = new Square(null, xPosition, yPosition);
        assertThat(square1.isNeighbourOf(square2));
    }

    @ParameterizedTest
    @CsvSource({"1, 3",
                "1, 1",
                "9, 9",
                "4, 4",
                "3, 3"
    })
    void notNeighbours(int xPosition, int yPosition) {
        Square square1 = new Square(null, 3, 3);
        Square square2 = new Square(null, xPosition, yPosition);
        assertThat(square1.isNeighbourOf(square2)).isFalse();
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

    @Test
    void isPair() {
        Square square1 = new Square(null, 1, 1);
        for (int i = 1; i < 8; i++) {
            square1.removeOption(i);
        }

        Square square2 = new Square(null, 1, 2);

        Square square3 = new Square(1, 2, 2);

        assertThat(square2.isPair()).isFalse();

        assertThat(square1.isPair()).isTrue();

        assertThat(square3.isPair()).isFalse();
    }
}
