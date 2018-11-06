import java.util.LinkedList;
import java.util.List;

public class AStarSearch {

    /**
     Objects in the list are ordered by their priority, determined by the object's Comparable interface.
     The highest priority item is first in the list.
     */
    public static class PriorityList extends LinkedList {

        public void add(Comparable object) {
            for (int i=0; i<size(); i++) {
                if (object.compareTo(get(i)) <= 0) {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }
    }

    /**
     Construct the path, not including the start node.
     */
    protected List<Position> constructPath(Position node) {
        LinkedList<Position> path = new LinkedList<>();
        while (node.pathParent != null) {
            path.addFirst(node);
            node = node.pathParent;
        }
        return path;
    }

    /**
     Find the path from the start node to the end node. A list of Positions is returned, or null if the path is
     not found.
     */
    public List<Position> findPath(Position startNode, Position goalNode) {

        PriorityList openList = new PriorityList();
        LinkedList<Position> closedList = new LinkedList<>();

        startNode.costFromStart = 0;
        startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Position node = (Position) openList.removeFirst();
            if (node == goalNode) {
                return constructPath(goalNode);
            }

            List neighbors = node.getNeighbours();
            for (Object neighbour : neighbors) {
                Position neighbourNode = (Position) neighbour;
                boolean isOpen = openList.contains(neighbourNode);
                boolean isClosed = closedList.contains(neighbourNode);
                float costFromStart = node.costFromStart + node.getCost(neighbourNode);

                // Check if the neighbor node has not been traversed, or if a shorter path to the
                // neighbor node has been found.
                if ((!isOpen && !isClosed) || costFromStart < neighbourNode.costFromStart) {
                    neighbourNode.pathParent = node;
                    neighbourNode.costFromStart = costFromStart;
                    neighbourNode.estimatedCostToGoal = neighbourNode.getEstimatedCost(goalNode);
                    if (isClosed) {
                        closedList.remove(neighbourNode);
                    }
                    if (!isOpen) {
                        openList.add(neighbourNode);
                    }
                }
            }
            closedList.add(node);
        }

        // No path found
        return null;
    }

    private List<Position> positions = Position.initialisePositions();

    Position getNearestPosition(double x, double y){
        Position best = null;
        double minDistance = 1000;
        for (Position position : positions) {
            double posX = position.getPositionX();
            double posY = position.getPositionY();
            double distance = Math.abs(posX - x) + Math.abs(posY - y);
            if (distance == 0) {
                return position;
            } else if (distance < minDistance) {
                best = position;
                minDistance = distance;
            }
        }
        return best;
    }
}