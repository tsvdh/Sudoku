package utilities;

import java.util.LinkedList;
import java.util.List;

class Grid extends SquareHolder {

    private List<Section> sectionList;

    Grid() {
        super();
        this.sectionList = new LinkedList<>();
    }

    List<Section> getSectionList() {
        return sectionList;
    }

    void addSection(Section section) {
        this.sectionList.add(section);
    }

    List<Section> getSectionsContaining(Square square) {
        List<Section> sectionList = new LinkedList<>();

        for (Section section : this.sectionList) {
            if (section.contains(square)) {
                sectionList.add(section);
            }
        }

        return sectionList;
    }
}
