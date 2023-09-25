package com.jordan;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

class MCTS {

    static int ROUNDS = 20;
    static int MAX_MOVES = 20;

    private MCTSTreeNode root;

    private Position position;

    private Random rand = new Random();

    private Deque<String> directions = new ArrayDeque<>();
    private Deque<MCTSTreeNode> nodesSelected = new ArrayDeque<>();

    private MCTSTreeNode leafNode;

    private boolean notInPlayout;

    MCTS(State gameState) {
        Position pacmanPosition = gameState.getPacmanPosition();
        position = Position.getNearestPosition(pacmanPosition.getPositionX(), pacmanPosition.getPositionY());
        initialiseTree();
        notInPlayout = true;
    }

    private void initialiseTree() {
        root = new MCTSTreeNode(null, position);
        root.expandChildren();
    }

    void selection() {
        directions.clear();
        nodesSelected.clear();

        MCTSTreeNode selected = root;
        nodesSelected.add(root);

        while (!selected.isLeaf()) {
            MCTSTreeNode next = null;
            double max = 0;
            for (MCTSTreeNode child: selected.getChildren()) {
                if (!child.leadsToDeath()) {
                    // Mean reward of child + exploration parameter * sqrt(ln(parent visits)/child visits)
                    double c = 10;

                    if (child.getVisitCount() == 0) {
                        next = child;
                        break;
                    } else {
                        double heuristic = child.getAverageScore() + c * Math.sqrt(Math.log(selected.getVisitCount()) / child.getVisitCount());
                        if (heuristic > max) {
                            max = heuristic;
                            next = child;
                        }
                    }
                }
            }
            if (next == null) {
                selected.setLeadsToDeath();
                if (selected.isRoot()) {
                    leafNode = root;
                    return;
                } else {
                    selected = selected.getParentNode();
                    nodesSelected.removeLast();
                    directions.removeLast();
                }
            } else {
                directions.add(directionToChild(selected, next));
                selected = next;
                nodesSelected.add(selected);
            }
        }

        leafNode = selected;
    }

    void expansion() {
        if (leafNode.getChildren().size() == 0) {
            leafNode.expandChildrenNoBacktrack();
        }

        MCTSTreeNode child = getRandomChild(leafNode);

        directions.add(directionToChild(leafNode, child));
        nodesSelected.add(child);
        leafNode = child;
    }

    private MCTSTreeNode getRandomChild(MCTSTreeNode parent) {
        List<MCTSTreeNode> nodes = parent.getChildren();
        return nodes.get(rand.nextInt(nodes.size()));
    }

    void backPropagation(int score) {
        notInPlayout = true;

        if (directions.size() != 0) {
            leafNode = nodesSelected.removeFirst();
            leafNode.setLeadsToDeath();
        }

        leafNode.updateStats(score);

    }

    String nextDirection() {
        if (directions.size() == 0) {
            return null;
        } else {
            nodesSelected.removeFirst();
            return directions.removeFirst();
        }
    }

    String evaluateTree() {
        MCTSTreeNode best = null;
        double bestScore = -Double.MAX_VALUE;
        for (MCTSTreeNode child : root.getChildren()) {
            if (child.getAverageScore() > bestScore) {
                bestScore = child.getAverageScore();
                best = child;
            }
        }
        assert best != null;
        return directionToChild(root, best);
    }

    private String directionToChild(MCTSTreeNode from, MCTSTreeNode to) {
        Position fromPos = from.getPosition();
        Position toPos = to.getPosition();
        if (toPos.getPositionX() > fromPos.getPositionX()) {
            return "RIGHT";
        } else if (toPos.getPositionX() < fromPos.getPositionX()) {
            return "LEFT";
        } else if (toPos.getPositionY() > fromPos.getPositionY()) {
            return "DOWN";
        } else {
            return "UP";
        }
    }

    boolean notInPlayout() {
        return notInPlayout;
    }

    void setInPlayout(boolean inPlayout) {
        this.notInPlayout = !inPlayout;
    }
}
