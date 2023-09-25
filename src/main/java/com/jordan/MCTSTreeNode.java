package com.jordan;

import java.util.ArrayList;
import java.util.List;

class MCTSTreeNode {
    private final Position position;
    private double averageScore;
    private double maxScore;
    private int visitCount;
    private final boolean root;
    private boolean leadsToDeath = false;

    private final ArrayList<MCTSTreeNode> children = new ArrayList<>();
    private MCTSTreeNode parentNode;

    MCTSTreeNode(MCTSTreeNode parent, Position nodePosition) {
        position = nodePosition;
        if (parent == null) {
            root = true;
        } else {
            root = false;
            parentNode = parent;
        }
    }

    void expandChildren() {
        List<Position> neighbours = position.getNeighbours();
        for (Position neighbour : neighbours) {
            MCTSTreeNode child = new MCTSTreeNode(this, neighbour);
            children.add(child);
        }
    }

    void expandChildrenNoBacktrack() {
        List<Position> neighbours = position.getNeighbours();
        for (Position neighbour : neighbours) {
            if (!this.parentNode.getPosition().equals(neighbour)) {
                MCTSTreeNode child = new MCTSTreeNode(this, neighbour);
                children.add(child);
            }
        }
    }

    List<MCTSTreeNode> getChildren() {
        return children;
    }

    boolean isLeaf() {
        return children.isEmpty();
    }

    void updateStats(int score) {
        double temp = averageScore * visitCount;
        visitCount += 1;
        temp += score;
        averageScore = temp / visitCount;

        if (score > maxScore) {
            maxScore = score;
        }

        if (!root) {
            parentNode.updateStats(score);
        }
    }

    boolean isRoot() {
        return root;
    }

    MCTSTreeNode getParentNode() {
        return parentNode;
    }

    double getAverageScore() {
        return averageScore;
    }

    double getMaxScore() {
        return maxScore;
    }

    double getVisitCount() {
        return visitCount;
    }

    Position getPosition() {
        return position;
    }

    boolean leadsToDeath() {
        return leadsToDeath;
    }

    void setLeadsToDeath() {
        this.leadsToDeath = true;
    }
}
