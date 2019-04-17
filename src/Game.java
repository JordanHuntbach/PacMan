import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

public class Game extends Application {

    // This data structure represents the map.
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
    9 = Door
    10= Inaccessible
    */
    private int X = 10;
    private int[][] map = new int[][]{
            {6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 8, 6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 8},
            {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
            {4, 1, 6, 3, 3, 8, 1, 6, 3, 3, 3, 8, 1, 4, 4, 1, 6, 3, 3, 3, 8, 1, 6, 3, 3, 8, 1, 4},
            {4, 2, 4, X, X, 4, 1, 4, X, X, X, 4, 1, 4, 4, 1, 4, X, X, X, 4, 1, 4, X, X, 4, 2, 4},
            {4, 1, 5, 3, 3, 7, 1, 5, 3, 3, 3, 7, 1, 5, 7, 1, 5, 3, 3, 3, 7, 1, 5, 3, 3, 7, 1, 4},
            {4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
            {4, 1, 6, 3, 3, 8, 1, 6, 8, 1, 6, 3, 3, 3, 3, 3, 3, 8, 1, 6, 8, 1, 6, 3, 3, 8, 1, 4},
            {4, 1, 5, 3, 3, 7, 1, 4, 4, 1, 5, 3, 3, 8, 6, 3, 3, 7, 1, 4, 4, 1, 5, 3, 3, 7, 1, 4},
            {4, 1, 1, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 4, 4, 1, 1, 1, 1, 1, 1, 4},
            {5, 3, 3, 3, 3, 8, 1, 4, 5, 3, 3, 8, 0, 4, 4, 0, 6, 3, 3, 7, 4, 1, 6, 3, 3, 3, 3, 7},
            {X, X, X, X, X, 4, 1, 4, 6, 3, 3, 7, 0, 5, 7, 0, 5, 3, 3, 8, 4, 1, 4, X, X, X, X, X},
            {X, X, X, X, X, 4, 1, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1, 4, X, X, X, X, X},
            {X, X, X, X, X, 4, 1, 4, 4, 0, 6, 3, 3, 9, 9, 3, 3, 8, 0, 4, 4, 1, 4, X, X, X, X, X},
            {3, 3, 3, 3, 3, 7, 1, 5, 7, 0, 4, X, X, X, X, X, X, 4, 0, 5, 7, 1, 5, 3, 3, 3, 3, 3},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 4, X, X, X, X, X, X, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {3, 3, 3, 3, 3, 8, 1, 6, 8, 0, 4, X, X, X, X, X, X, 4, 0, 6, 8, 1, 6, 3, 3, 3, 3, 3},
            {X, X, X, X, X, 4, 1, 4, 4, 0, 5, 3, 3, 3, 3, 3, 3, 7, 0, 4, 4, 1, 4, X, X, X, X, X},
            {X, X, X, X, X, 4, 1, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 1, 4, X, X, X, X, X},
            {X, X, X, X, X, 4, 1, 4, 4, 0, 6, 3, 3, 3, 3, 3, 3, 8, 0, 4, 4, 1, 4, X, X, X, X, X},
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

    // Lists of pills and walls (for rendering).
    private ArrayList<Sprite> pillsList;
    private ArrayList<Sprite> powerPillsList;
    private ArrayList<Sprite> wallsList;

    // The number of ghosts eaten since a power pellet was last consumed.
    private int ghostsEaten;

    // GUI Stuff
    private Group root;
    private Scene scene;
    private Stage mainStage;
    private Canvas canvas;
    private GraphicsContext gc;
    private Scene optionsScene;

    // Pac-Man and Ghosts
    private Sprite pacman;
    private ArrayList<Ghost> ghosts;
    private Ghost blinky;
    private Ghost pinky;
    private Ghost inky;
    private Ghost clyde;

    // Player speed.
    private int SPEED = 2;

    // Sprites that mark Pac-Man's position and the ghosts' targets.
    private boolean debug = false;
    private Sprite previousMarker;
    private Sprite nextMarker;
    private Sprite blinkyMarker;
    private Sprite pinkyMarker;
    private Sprite inkyMarker;
    private Sprite clydeMarker;

    // Score and life counters.
    private int score;
    private int lives;

    // Timers for when the ghosts get released.
    private int pinkyCounter;
    private int pinkyLimit;
    private int inkyCounter;
    private int inkyLimit;
    private int clydeCounter;
    private int clydeLimit;
    private int eatenCoolDown;
    private int scaredCounter;

    private int[] modeTimes;
    private int currentMode;
    private int modeCounter;
    private int level = 1;

    // Fields used in Pac-Man's movement.
    private String currentDirection;
    private String nextDirection;
    private int mouthPause = 0;
    private boolean mouthOpen = true;

    // Fields used for tracking the MCTS state.
    private boolean simulation = false;
    private State realState;
    private MCTS mcts;
    private int roundsCounter;
    private int movesCounter;

    // Innovation counters for NEAT.
    private Counter nodeInnovation = new Counter();
    private Counter connectionInnovation = new Counter();

    // Neural network stuff.
    private Evaluator evaluator;
    private NeuralNetwork neuralNetwork;
    private float [] inputs = new float[12];

    // Training stuff.
    private int populationSize = 100;
    private int generations = 250;

    // Game settings.
    private boolean ai = false;
    private boolean training = false;
    private boolean useMCTS = true;
    private boolean useNN = true;

    private boolean useDots = true;
    private boolean useEnergizers = true;
    private boolean useGhosts = true;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("Styling/PacFont.ttf"), 16);
        mainStage = stage;
        mainStage.setTitle("Pac-Man");
        optionsScene = new Scene(optionsSetup(), 592,720, Color.BLACK);
        optionsScene.getStylesheets().add("Styling/style.css");
        menu();
    }

    // Display menu screen.
    private void menu() {
        VBox mainMenu = new VBox();
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setSpacing(10);
        mainMenu.setPadding(new Insets(25));

        Text sceneTitle = new Text("PAC-MAN");
        sceneTitle.getStyleClass().add("title");
        mainMenu.getChildren().add(sceneTitle);

        Image image = new Image("Styling/menu.png", true);
        ImageView imageView = new ImageView(image);
        VBox.setMargin(imageView, new Insets(100, 0, 50, 0));
        mainMenu.getChildren().add(imageView);

        ToggleGroup group = new ToggleGroup();
        RadioButton humanPlayer = new RadioButton("Human Player");
        humanPlayer.setToggleGroup(group);
        humanPlayer.setSelected(true);
        humanPlayer.setOnAction(event -> {ai = false; training = false;});
        RadioButton aiPlayer = new RadioButton("AI Player");
        aiPlayer.setToggleGroup(group);
        aiPlayer.setOnAction(event -> {ai = true; training = false;});
        RadioButton trainNeuralNetwork = new RadioButton("Train Neural Network");
        trainNeuralNetwork.setToggleGroup(group);
        trainNeuralNetwork.setOnAction(event -> {ai = true; training = true;});

        HBox hBox = new HBox();
        hBox.getChildren().addAll(humanPlayer, aiPlayer, trainNeuralNetwork);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setAlignment(Pos.CENTER);
        mainMenu.getChildren().add(hBox);

        Button newGameButton = new Button();
        newGameButton.setOnAction(event -> {
            // Either train the neural network, or play a game.
            if (training) {
                guiSetup();
                setUpNN();
            } else {
                newGame();
            }
        });
        setButtonText(newGameButton, "start game");

        Button optionsButton = new Button();
        optionsButton.setOnAction(event -> mainStage.setScene(optionsScene));
        setButtonText(optionsButton, "options");

        Button exitButton = new Button();
        exitButton.setOnAction(event -> Platform.exit());
        setButtonText(exitButton, "quit");

        mainMenu.getChildren().addAll(newGameButton, optionsButton, exitButton);

        Text text = new Text();
        text.setText("Created by Jordan Huntbach for a dissertation on 'Machine Learning for Pac-Man'\n2019");
        text.getStyleClass().add("info");
        VBox.setMargin(text, new Insets(50, 0, 0, 0));

        mainMenu.getChildren().add(text);

        scene = new Scene(mainMenu, 592,720, Color.BLACK);
        scene.getStylesheets().add("Styling/style.css");
        mainStage.setScene(scene);
        mainStage.show();
    }

    // Set up options menu.
    private VBox optionsSetup() {
        VBox optionsMenu = new VBox();
        optionsMenu.setAlignment(Pos.CENTER);
        optionsMenu.setSpacing(10);
        optionsMenu.setPadding(new Insets(25));

        Text sceneTitle = new Text("OPTIONS");
        sceneTitle.getStyleClass().add("title");
        optionsMenu.getChildren().add(sceneTitle);
        VBox.setMargin(sceneTitle, new Insets(0, 0, 100, 0));

        CheckBox mctsCheck = new CheckBox("Tree Search");
        CheckBox networkCheck = new CheckBox("Neural Network");
        mctsCheck.setIndeterminate(false);
        networkCheck.setIndeterminate(false);

        mctsCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            if (!useNN) {
                networkCheck.fire();
            }
            useMCTS = new_val;
        });
        mctsCheck.setSelected(true);

