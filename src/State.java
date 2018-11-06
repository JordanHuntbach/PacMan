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
    private int inkyCounter;
    private int clydeCounter;
    private int eatenCoolDown;
    private int scaredCounter;

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
        this.inkyCounter = game.getInkyCounter();
        this.clydeCounter = game.getClydeCounter();
        this.eatenCoolDown = game.getEatenCoolDown();
        this.scaredCounter = game.getScaredCounter();
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
}
