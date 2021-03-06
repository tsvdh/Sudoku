package sudoku.core.solving;

import sudoku.core.structure.Grid;
import sudoku.core.structure.Section;
import sudoku.core.structure.Square;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public abstract class GridSolver extends Observable {

    private Grid grid;
    private Iterator<Square> iterator;
    private boolean gridChanged;
    private boolean success;

    GridSolver(Grid grid) {
        this.grid = grid;
        if (!filledIn()) {
            throw new IllegalArgumentException("Not all sections of given grid are completely filled.");
        }

        this.gridChanged = false;
        resetIterator();
        success = true;
    }

    Iterator<Square> getIterator() {
        return iterator;
    }

    boolean getSuccess() {
        return success;
    }

    public abstract void solve();

    abstract void updateNextSquare();


    void resetIterator() {
        this.iterator = grid.getSquareList().iterator();
        gridChanged = false;
    }

    private void removeDeprecatedOptions(Square thisSquare) {
        List<Section> sections = grid.getSectionsContaining(thisSquare);

        for (Section section : sections) {
            for (Square otherSquare : section.getSquareList()) {
                if (otherSquare.hasValue() && thisSquare.hasNoValue()) {

                    removeOption(thisSquare, otherSquare.getValue());
                }
            }
        }
    }

    boolean isSolved() {
        for (Square square : grid.getSquareList()) {
            if (square.hasNoValue()) {
                return false;
            }
        }
        return true;
    }

    boolean stuck() {
        return !gridChanged;
    }

    private void removeOption(Square square, Integer value) {
        boolean changed = square.removeOption(value);
        if (changed) {
            gridChanged = true;
        }
    }

    private void checkOptionOccursOnce(Square thisSquare) {

        List<Section> sections = grid.getSectionsContaining(thisSquare);
        List<Integer> allOptions = new LinkedList<>();

        for (Section section : sections) {

            for (Square otherSquare : section.getSquareList()) {
                if (!otherSquare.equals(thisSquare) && otherSquare.hasNoValue()) {

                    allOptions.addAll(otherSquare.getOptions());
                }
            }

            for (Integer integer : thisSquare.getOptions()) {
                if (!allOptions.contains(integer)) {

                    List<Integer> optionsToRemove = new LinkedList<>(thisSquare.getOptions());
                    optionsToRemove.remove(integer);

                    for (Integer integerToRemove : optionsToRemove) {

                        removeOption(thisSquare, integerToRemove);
                    }

                    return;
                }
            }

            allOptions.clear();
        }
    }

    void optimizeSquare(Square thisSquare) {
        removeDeprecatedOptions(thisSquare);

        if (thisSquare.hasNoValue()) {
            checkOptionOccursOnce(thisSquare);
        }
    }

    void determinePair() {
        Square pairSquare = null;
        for (Square square : grid.getSquareList()) {
            if (square.isPair()) {
                pairSquare = square;
                break;
            }
        }

        if (pairSquare == null) {
            success = false;
            return;
        }

        Grid clone = null;
        try {
            clone = (Grid) grid.clone();

        } catch (CloneNotSupportedException e) {
            System.out.println(e.getMessage());
        }
        assert clone != null;

        int option1 = pairSquare.getOptions().get(0);
        int option2 = pairSquare.getOptions().get(1);

        clone.getEquivalent(pairSquare).setValue(option1);

        IndependentGridSolver solver = new IndependentGridSolver(clone);
        solver.solve();

        if (solver.getSuccess()) {
            if (solver.isComplete()) {
                pairSquare.removeOption(option2);

            } else {
                pairSquare.removeOption(option1);
            }
        } else {
            success = false;
        }
    }

    boolean isComplete() {
        if (!isSolved()) {
            return false;
        }

        for (Section section : grid.getSectionList()) {
            List<Integer> list = new LinkedList<>();
            List<Square> squares = section.getSquareList();

            for (Square square : squares) {
                list.add(square.getValue());
            }

            if (!isCompleteList(list)) {
                return false;
            }
        }

        return true;
    }

    private boolean isCompleteList(List<Integer> list) {
        for (int i = 1; i < 10; i++) {
            if (!list.contains(i)) {
                return false;
            }
        }

        return true;
    }

    private boolean filledIn() {
        for (Section section : grid.getSectionList()) {
            if (section.getSquareList().size() != 9) {
                return false;
            }
        }

        return true;
    }
}
