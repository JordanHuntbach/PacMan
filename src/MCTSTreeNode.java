import java.util.ArrayList;
import java.util.List;

class MCTSTreeNode {
    private Position position;
    private double averageScore;
    private double maxScore;
    private int visitCount;
    private boolean root;
    private boolean leadsToDeath = false;

    private ArrayList<MCTSTreeNode> children = new ArrayList<>();
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

    public void expandChildren() {
        List<Position> neighbours = position.getNeighbours();
        for (Position neighbour : neighbours) {
            MCTSTreeNode child = new MCTSTreeNode(this, neighbour);
            children.add(child);
        }
    }

    public List<MCTSTreeNode> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void updateStats(int score) {
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

    public boolean isRoot() {
        return root;
    }

    public MCTSTreeNode getParentNode() {
        return parentNode;
    }

    public double getAverageScore(){
        return averageScore;
    }

    public double getMaxScore(){
        return maxScore;
    }

    public double getVisitCount(){
        return visitCount;
    }

    public Position getPosition() {
        return position;
    }

    public boolean leadsToDeath() {
        return leadsToDeath;
    }

    public void setLeadsToDeath(boolean leadsToDeath) {
        this.leadsToDeath = leadsToDeath;
    }
}
