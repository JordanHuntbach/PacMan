package com.jordan;

import java.util.HashMap;
import java.util.Map;

class Counter {

    private Map<Integer, ConnectionGene> connectionGeneMap;
    private Map<Integer, NodeGene> nodeGeneMap;

    private int currentInnovation = 0;

    int getInnovation() {
        return currentInnovation ++;
    }

    Counter() {
        connectionGeneMap = new HashMap<>();
        nodeGeneMap = new HashMap<>();
    }

    void addConnectionGene(ConnectionGene gene) {
        int id = gene.getInnovation();
        if (!connectionGeneMap.containsKey(id)) {
            connectionGeneMap.put(id, gene);
        }
    }

    void addNodeGene(NodeGene gene) {
        int id = gene.getId();
        if (!nodeGeneMap.containsKey(id)) {
            nodeGeneMap.put(id, gene);
        }
    }
    Map<Integer, ConnectionGene> getConnectionGenes() {
        return connectionGeneMap;
    }

    Map<Integer, NodeGene> getNodeGenes() {
        return nodeGeneMap;
    }
}
