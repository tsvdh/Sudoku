package utilities;

import java.util.LinkedList;
import java.util.List;

class Square {

    private Integer value;

    private List<Integer> options;

    private List<Section> sections;


    Square(Integer value) {
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

    void addSection(Section section) {
        this.sections.add(section);
    }
}
