package sudoku.core.structure;

import sudoku.core.misc.options.Mode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GridTest {

    Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid(Mode.NORMAL);
    }

    @AfterEach
    void tearDown() {
        grid = null;
    }

    @Test
    void testClone() {
        grid = new Grid(Mode.JIGSAW);
        grid.addSquare(new Square(1, 1, 1), 1);
        grid.addSquare(new Square(2, 2, 2), 3);

        List<Integer> special = new LinkedList<>();
        special.add(0); special.add(1);
        special.add(9); special.add(10);
        special.add(18); special.add(20);

        for (int i = 0; i < 27; i++) {
            if (special.contains(i)) {
                assertThat(grid.getSectionList().get(i).getSquareList()).isNotEmpty();
            } else {
                assertThat(grid.getSectionList().get(i).getSquareList()).isEmpty();
            }
        }

        Grid clone = null;
        try {
            clone = (Grid) grid.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assert clone != null;

        for (int i = 0; i < 27; i++) {
            if (special.contains(i)) {
                assertThat(clone.getSectionList().get(i).getSquareList()).isNotEmpty();
            } else {
                assertThat(clone.getSectionList().get(i).getSquareList()).isEmpty();
            }
        }
    }
}
