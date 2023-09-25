package com.jordan;

import java.util.LinkedList;
import java.util.List;

public class AStarSearch {

    /**
     * Objects in the list are ordered by their priority, determined by the object's Comparable interface.
     * The highest priority item is first in the list.
     */
    public static class PriorityList<T extends Comparable<T>> extends LinkedList<T> {

        public boolean add(T object) {
            for (int i = 0; i < size(); i++) {
                if (object.compareTo(get(i)) <= 0) {
                    add(i, object);
                    return true;
                }
            }
            addLast(object);
            return true;
        }
    }

    /**
     * Construct the path, not including the start node.
     */
    private List<Position> constructPath(Position node) {
        LinkedList<Position> path = new LinkedList<>();
        while (node.pathParent != null) {
            path.addFirst(node);
            node = node.pathParent;
        }
        return path;
    }

    /**
     * Find the path from the start node to the end node. A list of Positions is returned, or null if the path is
     * not found.
     */
    public List<Position> findPath(Position startNode, Position goalNode) {

        PriorityList<Position> openList = new PriorityList<>();
        LinkedList<Position> closedList = new LinkedList<>();

        startNode.costFromStart = 0;
        startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Position node = openList.removeFirst();
            if (node.equals(goalNode)) {
                return constructPath(node);
            }

            List<Position> neighbors = node.getNeighbours();
            for (Position neighbour : neighbors) {
                boolean isOpen = openList.contains(neighbour);
                boolean isClosed = closedList.contains(neighbour);
                float costFromStart = node.costFromStart + node.getCost(neighbour);

                // Check if the neighbor node has not been traversed, or if a shorter path to the
                // neighbor node has been found.
                if ((!isOpen && !isClosed) || costFromStart < neighbour.costFromStart) {
                    neighbour.pathParent = node;
                    neighbour.costFromStart = costFromStart;
                    neighbour.estimatedCostToGoal = neighbour.getEstimatedCost(goalNode);
                    if (isClosed) {
                        closedList.remove(neighbour);
                    }
                    if (!isOpen) {
                        openList.add(neighbour);
                    }
                }
            }
            closedList.add(node);
        }

        // No path found
        return null;
    }

    public List<Position> checkPathEnd(List<Position> path, Position target) {
        int length = path.size();
        if (length == 0 || !path.get(length - 1).equals(target)) {
            path.add(target);
        }
        return path;
    }
}