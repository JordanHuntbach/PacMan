import javafx.application.Application;
import javafx.application.Platform;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

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

    private IntValue score = new IntValue(0);
    private IntValue lives = new IntValue(2);
    private IntValue pinkyCounter = new IntValue(0);
    private IntValue inkyCounter = new IntValue(0);
    private IntValue clydeCounter = new IntValue(0);
    private IntValue eatenCoolDown = new IntValue(0);

    private IntValue scaredCounter = new IntValue(-1);

    private AtomicReference<String> currentDirection;
    private AtomicReference<String> nextDirection;

    private Stage mainStage;

    private IntValue mouthPause = new IntValue(0);
    private AtomicReference<Boolean> mouthOpen = new AtomicReference<>();

    private boolean ai = true; // FALSE LETS YOU CONTROL PAC-MAN, TRUE LETS AI DO IT
    private boolean debug = false;

    private boolean simulation = false;
    private State realState;
    private MCTS mcts;
    private int roundsCounter;
    private int movesCounter;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        mainStage.setTitle("Pac-Man");
        newGame();
    }

    private void newGame() {
        Group root = new Group();
        Scene scene = new Scene(root, 592, 720);
        mainStage.setScene(scene);
        mainStage.show();

        canvas = new Canvas(592,720);
        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font("Helvetica", FontWeight.BOLD,24));
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        pacman = new Sprite();
        currentDirection = new AtomicReference<>();
        nextDirection = new AtomicReference<>();

        resetPacman();

        if (!ai) {
            scene.setOnKeyPressed(
                e -> {
                    String direction = e.getCode().toString();
                    switch (direction) {
                        case "UP":
                            nextDirection.set("UP");
                            break;
                        case "DOWN":
                            nextDirection.set("DOWN");
                            break;
                        case "LEFT":
                            nextDirection.set("LEFT");
                            break;
                        case "RIGHT":
                            nextDirection.set("RIGHT");
                            break;
                    }
                });
        }

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

        score = new IntValue(0);
        lives = new IntValue(2);
        pinkyCounter.value = 0;
        inkyCounter.value = 0;
        clydeCounter.value = 0;
        ghostsEaten = 0;

        if (ai) {
            nextDirection.set("LEFT");
        }

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
                            nextDirection.set(next);
                        } else if (movesCounter < MCTS.MAX_MOVES) {
                            nextDirection.set(PacManController.getNextDirection(pacman));
                        } else {
                            mcts.backPropagation(score.value);
                            restoreFromState(realState);
                        }
                    } else {
                        simulation = false;
                        restoreFromState(realState);
                        for (Ghost ghost : ghosts) {
                            ghost.setSpeedUp(false);
                        }
                        nextDirection.set(mcts.evaluateTree());
                    }
                }

                updatePacman();

                eatPills();

                if (simulation) {
                    eatenCoolDown.value += 5;
                } else {
                    eatenCoolDown.value += 1;
                }

                if (eatenCoolDown.value == 240) {
                    eatenCoolDown.value = 0;
                    if (!pinky.isActive()) {
                        pinky.setActive();
                    } else if (!inky.isActive()) {
                        inky.setActive();
                    } else if (!clyde.isActive()) {
                        clyde.setActive();
                    }
                }

                if (pillsList.isEmpty() && powerPillsList.isEmpty()) {
                    gameOver(true);
                    this.stop();
                }

                if (scaredCounter.value >= 0) {
                    if (simulation) {
                        scaredCounter.value += 5;
                    } else {
                        scaredCounter.value += 1;
                    }
                    if (scaredCounter.value > 500) {
                        scaredGhosts(false);
                        updateGhosts(null);
                    } else if (scaredCounter.value > 350) {
                        if ((scaredCounter.value / 10) % 2 == 0) {
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

//                if (!simulation) {
                    updateScreen();
//                }

                for (Ghost ghost : ghosts) {
                    if (ghost.canCatch(pacman)) {
                        if (ghost.isSpooked()) {
                            ghostEaten(ghost);
                        } else if (!ghost.isEyes()) {
                            if (simulation) {
                                mcts.backPropagation(score.value);
                                restoreFromState(realState);
                                break;
                            } else if (lives.value > 0) {
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

        switch (nextDirection.get()) {
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
                    currentDirection.set("UP");
                    nextDirection.set("");
                    if (mouthOpen.get()) {
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
                    currentDirection.set("DOWN");
                    nextDirection.set("");
                    if (mouthOpen.get()) {
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
                    currentDirection.set("LEFT");
                    nextDirection.set("");
                    if (mouthOpen.get()) {
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
                    currentDirection.set("RIGHT");
                    nextDirection.set("");
                    if (mouthOpen.get()) {
                        pacman.setImage("Sprites/Pac-Man/pacmanR.png");
                    }
                }
                break;
        }

        mouthPause.value += 1;

        if (!alreadyMoved){
            switch (currentDirection.get()) {
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

            if (moved && mouthPause.value > 5) {
                mouthPause.value = 0;
                if (mouthOpen.get()) {
                    mouthOpen.set(false);
                    pacman.setImage("Sprites/Pac-Man/pacman.png");
                } else {
                    mouthOpen.set(true);
                    pacman.setImage("Sprites/Pac-Man/pacman" + currentDirection.get().substring(0, 1) + ".png");
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
                score.value += 10;
                if (!pinky.isActive()) {
                    pinkyCounter.value += 1;
                } else if (!inky.isActive()) {
                    inkyCounter.value += 1;
                } if (!clyde.isActive()) {
                    clydeCounter.value += 1;
                }
                eatenCoolDown.value = 0;
            }
        }
        Iterator<Sprite> powerPills = powerPillsList.iterator();
        while (powerPills.hasNext()) {
            Sprite powerPill = powerPills.next();
            if (pacman.canEat(powerPill)) {
                powerPills.remove();
                score.value += 50;
                scaredGhosts(true);
                ghostsEaten = 0;
                if (!pinky.isActive()) {
                    pinkyCounter.value += 1;
                } else if (!inky.isActive()) {
                    inkyCounter.value += 1;
                } if (!clyde.isActive()) {
                    clydeCounter.value += 1;
                }
                eatenCoolDown.value = 0;
            }
        }
        if (!pinky.isActive() && pinkyCounter.value == 3) {
            pinky.setActive();
        } else if (!inky.isActive() && inkyCounter.value == 30) {
            inky.setActive();
        } else if (clyde.isActive() && clydeCounter.value == 60) {
            clyde.setActive();
        }
    }

    private void updateScreen() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        pacman.render(gc);

        for (Sprite pill : pillsList ) {
            pill.render(gc);
        }
        for (Sprite powerPill : powerPillsList ) {
            powerPill.render(gc);
        }
        for (Sprite wall : wallsList ) {
            wall.render(gc);
        }
        for (Ghost ghost : ghosts) {
            ghost.render(gc);
        }

        String pointsText = "Score: " + (score.value);
        gc.setFill(Color.WHITE);
        gc.fillText(pointsText, 20, 670 );
        gc.strokeText(pointsText, 20, 670 );

        String livesText = "Lives: " + (lives.value);
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
            scaredCounter.value = 0;
        } else {
            scaredCounter.value = -1;
        }

        for (Ghost ghost : ghosts) {
            adjustPosition(ghost);
            if (!ghost.isEyes()) {
                ghost.setScared(scared);
            }
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
        currentDirection.set("R");
        if (ai) {
            nextDirection.set("RIGHT");
        } else {
            nextDirection.set("");
        }
        mouthOpen.set(true);
        mouthPause.value = 0;
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
        scaredCounter.value = -1;
    }

    private void lostLife() {
        lives.value -= 1;
        resetGhosts();
        resetPacman();
        updateScreen();

        pinkyCounter.value = -4;
        inkyCounter.value = 20;
        clydeCounter.value = 52;

        try {
            Thread.sleep(1500);
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

        Text scoreText = new Text("Final Score: " + score.value);
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

        Scene scene = new Scene(grid, 592,720);
        mainStage.setScene(scene);
    }

    private void ghostEaten(Ghost ghost) {
        ghost.setScared(false);
        ghost.setEyes(true);

        adjustPosition(ghost);

        score.value += 200 * Math.pow(2, ghostsEaten);
        ghostsEaten += 1;
    }

    private State getGameState() {
        return new State(this);
    }

    private void restoreFromState(State gameState) {
        this.pacman.setPosition(gameState.getPacmanPosition().getPositionX(), gameState.getPacmanPosition().getPositionY());
        this.pillsList = new ArrayList<>(gameState.getPillsList());
        this.powerPillsList = new ArrayList<>(gameState.getPowerPillsList());
        this.score.value = gameState.getScore();
        this.blinky = new Ghost(gameState.getBlinky());
        this.pinky = new Ghost(gameState.getPinky());
        this.inky = new Ghost(gameState.getInky());
        this.clyde = new Ghost(gameState.getClyde());
        this.ghostsEaten = gameState.getGhostsEaten();
        this.pinkyCounter.value = gameState.getPinkyCounter();
        this.inkyCounter.value = gameState.getInkyCounter();
        this.clydeCounter.value = gameState.getClydeCounter();
        this.eatenCoolDown.value = gameState.getEatenCoolDown();
        this.scaredCounter.value = gameState.getScaredCounter();
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
        return score.value;
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
        return pinkyCounter.value;
    }

    public int getInkyCounter() {
        return inkyCounter.value;
    }

    public int getClydeCounter() {
        return clydeCounter.value;
    }

    public int getEatenCoolDown() {
        return eatenCoolDown.value;
    }

    public int getScaredCounter() {
        return scaredCounter.value;
    }


}