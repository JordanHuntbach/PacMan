public class NodeGene {

    enum TYPE {
        INPUT,
        OUTPUT,
        HIDDEN,
        ;
    }

    private TYPE type;
    private int id;

    public NodeGene(TYPE type, int id) {
        this.type = type;
        this.id = id;
    }

    public NodeGene(NodeGene gene) {
        this.type = gene.type;
        this.id = gene.id;
    }

    public TYPE getType() {
        return type;
    }

    public int getId() {
        return id;
    }

}
