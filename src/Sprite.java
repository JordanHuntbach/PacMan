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

    public Image getImage() {
        return image;
    }

    public void setImage(String filename) {
        Image i = new Image(filename);
        setImage(i);
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public Position getPosition() {
        return new Position(positionX, positionY);
    }

    public void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
    }

    public void setPosition(Position position) {
        positionX = position.getPositionX();
        positionY = position.getPositionY();
    }

    public void setVelocity(double x, double y) {
        velocityX = x;
        velocityY = y;
    }

    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }

    public void undo() {
        positionX -= velocityX;
        positionY -= velocityY;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    public Rectangle2D eatBoundary() {
        return new Rectangle2D(positionX + 10, positionY + 10, width - 20, height - 20);
    }

    public boolean canEat(Sprite s) {
        return s.getBoundary().intersects(this.eatBoundary());
    }

    public boolean intersects(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public String toString() {
        return "Position: [" + (double)Math.round(positionX * 100d) / 100d + "," + (double)Math.round(positionY * 100d) / 100d + "]";
    }
}