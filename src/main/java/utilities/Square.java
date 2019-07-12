package utilities;

import com.sun.istack.internal.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class Square extends Observable implements Cloneable {

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

    public boolean hasNoValue() {
        return this.value == null;
    }

    public List<Integer> getOptions() {
        return this.options;
    }

    public void setValue(Integer value) throws OverrideException {
        if (value == null) {
            this.value = null;
            options = this.makeOptions();

        } else {
            assert validRange(value);

            if (this.value == null) {
                options = null;
                this.value = value;
            } else {
                throw new OverrideException();
            }
        }

        setChanged();
        notifyObservers(false);
    }

    boolean removeOption(Integer option) {

        if (options.contains(option)) {

            options.remove(option);

            if (options.size() == 1) {
                value = options.get(0);
                options = null;
            }

            setChanged();
            notifyObservers(true);
            return true;
        } else {
            return false;
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();

        return new Square(this.value, this.xPosition, this.yPosition);
    }

    public boolean isPair() {
        if (value != null) {
            return false;
        } else {
            return options.size() == 2;
        }
    }
}
