package com.jordan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Genome {

    private float fitness;

    private static List<Integer> tempList1 = new ArrayList<>();
    private static List<Integer> tempList2 = new ArrayList<>();

    private static Random random = new Random();

    private Map<Integer, ConnectionGene> connections;
    private Map<Integer, NodeGene> nodes;

    Genome() {
        connections = new HashMap<>();
        nodes = new HashMap<>();
    }

    Genome(Genome toCopy) {
        nodes = new HashMap<>();
        connections = new HashMap<>();

        for (Integer index : toCopy.getNodes().keySet()) {
            nodes.put(index, new NodeGene(toCopy.getNodes().get(index)));
        }

        for (Integer index : toCopy.getConnections().keySet()) {
            connections.put(index, new ConnectionGene(toCopy.getConnections().get(index)));
        }
    }

    void overwriteConnections(List<ConnectionGene> connectionGenes) {
        List<Integer> inputNodes = new ArrayList<>();
        List<Integer> outputNodes = new ArrayList<>();
        List<Integer> hiddenNodes = new ArrayList<>();

        for (Integer id : nodes.keySet()) {
            if (nodes.get(id).getType() == NodeGene.TYPE.INPUT) {
                inputNodes.add(id);
            } else if (nodes.get(id).getType() == NodeGene.TYPE.OUTPUT) {
                outputNodes.add(id);
            }
        }

        // Overwrite the values of the existing connection genes.
        for (ConnectionGene existingConnectionGene : connections.values()) {
            for (ConnectionGene otherConnectionGene : connectionGenes) {
                if (existingConnectionGene.sameConnection(otherConnectionGene)) {
                    existingConnectionGene.setExpressed(otherConnectionGene.isExpressed());
                    existingConnectionGene.setWeight(otherConnectionGene.getWeight());

                    connectionGenes.remove(otherConnectionGene);
                    break;
                }
            }
        }

        // Create new connection genes to match those passed in.
        for (ConnectionGene connectionGene : connectionGenes) {
            int inNode = connectionGene.getInNode();
            int outNode = connectionGene.getOutNode();

            if (!inputNodes.contains(inNode) && !hiddenNodes.contains(inNode)) {
                nodes.put(inNode, new NodeGene(NodeGene.TYPE.HIDDEN, inNode));
                hiddenNodes.add(inNode);
            }
            if (!outputNodes.contains(outNode) && !hiddenNodes.contains(outNode)) {
                nodes.put(outNode, new NodeGene(NodeGene.TYPE.HIDDEN, outNode));
                hiddenNodes.add(outNode);
            }

            connections.put(connections.size(), connectionGene);
        }
    }

    void addConnectionGene(ConnectionGene connectionGene, Counter innovation) {
        connections.put(connectionGene.getInnovation(), connectionGene);
        innovation.addConnectionGene(connectionGene);
    }

    Map<Integer, ConnectionGene> getConnections() {
        return connections;
    }

    void addNodeGene(NodeGene nodeGene, Counter innovation) {
        nodes.put(nodeGene.getId(), nodeGene);
        innovation.addNodeGene(nodeGene);
    }

    Map<Integer, NodeGene> getNodes() {
        return nodes;
    }

    void addConnectionMutation(Counter innovation) {
        int tries = 0;
        boolean success = false;
        while (!success && tries < 200) {
            List<NodeGene> nodesList = new ArrayList<>(nodes.values());

            NodeGene node1 = nodesList.get(random.nextInt(nodesList.size()));
            NodeGene node2 = nodesList.get(random.nextInt(nodesList.size()));

            boolean reversed = false;
            boolean connectionImpossible = false;
            if (node1.getType() == NodeGene.TYPE.HIDDEN && node2.getType() == NodeGene.TYPE.INPUT) {
                reversed = true;
            } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.HIDDEN) {
                reversed = true;
            } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.INPUT) {
                reversed = true;
            } else if (node1.getType() == NodeGene.TYPE.INPUT && node2.getType() == NodeGene.TYPE.INPUT) {
                connectionImpossible = true;
            } else if (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.OUTPUT) {
                connectionImpossible = true;
            } else if (node1 == node2) {
                connectionImpossible = true;
            }

            if (reversed) {
                NodeGene temp = node1;
                node1 = node2;
                node2 = temp;
            }

            // Check for circular structures.
            if (!connectionImpossible && node2.getType() != NodeGene.TYPE.OUTPUT) {
                List<Integer> needsChecking = new LinkedList<>();   // List of nodes that should have their connections checked.
                List<Integer> nodeIDs = new LinkedList<>();        // List of nodes that require node2's result.
                for (Integer connectionID : connections.keySet()) {
                    ConnectionGene connectionGene = connections.get(connectionID);
                    if (connectionGene.getInNode() == node2.getId()) {  // Connection comes from node2
                        nodeIDs.add(connectionGene.getOutNode());
                        needsChecking.add(connectionGene.getOutNode());
                    }
                }
                while (!needsChecking.isEmpty()) {
                    int nodeID = needsChecking.get(0);
                    for (Integer connectionID : connections.keySet()) {
                        ConnectionGene connectionGene = connections.get(connectionID);
                        if (connectionGene.getInNode() == nodeID) { // The connection comes from the needsChecking node.
                            nodeIDs.add(connectionGene.getOutNode());
                            needsChecking.add(connectionGene.getOutNode());
                        }
                    }
                    needsChecking.remove(0);
                }
                for (Integer i : nodeIDs) {     // Loop through dependent nodes
                    if (i == node1.getId()) {   // If we make it here, then node1 calculation is dependent on node2.
                        connectionImpossible = true;
                        break;
                    }
                }
            }

            if (connectionImpossible) {
                tries++;
                continue;
            }

            boolean connectionExists = false;
            for (ConnectionGene connection : connections.values()) {
                if (connection.getInNode() == node1.getId() && connection.getOutNode() == node2.getId()) {
                    connectionExists = true;
                    break;
                } else if (connection.getInNode() == node2.getId() && connection.getOutNode() == node1.getId()) {
                    connectionExists = true;
                    break;
                }
            }

            if (connectionExists) {
                tries++;
            } else {
                int innovationNumber = -1;

                for (ConnectionGene connectionGene : innovation.getConnectionGenes().values()) {
                    if (connectionGene.getInNode() == node1.getId() && connectionGene.getOutNode() == node2.getId()) {
                        innovationNumber = connectionGene.getInnovation();
                        break;
                    } else if (connectionGene.getInNode() == node2.getId() && connectionGene.getOutNode() == node1.getId()) {
                        innovationNumber = connectionGene.getInnovation();
                        break;
                    }
                }

                if (innovationNumber == -1) {
                    innovationNumber = innovation.getInnovation();
                }

                float weight = random.nextFloat() * 2f - 1f;
                ConnectionGene newConnection = new ConnectionGene(node1.getId(), node2.getId(), weight, true, innovationNumber);
                addConnectionGene(newConnection, innovation);
                success = true;
            }
        }
    }

    void mutation() {
        for (ConnectionGene connection : connections.values()) {
            float decider = random.nextFloat();

            float PROBABILITY_OF_MUTATION = 0.50f;
            float PROBABILITY_PERTURBING = 0.90f;

            if (decider < PROBABILITY_OF_MUTATION) {
                if (random.nextFloat() < PROBABILITY_PERTURBING) {
                    connection.setWeight(connection.getWeight() * (random.nextFloat() * 4f - 2f));
                } else {
                    connection.setWeight(random.nextFloat() * 4f - 2f);
                }
            }
        }
    }

    void addNodeMutation(Counter nodeInnovation, Counter connectionInnovation) {
        List<ConnectionGene> connectionsList = new ArrayList<>(connections.values());
        if (connectionsList.size() == 0) {
            return;
        }
        int index = random.nextInt(connectionsList.size());
        ConnectionGene connection = connectionsList.get(index);
        connection.setExpressed(false);

        int inNodeID = connection.getInNode();
        int outNodeID = connection.getOutNode();

        NodeGene inNode = nodes.get(inNodeID);
        NodeGene outNode = nodes.get(outNodeID);

        // Get the list of node IDs, X, that inNode connects to only once.
        List<Integer> outIDs = new ArrayList<>();
        List<Integer> excluded = new ArrayList<>();
        for (ConnectionGene connectionGene : connectionInnovation.getConnectionGenes().values()) {
            int in = connectionGene.getInNode();
            int out = connectionGene.getOutNode();
            if (in == inNodeID && !excluded.contains(out)) {
                if (outIDs.contains(out)) {
                    outIDs.remove(outIDs.indexOf(out));
                    excluded.add(out);
                } else {
                    outIDs.add(out);
                }
            }
        }

        // Get the list of node IDs that the X nodes connect to.
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (Integer node : outIDs) {
            map.put(node, new ArrayList<>());
        }
        for (ConnectionGene connectionGene : connectionInnovation.getConnectionGenes().values()) {
            int in = connectionGene.getInNode();
            if (outIDs.contains(in)) {
                map.get(in).add(connectionGene.getOutNode());
            }
        }

        // If there is an X that links to a single other node, which is the outNode from before, that X is the newNode..
        NodeGene newNode = null;
        for (Integer integer : map.keySet()) {
            List<Integer> list = map.get(integer);
            if (list.size() == 1 && list.get(0) == outNodeID) {
                newNode = nodeInnovation.getNodeGenes().get(integer);
                break;
            }
        }

        // ..otherwise, make a new node.
        if (newNode == null) {
            newNode = new NodeGene(NodeGene.TYPE.HIDDEN, nodeInnovation.getInnovation());
        }

        // Make the new connections to this node.
        ConnectionGene inToNew = new ConnectionGene(inNode.getId(), newNode.getId(), 1f, true, connectionInnovation.getInnovation());
        ConnectionGene newToOut = new ConnectionGene(newNode.getId(), outNode.getId(), connection.getWeight(), true, connectionInnovation.getInnovation());

        // Add the new node and connections to the genome.
        addNodeGene(newNode, nodeInnovation);
        addConnectionGene(inToNew, connectionInnovation);
        addConnectionGene(newToOut, connectionInnovation);
    }

    // Note that parent1 must be the fitter parent.
    static Genome crossover(Genome parent1, Genome parent2, Counter nodeInnovation, Counter counterInnovation) {
        Genome child = new Genome();

        for (NodeGene parent1Node : parent1.getNodes().values()) {
            child.addNodeGene(parent1Node, nodeInnovation);
        }

        for (ConnectionGene parent1connection : parent1.getConnections().values()) {
            if (parent2.getConnections().containsKey(parent1connection.getInnovation())) {
                ConnectionGene parent2connection = parent2.getConnections().get(parent1connection.getInnovation());
                ConnectionGene childConnectionGene = random.nextBoolean() ? new ConnectionGene(parent1connection) : new ConnectionGene(parent2connection);

                boolean disabled = !parent1connection.isExpressed() || !parent2connection.isExpressed();
                if (disabled && random.nextFloat() > 0.3f) {
                    childConnectionGene.setExpressed(false);
                }

                child.addConnectionGene(childConnectionGene, counterInnovation);
            } else {
                child.addConnectionGene(new ConnectionGene(parent1connection), counterInnovation);
            }
        }

        return child;
    }

    static float compatibilityDistance(Genome genome1, Genome genome2, float c1, float c2, float c3) {
        int disjointGenes = 0;
        int matchingGenes = 0;
        float weightDifference = 0;
        int excessGenes = 0;

        List<Integer> nodeKeys1 = asSortedList(genome1.getNodes().keySet(), tempList1);
        List<Integer> nodeKeys2 = asSortedList(genome2.getNodes().keySet(), tempList2);

        int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
        int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
        int lower = Math.min(highestInnovation1, highestInnovation2);
        int higher = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= lower; i++) {
            NodeGene node1 = genome1.getNodes().get(i);
            NodeGene node2 = genome2.getNodes().get(i);

            if (node1 == null && node2 != null) {
                disjointGenes++;
            } else if (node2 == null && node1 != null) {
                disjointGenes++;
            }
        }
        for (int i = lower; i <= higher; i++) {
            if (genome1.getNodes().get(i) != null || genome2.getNodes().get(i) != null) {
                excessGenes++;
            }
        }

        List<Integer> connectionKeys1 = asSortedList(genome1.getConnections().keySet(), tempList1);
        List<Integer> connectionKeys2 = asSortedList(genome2.getConnections().keySet(), tempList2);

        highestInnovation1 = connectionKeys1.get(connectionKeys1.size() - 1);
        highestInnovation2 = connectionKeys2.get(connectionKeys2.size() - 1);
        lower = Math.min(highestInnovation1, highestInnovation2);
        higher = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= lower; i++) {
            ConnectionGene connection1 = genome1.getConnections().get(i);
            ConnectionGene connection2 = genome2.getConnections().get(i);

            if (connection1 == null && connection2 != null) {
                disjointGenes++;
            } else if (connection2 == null && connection1 != null) {
                disjointGenes++;
            } else if (connection1 != null) {
                matchingGenes++;
                weightDifference += Math.abs(connection1.getWeight() - connection2.getWeight());
            }
        }
        for (int i = lower; i <= higher; i++) {
            if (genome1.getConnections().get(i) != null || genome2.getConnections().get(i) != null) {
                excessGenes++;
            }
        }

        float averageWeightDifference = weightDifference / matchingGenes;

        int genome1Genes = genome1.getNodes().size() + genome1.getConnections().size();
        int genome2Genes = genome2.getNodes().size() + genome2.getConnections().size();
        int n = Math.max(genome1Genes, genome2Genes);

        return (c1 * excessGenes / n) + (c2 * disjointGenes / n) + (c3 * averageWeightDifference);
    }

    // Sorts in ascending order.
    private static List<Integer> asSortedList(Collection<Integer> c, List<Integer> list) {
        list.clear();
        list.addAll(c);
        java.util.Collections.sort(list);
        return list;
    }

    float getFitness() {
        return fitness;
    }

    void setFitness(float fitness) {
        this.fitness = fitness;
    }
}
