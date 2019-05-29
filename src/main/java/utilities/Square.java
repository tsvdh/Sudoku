package utilities;

import com.sun.istack.internal.Nullable;

import java.util.LinkedList;
import java.util.List;

public class Square {

    private Integer value;

    private List<Integer> options;

    private int xPosition;
    private int yPosition;


    public Square(@Nullable Integer value, int xPosition, int yPosition) {
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

    public Integer getValue() {
        return this.value;
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public List<Integer> getOptions() {
        return this.options;
    }

    public void setValue(Integer value) throws OverrideException {
        assert validRange(value);

        if (this.value == null) {
            options = null;
            this.value = value;
        } else {
            throw new OverrideException();
        }

    }

    void removeOption(Integer option) {
        options.remove(option);

        if (options.size() == 1) {
            value = options.get(0);
            options = null;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Square) {
            Square that = (Square) other;
            return (this.xPosition == that.xPosition && this.yPosition == that.yPosition);
        }
        return false;
    }

    boolean isFilled() {
        return (value != null);
    }

    @Override
    public String toString() {
        String string = "<Square(coordinates: (";

        string += "x: " + xPosition + ", ";
        string += "y: " + yPosition + ")";

        string += ", value: ";
        if (value == null) {
            string += "null";
        } else {
            string += value;
        }

        string += ", options: ";
        if (options == null) {
            string += "null";
        } else {
            string += options.toString();
        }

        string += ")>";
        return string;
    }
}
