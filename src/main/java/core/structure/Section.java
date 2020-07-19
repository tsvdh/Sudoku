package core.structure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Section extends SquareHolder {

    boolean isValid() {
        List<Square> squares = getSquareList();
        Queue<Square> queue = new LinkedList<>();
        Collection<Square> visited = new LinkedList<>();

        queue.add(squares.get(0));
        visited.add(squares.get(0));

        while (!queue.isEmpty()) {
            Square current = queue.remove();

            for (Square other : squares) {
                if (current.isNeighbourOf(other) && !visited.contains(other)) {
                    queue.add(other);
                    visited.add(other);
                }
            }
        }

        return visited.size() == 9;
    }
}
