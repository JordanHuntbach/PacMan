package com.jordan;

class NodeGene {

    enum TYPE {
        INPUT,
        OUTPUT,
        HIDDEN,
        ;
    }

    private TYPE type;
    private int id;

    NodeGene(TYPE type, int id) {
        this.type = type;
        this.id = id;
    }

    NodeGene(NodeGene gene) {
        this.type = gene.type;
        this.id = gene.id;
    }

    TYPE getType() {
        return type;
    }

    int getId() {
        return id;
    }

}
