package com.jordan;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

public class Sprite
{
    private Image image;
    double positionX;
    double positionY;
    double velocityX;
    double velocityY;
    double width;
    double height;

    Sprite() {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    void setImage(Image i) {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    Image getImage() {
        return image;
    }

    void setImage(String filename) {
        Image i = new Image(filename);
        setImage(i);
    }

    double getPositionX() {
        return positionX;
    }

    double getPositionY() {
        return positionY;
    }

    Position getPosition() {
        return new Position(positionX, positionY);
    }

    void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
    }

    void setPosition(Position position) {
        positionX = position.getPositionX();
        positionY = position.getPositionY();
    }

    void setVelocity(double x, double y) {
        velocityX = x;
        velocityY = y;
    }

    void update() {
        positionX += velocityX;
        positionY += velocityY;
    }

    void undo() {
        positionX -= velocityX;
        positionY -= velocityY;
    }

    void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    Rectangle2D eatBoundary() {
        return new Rectangle2D(positionX + 7, positionY + 7, width - 14, height - 14);
    }

    Rectangle2D pillBoundary() {
        return new Rectangle2D(positionX + 2, positionY + 2, width - 4, height - 4);
    }

    boolean canEat(Sprite pill) {
        return this.eatBoundary().intersects(pill.pillBoundary());
    }

    public boolean intersects(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public String toString() {
        return "Position: [" + (double)Math.round(positionX * 100d) / 100d + "," + (double)Math.round(positionY * 100d) / 100d + "]";
    }
}