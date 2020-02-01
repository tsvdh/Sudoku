package utilities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @Test
    void valid() {
        Section section = new Section();
        for (int i = 1; i < 10; i++) {
            section.addSquare(new Square(null, i, 1));
        }
        assertThat(section.isValid());
    }

    @Test
    void inValid() {
        Section section = new Section();
        section.addSquare(new Square(null, 1, 2));
        for (int i = 2; i < 10; i++) {
            section.addSquare(new Square(null, i, 1));
        }
        assertThat(section.isValid()).isFalse();
    }
}
