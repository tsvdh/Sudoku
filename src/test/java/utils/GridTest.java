package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class GridTest {

    Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid("normal");
    }

    @AfterEach
    void tearDown() {
        grid = null;
    }
}
