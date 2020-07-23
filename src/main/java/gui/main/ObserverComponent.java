package gui.main;

import core.misc.SettingsHandler;
import core.solving.LinkedGridSolver;
import core.structure.Square;
import gui.popups.Settings;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

class ObserverComponent implements Observer {

    private Sudoku parent;

    ObserverComponent(Sudoku parent) {
        this.parent = parent;
    }

    @Override
    public void update(Observable o, Object arg) {
        String status = (String) arg;

        if (o instanceof LinkedGridSolver) {
            if (status.equals("magic")) {
                highLightFirstPair();
                return;
            }

            List<GridElement> list = getGridElements((LinkedGridSolver) o);

            GridElement currentElement = list.get(0);
            GridElement previousElement = null;
            if (list.size() > 1) {
                previousElement = list.get(1);
            }

            if (status.equals("done")) {
                parent.eventComponent.setButtonsDoneSolving();
                currentElement.revertBorderColor();
            }
            if (status.equals("working")) {
                if (previousElement != null) {
                    previousElement.revertBorderColor();
                }
                currentElement.setBorderColor("yellow");
            }
        }

        if (o instanceof Settings) {
            parent.eventComponent.setSolveButtonAction();

            parent.builderComponent.rebuildGrid();
            parent.builderComponent.setSpecificButtons();

            parent.oldMode = SettingsHandler.getInstance().getMode();
        }
    }

    private List<GridElement> getGridElements(LinkedGridSolver gridSolver) {
        LinkedList<GridElement> list = new LinkedList<>();

        Square previousSquare = gridSolver.getPreviousSquare();
        Square currentSquare = gridSolver.getCurrentSquare();

        boolean foundPrevious = false;
        for (GridElement gridElement : parent.gridElements) {

            if (!foundPrevious && gridElement.getSquare().equals(previousSquare)) {
                list.addLast(gridElement);
                foundPrevious = true;
            }
            else if (gridElement.getSquare().equals(currentSquare)) {
                list.addFirst(gridElement);
            }
        }

        return list;
    }

    private void highLightFirstPair() {
        for (GridElement gridElement : parent.gridElements) {
            Square square = gridElement.getSquare();
            if (square.isPair()) {
                gridElement.setBorderColor("red");
                break;
            }
        }
    }
}
