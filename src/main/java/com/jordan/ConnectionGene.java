package com.jordan;

class ConnectionGene {

    private final int inNode;
    private final int outNode;
    private float weight;
    private boolean expressed;
    private final int innovation;

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

    int getInNode() {
        return inNode;
    }

    int getOutNode() {
        return outNode;
    }

    float getWeight() {
        return weight;
    }

    void setWeight(float weight) {
        this.weight = weight;
    }

    boolean isExpressed() {
        return expressed;
    }

    int getInnovation() {
        return innovation;
    }

    void setExpressed(boolean expressed) {
        this.expressed = expressed;
    }

    boolean sameConnection(ConnectionGene connectionGene) {
        return connectionGene.getInNode() == this.inNode && connectionGene.getOutNode() == this.outNode;
    }

}
