package utils;

import utils.SettingsPossibilities.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
}