        networkCheck.selectedProperty().addListener((ov, old_val, new_val) -> {
            if (!useMCTS) {
                mctsCheck.fire();
            }
            useNN = new_val;
        });
        networkCheck.setSelected(true);

        Text ai_options = new Text("AI Options");
        ai_options.getStyleClass().add("heading");
        optionsMenu.getChildren().addAll(ai_options, mctsCheck, networkCheck);

        Text game_options = new Text("Game Options");
        game_options.getStyleClass().add("heading");

        CheckBox dots = new CheckBox("Dots");
        CheckBox energizers = new CheckBox("Energizers");
        CheckBox ghosts = new CheckBox("Ghosts");
        dots.setIndeterminate(false);
        energizers.setIndeterminate(false);
        ghosts.setIndeterminate(false);

        dots.selectedProperty().addListener((ov, old_val, new_val) -> useDots = new_val);
        energizers.selectedProperty().addListener((ov, old_val, new_val) -> useEnergizers = new_val);
        ghosts.selectedProperty().addListener((ov, old_val, new_val) -> useGhosts = new_val);

        dots.setSelected(true);
        energizers.setSelected(true);
        ghosts.setSelected(true);

        optionsMenu.getChildren().addAll(game_options, dots, energizers, ghosts);
        VBox.setMargin(game_options, new Insets(20, 0, 0, 0));

        Button backButton = new Button();
        backButton.setOnAction(event -> mainStage.setScene(scene));
        setButtonText(backButton, "main menu");

        Button exitButton = new Button();
        exitButton.setOnAction(event -> Platform.exit());
        setButtonText(exitButton, "quit");

        optionsMenu.getChildren().addAll(backButton, exitButton);
        VBox.setMargin(backButton, new Insets(50, 0, 0, 0));

