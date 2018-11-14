import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class MCTS {

    public static int ROUNDS = 25;
    public static int MAX_MOVES = 15;

    private MCTSTreeNode root;

    private Position position;

    private Random rand = new Random();

    private Deque<String> directions = new ArrayDeque<>();

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

    private MCTSTreeNode getRandomChild(MCTSTreeNode parent) {
        List<MCTSTreeNode> nodes = parent.getChildren();
        MCTSTreeNode selected = nodes.get(rand.nextInt(nodes.size()));
        directions.add(directionToChild(parent, selected));
        return selected;
    }

    public void selection() {

        MCTSTreeNode selected = root;

        while (!selected.isLeaf()) {
            MCTSTreeNode next = null;
            double max = 0;
            for (MCTSTreeNode child: selected.getChildren()) {
                // Mean reward of child + exploration parameter * sqrt(ln(parent visits)/child visits)
                double c = 10;

                if (child.getVisitCount() == 0) {
                    directions.add(directionToChild(selected, child));
                    leafNode = child;
                    return;
                } else {
                    double heuristic = child.getAverageScore() + c * Math.sqrt(Math.log(selected.getVisitCount())/child.getVisitCount());
                    if (heuristic > max) {
                        max = heuristic;
                        next = child;
                    }
                }
            }
            assert next != null;
            directions.add(directionToChild(selected, next));
            selected = next;
        }

        leafNode = selected;
    }

    public void expansion() {
        leafNode.expandChildren();
        leafNode = getRandomChild(leafNode);
    }

    public void backPropagation(int score) {
        leafNode.updateStats(score);
        notInPlayout = true;
    }

    public String nextDirection() {
        if (directions.size() == 0) {
            return null;
        } else {
            return directions.removeFirst();
        }
    }

    public String evaluateTree() {
        MCTSTreeNode best = null;
        double bestScore = 0;
        for (MCTSTreeNode child : root.getChildren()) {
            if (child.getMaxScore() > bestScore) {
                bestScore = child.getMaxScore();
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

    public boolean notInPlayout() {
        return notInPlayout;
    }

    public void setInPlayout(boolean inPlayout) {
        this.notInPlayout = !inPlayout;
    }
}
