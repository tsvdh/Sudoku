package utilities;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid();
    }

    @AfterEach
    void tearDown() {
        grid = null;
    }
}
