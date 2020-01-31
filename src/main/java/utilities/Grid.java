package utilities;

import java.util.LinkedList;
import java.util.List;

public class Grid extends SquareHolder implements Cloneable {

    private List<Section> sectionList;
    private String mode;
    private int blockIndex;

    public Grid(String mode) {
        super();
        this.sectionList = new LinkedList<>();
        this.mode = mode;

        int upper = 27;
        if (mode.equals("diagonal")) {
            upper = 29;
        }
        for (int i = 1; i <= upper; i++) {
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


    public void addSquare(Square square, int blockIndex) {
        if (isJigsaw()) {
            this.blockIndex = blockIndex;
        }
        addSquare(square);
    }

    private int getHorizontalSectionIndex(Square square) {
        return square.getYPosition();
    }

    private int getVerticalSectionIndex(Square square) {
        return square.getXPosition() + 9;
    }

    private int getBlockSectionIndex(Square square) {
        if (!isJigsaw()) {
            int xCoordinate = ((square.getXPosition() - 1) / 3) + 1;
            int yCoordinate = ((square.getYPosition() - 1) / 3);

            return (xCoordinate + (yCoordinate * 3)) + 18;
        }
        else {
            return blockIndex + 18;
        }
    }

    private void addDiagonalIndexes(Square square, int[] indexes) {
        indexes[3] = -1;
        indexes[4] = -1;

        int x = square.getXPosition();
        int y = square.getYPosition();

        if (x == y) {
            indexes[3] = 28;
        }
        if (10 - x == y) {
            indexes[4] = 29;
        }
    }

    private void addToSections(Square square) {
        int[] indexes = new int[3];
        if (mode.equals("diagonal")) {
            indexes = new int[5];
        }

        indexes[0] = getHorizontalSectionIndex(square);
        indexes[1] = getVerticalSectionIndex(square);
        indexes[2] = getBlockSectionIndex(square);
        if (mode.equals("diagonal")) {
            addDiagonalIndexes(square, indexes);
        }

        for (int index : indexes) {
            if (index != -1) {
                sectionList.get(index - 1).addSquare(square);
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();

        Grid grid = new Grid(mode);
        for (Square square : this.getSquareList()) {
            Square clone = (Square) square.clone();
            grid.addSquare(clone);
        }

        return grid;
    }

    Square getEquivalent(Square square) {
        for (Square tempSquare : getSquareList()) {
            if (tempSquare.equals(square)) {
                return tempSquare;
            }
        }
        return null;
    }

    public boolean diagonalModeEnabledAndOnDiagonal(Square square) {
        if (getSectionList().size() == 27) {
            return false;
        }

        Section diagonal1 = getSectionList().get(27);
        Section diagonal2 = getSectionList().get(28);

        return diagonal1.contains(square) || diagonal2.contains(square);
    }

    private boolean isJigsaw() {
        return this.mode.equals("jigsaw");
    }

    public boolean isValid() {
        List<Section> sectionList = getSectionList();

        if (sectionList.size() < 27) {
            return false;
        }

        for (Section section : sectionList) {
            if (section.getSquareList().size() != 9) {
                return false;
            }
        }

        for (Section section : sectionList) {

            List<Integer> list = new LinkedList<>();
            for (Square square : section.getSquareList()) {
                if (square.hasValue()) {
                    list.add(square.getValue());
                }
            }

            if (!isValidList(list)) {
                return false;
            }
        }

        //check if from every square, all other squares can be reached

//        for (Section section : sectionList) {
//
//        }

        return true;
    }

    private boolean isValidList(List<Integer> list) {
        for (int i = 1; i < 10; i++) {

            list.remove(new Integer(i));
            if (list.contains(i)) {
                return false;
            }
        }

        return true;
    }
}
