package utilities;

import java.util.ArrayList;

class Square {

    private Integer value;

    private ArrayList<Integer> options;

    Square(Integer value) {
        this.value = value;

        if (value != null) {
            this.options = null;
        } else {
            this.options = this.makeOptions();
        }
    }

    private ArrayList<Integer> makeOptions() {
        ArrayList<Integer> options = new ArrayList<>();

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
}
