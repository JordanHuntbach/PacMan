package com.jordan;

import java.util.ArrayList;

class State {
    private final Position pacmanPosition;
    private final ArrayList<Sprite> pillsList;
    private final ArrayList<Sprite> powerPillsList;
    private final int score;
    private final Ghost blinky;
    private final Ghost pinky;
    private final Ghost inky;
    private final Ghost clyde;
    private final int ghostsEaten;
    private final int pinkyCounter;
    private final int pinkyLimit;
    private final int inkyCounter;
    private final int inkyLimit;
    private final int clydeCounter;
    private final int clydeLimit;
    private final int eatenCoolDown;
    private final int scaredCounter;
    private final int modeCounter;
    private final int currentMode;

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

    Position getPacmanPosition() {
        return pacmanPosition;
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

    int getModeCounter() {
        return modeCounter;
    }

    int getCurrentMode() {
        return currentMode;
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
