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
    private float d = 10.0f;
    private float MUTATION_RATE = 0.5f;
    private float ADD_CONNECTION_RATE = 0.1f;
    private float ADD_NODE_RATE = 0.1f;

    private Counter nodeInnovation;
    private Counter connectionInnovation;

    private Random random = new Random();

    private float highestScore = 0;
    private Genome fittestGenome = null;

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

    void initialMutate(){
        System.out.println("Mutating genomes.");
        int counter = 0;
        for (Genome genome : genomes) {
            genome.mutation();
            if (counter < 10) {
                counter++;
                genome.addNodeMutation(nodeInnovation, connectionInnovation);
            }
        }
    }

    void evaluate() {
        generationNumber ++;
        speciesList.forEach(Species::reset);
        speciesMap.clear();
        nextGenerationGenomes.clear();

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

        System.out.println("Removing unused species.");
        speciesList.removeIf(species -> species.getMembers().isEmpty());
        System.out.println("There are currently " + speciesList.size() + " species.");

        int count = 0;

        System.out.println("Evaluating " + populationSize + " genomes and assigning fitness.");
        for (Genome genome : genomes) {
            System.out.print(count + ".. ");
            Species species = speciesMap.get(genome);

            float score = evaluateGenome(genome, generationNumber, count++, highestScore); // Play the game
            float adjustedScore = score / species.getMembers().size();

            species.addAdjustedFitness(adjustedScore);
            genome.setFitness(adjustedScore);

            if (score > highestScore) {
                System.out.println("New highest score found! (" + score + ")");
                highestScore = score;
                fittestGenome = genome;
            }
        }

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
        count = 0;
        while (nextGenerationGenomes.size() < populationSize) {
            System.out.print(count++ + ".. ");
            Species species = getFitnessBiasedSpecies();

            Genome parent1 = getFitnessBiasedGenome(species);
            Genome parent2 = getFitnessBiasedGenome(species);

            Genome child;
            if (parent1.getFitness() > parent2.getFitness()) {
                child = Genome.crossover(parent1, parent2, nodeInnovation, connectionInnovation);
            } else {
                child = Genome.crossover(parent2, parent1, nodeInnovation, connectionInnovation);
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
        System.out.println("Generation " + generationNumber + " processed.\n\n");
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
}
