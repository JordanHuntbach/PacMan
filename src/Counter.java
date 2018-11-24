import java.util.HashMap;
import java.util.Map;

public class Counter {

    private Map<Integer, ConnectionGene> connectionGeneMap;
    private Map<Integer, NodeGene> nodeGeneMap;

    private int currentInnovation = 0;

    public int getInnovation() {
        return currentInnovation ++;
    }

    Counter() {
        connectionGeneMap = new HashMap<>();
        nodeGeneMap = new HashMap<>();
    }

    public void addConnectionGene(ConnectionGene gene) {
        int id = gene.getInnovation();
        if (!connectionGeneMap.containsKey(id)) {
            connectionGeneMap.put(id, gene);
        }
    }

    public void addNodeGene(NodeGene gene) {
        int id = gene.getId();
        if (!nodeGeneMap.containsKey(id)) {
            nodeGeneMap.put(id, gene);
        }
    }
    public Map<Integer, ConnectionGene> getConnectionGenes() {
        return connectionGeneMap;
    }

    public Map<Integer, NodeGene> getNodeGenes() {
        return nodeGeneMap;
    }
}
