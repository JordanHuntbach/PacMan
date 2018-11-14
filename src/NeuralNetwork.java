import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NeuralNetwork {

    private Map<Integer, Neuron> neurons; // All neurons in genome, mapped by ID

    private List<Integer> input;  // IDs of input neurons
    private List<Integer> output; // IDs of output neurons

    private List<Neuron> unprocessed;

    NeuralNetwork(Genome genome) {
        input = new ArrayList<>();
        output = new ArrayList<>();
        neurons = new HashMap<>();
        unprocessed = new LinkedList<>();

        Map<Integer, NodeGene> nodes = genome.getNodes();

        for (Integer nodeID : nodes.keySet()) {
            NodeGene node = nodes.get(nodeID);
            Neuron neuron = new Neuron();
            neurons.put(nodeID, neuron);

            if (node.getType() == NodeGene.TYPE.INPUT) {
                neuron.addInputConnection();
                input.add(nodeID);
            } else if (node.getType() == NodeGene.TYPE.OUTPUT) {
                output.add(nodeID);
            }
        }

        Map<Integer, ConnectionGene> connections = genome.getConnections();

        for (Integer connectionID : connections.keySet()) {
            ConnectionGene connection = connections.get(connectionID);
            if (connection.isExpressed()) {
                // Get the neuron related to the connection's inNode, and give it an output (node) and weight.
                Neuron inputter = neurons.get(connection.getInNode());
                inputter.addOutputConnection(connection.getOutNode(), connection.getWeight());

                // Get the neuron related to the outNode, and give it an input connection.
                Neuron outputReceiver = neurons.get(connection.getOutNode());
                outputReceiver.addInputConnection();
            }
        }
    }

    public float[] calculate(float[] input_parameter) {
        if (input_parameter.length != input.size()) {
            throw new IllegalArgumentException("Number of inputs must match number of input neurons in genome.");
        }

        neurons.keySet().forEach(key -> neurons.get(key).reset());

        unprocessed.clear();
        unprocessed.addAll(neurons.values());

        for (int i = 0; i < input_parameter.length; i++) {
            // Feed inputs to the input neurons.
            Neuron inputNeuron = neurons.get(input.get(i));
            inputNeuron.feedInput(input_parameter[i]);
            inputNeuron.calculate();

            for (int j = 0; j < inputNeuron.getOutputIDs().size(); j++) {
                // Feed the result to the input neurons' connections, multiplying by respective weights.
                Neuron receiver = neurons.get(inputNeuron.getOutputIDs().get(j));
                receiver.feedInput(inputNeuron.getOutput() * inputNeuron.getOutputWeights().get(j));
            }
            unprocessed.remove(inputNeuron);
        }

        int loops = 0;
        while (unprocessed.size() > 0) {
            loops++;
            if (loops > 1000) {
                //System.out.println("Can't solve network. Giving up and returning null");
                return null;
            }

            Iterator<Neuron> it = unprocessed.iterator();
            while (it.hasNext()) {
                Neuron neuron = it.next();
                if (neuron.isReady()) {     // If neuron has all its inputs, calculate the neuron's output...
                    neuron.calculate();
                    for (int i = 0; i < neuron.getOutputIDs().size(); i++) { // ... and feed results to next neurons.
                        int receiverID = neuron.getOutputIDs().get(i);
                        float receiverValue = neuron.output * neuron.getOutputWeights().get(i);
                        neurons.get(receiverID).feedInput(receiverValue);
                    }
                    it.remove();
                }
            }
        }

        // Copy output from output neurons into array, and return it.
        float[] outputs = new float[output.size()];
        for (int i = 0; i < output.size(); i++) {
            outputs[i] = neurons.get(output.get(i)).getOutput();
        }
        return outputs;
    }

    public static class Neuron {

        private float output;
        private ArrayList<Float> inputs;

        private ArrayList<Integer> outputIDs;
        private ArrayList<Float> outputWeights;

        Neuron() {
            inputs = new ArrayList<>();
            outputIDs = new ArrayList<>();
            outputWeights = new ArrayList<>();
        }

        void addOutputConnection(int outputID, float weight) {
            outputIDs.add(outputID);
            outputWeights.add(weight);
        }

        void addInputConnection() {
            inputs.add(null);
        }

        List<Integer> getOutputIDs() {
            return outputIDs;
        }

        List<Float> getOutputWeights() {
            return outputWeights;
        }

        void calculate() {
            float sum = 0f;
            for (float f : inputs) {
                sum += f;
            }
            output = sigmoidActivationFunction(sum);
        }

        boolean isReady() {
            boolean foundNull = false;
            for (Float f : inputs) {
                if (f == null) {
                    foundNull = true;
                    break;
                }
            }
            return !foundNull;
        }

        // Adds an input to the neuron in the first slot available.
        void feedInput(float input) {
            boolean foundSlot = false;
            for (int i = 0; i < inputs.size(); i++) {
                if (inputs.get(i) == null) {
                    inputs.set(i, input);
                    foundSlot = true;
                    break;
                }
            }
            if (!foundSlot) {
                throw new RuntimeException("No input slot ready for input.");
            }
        }

        float getOutput() {
            return output;
        }

        void reset() {
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, null);
            }
            output = 0f;
        }

        // Takes any float, and returns a value between 0 and 1. An input of 0 returns 0.5
        private float sigmoidActivationFunction(float in) {
            return (float) (1f / (1f + Math.exp(-4.9d * in)));
        }
    }
}
