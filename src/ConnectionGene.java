public class ConnectionGene {

    private int inNode;
    private int outNode;
    private float weight;
    private boolean expressed;
    private int innovation;

    ConnectionGene(int inNode, int outNode, float weight, boolean expressed, int innovation) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.expressed = expressed;
        this.innovation = innovation;
    }

    ConnectionGene(ConnectionGene gene) {
        this.inNode = gene.inNode;
        this.outNode = gene.outNode;
        this.weight = gene.weight;
        this.expressed = gene.expressed;
        this.innovation = gene.innovation;
    }

    public int getInNode() {
        return inNode;
    }

    public int getOutNode() {
        return outNode;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isExpressed() {
        return expressed;
    }

    public int getInnovation() {
        return innovation;
    }

    public void setExpressed(boolean expressed) {
        this.expressed = expressed;
    }

    public ConnectionGene copy() {
        return new ConnectionGene(inNode, outNode, weight, expressed, innovation);
    }
}
