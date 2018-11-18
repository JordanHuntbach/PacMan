import javax.xml.soap.Node;
import java.util.*;

public class Genome {

    private float fitness;

    private static List<Integer> tempList1 = new ArrayList<>();
    private static List<Integer> tempList2 = new ArrayList<>();

    private static Random random = new Random();

    private Map<Integer, ConnectionGene> connections;
    private Map<Integer, NodeGene> nodes;

    private float PROBABILITY_PERTURBING = 0.90f;

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

    public void addConnectionGene(ConnectionGene connectionGene, Counter innovation) {
        connections.put(connectionGene.getInnovation(), connectionGene);
        innovation.addConnectionGene(connectionGene);
    }

    public Map<Integer, ConnectionGene> getConnections() {
        return connections;
    }

    public void addNodeGene(NodeGene nodeGene, Counter innovation) {
        nodes.put(nodeGene.getId(), nodeGene);
        innovation.addNodeGene(nodeGene);
    }

    public Map<Integer, NodeGene> getNodes() {
        return nodes;
    }

    public void addConnectionMutation(Counter innovation, int maxAttempts) {
        int tries = 0;
        boolean success = false;
        while (!success && tries < maxAttempts) {
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
            }

            if(connectionImpossible) {
                tries ++;
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

            if(connectionExists) {
                tries ++;
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

                float weight = random.nextFloat() * 2 - 1;
                ConnectionGene newConnection = new ConnectionGene(reversed ? node1.getId() : node2.getId(), reversed ? node2.getId() : node1.getId(), weight, true, innovationNumber);
                addConnectionGene(newConnection, innovation);
                success = true;
            }
        }
    }

    public void mutation() {
        for (ConnectionGene connection : connections.values()) {
            if (random.nextFloat() < PROBABILITY_PERTURBING) {
                connection.setWeight(connection.getWeight() * (random.nextFloat() * 4f - 2f));
            } else {
                connection.setWeight(random.nextFloat() * 4f - 2f);
            }
        }
    }

    // TODO: Can check if the node exists by looking for connections that go from inNode -> X -> outNode, where no other connections go to/from X.
    public void addNodeMutation(Counter nodeInnovation, Counter connectionInnovation) {
        List<ConnectionGene> connectionsList = new ArrayList<>(connections.values());
        int index = random.nextInt(connectionsList.size());
        ConnectionGene connection = connectionsList.get(index);

        NodeGene inNode = nodes.get(connection.getInNode());
        NodeGene outNode = nodes.get(connection.getOutNode());

        connection.setExpressed(false);

        NodeGene newNode = new NodeGene(NodeGene.TYPE.HIDDEN, nodeInnovation.getInnovation());
        ConnectionGene inToNew = new ConnectionGene(inNode.getId(), newNode.getId(), 1f, true, connectionInnovation.getInnovation());
        ConnectionGene newToOut = new ConnectionGene(newNode.getId(), outNode.getId(), connection.getWeight(), true, connectionInnovation.getInnovation());

        addNodeGene(newNode, nodeInnovation);
        addConnectionGene(inToNew, connectionInnovation);
        addConnectionGene(newToOut, connectionInnovation);
    }

    // Note that parent1 must be the fitter parent.
    public static Genome crossover(Genome parent1, Genome parent2, Counter nodeInnovation, Counter counterInnovation) {
        Genome child = new Genome();

        for (NodeGene parent1Node : parent1.getNodes().values()) {
            child.addNodeGene(parent1Node, nodeInnovation);
        }

        for (ConnectionGene parent1connection : parent1.getConnections().values()) {
            if (parent2.getConnections().containsKey(parent1connection.getInnovation())) {
                ConnectionGene childConnectionGene = random.nextBoolean() ? parent1connection.copy() : parent2.getConnections().get(parent1connection.getInnovation()).copy();
                child.addConnectionGene(childConnectionGene, counterInnovation);
            } else {
                child.addConnectionGene(parent1connection.copy(), counterInnovation);
            }
        }

        return child;
    }

    public static float compatibilityDistance(Genome genome1, Genome genome2, float c1, float c2, float c3) {
        int excessGenes = countExcessGenes(genome1, genome2);
        int disjointGenes = countDisjointGenes(genome1, genome2);
        float averageWeightDifference = averageWeightDifference(genome1, genome2);

        return c1 * excessGenes + c2 * disjointGenes + c3 * averageWeightDifference;
    }

    public static int countMatchingGenes(Genome genome1, Genome genome2) {
        int matchingGenes = 0;

        List<Integer> nodeKeys1 = asSortedList(genome1.getNodes().keySet(), tempList1);
        List<Integer> nodeKeys2 = asSortedList(genome2.getNodes().keySet(), tempList2);

        int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
        int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
        int indices = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            NodeGene node1 = genome1.getNodes().get(i);
            NodeGene node2 = genome2.getNodes().get(i);

            if (node1 != null && node2 != null) {
                matchingGenes ++;
            }
        }

