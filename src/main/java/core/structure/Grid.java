package core.structure;

import core.misc.options.Mode;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class Grid extends SquareHolder implements Cloneable {

    private List<Section> sectionList;
    private Mode mode;

    private boolean filled;
    private boolean painted;

    public Grid(Mode mode) {
        reset(mode);
    }

    public void reset(Mode mode) {
        super.reset();

        this.sectionList = new LinkedList<>();
        this.mode = mode;

        this.filled = false;
        this.painted = (mode != Mode.JIGSAW);

        int upper = 27;
        if (mode == Mode.DIAGONAL) {
            upper = 29;
        }
        for (int i = 1; i <= upper; i++) {
            this.sectionList.add(new Section());
        }
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean isPainted() {
        return painted;
    }

    public void setPainted(boolean painted) {
        this.painted = painted;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public List<Section> getSectionsContaining(Square square) {
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
        this.addSquare(square, null);
    }

    public void addSquare(Square square, @Nullable Integer blockIndex) {
        if (!isJigsaw()) {
            super.addSquare(square);
            this.addToSections(square);
        }
        else {
            if (!getSquareList().contains(square)) {
                super.addSquare(square);
            }

            if (blockIndex != null) {
                this.addToSections(square, blockIndex);
            }
        }
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
            throw new IllegalStateException("This method should only be called when not in jigsaw mode.");
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
        addToSections(square, null);
    }

    // Note to self: the combination blockIndex = null and mode = jigsaw will never occur.
    private void addToSections(Square square, @Nullable Integer blockIndex) {
        int[] indexes = new int[3];
        if (mode == Mode.DIAGONAL) {
            indexes = new int[5];
        }

        indexes[0] = getHorizontalSectionIndex(square);
        indexes[1] = getVerticalSectionIndex(square);
        if (!isJigsaw()) {
            indexes[2] = getBlockSectionIndex(square);
        } else {
            assert blockIndex != null;
            indexes[2] = blockIndex + 18;
        }
        if (mode == Mode.DIAGONAL) {
            addDiagonalIndexes(square, indexes);
        }

        for (int index : indexes) {
            if (index != -1) {
                sectionList.get(index - 1).addSquare(square);
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();

        Grid newGrid = new Grid(mode);
        for (Square square : this.getSquareList()) {
            Square clone = (Square) square.clone();

            if (!isJigsaw()) {
                newGrid.addSquare(clone);
            }
            else {
                List<Section> sections = this.sectionList;
                Section[] sectionArray = sections.toArray(new Section[]{});

                boolean added = false;
                for (Section sectionContaining : this.getSectionsContaining(square)) {

                    for (int i = 18; i < sectionArray.length; i++) {
                        if (sectionArray[i] == sectionContaining) {
                            newGrid.addSquare(clone, i - 17);
                            added = true;
                            break;
                        }
                    }
                    if (added) {
                        break;
                    }
                }
            }
        }

        return newGrid;
    }

    public Square getEquivalent(Square square) {
        for (Square tempSquare : getSquareList()) {
            if (tempSquare.equals(square)) {
                return tempSquare;
            }
        }
        return null;
    }

    public boolean diagonalModeEnabledAndOnDiagonal(Square square) {
        if (this.sectionList.size() == 27) {
            return false;
        }

        Section diagonal1 = this.sectionList.get(27);
        Section diagonal2 = this.sectionList.get(28);

        return diagonal1.contains(square) || diagonal2.contains(square);
    }

    private boolean isJigsaw() {
        return this.mode == Mode.JIGSAW;
    }

    public boolean isValid() {
        /* Two cases when validating fill:
        1. Not jigsaw, so painted is always true.
        2. Jigsaw, so painted must be true so squares are in their sections.
        So painted must be true for the fill to be checked.
         */
        if (filled && painted) {
            if (!validFill()) {
                return false;
            }
        }

        // Jigsaw specific tests
        if (isJigsaw() && painted) {
            return validPaint();
        }

        return true;
    }

    private boolean validFill() {
        if (notSectionsFilled()) {
            return false;
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

        return true;
    }

    private boolean validPaint() {
        for (Section section : sectionList) {
            if (!section.isValid()) {
                return false;
            }
        }
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

    public boolean notSectionsFilled() {
        for (Section section : sectionList) {
            if (section.getSquareList().size() != 9) {
                return true;
            }
        }
        return false;
    }
}
