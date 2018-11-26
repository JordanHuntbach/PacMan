import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.AnimationTimer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import static java.lang.Thread.sleep;

public class Game extends Application {

    /**
    Codes:
    1 = Pill
    2 = PowerPill
    3 = Horizontal Wall
    4 = Vertical Wall
    5 = Corner (Top <-> Right)
    6 = Corner (Bottom <-> Right)
    7 = Corner (Top <-> Left)
    8 = Corner (Bottom <-> Left)
    */

    private int[][] level = new int[][]{
            {6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 8, 6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 8},
            {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
            {4, 1, 6, 3, 3, 8, 1, 6, 3, 3, 3, 8, 1, 4, 4, 1, 6, 3, 3, 3, 8, 1, 6, 3, 3, 8, 1, 4},
            {4, 2, 4, 0, 0, 4, 1, 4, 0, 0, 0, 4, 1, 4, 4, 1, 4, 0, 0, 0, 4, 1, 4, 0, 0, 4, 2, 4},
            {4, 1, 5, 3, 3, 7, 1, 5, 3, 3, 3, 7, 1, 5, 7, 1, 5, 3, 3, 3, 7, 1, 5, 3, 3, 7, 1, 4},
            {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
            {4, 1, 6, 3, 3, 8, 1, 6, 8, 1, 6, 3, 3, 3, 3, 3, 3, 8, 1, 6, 8, 1, 6, 3, 3, 8, 1, 4},
            {4, 1, 5, 3, 3, 7, 1, 4, 4, 1, 5, 3, 3, 8, 6, 3, 3, 7, 1, 4, 4, 1, 5, 3, 3, 7, 1, 4},
            {4, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 4},
            {5, 3, 3, 3, 3, 8, 1, 4, 5, 3, 3, 8, 0, 4, 4, 0, 6, 3, 3, 7, 4, 1, 6, 3, 3, 3, 3, 7},
            {0, 0, 0, 0, 0, 4, 1, 4, 6, 3, 3, 7, 0, 5, 7, 0, 5, 3, 3, 8, 4, 1, 4, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 4, 1, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1, 4, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 4, 1, 4, 4, 0, 6, 3, 3, 3, 3, 3, 3, 8, 0, 4, 4, 1, 4, 0, 0, 0, 0, 0},
            {3, 3, 3, 3, 3, 7, 1, 5, 7, 0, 4, 0, 0, 0, 0, 0, 0, 4, 0, 5, 7, 1, 5, 3, 3, 3, 3, 3},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {3, 3, 3, 3, 3, 8, 1, 6, 8, 0, 4, 0, 0, 0, 0, 0, 0, 4, 0, 6, 8, 1, 6, 3, 3, 3, 3, 3},
            {0, 0, 0, 0, 0, 4, 1, 4, 4, 0, 5, 3, 3, 3, 3, 3, 3, 7, 0, 4, 4, 1, 4, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 4, 1, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1, 4, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 4, 1, 4, 4, 0, 6, 3, 3, 3, 3, 3, 3, 8, 0, 4, 4, 1, 4, 0, 0, 0, 0, 0},
            {6, 3, 3, 3, 3, 7, 1, 5, 7, 0, 5, 3, 3, 8, 6, 3, 3, 7, 0, 5, 7, 1, 5, 3, 3, 3, 3, 8},
            {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
            {4, 1, 6, 3, 3, 8, 1, 6, 3, 3, 3, 8, 1, 4, 4, 1, 6, 3, 3, 3, 8, 1, 6, 3, 3, 8, 1, 4},
            {4, 1, 5, 3, 8, 4, 1, 5, 3, 3, 3, 7, 1, 5, 7, 1, 5, 3, 3, 3, 7, 1, 4, 6, 3, 7, 1, 4},
            {4, 2, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 2, 4},
            {5, 3, 8, 1, 4, 4, 1, 6, 8, 1, 6, 3, 3, 3, 3, 3, 3, 8, 1, 6, 8, 1, 4, 4, 1, 6, 3, 7},
            {6, 3, 7, 1, 5, 7, 1, 4, 4, 1, 5, 3, 3, 8, 6, 3, 3, 7, 1, 4, 4, 1, 5, 7, 1, 5, 3, 8},
            {4, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 4},
            {4, 1, 6, 3, 3, 3, 3, 7, 5, 3, 3, 8, 1, 4, 4, 1, 6, 3, 3, 7, 5, 3, 3, 3, 3, 8, 1, 4},
            {4, 1, 5, 3, 3, 3, 3, 3, 3, 3, 3, 7, 1, 5, 7, 1, 5, 3, 3, 3, 3, 3, 3, 3, 3, 7, 1, 4},
            {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
            {5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 7}
    };

    private ArrayList<Sprite> pillsList;
    private ArrayList<Sprite> powerPillsList;
    private ArrayList<Sprite> wallsList;

    private int ghostsEaten;

    private Group root;
    private Scene scene;
    private Stage mainStage;
    private Canvas canvas;
    private GraphicsContext gc;

    private Sprite pacman;
    private ArrayList<Ghost> ghosts;
    private Ghost blinky;
    private Ghost pinky;
    private Ghost inky;
    private Ghost clyde;

    private Sprite previousMarker;
    private Sprite nextMarker;

    private Sprite blinkyMarker;
    private Sprite pinkyMarker;
    private Sprite inkyMarker;
    private Sprite clydeMarker;

    private int score = 0;
    private int lives = 2;
    private int pinkyCounter = 0;
    private int inkyCounter = 0;
    private int clydeCounter = 0;
    private int eatenCoolDown = 0;

    private int scaredCounter = -1;

    private String currentDirection;
    private String nextDirection;

    private int mouthPause = 0;
    private boolean mouthOpen = true;

    private boolean simulation = false;
    private State realState;
    private MCTS mcts;
    private int roundsCounter;
    private int movesCounter;

    private Counter nodeInnovation = new Counter();
    private Counter connectionInnovation = new Counter();
    private Evaluator evaluator;

    private boolean ai = true; // FALSE LETS YOU CONTROL PAC-MAN, TRUE LETS AI DO IT
    private boolean training = false; // TRUE HAS THE NEURAL NETWORK TRAIN
    private boolean trainWithGUI = false;
    private int populationSize = 100;
    private boolean debug = false;

    private float [] inputs = new float[26];
    private float [] nnOutputs = new float[4];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        mainStage.setTitle("Pac-Man");

        if (training) {
            if (trainWithGUI) {
                guiSetup();
            }
            setUpNN();
        } else {
            newGame();
        }
    }

    private void setUpNN() {
        evaluator = new Evaluator(populationSize, newGenome(), nodeInnovation, connectionInnovation) {
            @Override
            float evaluateGenome(Genome genome, int generation, int member, float highestScore) {
                return playGame(genome, generation, member, highestScore);
            }
        };
        evaluator.initialMutate();
        evaluator.initialMutate();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                for (int i = 0; i < 100; i++) {
                    evaluator.evaluate();
                }
                evaluator.saveBestGenome();
                return null;
            }
        };

        // Run task in new thread
        new Thread(task).start();
    }

    private Genome newGenome() {
        /* Inputs are:
           Distance / direction to each ghost
           Is ghost edible
           Is ghost moving towards Pac-Man
           Direction / distance to closest pill
           Direction / distance to closest powerPill

           Outputs are:
           Left, Right, Up, Down. Move in the direction with the greatest value.
          */

        Genome genome = new Genome();

        NodeGene blinkyActive = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene blinkyDistance = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene blinkyUpOrDown = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene blinkyLeftOrRight = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene blinkyEdible = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(blinkyActive, nodeInnovation);
        genome.addNodeGene(blinkyDistance, nodeInnovation);
        genome.addNodeGene(blinkyUpOrDown, nodeInnovation);
        genome.addNodeGene(blinkyLeftOrRight, nodeInnovation);
        genome.addNodeGene(blinkyEdible, nodeInnovation);

        NodeGene pinkyActive = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene pinkyDistance = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene pinkyUpOrDown = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene pinkyLeftOrRight = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene pinkyEdible = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(pinkyActive, nodeInnovation);
        genome.addNodeGene(pinkyDistance, nodeInnovation);
        genome.addNodeGene(pinkyUpOrDown, nodeInnovation);
        genome.addNodeGene(pinkyLeftOrRight, nodeInnovation);
        genome.addNodeGene(pinkyEdible, nodeInnovation);

        NodeGene inkyActive = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene inkyDistance = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene inkyUpOrDown = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene inkyLeftOrRight = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene inkyEdible = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(inkyActive, nodeInnovation);
        genome.addNodeGene(inkyDistance, nodeInnovation);
        genome.addNodeGene(inkyUpOrDown, nodeInnovation);
        genome.addNodeGene(inkyLeftOrRight, nodeInnovation);
        genome.addNodeGene(inkyEdible, nodeInnovation);

        NodeGene clydeActive = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene clydeDistance = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene clydeUpOrDown = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene clydeLeftOrRight = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene clydeEdible = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(clydeActive, nodeInnovation);
        genome.addNodeGene(clydeDistance, nodeInnovation);
        genome.addNodeGene(clydeUpOrDown, nodeInnovation);
        genome.addNodeGene(clydeLeftOrRight, nodeInnovation);
        genome.addNodeGene(clydeEdible, nodeInnovation);

        NodeGene distanceToClosestPill = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene closestPillUpOrDown = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene closestPillLeftOrRight = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(distanceToClosestPill, nodeInnovation);
        genome.addNodeGene(closestPillUpOrDown, nodeInnovation);
        genome.addNodeGene(closestPillLeftOrRight, nodeInnovation);

        NodeGene distanceToClosestPowerPill = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene closestPowerPillUpOrDown = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        NodeGene closestPowerPillLeftOrRight = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(distanceToClosestPowerPill, nodeInnovation);
        genome.addNodeGene(closestPowerPillUpOrDown, nodeInnovation);
        genome.addNodeGene(closestPowerPillLeftOrRight, nodeInnovation);

        NodeGene up = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        NodeGene down = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        NodeGene left = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        NodeGene right = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(up, nodeInnovation);
        genome.addNodeGene(down, nodeInnovation);
        genome.addNodeGene(left, nodeInnovation);
        genome.addNodeGene(right, nodeInnovation);

        Random random = new Random();

        for (NodeGene nodeGene : genome.getNodes().values()) {
            if (nodeGene.getType() == NodeGene.TYPE.INPUT) {
                ConnectionGene upConnection = new ConnectionGene(nodeGene.getId(), up.getId(), random.nextFloat() * 2 - 1, true, connectionInnovation.getInnovation());
                genome.addConnectionGene(upConnection, connectionInnovation);
                ConnectionGene downConnection = new ConnectionGene(nodeGene.getId(), down.getId(), random.nextFloat() * 2 - 1, true, connectionInnovation.getInnovation());
                genome.addConnectionGene(downConnection, connectionInnovation);
                ConnectionGene leftConnection = new ConnectionGene(nodeGene.getId(), left.getId(), random.nextFloat() * 2 - 1, true, connectionInnovation.getInnovation());
                genome.addConnectionGene(leftConnection, connectionInnovation);
                ConnectionGene rightConnection = new ConnectionGene(nodeGene.getId(), right.getId(), random.nextFloat() * 2 - 1, true, connectionInnovation.getInnovation());
                genome.addConnectionGene(rightConnection, connectionInnovation);
            }
        }
        return genome;
    }

    private float playGame(Genome genome, int genNumber, int memNumber, float highScore) {
        gameSetup();

        if (trainWithGUI) {
            Platform.runLater(this::refreshCanvas);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        NeuralNetwork neuralNetwork = new NeuralNetwork(genome);

        while (!pillsList.isEmpty() || !powerPillsList.isEmpty()) {

            getNextDirectionFromNN(neuralNetwork);

            updatePacman();

            eatenCoolDown += 1;

            updateGhostsWrapper();

            eatPills();

            if (trainWithGUI) {
                Platform.runLater(this::updateScreen);
                Platform.runLater(() -> trainingStats(genNumber, memNumber, highScore));

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (Ghost ghost : ghosts) {
                if (ghost.canCatch(pacman)) {
                    if (ghost.isSpooked()) {
                        ghostEaten(ghost);
                    } else if (!ghost.isEyes()) {
                        return score;
                    }
                }
            }
        }

        return score;
    }

    private void getNextDirectionFromNN(NeuralNetwork neuralNetwork){
        getInputs(inputs);
        nnOutputs = neuralNetwork.calculate(inputs); // 0=UP, 1=DOWN, 2=LEFT, 3=RIGHT

        if (nnOutputs != null) {
            float maximum = Float.MIN_VALUE;
            int direction = -1;
            for (int i = 0; i < nnOutputs.length; i++) {
                if (nnOutputs[i] > maximum) {
                    maximum = nnOutputs[i];
                    direction = i;
                }
            }
            switch (direction) {
                case 0:
                    nextDirection = "UP";
                    break;
                case 1:
                    nextDirection = "DOWN";
                    break;
                case 2:
                    nextDirection = "LEFT";
                    break;
                case 3:
                    nextDirection = "RIGHT";
                    break;
                default:
                    System.out.println("Bad evaluation.");
            }
        }
    }

    private void guiSetup() {
        root = new Group();
        scene = new Scene(root, 592, 720);
        mainStage.setScene(scene);
        mainStage.show();

        canvas = new Canvas(592,720);
        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("Helvetica", FontWeight.BOLD,24));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        if (!ai) {
            scene.setOnKeyPressed(
                    e -> nextDirection = e.getCode().toString());
        }
    }

    private void refreshCanvas() {
        root.getChildren().remove(canvas);
        canvas = new Canvas(592,720);
        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("Helvetica", FontWeight.BOLD,24));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void gameSetup() {
        pacman = new Sprite();
        currentDirection = "";
        nextDirection = "";

        resetPacman();

        pillsList = new ArrayList<>();
        powerPillsList = new ArrayList<>();
        wallsList = new ArrayList<>();
        ghosts = new ArrayList<>();

        int rowCounter = -1;
        int colCounter = -1;
        int COLUMNS = 28;

        for (int[] row : level) {
            rowCounter += 1;
            for (int code : row) {
                colCounter = (colCounter + 1) % COLUMNS;
                if(code == 1){
                    Sprite pill = new Sprite();
                    pill.setImage("Sprites/Pickups/pill.png");
                    double px = 21 + 20 * colCounter;
                    double py = 21 + 20 * rowCounter;
                    pill.setPosition(px, py);
                    pillsList.add(pill);
                } else if (code == 2) {
                    Sprite powerPill = new Sprite();
                    powerPill.setImage("Sprites/Pickups/powerPill.png");
                    double px = 13 + 20 * colCounter;
                    double py = 13 + 20 * rowCounter;
                    powerPill.setPosition(px, py);
                    powerPillsList.add(powerPill);
                } else if (3 <= code && code <= 8) {
                    Sprite wall;
                    double px = 16 + 20 * colCounter;
                    double py = 16 + 20 * rowCounter;
                    switch (code) {
                        case 3:
                            wall = new Sprite();
                            wall.setImage("Sprites/Walls/wallH.png");
                            py += 9;
                            break;
                        case 4:
                            wall = new Sprite();
                            wall.setImage("Sprites/Walls/wallV.png");
                            px += 9;
                            break;
                        case 5:
                            wall = new Corner("TR");
                            wall.setImage("Sprites/Walls/cornerTR.png");
                            px += 9;
                            break;
                        case 6:
                            wall = new Corner("BR");
                            wall.setImage("Sprites/Walls/cornerBR.png");
                            px += 9;
                            py += 9;
                            break;
                        case 7:
                            wall = new Corner("TL");
                            wall.setImage("Sprites/Walls/cornerTL.png");
                            break;
                        default:
                            wall = new Corner("BL");
                            wall.setImage("Sprites/Walls/cornerBL.png");
                            py += 9;
                            break;
                    }
                    wall.setPosition(px, py);
                    wallsList.add(wall);
                }
            }
        }

        blinky = new Ghost("blinky");
        pinky = new Ghost("pinky");
        inky = new Ghost("inky");
        clyde = new Ghost("clyde");
        ghosts.add(blinky);
        ghosts.add(pinky);
        ghosts.add(inky);
        ghosts.add(clyde);
        resetGhosts();

        previousMarker = new Sprite();
        previousMarker.setImage("Sprites/Markers/pacman.png");
        nextMarker = new Sprite();
        nextMarker.setImage("Sprites/Markers/pacman.png");

        blinkyMarker = new Sprite();
        blinkyMarker.setImage("Sprites/Markers/blinky.png");
        pinkyMarker = new Sprite();
        pinkyMarker.setImage("Sprites/Markers/pinky.png");
        inkyMarker = new Sprite();
        inkyMarker.setImage("Sprites/Markers/inky.png");
        clydeMarker = new Sprite();
        clydeMarker.setImage("Sprites/Markers/clyde.png");

        score = 0;
        lives = 2;
        pinkyCounter = 0;
        inkyCounter = 0;
        clydeCounter = 0;
        ghostsEaten = 0;
    }

    private void getInputs(float[] inputs) {
        int access = 0;

        // Current position.

        // Can turn left/right/up/down.

        for (Ghost ghost : ghosts) {
            inputs[access++] = ghost.isActive() ? 1 : -1;
            inputDistanceAndDirection(ghost, inputs, access);
            access += 3;
            inputs[access++] = ghost.isSpooked() ? 1 : -1;
        }

        // Distance + direction to closest pill.
        Sprite pill = closestPill(pillsList);
        inputDistanceAndDirection(pill, inputs, access);
        access += 3;

        // Distance + direction to closest powerPill.
        Sprite powerPill = closestPill(pillsList);
        inputDistanceAndDirection(powerPill, inputs, access);
    }

    private void inputDistanceAndDirection(Sprite sprite, float[] inputs, int access) {
        if (sprite == null) {
            inputs[access++] = 0;
            inputs[access++] = 0;
            inputs[access] = 0;
        } else {
            float distance = distanceToSprite(sprite);
            inputs[access++] = distance > 500 ? -1 : 1 - distance / 250;
            if (distance != 0) {
                double direction = Math.toRadians(directionToSprite(sprite));
                inputs[access++] = (float) Math.cos(direction);
                inputs[access] = (float) Math.sin(direction);
            } else {
                inputs[access++] = 0;
                inputs[access] = 0;
            }
        }
    }

    private Sprite closestPill(List<Sprite> pills) {
        float minDistance = Float.MAX_VALUE;
        Sprite closest = null;
        for (Sprite pill : pills) {
            float dx = (float) Math.abs(pacman.getPositionX() - pill.getPositionX());
            float dy = (float) Math.abs(pacman.getPositionY() - pill.getPositionY());
            float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closest = pill;
            }
        }
        return closest;
    }

    private float distanceToSprite(Sprite sprite) {
        float dx = (float) Math.abs(pacman.getPositionX() - sprite.getPositionX());
        float dy = (float) Math.abs(pacman.getPositionY() - sprite.getPositionY());
        float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        if (Float.isNaN(distance)) {
            System.out.println("Distance NaN");
        }
        return distance;
    }

    private double directionToSprite(Sprite sprite) {
        double x = (sprite.getPositionX() - pacman.getPositionX()); // +ve if sprite to the right
        double y = (pacman.getPositionY() - sprite.getPositionY()); // +ve if sprite above
        double angle = Math.toDegrees(Math.atan(y / x)) - 90;
        if (Double.isNaN(angle)) {
            System.out.println("Direction NaN");
        }
        return angle;
    }

//    void saveBestGenome() {
//        try {
//            FileWriter fileWriter = new FileWriter("bestGenome.gen");
//            fileWriter.write("SCORE ACHEIVED: " + highestScore + "\n");
//            for (ConnectionGene connectionGene : fittestGenome.getConnections().values()) {
//                String geneAsString = connectionGene.getInnovation() + "|"
//                        + connectionGene.getInNode() + "|"
//                        + connectionGene.getOutNode() + "|"
//                        + connectionGene.getWeight() + "|"
//                        + connectionGene.isExpressed() + "\n";
//                fileWriter.write(geneAsString);
//            }
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private Genome loadGenome() {
        ArrayList<String> lines = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("bestGenome.gen"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ConnectionGene> connectionGenes = new ArrayList<>();

        lines.remove(0);
        for (String line : lines) {
            String[] things = line.split("\\|");
            int innovation = Integer.valueOf(things[0]);
            int inNode = Integer.valueOf(things[1]);
            int outNode = Integer.valueOf(things[2]);
            float weight = Float.valueOf(things[3]);
            boolean expressed = Boolean.valueOf(things[4]);

            ConnectionGene connectionGene = new ConnectionGene(inNode, outNode, weight, expressed, innovation);
            connectionGenes.add(connectionGene);
        }

        Genome genome = newGenome();

        genome.overwriteConnections(connectionGenes);

        return genome;
    }

    private void newGame() {
        guiSetup();
        gameSetup();

        if (ai) {
            nextDirection = "LEFT";
        }

        NeuralNetwork neuralNetwork = new NeuralNetwork(loadGenome());

        new AnimationTimer() {
            public void handle(long currentNanoTime) {

                if (ai && Position.isJunction(pacman.getPositionX(), pacman.getPositionY())) {
                    if (!simulation) {
                        realState = getGameState();
                        mcts = new MCTS(realState);
                        roundsCounter = 0;
                        simulation = true;
                    }
                    if (roundsCounter <= MCTS.ROUNDS) {
                        if (mcts.notInPlayout()) {
                            mcts.selection();
                            mcts.expansion();
                            mcts.setInPlayout(true);
                            roundsCounter++;
                            movesCounter = 0;
                            for (Ghost ghost : ghosts) {
                                ghost.setSpeedUp(true);
                                adjustPosition(ghost);
                            }
                        }
                        String next = mcts.nextDirection();
                        movesCounter++;
                        if (next != null) {
                            // This is when the MCTS is still navigating the tree.
                            nextDirection = next;
                        } else if (movesCounter < MCTS.MAX_MOVES) {
                            // This is when the tree navigation is done, so the neural network is responsible for making moves.
                            getNextDirectionFromNN(neuralNetwork);
                        } else {
                            // Max moves used. Back-propagate and restore state.
                            mcts.backPropagation(score);
                            restoreFromState(realState);
                        }
                    } else {
                        simulation = false;
                        restoreFromState(realState);
                        for (Ghost ghost : ghosts) {
                            ghost.setSpeedUp(false);
                        }
                        nextDirection = mcts.evaluateTree();
                    }
                }

                updatePacman();

                eatPills();

                if (simulation) {
                    eatenCoolDown += 5;
                } else {
                    eatenCoolDown += 1;
                }

                if (pillsList.isEmpty() && powerPillsList.isEmpty()) {
                    gameOver(true);
                    this.stop();
                }

                updateGhostsWrapper();

//                if (!simulation) {
                    updateScreen();
//                }

                for (Ghost ghost : ghosts) {
                    if (ghost.canCatch(pacman)) {
                        if (ghost.isSpooked()) {
                            ghostEaten(ghost);
                        } else if (!ghost.isEyes()) {
                            if (simulation) {
                                mcts.backPropagation(score);
                                restoreFromState(realState);
                                break;
                            } else if (lives > 0) {
                                lostLife();
                                break;
                            } else {
                                gameOver(false);
                                this.stop();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    private void updatePacman() {
        boolean canTurn = true;
        boolean alreadyMoved = false;

        int SPEED;
        if (simulation) {
            SPEED = 10;
        } else {
            SPEED = 2;
        }

        switch (nextDirection) {
            case "UP":
                pacman.setVelocity(0, -SPEED);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        canTurn = false;
                        break;
                    }
                }
                if (canTurn) {
                    alreadyMoved = true;
                    currentDirection = nextDirection;
                    nextDirection = "";
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanU.png");
                    }
                }
                break;
            case "DOWN":
                pacman.setVelocity(0, SPEED);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        canTurn = false;
                        break;
                    }
                }
                if (canTurn) {
                    alreadyMoved = true;
                    currentDirection = nextDirection;
                    nextDirection = "";
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanD.png");
                    }
                }
                break;
            case "LEFT":
                pacman.setVelocity(-SPEED, 0);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        canTurn = false;
                        break;
                    }
                }
                if (canTurn) {
                    alreadyMoved = true;
                    currentDirection = nextDirection;
                    nextDirection = "";
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanL.png");
                    }
                }
                break;
            case "RIGHT":
                pacman.setVelocity(SPEED, 0);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        canTurn = false;
                        break;
                    }
                }
                if (canTurn) {
                    alreadyMoved = true;
                    currentDirection = nextDirection;
                    nextDirection = "";
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanR.png");
                    }
                }
                break;
        }

        mouthPause += 1;

        if (!alreadyMoved){
            switch (currentDirection) {
                case "UP":
                    pacman.setVelocity(0, -SPEED);
                    break;
                case "DOWN":
                    pacman.setVelocity(0, SPEED);
                    break;
                case "LEFT":
                    pacman.setVelocity(-SPEED, 0);
                    break;
                case "RIGHT":
                    pacman.setVelocity(SPEED, 0);
                    break;
            }
            pacman.update();

            boolean moved = true;

            // Collision detection
            for (Sprite wall : wallsList) {
                if (wall.intersects(pacman)) {
                    pacman.undo();
                    moved = false;
                    break;
                }
            }

            if (moved && mouthPause > 5) {
                mouthPause = 0;
                if (mouthOpen) {
                    mouthOpen = false;
                    pacman.setImage("Sprites/Pac-Man/pacman.png");
                } else {
                    mouthOpen = true;
                    pacman.setImage("Sprites/Pac-Man/pacman" + currentDirection.substring(0, 1) + ".png");
                }
            }
        }

        // Teleporter
        if (pacman.getPositionX() <= -15) {
            pacman.setPosition(567, 287);
        } else if (pacman.getPositionX() >= 570) {
            pacman.setPosition(-13, 287);
        }

        Position pMarkerPos = Position.getNearestPosition(pacman.positionX, pacman.positionY);
        previousMarker.setPosition(6 + pMarkerPos.getPositionX(), 6 + pMarkerPos.getPositionY());
    }

    private void eatPills() {
        Iterator<Sprite> pills = pillsList.iterator();
        while (pills.hasNext()) {
            Sprite pill = pills.next();
            if (pacman.canEat(pill)) {
                pills.remove();
                score += 10;
                if (!pinky.isActive()) {
                    pinkyCounter += 1;
                } else if (!inky.isActive()) {
                    inkyCounter += 1;
                } if (!clyde.isActive()) {
                    clydeCounter += 1;
                }
                eatenCoolDown = 0;
            }
        }
        Iterator<Sprite> powerPills = powerPillsList.iterator();
        while (powerPills.hasNext()) {
            Sprite powerPill = powerPills.next();
            if (pacman.canEat(powerPill)) {
                powerPills.remove();
                score += 50;
                scaredGhosts(true);
                ghostsEaten = 0;
                if (!pinky.isActive()) {
                    pinkyCounter += 1;
                } else if (!inky.isActive()) {
                    inkyCounter += 1;
                } if (!clyde.isActive()) {
                    clydeCounter += 1;
                }
                eatenCoolDown = 0;
            }
        }
        if (!pinky.isActive() && pinkyCounter == 3) {
            pinky.setActive();
        } else if (!inky.isActive() && inkyCounter == 30) {
            inky.setActive();
        } else if (clyde.isActive() && clydeCounter == 60) {
            clyde.setActive();
        }
    }

    private void updateScreen() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        pacman.render(gc);

        // Loop through copies to prevent concurrent access problems.
        ArrayList<Sprite> copy = new ArrayList<>(pillsList);
        copy.forEach(pill -> pill.render(gc));

        copy = new ArrayList<>(powerPillsList);
        copy.forEach(powerPill -> powerPill.render(gc));

        copy = new ArrayList<>(wallsList);
        copy.forEach(wall -> wall.render(gc));

        ghosts.forEach(ghost -> ghost.render(gc));

        String pointsText = "Score: " + (score);
        gc.setFill(Color.WHITE);
        gc.fillText(pointsText, 20, 670 );
        gc.strokeText(pointsText, 20, 670 );

        String livesText = "Lives: " + (lives);
        gc.fillText(livesText, 20, 700 );
        gc.strokeText(livesText, 20, 700 );

        if (debug) {
            previousMarker.render(gc);
            nextMarker.render(gc);
            blinkyMarker.render(gc);
            pinkyMarker.render(gc);
            inkyMarker.render(gc);
            clydeMarker.render(gc);
        }
    }

    private void trainingStats(int gen, int member, float highScore) {
        String text = "High Score: " + highScore;
        gc.setFill(Color.WHITE);
        gc.fillText(text, 200, 700 );
        gc.strokeText(text, 200, 700 );

        text = "Member: " + member + "/" + populationSize;
        gc.fillText(text, 200, 670 );
        gc.strokeText(text, 200, 670 );

        text = "Generation: " + gen;
        gc.fillText(text, 400, 670 );
        gc.strokeText(text, 400, 670 );
    }

    private void adjustPosition(Ghost ghost) {
        double ghostX = ghost.getPositionX();
        double ghostY = ghost.getPositionY();
        if (simulation) {
            double offsetX = (ghostX - 7) % 10;
            double offsetY = (ghostY - 7) % 10;
            ghost.positionX -= offsetX;
            ghost.positionY -= offsetY;
        } else {
            if (ghostX % 2 == 0) {
                ghost.positionX += 1;
            } else if (ghostY % 2 == 0) {
                ghost.positionY += 1;
            }
        }
    }

    private void scaredGhosts(boolean scared) {
        if (scared){
            scaredCounter = 0;
        } else {
            scaredCounter = -1;
        }

        for (Ghost ghost : ghosts) {
            adjustPosition(ghost);
            if (!ghost.isEyes()) {
                ghost.setScared(scared);
            }
        }
    }

    private void updateGhostsWrapper() {
        if (eatenCoolDown == 240) {
            eatenCoolDown = 0;
            if (!pinky.isActive()) {
                pinky.setActive();
            } else if (!inky.isActive()) {
                inky.setActive();
            } else if (!clyde.isActive()) {
                clyde.setActive();
            }
        }

        if (scaredCounter >= 0) {
            if (simulation) {
                scaredCounter += 5;
            } else {
                scaredCounter += 1;
            }
            if (scaredCounter > 500) {
                scaredGhosts(false);
                updateGhosts(null);
            } else if (scaredCounter > 350) {
                if ((scaredCounter / 10) % 2 == 0) {
                    updateGhosts("Blue");
                } else {
                    updateGhosts("White");
                }
            } else {
                updateGhosts("Blue");
            }
        } else {
            updateGhosts(null);
        }
    }

    private void updateGhosts(String colour) {
        double pacX = pacman.positionX;
        double pacY = pacman.positionY;

        if (blinky.isActive()) {
            if (blinky.isEyes()) {
                blinky.update(colour, 277, 227);
                blinkyMarker.setPosition(6 + 277, 6 + 227);
            } else {
                blinky.update(colour, pacX, pacY);
                blinkyMarker.setPosition(6 + pacX, 6 + pacY);
            }
        }

        double blinkyX = blinky.getPositionX();
        double blinkyY = blinky.getPositionY();

        double vectorX;
        double vectorY;

        if (pacman.velocityX > 0) {
            if (pinky.isEyes()) {
                pinky.update(colour, 277, 227);
                pinkyMarker.setPosition(6 + 277, 6 + 227);
            } else if (pinky.isActive()) {
                pinky.update(colour, pacX + 80, pacY);
                pinkyMarker.setPosition(6 + pacX + 80, 6 + pacY);
            }
            vectorX = pacX + 40 - blinkyX;
            vectorY = pacY - blinkyY;
        } else if (pacman.velocityX < 0) {
            if (pinky.isEyes()) {
                pinky.update(colour, 277, 227);
                pinkyMarker.setPosition(6 + 277, 6 + 227);
            } else if (pinky.isActive()) {
                pinky.update(colour, pacX - 80, pacY);
                pinkyMarker.setPosition(6 + pacX - 80, 6 + pacY);
            }
            vectorX = pacX - 40 - blinkyX;
            vectorY = pacY - blinkyY;
        } else if (pacman.velocityY > 0) {
            if (pinky.isEyes()) {
                pinky.update(colour, 277, 227);
                pinkyMarker.setPosition(6 + 277, 6 + 227);
            } else if (pinky.isActive()) {
                pinky.update(colour, pacX, pacY + 80);
                pinkyMarker.setPosition(6 + pacX, 6 + pacY + 80);
            }
            vectorX = pacX - blinkyX;
            vectorY = pacY + 40 - blinkyY;
        } else {
            if (pinky.isEyes()) {
                pinky.update(colour, 277, 227);
                pinkyMarker.setPosition(6 + 277, 6 + 227);
            } else if (pinky.isActive()) {
                pinky.update(colour, pacX, pacY - 80);
                pinkyMarker.setPosition(6 + pacX, 6 + pacY - 80);
            }
            vectorX = pacX - blinkyX;
            vectorY = pacY - 40 - blinkyY;
        }

        if (inky.isEyes()) {
            inky.update(colour, 277, 227);
            inkyMarker.setPosition(6 + 277, 6 + 227);
        } else if (inky.isActive()) {
            inky.update(colour, blinkyX + 2 * vectorX, blinkyY + 2 * vectorY);
            inkyMarker.setPosition(6 + blinkyX + 2 * vectorX, 6 + blinkyY + 2 * vectorY);
        }

        if (clyde.isEyes()) {
            clyde.update(colour, 277, 227);
            clydeMarker.setPosition(6 + 277, 6 + 227);
        } else if (clyde.isActive()) {
            if (Math.abs(clyde.getPositionX() - pacX) + Math.abs(clyde.getPositionY() - pacY) > 160) {
                clyde.update(colour, pacX, pacY);
                clydeMarker.setPosition(6 + pacX, 6 + pacY);
            } else {
                clyde.update(colour, 27, 587);
                clydeMarker.setPosition(6 + 27, 6 + 587);
            }
        }
    }

    private void resetPacman() {
        pacman.setImage("Sprites/Pac-Man/pacman.png");
        pacman.setPosition(277, 467);
        pacman.setVelocity(0, 0);
        currentDirection = "R";
        if (ai) {
            nextDirection = "RIGHT";
        } else {
            nextDirection = "";
        }
        mouthOpen = true;
        mouthPause = 0;
    }

    private void resetGhosts() {
        blinky.setImage("Sprites/Ghosts/Blinky/blinkyUp.png");
        blinky.setPosition(277, 227);
        blinky.setScared(false);
        blinky.setActive();
        pinky.setImage("Sprites/Ghosts/Pinky/pinkyUp.png");
        pinky.setScared(false);
        pinky.setInactive(237);
        inky.setImage("Sprites/Ghosts/Inky/inkyUp.png");
        inky.setScared(false);
        inky.setInactive(277);
        clyde.setImage("Sprites/Ghosts/Clyde/clydeUp.png");
        clyde.setScared(false);
        clyde.setInactive(317);
        scaredCounter = -1;
    }

    private void lostLife() {
        lives -= 1;
        resetGhosts();
        resetPacman();
        updateScreen();

        pinkyCounter = -4;
        inkyCounter = 20;
        clydeCounter = 52;

        try {
            sleep(1500);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void gameOver(boolean won) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle;
        if (won) {
            sceneTitle = new Text("Congratulations!");
        } else {
            sceneTitle = new Text("Game Over");
        }
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0);

        Text scoreText = new Text("Final Score: " + score);
        scoreText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scoreText, 0, 1);

        Button newGameButton = new Button();
        newGameButton.setText("New Game");
        newGameButton.setOnAction(event -> newGame());
        grid.add(newGameButton, 0, 2);

        Button exitButton = new Button();
        exitButton.setText("Quit");
        exitButton.setOnAction(event -> Platform.exit());
        grid.add(exitButton, 0, 3);

        scene = new Scene(grid, 592,720);
        mainStage.setScene(scene);
    }

    private void ghostEaten(Ghost ghost) {
        ghost.setScared(false);
        ghost.setEyes(true);

        adjustPosition(ghost);

        score += 200 * Math.pow(2, ghostsEaten);
        ghostsEaten += 1;
    }

    private State getGameState() {
        return new State(this);
    }

    private void restoreFromState(State gameState) {
        this.pacman.setPosition(gameState.getPacmanPosition().getPositionX(), gameState.getPacmanPosition().getPositionY());
        this.pillsList = new ArrayList<>(gameState.getPillsList());
        this.powerPillsList = new ArrayList<>(gameState.getPowerPillsList());
        this.score = gameState.getScore();
        this.blinky = new Ghost(gameState.getBlinky());
        this.pinky = new Ghost(gameState.getPinky());
        this.inky = new Ghost(gameState.getInky());
        this.clyde = new Ghost(gameState.getClyde());
        this.ghostsEaten = gameState.getGhostsEaten();
        this.pinkyCounter = gameState.getPinkyCounter();
        this.inkyCounter = gameState.getInkyCounter();
        this.clydeCounter = gameState.getClydeCounter();
        this.eatenCoolDown = gameState.getEatenCoolDown();
        this.scaredCounter = gameState.getScaredCounter();
        ghosts = new ArrayList<>();
        ghosts.add(blinky);
        ghosts.add(pinky);
        ghosts.add(inky);
        ghosts.add(clyde);
    }

    public Sprite getPacman() {
        return pacman;
    }

    public ArrayList<Sprite> getPillsList() {
        return pillsList;
    }

    public ArrayList<Sprite> getPowerPillsList() {
        return powerPillsList;
    }

    public int getScore() {
        return score;
    }

    public Ghost getBlinky() {
        return blinky;
    }

    public Ghost getPinky() {
        return pinky;
    }

    public Ghost getInky() {
        return inky;
    }

    public Ghost getClyde() {
        return clyde;
    }

    public int getGhostsEaten() {
        return ghostsEaten;
    }

    public int getPinkyCounter() {
        return pinkyCounter;
    }

    public int getInkyCounter() {
        return inkyCounter;
    }

    public int getClydeCounter() {
        return clydeCounter;
    }

    public int getEatenCoolDown() {
        return eatenCoolDown;
    }

    public int getScaredCounter() {
        return scaredCounter;
    }

}