import javafx.geometry.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Ghost extends Sprite {

    enum ghostName {
        BLINKY,
        INKY,
        PINKY,
        CLYDE
    }

    private ghostName name;

    private int SPEED = 2;

    private boolean chase = true;
    private boolean scatter = true;
    private Position scatterTarget;

    private AStarSearch search = new AStarSearch();

    private boolean spooked = false;
    private boolean active = true;
    private boolean eyes = false;

    private boolean exitingDoor = false;
    private boolean enteringDoor = false;

    private String filepath;

    private Position home;

    Ghost(String ghostName, Position scatterTarget) {
        filepath = "Sprites/Ghosts/" + ghostName.substring(0, 1).toUpperCase() + ghostName.substring(1) + "/" + ghostName;
        this.scatterTarget = scatterTarget;
        switch (ghostName) {
            case "blinky":
                home = new Position(277, 227);
                setPosition(home);
                this.name = Ghost.ghostName.BLINKY;
                break;
            case "pinky":
                home = new Position(277, 287);
                setPosition(home);
                this.name = Ghost.ghostName.PINKY;
                break;
            case "inky":
                home = new Position(237, 287);
                setPosition(home);
                this.name = Ghost.ghostName.INKY;
                break;
            case "clyde":
                home = new Position(317, 287);
                setPosition(home);
                this.name = Ghost.ghostName.CLYDE;
                break;
        }
    }

    Ghost(Ghost ghost) {
        this.eyes = ghost.eyes;
        this.active = ghost.active;
        this.spooked = ghost.spooked;
        this.filepath = ghost.filepath;
        this.chase = ghost.chase;
        this.forwards = ghost.forwards;
        this.backwards = ghost.backwards;
        this.backwardsString = ghost.backwardsString;
        this.positionX = ghost.positionX;
        this.positionY = ghost.positionY;
        this.velocityX = ghost.velocityX;
        this.velocityY = ghost.velocityY;
        this.setImage(ghost.getImage());
        this.scatter = ghost.scatter;
        this.scatterTarget = ghost.scatterTarget;
        this.SPEED = ghost.SPEED;
        this.exitingDoor = ghost.exitingDoor;
        this.enteringDoor = ghost.enteringDoor;
        this.home = ghost.home;
    }

    private int forwards = 0; // 0 = UP, 1 = DOWN, 2 = LEFT, 3 = RIGHT
    private int backwards = 1;
    private String backwardsString = "";

    private static ArrayList<String> directions = new ArrayList<>(Arrays.asList("Up", "Down", "Left", "Right"));

    public void reverse() {
        if (eyes || !active) {
            return;
        }
        switch (backwardsString) {
            case "Up":
                backwardsString = "Down";
                velocityY = -SPEED;
                break;
            case "Down":
                backwardsString = "Up";
                velocityY = SPEED;
                break;
            case "Left":
                backwardsString = "Right";
                velocityX = -SPEED;
                break;
            case "Right":
                backwardsString = "Left";
                velocityX = SPEED;
                break;
        }

        int temp = backwards;
        backwards = forwards;
        forwards = temp;
    }

    public void reset() {
        this.setPosition(home);

        spooked = false;
        active = true;
        eyes = false;

        exitingDoor = false;
        enteringDoor = false;

        this.setImage(filepath + "Up.png");

        forwards = 0;
        backwards = 1;
        backwardsString = "";
    }

    public void setScatter(boolean scatter){
        this.scatter = scatter;
    }

    public void update(String colour, double targetX, double targetY) {
        if (!active) {
            return;
        }

        if (positionX <= -15) {
            positionX += 580;
        } else if (positionX >= 570) {
            positionX -= 580;
        }

        if (eyes && positionX == 277 && positionY == 227) {
            enteringDoor = true;
        }

        Position junction = null;


        for (Position position : Position.junctions.keySet()) {
            if (positionX == position.getPositionX() && positionY == position.getPositionY()) {
                junction = position;
                break;
            }
        }


        if (spooked && colour != null) {
            this.setImage("Sprites/Ghosts/scared" + colour + ".png");
        } else if (eyes) {
            this.setImage("Sprites/Ghosts/eyes.png");
        }

        if (exitingDoor) {
            if (positionX < 277) {
                this.setVelocity(1, 0);
            } else if (positionX > 277) {
                this.setVelocity(-1, 0);
            } else if (positionY > 227) {
                this.setVelocity(0, -1);
            } else if (positionY == 227) {
                this.setVelocity(0, 0);
                exitingDoor = false;
            }
        } else if (enteringDoor) {
            if (positionY < 287) {
                this.setVelocity(0, 2);
            } else if (positionX < home.getPositionX()) {
                this.setVelocity(2, 0);
            } else if (positionX > home.getPositionX()) {
                this.setVelocity(-2, 0);
            } else {
                this.setVelocity(0, 0);
                enteringDoor = false;
                eyes = false;
                active = false;
                this.setImage(filepath + "Up.png");
                if (name == ghostName.BLINKY) {
                    setActive();
                }
            }
        } else if (junction != null) {
            String nextDirection = null;

            if (scatter && !eyes) {
                targetX = scatterTarget.getPositionX();
                targetY = scatterTarget.getPositionY();
            }

            if (chase && !spooked) {
//                // A* Search - replaced with arcade search method
//                Position startNode = search.getNearestPosition(junction.getPositionX(), junction.getPositionY());
//                Position targetNode = search.getNearestPosition(targetX, targetY);
//                Position targetPosition = new Position(targetX, targetY);
//                List<Position> path = search.findPath(startNode, targetNode);
//                if (path == null || path.size() == 0) {
//                    System.out.println("Broken A* path");
//                    nextDirection = getRandomDirection(junction);
//                } else {
//                    Position next = path.get(0);
//                    nextDirection = getDirectionToNeighbour(junction, next);
//                    if (nextDirection.equals(backwardsString)) {
//                        nextDirection = getRandomDirection(junction); // TODO: Better method of preventing reversals.
//                    }
//                }

                // Old school search.
                boolean[] options = Position.directionOptions[Position.junctions.get(junction)];

                // Prevent ghosts turning up in special positions.
                for (Position position: Position.getSpecialPositions()) {
                    if (position.equals(junction)) {
                        options = new boolean[] {false, false, true, true};
                        break;
                    }
                }

                ArrayList<String> excludingBackwards = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (i != backwards && options[i]) {
                        excludingBackwards.add(directions.get(i));
                    }
                }

                double minDistance = Double.MAX_VALUE;
                for (String direction : excludingBackwards) {
                    Position move = junction;
                    switch (direction) {
                        case "Up":
                            move = new Position(this.positionX, this.positionY - 20);
                            break;
                        case "Down":
                            move = new Position(this.positionX, this.positionY + 20);
                            break;
                        case "Left":
                            move = new Position(this.positionX - 20, this.positionY);
                            break;
                        case "Right":
                            move = new Position(this.positionX + 20, this.positionY);
                            break;
                    }
                    Position target = new Position(targetX, targetY);
                    double distance = move.distanceTo(target);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextDirection = direction;
                    }
                }
            } else {
                nextDirection = getRandomDirection(junction);
            }

            if (junction.equals(new Position(127, 287))) {
                if ("Left".equals(nextDirection) || spooked) {
                    SPEED = 1;
                } else {
                    SPEED = 2;
                }
            } else if (junction.equals(new Position(427, 287))) {
                if ("Right".equals(nextDirection) || spooked) {
                    SPEED = 1;
                } else {
                    SPEED = 2;
                }
            }

            switch (nextDirection) {
                case "Up":
                    forwards = 0;
                    backwards = 1;
                    backwardsString = "Down";
                    if (spooked) {
                        this.setImage("Sprites/Ghosts/scared" + colour + ".png");
                    } else if (eyes) {
                        this.setImage("Sprites/Ghosts/eyes.png");
                    } else {
                        this.setImage(filepath + "Up.png");
                    }
                    this.setVelocity(0, -SPEED);
                    break;
                case "Down":
                    forwards = 1;
                    backwards = 0;
                    backwardsString = "Up";
                    if (spooked) {
                        this.setImage("Sprites/Ghosts/scared" + colour + ".png");
                    } else if (eyes) {
                        this.setImage("Sprites/Ghosts/eyes.png");
                    } else {
                        this.setImage(filepath + "Down.png");
                    }
                    this.setVelocity(0, SPEED);
                    break;
                case "Left":
                    forwards = 2;
                    backwards = 3;
                    backwardsString = "Right";
                    if (spooked) {
                        this.setImage("Sprites/Ghosts/scared" + colour + ".png");
                    } else if (eyes) {
                        this.setImage("Sprites/Ghosts/eyes.png");
                    } else {
                        this.setImage(filepath + "Left.png");
                    }
                    this.setVelocity(-SPEED, 0);
                    break;
                case "Right":
                    forwards = 3;
                    backwards = 2;
                    backwardsString = "Left";
                    if (spooked) {
                        this.setImage("Sprites/Ghosts/scared" + colour + ".png");
                    } else if (eyes) {
                        this.setImage("Sprites/Ghosts/eyes.png");
                    } else {
                        this.setImage(filepath + "Right.png");
                    }
                    this.setVelocity(SPEED, 0);
                    break;
            }
        }
        positionX += velocityX;
        positionY += velocityY;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive() {
        active = true;
        exitingDoor = true;
    }

    public void setInactive() {
        active = false;
    }

    public void setScared(boolean scared) {
        if (scared) {
            SPEED = 1;
            this.setImage("Sprites/Ghosts/scaredBlue.png");
            spooked = true;
        } else {
            SPEED = 2;
            this.setImage(filepath + directions.get(forwards) + ".png");
            spooked = false;
        }
        switch (directions.get(forwards)) {
            case "Up":
                this.setVelocity(0, -SPEED);
                break;
            case "Down":
                this.setVelocity(0, SPEED);
                break;
            case "Left":
                this.setVelocity(-SPEED, 0);
                break;
            case "Right":
                this.setVelocity(SPEED, 0);
                break;
        }
    }

    private String getDirectionToNeighbour(Position from, Position to) {
        double fromX = from.getPositionX();
        double fromY = from.getPositionY();
        double toX = to.getPositionX();
        double toY = to.getPositionY();
        if (toY < fromY) {
            return "Up";
        } else if (toY > fromY) {
            return "Down";
        } else if (fromX == 127 && fromY == 287 && toX == 427 && toY == 287) { // Checks for the tunnel.
            return "Left";
        } else if (fromX == 427 && fromY == 287 && toX == 127 && toY == 287) { // Checks for the tunnel.
            return "Right";
        } else if (toX > fromX) {
            return "Right";
        } else {
            return "Left";
        }
    }

    private String getRandomDirection(Position junction) {
        boolean[] options = Position.directionOptions[Position.junctions.get(junction)];
        ArrayList<String> excludingBackwards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != backwards && options[i]) {
                excludingBackwards.add(directions.get(i));
            }
        }
        Random random = new Random();
        int randomIndex = random.nextInt(excludingBackwards.size());
        return excludingBackwards.get(randomIndex);
    }

    private Rectangle2D catchBoundary() {
        return new Rectangle2D(positionX + 10, positionY + 10, width - 20, height - 20);
    }

    public boolean canCatch(Sprite s) {
        return s.eatBoundary().intersects(this.catchBoundary());
    }

    public boolean isSpooked(){
        return spooked;
    }

    public boolean isEyes() {
        return eyes;
    }

    public void setEyes(boolean eyes) {
        this.eyes = eyes;
    }

    public boolean isEnteringDoor() {
        return enteringDoor;
    }
}
