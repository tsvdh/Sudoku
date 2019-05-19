package utilities;

import com.sun.istack.internal.Nullable;

import java.util.LinkedList;
import java.util.List;

class Square {

    private Integer value;

    private List<Integer> options;

    private int xPosition;
    private int yPosition;


    Square(@Nullable Integer value, int xPosition, int yPosition) {
        if (value != null) {
            assert validRange(value);
        }
        assert validRange(xPosition);
        assert validRange(yPosition);


        this.xPosition = xPosition;
        this.yPosition = yPosition;

        this.value = value;

        if (value != null) {
            this.options = null;
        } else {
            this.options = this.makeOptions();
        }
    }

    private List<Integer> makeOptions() {
        List<Integer> options = new LinkedList<>();

        for (int i = 1; i < 10; i++) {
            options.add(i);
        }

        return options;
    }

    Integer getValue() {
        return this.value;
    }

    void removeOption(Integer option) {
        this.options.remove(option);

        if (this.options.size() == 1) {
            this.value = this.options.get(0);
        }
    }

    int getXPosition() {
        return xPosition;
    }

    int getYPosition() {
        return yPosition;
    }

    private boolean validRange(int value) {
        return (value >= 1 && value <= 9);
    }
}