        List<Integer> connectionKeys1 = asSortedList(genome1.getConnections().keySet(), tempList1);
        List<Integer> connectionKeys2 = asSortedList(genome2.getConnections().keySet(), tempList2);

        highestInnovation1 = connectionKeys1.get(connectionKeys1.size() - 1);
        highestInnovation2 = connectionKeys2.get(connectionKeys2.size() - 1);
        indices = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            ConnectionGene connection1 = genome1.getConnections().get(i);
            ConnectionGene connection2 = genome2.getConnections().get(i);

            if (connection1 != null && connection2 != null) {
                matchingGenes ++;
            }
        }

        return matchingGenes;
    }

    private static float averageWeightDifference(Genome genome1, Genome genome2) {
        int matchingGenes = 0;
        float weightDifference = 0;

        List<Integer> connectionKeys1 = asSortedList(genome1.getConnections().keySet(), tempList1);
        List<Integer> connectionKeys2 = asSortedList(genome2.getConnections().keySet(), tempList2);

        int highestInnovation1 = connectionKeys1.get(connectionKeys1.size() - 1);
        int highestInnovation2 = connectionKeys2.get(connectionKeys2.size() - 1);
        int indices = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            ConnectionGene connection1 = genome1.getConnections().get(i);
            ConnectionGene connection2 = genome2.getConnections().get(i);

            if (connection1 != null && connection2 != null) {
                matchingGenes ++;
                weightDifference += Math.abs(connection1.getWeight() - connection2.getWeight());
            }
        }

        return weightDifference / matchingGenes;
    }

    private static int countDisjointGenes(Genome genome1, Genome genome2) {
        int disjointGenes = 0;

        List<Integer> nodeKeys1 = asSortedList(genome1.getNodes().keySet(), tempList1);
        List<Integer> nodeKeys2 = asSortedList(genome2.getNodes().keySet(), tempList2);

        int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
        int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
        int indices = Math.min(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            NodeGene node1 = genome1.getNodes().get(i);
            NodeGene node2 = genome2.getNodes().get(i);

            if (node1 == null && node2 != null) {
                disjointGenes ++;
            } else if (node2 == null && node1 != null) {
                disjointGenes ++;
            }
        }

        List<Integer> connectionKeys1 = asSortedList(genome1.getConnections().keySet(), tempList1);
        List<Integer> connectionKeys2 = asSortedList(genome2.getConnections().keySet(), tempList2);

        highestInnovation1 = connectionKeys1.get(connectionKeys1.size() - 1);
        highestInnovation2 = connectionKeys2.get(connectionKeys2.size() - 1);
        indices = Math.min(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            ConnectionGene connection1 = genome1.getConnections().get(i);
            ConnectionGene connection2 = genome2.getConnections().get(i);

            if (connection1 == null && connection2 != null) {
                disjointGenes ++;
            } else if (connection2 == null && connection1 != null) {
                disjointGenes ++;
            }
        }

        return disjointGenes;
    }

    private static int countExcessGenes(Genome genome1, Genome genome2) {
        int excessGenes = 0;

        List<Integer> nodeKeys1 = asSortedList(genome1.getNodes().keySet(), tempList1);
        List<Integer> nodeKeys2 = asSortedList(genome2.getNodes().keySet(), tempList2);

        int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
        int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);

        if (highestInnovation1 >= highestInnovation2) {
            for (int i = highestInnovation2; i <= highestInnovation1; i++) {
                if (genome1.getNodes().get(i) != null) {
                    excessGenes ++;
                }
            }
        } else {
            for (int i = highestInnovation1; i <= highestInnovation2; i++) {
                if (genome2.getNodes().get(i) != null) {
                    excessGenes ++;
                }
            }
        }

        List<Integer> connectionKeys1 = asSortedList(genome1.getConnections().keySet(), tempList1);
        List<Integer> connectionKeys2 = asSortedList(genome2.getConnections().keySet(), tempList2);

        highestInnovation1 = connectionKeys1.get(connectionKeys1.size() - 1);
        highestInnovation2 = connectionKeys2.get(connectionKeys2.size() - 1);

        if (highestInnovation1 >= highestInnovation2) {
            for (int i = highestInnovation2; i <= highestInnovation1; i++) {
                if (genome1.getConnections().get(i) != null) {
                    excessGenes ++;
                }
            }
        } else {
            for (int i = highestInnovation1; i <= highestInnovation2; i++) {
                if (genome2.getConnections().get(i) != null) {
                    excessGenes ++;
                }
            }
        }

        return excessGenes;
    }

    // Sorts in ascending order.
    private static List<Integer> asSortedList(Collection<Integer> c, List<Integer> list) {
        list.clear();
        list.addAll(c);
        java.util.Collections.sort(list);
        return list;
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }
}
