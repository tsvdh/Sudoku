package utilities;

import java.util.LinkedList;
import java.util.List;

public class Grid extends SquareHolder implements Cloneable {

    private List<Section> sectionList;

    public Grid() {
        super();
        this.sectionList = new LinkedList<>();
        for (int i = 1; i <= 27; i++) {
            this.sectionList.add(new Section());
        }
    }

    List<Section> getSectionList() {
        return sectionList;
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

    @Override
    public void addSquare(Square square) {
        super.addSquare(square);
        this.addToSections(square);
    }

    private int getHorizontalSectionIndex(Square square) {
        return square.getYPosition();
    }

    private int getVerticalSectionIndex(Square square) {
        return square.getXPosition() + 9;
    }

    private int getBlockSectionIndex(Square square) {
        int xCoordinate = ((square.getXPosition() - 1) / 3) + 1;
        int yCoordinate = ((square.getYPosition() - 1) / 3);

        return (xCoordinate + (yCoordinate * 3)) + 18;
    }

    private void addToSections(Square square) {
        int[] indexes = new int[3];
        indexes[0] = getHorizontalSectionIndex(square);
        indexes[1] = getVerticalSectionIndex(square);
        indexes[2] = getBlockSectionIndex(square);

        for (int index : indexes) {
            sectionList.get(index - 1).addSquare(square);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();

        Grid grid = new Grid();
        for (Square square : this.getSquareList()) {
            Square clone = (Square) square.clone();
            grid.addSquare(clone);
        }

        return grid;
    }
}
