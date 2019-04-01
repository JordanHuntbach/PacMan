import java.util.ArrayList;

public class State {
    private Position pacmanPosition;
    private ArrayList<Sprite> pillsList;
    private ArrayList<Sprite> powerPillsList;
    private int score;
    private Ghost blinky;
    private Ghost pinky;
    private Ghost inky;
    private Ghost clyde;
    private int ghostsEaten;
    private int pinkyCounter;
    private int pinkyLimit;
    private int inkyCounter;
    private int inkyLimit;
    private int clydeCounter;
    private int clydeLimit;
    private int eatenCoolDown;
    private int scaredCounter;
    private int modeCounter;
    private int currentMode;

    State(Game game) {
        Sprite pacman = game.getPacman();
        this.pacmanPosition = new Position(pacman.getPositionX(), pacman.getPositionY());
        this.pillsList = new ArrayList<>(game.getPillsList());
        this.powerPillsList = new ArrayList<>(game.getPowerPillsList());
        this.score = game.getScore();
        this.blinky = new Ghost(game.getBlinky());
        this.pinky = new Ghost(game.getPinky());
        this.inky = new Ghost(game.getInky());
        this.clyde = new Ghost(game.getClyde());
        this.ghostsEaten = game.getGhostsEaten();
        this.pinkyCounter = game.getPinkyCounter();
        this.pinkyLimit = game.getPinkyLimit();
        this.inkyCounter = game.getInkyCounter();
        this.inkyLimit = game.getInkyLimit();
        this.clydeCounter = game.getClydeCounter();
        this.clydeLimit = game.getClydeLimit();
        this.eatenCoolDown = game.getEatenCoolDown();
        this.scaredCounter = game.getScaredCounter();
        this.modeCounter = game.getModeCounter();
        this.currentMode = game.getCurrentMode();
    }

    public Position getPacmanPosition() {
        return pacmanPosition;
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

    public int getModeCounter() {
        return modeCounter;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public int getPinkyLimit() {
        return pinkyLimit;
    }

    public int getInkyLimit() {
        return inkyLimit;
    }

    public int getClydeLimit() {
        return clydeLimit;
    }
}
