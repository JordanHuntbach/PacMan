import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class Evaluator {
    private int populationSize;

    private List<Genome> genomes;
    private Map<Genome, Species> speciesMap;
    private List<Species> speciesList;
    private List<Genome> nextGenerationGenomes;

    private float c1 = 1.0f;
    private float c2 = 1.0f;
    private float c3 = 0.4f;
    private float d = 6.0f;
    private float MUTATION_RATE = 0.8f;
    private float ADD_CONNECTION_RATE = 0.1f;
    private float ADD_NODE_RATE = 0.1f;
    private float CHILD_IS_PARENT_CLONE = 0.1f;

    private Counter nodeInnovation;
    private Counter connectionInnovation;

    private Random random = new Random();

    private float highestScore = 0;
    private Genome fittestGenome = null;
    private int stagnation = 0;

    private int generationNumber = 0;

    Evaluator(int populationSize, Genome startingGenome, Counter nodeInnovation, Counter connectionInnovation) {
        this.populationSize = populationSize;
        this.nodeInnovation = nodeInnovation;
        this.connectionInnovation = connectionInnovation;
        genomes = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            genomes.add(new Genome(startingGenome));
        }
        nextGenerationGenomes = new ArrayList<>();
        speciesMap = new HashMap<>();
        speciesList = new ArrayList<>();
    }

    abstract float evaluateGenome(Genome genome, int generationNumber, int memberNumber, float highestScore);

    void saveBestGenome() {
        try {
            FileWriter fileWriter = new FileWriter("bestGenome.gen");
            fileWriter.write("SCORE ACHEIVED: " + highestScore + "\n");
            for (ConnectionGene connectionGene : fittestGenome.getConnections().values()) {
                String geneAsString = connectionGene.getInnovation() + "|"
                        + connectionGene.getInNode() + "|"
                        + connectionGene.getOutNode() + "|"
                        + connectionGene.getWeight() + "|"
                        + connectionGene.isExpressed() + "\n";
                fileWriter.write(geneAsString);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initialMutate() {
        System.out.println("Mutating genomes.");
        int counter = 0;
        for (Genome genome : genomes) {
            if (counter < 10) {
                counter++;
                genome.addNodeMutation(nodeInnovation, connectionInnovation);
            }
            for (int i = 0; i < 100; i++) {
                genome.mutation();
            }
        }
    }

    void evaluate() {
        stagnation ++;
        generationNumber ++;
        speciesList.forEach(Species::reset);
        speciesMap.clear();
        nextGenerationGenomes.clear();

        System.out.println("");
        System.out.println("Allocating each genome a species.");
        for (Genome genome : genomes) {
            boolean foundSpecies = false;
            for (Species species : speciesList) {
                if (Genome.compatibilityDistance(genome, species.getMascot(), c1, c2, c3) < d) {
                    species.addMember(genome);
                    speciesMap.put(genome, species);
                    foundSpecies = true;
                    break;
                }
            }
            if (!foundSpecies) {
                Species newSpecies = new Species(genome);
                speciesList.add(newSpecies);
                speciesMap.put(genome, newSpecies);
            }
        }

        int count = 0;

        System.out.println("Evaluating " + populationSize + " genomes and assigning fitness.");
        for (Genome genome : genomes) {
            System.out.print(count + "|");
            Species species = speciesMap.get(genome);

            float score = evaluateGenome(genome, generationNumber, count++, highestScore); // Play the game
            float adjustedScore = score / species.getMembers().size();

            species.addAdjustedFitness(adjustedScore);
            genome.setFitness(adjustedScore);

            if (score > highestScore) {
                System.out.println("New highest score found! (" + score + ")");
                highestScore = score;
                fittestGenome = genome;
                stagnation = 0;
            }
        }
        System.out.println("");

        if (stagnation > 30) {
            System.out.println("Stagnant population. Killing off all but 20 genomes.");

            genomes.sort(new FitnessComparator());
            Collections.reverse(genomes);
            for (int i = genomes.size() - 1; i >= 20; i--) {
                Genome genome = genomes.get(i);
                Species species = speciesMap.get(genome);
                species.removeMember(genome);
                speciesMap.remove(genome);
                genomes.remove(genome);
            }

            stagnation = 0;
        }

        System.out.println("Removing unused species.");
        speciesList.removeIf(species -> species.getMembers().isEmpty());
        System.out.println("There are currently " + speciesList.size() + " species.");

        System.out.println("Moving the best of each species to the next generation.");
        for (Species species : speciesList) {
            float bestFitness = 0;
            Genome bestGenome = null;
            for (Genome genome : species.getMembers()) {
                if (genome.getFitness() > bestFitness) {
                    bestFitness = genome.getFitness();
                    bestGenome = genome;
                }
            }
            assert bestGenome != null;
            nextGenerationGenomes.add(bestGenome);
        }

        System.out.println("Breeding " + (populationSize - nextGenerationGenomes.size()) +  " new members.");
        while (nextGenerationGenomes.size() < populationSize) {
            Species species = getFitnessBiasedSpecies();

            Genome parent1 = getFitnessBiasedGenome(species);
            Genome parent2 = getFitnessBiasedGenome(species);

            Genome child;
            if (parent1.getFitness() > parent2.getFitness()) {
                child = random.nextFloat() > CHILD_IS_PARENT_CLONE ? Genome.crossover(parent1, parent2, nodeInnovation, connectionInnovation) : new Genome(parent1);
            } else {
                child = random.nextFloat() > CHILD_IS_PARENT_CLONE ? Genome.crossover(parent2, parent1, nodeInnovation, connectionInnovation) : new Genome(parent2);
            }

            if (random.nextFloat() < MUTATION_RATE) {
                child.mutation();
            }
            if (random.nextFloat() < ADD_CONNECTION_RATE) {
                child.addConnectionMutation(connectionInnovation, 50);
            }
            if (random.nextFloat() < ADD_NODE_RATE) {
                child.addNodeMutation(nodeInnovation, connectionInnovation);
            }
            nextGenerationGenomes.add(child);
        }

        genomes = nextGenerationGenomes;
        nextGenerationGenomes = new ArrayList<>();
        System.out.println("Generation " + generationNumber + " processed. Current high score: " + highestScore + "\n");
    }

    private Species getFitnessBiasedSpecies() {
        double completeWeight = speciesList.stream().mapToDouble(Species::getTotalAdjustedFitness).sum();
        double r = Math.random() * completeWeight;
        double countWeight = 0;
        for (Species species : speciesList) {
            countWeight += species.getTotalAdjustedFitness();
            if (countWeight >= r) {
                return species;
            }
        }
        System.out.println("Couldn't get a species.");
        throw new RuntimeException("Couldn't get a species.");
    }

    private Genome getFitnessBiasedGenome(Species species) {
        double completeWeight = species.getMembers().stream().mapToDouble(Genome::getFitness).sum();
        double r = Math.random() * completeWeight;
        double countWeight = 0;
        for (Genome genome : species.getMembers()) {
            countWeight += genome.getFitness();
            if (countWeight >= r) {
                return genome;
            }
        }
        System.out.println("Couldn't get a genome.");
        throw new RuntimeException("Couldn't get a genome.");
    }

    public class Species {
        private Genome mascot;
        private List<Genome> members;
        private float totalAdjustedFitness = 0f;

        Species(Genome mascot) {
            this.mascot = mascot;
            this.members = new LinkedList<>();
            this.members.add(mascot);
        }

        Genome getMascot() {
            return mascot;
        }

        void addMember(Genome member) {
            this.members.add(member);
        }

        void removeMember(Genome member) {
            this.members.remove(member);
        }

        List<Genome> getMembers() {
            return members;
        }

        void addAdjustedFitness(float adjustedFitness) {
            this.totalAdjustedFitness += adjustedFitness;
        }

        float getTotalAdjustedFitness() {
            return totalAdjustedFitness;
        }

        private void reset() {
            this.mascot = members.get(random.nextInt(members.size()));
            members.clear();
            totalAdjustedFitness = 0f;
        }
    }

    public class FitnessComparator implements Comparator<Genome> {

        @Override
        public int compare(Genome one, Genome two) {
            return Float.compare(one.getFitness(), two.getFitness());
        }

    }
}