        return optionsMenu;
    }

    // Used to create button effects.
    private void setButtonText(Button button, String text){
        button.textProperty().bind(
                Bindings.when(button.hoverProperty())
                        .then(text.toLowerCase())
                        .otherwise(text.toUpperCase()));
    }

    // Initialise NEAT.
    private void setUpNN() {
        Genome newGenome = newGenome();

        evaluator = new Evaluator(populationSize, newGenome, nodeInnovation, connectionInnovation) {
            @Override
            float evaluateGenome(Genome genome, int generation, int member, float highestScore) {
                return trainOnGame(genome, generation, member, highestScore);
            }
        };

        // Mutate the starting genomes a little, for some initial variation.
        evaluator.initialMutate();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> evaluator.saveBestGenome()));

        // Create a task which can be run in a non-GUI thread, to prevent blocking.
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {


                // Evaluate each generation
                for (int i = 0; i < generations; i++) {
                    try {
                        evaluator.evaluate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Training finished, saving best genome to file.");

                // When done, save the best genome to a file.
                evaluator.saveBestGenome();

                score = (int) evaluator.getHighestScore();

                Platform.runLater(() -> finalNNScreen());

                return null;
            }
        };

        // Run task in new thread
        new Thread(task).start();
    }

    // Create a minimal genome to initialise the NEAT algorithm.
    private Genome newGenome() {
        /* Inputs are:
            4 x Directions can move in
            4 x Are there dots in that direction
            4 x Closest ghosts in that direction

           Outputs are:
            Forward, Backwards, Left turn, Right turn. Move in the direction with the greatest value.
          */

        connectionInnovation = new Counter();
        nodeInnovation = new Counter();

        Genome genome = new Genome();

        for (int i = 0; i < 12; i++) {
            NodeGene nodeGene = new NodeGene(NodeGene.TYPE.INPUT, nodeInnovation.getInnovation());
            genome.addNodeGene(nodeGene, nodeInnovation);
        }

        NodeGene up = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        NodeGene down = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        NodeGene left = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        NodeGene right = new NodeGene(NodeGene.TYPE.OUTPUT, nodeInnovation.getInnovation());
        genome.addNodeGene(up, nodeInnovation);
        genome.addNodeGene(down, nodeInnovation);
        genome.addNodeGene(left, nodeInnovation);
        genome.addNodeGene(right, nodeInnovation);

        for (int i = 0; i < 12; i++) {
            ConnectionGene connectionGene = new ConnectionGene(i, 12 + i % 4, 1, true, connectionInnovation.getInnovation());
            genome.addConnectionGene(connectionGene, connectionInnovation);
        }

        return genome;
    }

    // This method is called to play a game, when training the neural network.
    private float trainOnGame(Genome genome, int genNumber, int memNumber, float highScore) {
        // Set up the game.
        gameSetup();

        // Get the GUI thread to run the refreshCanvas() method.
        Platform.runLater(this::refreshCanvas);

        // Pause so we can see what's going on.
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create a neural network based on the genome parameter.
        NeuralNetwork neuralNetwork = new NeuralNetwork(genome);

        eatenCoolDown = 0;

        // Game loop
        while (true) {

            // Evaulate the NN to get the next direction.
            getNextDirectionFromNN(neuralNetwork);

            // Move Pac-Man in the desired direction.
            updatePacman();

            if (useGhosts) {
                // Move the ghosts.
                updateGhostsWrapper();
            }

            // Eat any pills.
            eatPills();

            if (pillsList.isEmpty() && powerPillsList.isEmpty()) {
                nextLevel();
                continue;
            }

            if (eatenCoolDown >= 1000) {
                return score;
            }

            // Get the GUI thread to update the screen.
            Platform.runLater(this::updateScreen);
            Platform.runLater(() -> trainingStats(genNumber, memNumber, highScore));

            // Pause so we can see what's going on.
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Handle ghost collisions.
            for (Ghost ghost : ghosts) {
                if (ghost.canCatch(pacman)) {
                    if (ghost.isSpooked()) {
                        ghostEaten(ghost);  // Pac-Man eats ghost.
                    } else if (!ghost.isEyes()) {
                        if (lives == 0) {
                            return score;
                        } else {
                            lostLife();
                        }
                    }
                }
            }
        }
    }

    // This method passes the inputs to a given NN, and sets nextDirection if appropriate.
    private void getNextDirectionFromNN(NeuralNetwork neuralNetwork){
        // Calculate inputs.
        getInputs();
        // Calculate outputs.
        float[] nnOutputs = neuralNetwork.calculate(inputs);

        if (nnOutputs != null) {
            // Get the highest value.
            float maximum = -Float.MAX_VALUE;
            int direction = -1;
            for (int i = 0; i < nnOutputs.length; i++) {
                if (nnOutputs[i] > maximum) {
                    maximum = nnOutputs[i];
                    direction = i;
                }
            }

            int[] orientation; // [FORWARDS, BACKWARDS, LEFT, RIGHT] where UP=0 DOWN=1 LEFT=2 RIGHT=3

            switch (currentDirection) {
                case "UP":
                    orientation = new int[] {0, 1, 2, 3};
                    break;
                case "DOWN":
                    orientation = new int[] {1, 0, 3, 2};
                    break;
                case "LEFT":
                    orientation = new int[] {2, 3, 1, 0};
                    break;
                default:
                    orientation = new int[] {3, 2, 0, 1};
                    break;
            }

            direction = orientation[direction];

            // Set the nextDirection.
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

    // Called at the start of the game to initialise the GUI.
    private void guiSetup() {
        root = new Group();
        scene = new Scene(root, 592, 720, Color.BLACK);
        mainStage.setScene(scene);

        refreshCanvas();

        // If the game is not being controlled by AI, register the keys to change direction.
        if (!ai) {
            scene.setOnKeyPressed(e -> nextDirection = e.getCode().toString());
        }
    }

    // Called between games to refresh the GUI.
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

    // Called at the start of the game to initialise things.
    private void gameSetup() {
        // Initialise Pac-Man.
        pacman = new Sprite();
        currentDirection = "";
        nextDirection = "";
        resetPacman();

        // Initialise lists.
        pillsList = new ArrayList<>();
        powerPillsList = new ArrayList<>();
        wallsList = new ArrayList<>();
        ghosts = new ArrayList<>();

        int rowCounter = -1;
        int colCounter = -1;
        int COLUMNS = 28;

        // Build the map.
        for (int[] row : map) {
            rowCounter += 1;
            for (int code : row) {
                colCounter = (colCounter + 1) % COLUMNS;
                if(code == 1 && useDots) {
                    Sprite pill = new Sprite();
                    pill.setImage("Sprites/Pickups/pill.png");
                    double px = 22 + 20 * colCounter;
                    double py = 22 + 20 * rowCounter;
                    pill.setPosition(px, py);
                    pillsList.add(pill);
                } else if (code == 2 && useEnergizers) {
                    Sprite powerPill = new Sprite();
                    powerPill.setImage("Sprites/Pickups/powerPill.png");
                    double px = 13 + 20 * colCounter;
                    double py = 13 + 20 * rowCounter;
                    powerPill.setPosition(px, py);
                    powerPillsList.add(powerPill);
                } else if (3 <= code && code <= 9) {
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
                        case 9:
                            wall = new Sprite();
                            wall.setImage("Sprites/Walls/door.png");
                            py += 9;
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

        if (useGhosts) {
            // Initialise ghosts.
            blinky = new Ghost("blinky");
            pinky = new Ghost("pinky");
            inky = new Ghost("inky");
            clyde = new Ghost("clyde");

            ghosts.clear();
            ghosts.add(blinky);
            ghosts.add(pinky);
            ghosts.add(inky);
            ghosts.add(clyde);
            resetGhosts();

            // Initialise debug markers.
            blinkyMarker = new Sprite();
            blinkyMarker.setImage("Sprites/Markers/blinky.png");
            pinkyMarker = new Sprite();
            pinkyMarker.setImage("Sprites/Markers/pinky.png");
            inkyMarker = new Sprite();
            inkyMarker.setImage("Sprites/Markers/inky.png");
            clydeMarker = new Sprite();
            clydeMarker.setImage("Sprites/Markers/clyde.png");

            // Initialise counters.
            pinkyCounter = 0;
            pinkyLimit = 0;
            inkyCounter = 0;
            inkyLimit = 30;
            clydeCounter = 0;
            clydeLimit = 60;
            ghostsEaten = 0;
            modeCounter = 0;
            currentMode = 0;
            eatenCoolDown = 0;
        }

        // Start in scatter mode, then switch after 7, 20,  7, 20,  5, 20, 5 seconds
        // modeTimes = new int[] {7, 27, 34, 54, 59, 79, 84};
        modeTimes = new int[] {385, 1485, 1870, 2970, 3245, 4345, 4620}; // Multiply by 55 to get approximate seconds

        // Initialise debug markers.
        previousMarker = new Sprite();
        previousMarker.setImage("Sprites/Markers/pacman.png");
        nextMarker = new Sprite();
        nextMarker.setImage("Sprites/Markers/pacman.png");

        // Initialise counters.
        score = 0;
        lives = 2;
    }

    // Fills the input array with the relevant values.
    private void getInputs() {
        // Pointer to current position in the input array.
        int[] orientation; // [UP, DOWN, LEFT, RIGHT] where FORWARDS=0 BACKWARDS=1 LEFT=2 RIGHT=3

        switch (currentDirection) {
            case "UP":
                orientation = new int[] {0, 1, 2, 3};
                break;
            case "DOWN":
                orientation = new int[] {1, 0, 3, 2};
                break;
            case "LEFT":
                orientation = new int[] {3, 2, 0, 1};
                break;
            default:
                orientation = new int[] {2, 3, 1, 0};
                break;
        }

        // Valid moves
        inputs[orientation[0]] = canMove("UP") ? 1 : -1;
        inputs[orientation[1]] = canMove("DOWN") ? 1 : -1;
        inputs[orientation[2]] = canMove("LEFT") ? 1 : -1;
        inputs[orientation[3]] = canMove("RIGHT") ? 1 : -1;

        char[][] mapView = getMapView();

        int pacmanIndexX = (int) pacman.positionX / 20;
        int pacmanIndexY = (int) pacman.positionY / 20;

        // Is there a dot in each direction
        int access = 0;
        for (int[] vector : new int[][] {new int[] {0, -1}, new int[] {0, 1}, new int[] {-1, 0}, new int[] {1, 0}}) {
            int pointer = 4 + orientation[access++];
            if (inputs[pointer - 4] == -1) {
                inputs[pointer] = 0;
            } else {
                int checkX = pacmanIndexX;
                int checkY = pacmanIndexY;
                while (true) {
                    checkX += vector[0];
                    checkY += vector[1];

                    if (checkX <= 0) {
                        checkX = map[0].length - 1;
                    } else if (checkX >= map[0].length) {
                        checkX = 0;
                    }

                    if (mapView[checkY][checkX] == '.') {
                        inputs[pointer] = 1;
                        break;
                    } else if (mapView[checkY][checkX] == 'X') {
                        inputs[pointer] = 0;
                        break;
                    }
                }
            }
        }

        // Distance to ghost in each direction
        access = 0;
        int[][] vectors = new int[][]{new int[]{0, -1}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{1, 0}};
        if (useGhosts) {
            for (int[] vector : vectors) {
                int pointer = 8 + orientation[access++];
                if (inputs[pointer - 8] == -1) {                // If we can't move in that direction,
                    inputs[pointer] = 0;                        // it's just a zero.
                } else {                                        // If we can move in that direction...
                    Position check = new Position(pacman.positionX, pacman.positionY);
                    int distance = 0;
                    boolean done = false;
                    while (!done) {
                        check.move(vector[0], vector[1]);

                        if (check.getPositionX() <= -15) {
                            check.setPositionX(567);
                        } else if (check.getPositionX() >= 570) {
                            check.setPositionX(-13);
                        }

                        distance += 1;                          // Keep moving along

                        for (Ghost ghost : ghosts) {            // If we hit a ghost, bingo
                            if (ghost.getPosition().equals(check)) {
                                inputs[pointer] = (float) (ghost.isSpooked() ? 40.0/distance : -40.0/distance);
                                done = true;
                                break;
                            }
                        }

                        if (!done) {                            // Check if we're at a junction
                            if (Position.isJunction(check.getPositionX(), check.getPositionY())) {
                                int junctionType = Position.junctions.get(check);
                                if (junctionType >= 5) {        // If we're at a crossroads, we can't keep manually searching, so use some A* search.
                                    int minDistance = Integer.MAX_VALUE;
                                    boolean scared = false;
                                    AStarSearch search = new AStarSearch();
                                    for (Ghost ghost : ghosts) {
                                        if (ghost.isActive()) {
                                            Position ghostJunction = Position.getNearestPosition(ghost.positionX, ghost.positionY);
                                            List<Position> path = search.findPath(ghostJunction, check);
                                            int length;
                                            if (path.size() == 0) {
                                                length = (int) check.distanceTo(ghost.getPosition());
                                            } else {
                                                length = pathLength(path);
                                                if (onPath(ghostJunction, path.get(0), ghost.getPosition())) {
                                                    length -= (int) ghostJunction.distanceTo(ghost.getPosition());
                                                } else {
                                                    length += (int) ghostJunction.distanceTo(ghost.getPosition());
                                                }
                                            }
                                            if (length < minDistance) {
                                                minDistance = length;
                                                scared = ghost.isSpooked();
                                            }
                                        }
                                    }
                                    if (minDistance == Integer.MAX_VALUE) {
                                        inputs[pointer] = 0;
                                        break;
                                    } else {
                                        distance += minDistance;
                                        inputs[pointer] = (float) (scared ? 40.0/distance : -40.0/distance);
                                        break;
                                    }
                                } else {                        // Otherwise, automatically change direction
                                    boolean[] options = Position.directionOptions[junctionType].clone();
                                    int backwards;
                                    if (vector[0] == 0) {
                                        if (vector[1] == 1) {
                                            backwards = 0;
                                        } else {
                                            backwards = 1;
                                        }
                                    } else {
                                        if (vector[1] == 1) {
                                            backwards = 2;
                                        } else {
                                            backwards = 3;
                                        }
                                    }
                                    options[backwards] = false;

                                    int newVector = 0;
                                    for (int i = 0; i < 4; i++) {
                                        if (options[i]) {
                                            newVector = i;
                                            break;
                                        }
                                    }

                                    vector = vectors[newVector];
                                }
                            }
                        }
                    }
                }
            }
        } else {
            inputs[8] = 0;
            inputs[9] = 0;
            inputs[10] = 0;
            inputs[11] = 0;
        }

    }

    private boolean onPath(Position start, Position end, Position check) {
        double pathX = end.getPositionX() - start.getPositionX();
        double pathY = end.getPositionY() - start.getPositionY();

        double checkX = check.getPositionX() - start.getPositionX();
        double checkY = check.getPositionY() - start.getPositionY();

        return Math.signum(checkX) == Math.signum(pathX) && Math.signum(checkY) == Math.signum(pathY);
    }

    private int pathLength(List<Position> path) {
        int distance = 0;
        Position previous = path.get(0);
        for (Position next : path) {
            distance += previous.distanceTo(next);
            previous = next;
        }
        return distance;
    }

//    private int additionalLength(List<Position> path, Position position) {
//        Position pos1 = path.get()
//        int x = path
//    }

    private char[][] getMapView(){
        int viewHeight = map.length;
        int viewWidth = map[0].length;

        char[][] mapView = new char[viewHeight][viewWidth];

        for (int i = 0; i < viewHeight; i++) {
            for (int j = 0; j < viewWidth; j++) {
                if (map[i][j] > 2) {
                    mapView[i][j] = 'X';
                } else if (map[i][j] == 1) {
                    if (pillStillActive(i, j)) {
                        mapView[i][j] = '.';
                    } else {
                        mapView[i][j] = ' ';
                    }
                } else if (map[i][j] == 2) {
                    if (powerPillStillActive(i, j)) {
                        mapView[i][j] = 'O';
                    } else {
                        mapView[i][j] = ' ';
                    }
                } else if (map[i][j] == 0) {
                    mapView[i][j] = ' ';
                }
            }
        }

        // viewMap(mapView);

        return mapView;
    }

//    private void viewMap(char[][] array) {
//        int pacmanIndexX = (int) pacman.positionX / 20;
//        int pacmanIndexY = (int) pacman.positionY / 20;
//
//        array[pacmanIndexY][pacmanIndexX] = 'P';
//
//        for (char[] row : array) {
//            for (char element : row) {
//                System.out.print(element + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//    }

    private boolean pillStillActive(int mapPointerY, int mapPointerX) {
        Position pillPosition = new Position(22 + mapPointerX * 20, 22 + mapPointerY * 20);
        for (Sprite pill : pillsList) {
            if (pill.getPosition().equals(pillPosition)) {
                return true;
            }
        }
        return false;
    }

    private boolean powerPillStillActive(int mapPointerY, int mapPointerX) {
        Position pillPosition = new Position(13 + mapPointerX * 20, 13 + mapPointerY * 20);
        for (Sprite powerPill : powerPillsList) {
            if (powerPill.getPosition().equals(pillPosition)) {
                return true;
            }
        }
        return false;
    }

    // Returns whether Pac-Man can move in a specified direction.
    private boolean canMove(String direction) {
        switch (direction) {
            case "UP":
                pacman.setVelocity(0, -SPEED);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        return false;
                    }
                }
                pacman.undo();
                return true;
            case "DOWN":
                pacman.setVelocity(0, SPEED);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        return false;
                    }
                }
                pacman.undo();
                return true;
            case "LEFT":
                pacman.setVelocity(-SPEED, 0);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        return false;
                    }
                }
                pacman.undo();
                return true;
            case "RIGHT":
                pacman.setVelocity(SPEED, 0);
                pacman.update();
                for (Sprite wall : wallsList) {
                    if (wall.intersects(pacman)) {
                        pacman.undo();
                        return false;
                    }
                }
                pacman.undo();
                return true;
            default:
                return false;
        }
    }

    // Returns the genome stored in bestGenome.gen.
    private Genome loadGenome() {
        String file = "bestGenome.gen";

        // Read each line from the file into an ArrayList.
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
            {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Genome " + file + " failed to load.");
            return null;
        }

        // Initialise a list of connectionGenes.
        List<ConnectionGene> connectionGenes = new ArrayList<>();

        // Delete the first line (just contains the score).
        lines.remove(0);

        // For every connection gene listed in the file, add it to a list.
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

        // Create a new genome, and give it the connections read from the file.
        Genome genome = newGenome();
        genome.overwriteConnections(connectionGenes);

        return genome;
    }

    // This function handles the MCTS, and is called every game loop.
    private void mctsStuff() {
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
                if (useNN) {
                    getNextDirectionFromNN(neuralNetwork);
                } else {
                    nextDirection = PacManController.getNextDirection(pacman);
                }
            } else {
                // Max moves used. Back-propagate and restore state.
                mcts.backPropagation(score);
                restoreFromState(realState);
            }
        } else {
            simulation = false;
            restoreFromState(realState);
            nextDirection = mcts.evaluateTree();
        }
    }

    // This function allows a game to be played, outside of training the NN.
    private void newGame() {
        // Setup the GUI and Game.
        guiSetup();
        gameSetup();

        // Kick-start the AI.
        if (ai) {
            nextDirection = "LEFT";
        }

        // Create a NN from the best genome found.
        nodeInnovation = new Counter();
        connectionInnovation = new Counter();

        simulation = false;

        Genome genome = loadGenome();
        if (genome != null) {
            neuralNetwork = new NeuralNetwork(genome);
        }

        // Start the game in a non-GUI thread, to prevent blocking.
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    gameLoop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    // The main game loop!
    private void gameLoop() {

        // While the game is not over..
        boolean endGame = false;
        while (!endGame) {
            if (ai) {
                if (useMCTS) {
                    if (Position.isJunction(pacman.getPositionX(), pacman.getPositionY())) {
                        mctsStuff();
                    }
                } else {
                    getNextDirectionFromNN(neuralNetwork);
                }
            }

            // Move Pac-Man.
            updatePacman();

            // Update each ghost.
            if (useGhosts) {
                updateGhostsWrapper();
            }

            // Eat pills.
            eatPills();

            // If all pills eaten, move onto next level.
            if (pillsList.isEmpty() && powerPillsList.isEmpty()) {
                nextLevel();
                continue;
            }

            // If we aren't in a MCTS simulation / play-out, update the screen.
            if (!simulation) {
                Platform.runLater(this::updateScreen);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (debug) {
                Platform.runLater(this::updateScreen);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Handle the ghost collisions.
            for (Ghost ghost : ghosts) {
                if (ghost.canCatch(pacman)) {
                    if (ghost.isSpooked()) {
                        ghostEaten(ghost);                  // Pac-Man eats ghost.
                    } else if (!ghost.isEyes()) {
                        if (simulation) {
                            mcts.backPropagation(score);    // Ghost eats Pac-Man, ending MCTS play-out.
                            restoreFromState(realState);    // Restore state to beginning of play-out.
                            break;
                        } else if (lives > 0) {             // Ghost eats Pac-Man outside of MCTS
                            lostLife();
                            break;
                        } else {
                            endGame = true;
                            Platform.runLater(this::gameOver);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void nextLevel() {
        level += 1;
        int tempScore = score;
        int tempLives = lives;
        gameSetup();
        score = tempScore;
        lives = tempLives;

        if (level < 5) {
            if (level == 2) {
                pinkyLimit = 0;
                inkyLimit = 0;
                clydeLimit = 50;
            } else if (level == 3) {
                clydeLimit = 0;
            }
            // modeTimes = new int[] {7, 27, 34, 54, 59, 1092, 1093};
            modeTimes = new int[] {385, 1485, 1870, 2970, 3245, 60060, 60065}; // Multiply by 55 to get approximate seconds
        } else {
            // modeTimes = new int[] {5, 25, 30, 50, 55, 1092, 1093};
            modeTimes = new int[] {275, 1375, 1650, 2750, 3025, 60060, 60065}; // Multiply by 55 to get approximate seconds
        }
    }

    // Moves Pac-Man
    private void updatePacman() {
        // Change direction if possible.
        boolean canTurn = canMove(nextDirection);
        if (canTurn) {
            currentDirection = nextDirection;
            nextDirection = "";
            switch (currentDirection) {
                case "UP":
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanU.png");
                    }
                    break;
                case "DOWN":
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanD.png");
                    }
                    break;
                case "LEFT":
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanL.png");
                    }
                    break;
                case "RIGHT":
                    if (mouthOpen) {
                        pacman.setImage("Sprites/Pac-Man/pacmanR.png");
                    }
                    break;
            }
        }

        // Move Pac-Man.
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

        // Undo move if Pac-Man hit a wall.
        boolean moved = true;
        for (Sprite wall : wallsList) {
            if (wall.intersects(pacman)) {
                pacman.undo();
                moved = false;
                break;
            }
        }

        // Handles mouth opening/closing.
        mouthPause += 1;
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

        // Teleporter tunnel.
        if (pacman.getPositionX() <= -15) {
            pacman.setPosition(567, 287);
        } else if (pacman.getPositionX() >= 570) {
            pacman.setPosition(-13, 287);
        }

        // Update debug marker.
        Position pMarkerPos = Position.getNearestPosition(pacman.positionX, pacman.positionY);
        previousMarker.setPosition(6 + pMarkerPos.getPositionX(), 6 + pMarkerPos.getPositionY());
    }

    private void eatPills() {
        Iterator<Sprite> pills = pillsList.iterator();
        boolean pillEaten = false;
        while (pills.hasNext()) {
            Sprite pill = pills.next();
            if (pacman.canEat(pill)) {
                pills.remove();
                score += 10;
                pillEaten = true;
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
                pillEaten = true;
            }
        }

        if (pillEaten) {
            eatenCoolDown = 0;
            if (useGhosts) {
                if (!pinky.isActive()) {
                    pinkyCounter += 1;
                } else if (!inky.isActive()) {
                    inkyCounter += 1;
                } else if (!clyde.isActive()) {
                    clydeCounter += 1;
                }
            }
        } else {
            eatenCoolDown += 1;
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

    private void finalNNScreen() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Training Completed");
        sceneTitle.getStyleClass().add("gameOver");

        Text scoreText = new Text("Best Score: " + score);
        scoreText.getStyleClass().add("gameOver");

        Button exitButton = new Button();
        setButtonText(exitButton, "Return to Menu");
        exitButton.setOnAction(event -> menu());

        vBox.getChildren().addAll(sceneTitle, scoreText, exitButton);

        VBox.setMargin(exitButton, new Insets(20, 0, 0, 0));

        scene = new Scene(vBox, 592,720, Color.BLACK);
        scene.getStylesheets().add("Styling/style.css");
        mainStage.setScene(scene);
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

        if (ghostX % 2 == 0) {
            ghost.positionX += 1;
        } else if (ghostY % 2 == 0) {
            ghost.positionY += 1;
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
                if (scared) {
                    ghost.reverse();
                }
            }
        }
    }

    private void updateGhostsWrapper() {
        if (!pinky.isActive() && pinkyCounter == pinkyLimit) {
            pinky.setActive();
        }
        if (!inky.isActive() && inkyCounter == inkyLimit) {
            inky.setActive();
        }
        if (clyde.isActive() && clydeCounter == clydeLimit) {
            clyde.setActive();
        }

        if (eatenCoolDown > 240) {
            if (!pinky.isActive()) {
                pinky.setActive();
                eatenCoolDown = 0;
            } else if (!inky.isActive()) {
                inky.setActive();
                eatenCoolDown = 0;
            } else if (!clyde.isActive()) {
                clyde.setActive();
                eatenCoolDown = 0;
            }
        }

        for (int i = currentMode; i < modeTimes.length; i++) {
            if (modeCounter >= modeTimes[i]) {
                if (i % 2 == 0) {
                    // Chase
                    for (Ghost ghost : ghosts) {
                        ghost.setScatter(false);
                        ghost.reverse();
                    }
                } else {
                    // Scatter
                    for (Ghost ghost : ghosts) {
                        ghost.setScatter(true);
                        ghost.reverse();
                    }
                }
                currentMode += 1;
                break;
            }
        }

        if (scaredCounter >= 0) {
            scaredCounter += 1;
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
            modeCounter += 1;
            updateGhosts(null);
        }

    }

    private void updateGhosts(String colour) {
        double pacX = pacman.positionX;
        double pacY = pacman.positionY;

        Position target;

        if (blinky.isActive()) {
            if (blinky.isEyes()) {
                target = new Position(277, 277);
            } else {
                target = new Position(pacX, pacY);
            }
            blinky.update(colour, target.getPositionX(), target.getPositionY());
            blinkyMarker.setPosition(target.getPositionX(), target.getPositionY());
        }

        double blinkyX = blinky.getPositionX();
        double blinkyY = blinky.getPositionY();

        double vectorX;
        double vectorY;

        if (pacman.velocityX > 0) {
            if (pinky.isEyes()) {
                target = new Position(277, 277);
            } else {
                target = new Position(pacX + 80, pacY);
            }
            vectorX = pacX + 40 - blinkyX;
            vectorY = pacY - blinkyY;
        } else if (pacman.velocityX < 0) {
            if (pinky.isEyes()) {
                target = new Position(277, 277);
            } else {
                target = new Position(pacX - 80, pacY);
            }
            vectorX = pacX - 40 - blinkyX;
            vectorY = pacY - blinkyY;
        } else if (pacman.velocityY > 0) {
            if (pinky.isEyes()) {
                target = new Position(277, 277);
            } else {
                target = new Position(pacX, pacY + 80);
            }
            vectorX = pacX - blinkyX;
            vectorY = pacY + 40 - blinkyY;
        } else {
            if (pinky.isEyes()) {
                target = new Position(277, 277);
            } else {
                target = new Position(pacX, pacY - 80);
            }
            vectorX = pacX - blinkyX;
            vectorY = pacY - 40 - blinkyY;
        }

        if (pinky.isActive()) {
            pinky.update(colour, target.getPositionX(),  target.getPositionY());
            pinkyMarker.setPosition(target.getPositionX(), target.getPositionY());
        }

        if (inky.isActive()) {
            if (inky.isEyes()) {
                target = new Position(277, 277);
            } else {
                target = new Position(blinkyX + 2 * vectorX, blinkyY + 2 * vectorY);
            }
            inky.update(colour, target.getPositionX(),  target.getPositionY());
            inkyMarker.setPosition(target.getPositionX(), target.getPositionY());
        }

        if (clyde.isActive()) {
            if (clyde.isEyes()) {
                target = new Position(277, 277);
            } else {
                if (Math.abs(clyde.getPositionX() - pacX) + Math.abs(clyde.getPositionY() - pacY) > 160) {
                    target = new Position(pacX, pacY);
                } else {
                    target = new Position(27, 627);
                }
            }
            clyde.update(colour, target.getPositionX(),  target.getPositionY());
            clydeMarker.setPosition(target.getPositionX(), target.getPositionY());
        }

        blinkyMarker.setPosition(blinkyMarker.getPositionX() + 6, blinkyMarker.getPositionY() + 6);
        pinkyMarker.setPosition(pinkyMarker.getPositionX() + 6, pinkyMarker.getPositionY() + 6);
        inkyMarker.setPosition(inkyMarker.getPositionX() + 6, inkyMarker.getPositionY() + 6);
        clydeMarker.setPosition(clydeMarker.getPositionX() + 6, clydeMarker.getPositionY() + 6);

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
        for (Ghost ghost : ghosts) {
            ghost.reset();
        }

        blinky.setActive();
        pinky.setInactive();
        inky.setInactive();
        clyde.setInactive();

        scaredCounter = -1;
    }

    private void lostLife() {
        lives -= 1;
        resetGhosts();
        resetPacman();
        updateScreen();

        pinkyCounter = 0;
        inkyCounter = 0;
        clydeCounter = 0;

        pinkyLimit = 7;
        inkyLimit = 10;
        clydeLimit = 15;

        if (!training) {
            try {
                sleep(1500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void gameOver() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Game Over");
        sceneTitle.getStyleClass().add("gameOver");

        Text scoreText = new Text("Final Score: " + score);
        scoreText.getStyleClass().add("gameOver");

        Button newGameButton = new Button();
        setButtonText(newGameButton, "New Game");
        newGameButton.setOnAction(event -> newGame());

        Button exitButton = new Button();
        setButtonText(exitButton, "Return to Menu");
        exitButton.setOnAction(event -> menu());

        vBox.getChildren().addAll(sceneTitle, scoreText, newGameButton, exitButton);

        VBox.setMargin(newGameButton, new Insets(20, 0, 0, 0));

        scene = new Scene(vBox, 592,720, Color.BLACK);
        scene.getStylesheets().add("Styling/style.css");
        mainStage.setScene(scene);
    }

    // Called when a ghost is eaten.
    private void ghostEaten(Ghost ghost) {
        ghost.setScared(false);
        ghost.setEyes();

        adjustPosition(ghost);

        score += 200 * Math.pow(2, ghostsEaten);
        ghostsEaten += 1;
    }

    // The following methods are all used to save / load a game state.
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
        this.pinkyLimit = gameState.getPinkyLimit();
        this.inkyCounter = gameState.getInkyCounter();
        this.inkyLimit = gameState.getInkyLimit();
        this.clydeCounter = gameState.getClydeCounter();
        this.clydeLimit = gameState.getClydeLimit();
        this.eatenCoolDown = gameState.getEatenCoolDown();
        this.scaredCounter = gameState.getScaredCounter();
        this.currentMode = gameState.getCurrentMode();
        this.modeCounter = gameState.getModeCounter();
        ghosts = new ArrayList<>();
        ghosts.add(blinky);
        ghosts.add(pinky);
        ghosts.add(inky);
        ghosts.add(clyde);
    }

    Sprite getPacman() {
        return pacman;
    }

    ArrayList<Sprite> getPillsList() {
        return pillsList;
    }

    ArrayList<Sprite> getPowerPillsList() {
        return powerPillsList;
    }

    int getScore() {
        return score;
    }

    Ghost getBlinky() {
        return blinky;
    }

    Ghost getPinky() {
        return pinky;
    }

    Ghost getInky() {
        return inky;
    }

    Ghost getClyde() {
        return clyde;
    }

    int getGhostsEaten() {
        return ghostsEaten;
    }

    int getPinkyCounter() {
        return pinkyCounter;
    }

    int getInkyCounter() {
        return inkyCounter;
    }

    int getClydeCounter() {
        return clydeCounter;
    }

    int getEatenCoolDown() {
        return eatenCoolDown;
    }

    int getScaredCounter() {
        return scaredCounter;
    }

    int getCurrentMode() {
        return currentMode;
    }

    int getModeCounter() {
        return modeCounter;
    }

    int getPinkyLimit() {
        return pinkyLimit;
    }

    int getInkyLimit() {
        return inkyLimit;
    }

    int getClydeLimit() {
        return clydeLimit;
    }
}