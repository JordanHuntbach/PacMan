import javafx.geometry.Rectangle2D;

public class Corner extends Sprite {
    private String type;

    public Corner(String cornerType) {
        positionX = 0;
        positionY = 0;
        type = cornerType;
    }

    public Rectangle2D getBoundary1() {
        switch(type){
            case "TR":
                return new Rectangle2D(positionX, positionY, 2, height);
            case "TL":
                return new Rectangle2D(positionX + width - 2, positionY, 2, height);
            case "BR":
                return new Rectangle2D(positionX, positionY, 2, height);
            case "BL":
                return new Rectangle2D(positionX + width - 2, positionY, 2, height);
            default:
                return new Rectangle2D(positionX, positionY, width, height);
        }

    }

    public Rectangle2D getBoundary2() {
        switch(type){
            case "TR":
                return new Rectangle2D(positionX, positionY + height - 2, width, 2);
            case "TL":
                return new Rectangle2D(positionX, positionY + height - 2, width, 2);
            case "BR":
                return new Rectangle2D(positionX, positionY, width, 2);
            case "BL":
                return new Rectangle2D(positionX, positionY, width, 2);
            default:
                return new Rectangle2D(positionX, positionY, width, height);
        }
    }

    public boolean intersects(Sprite s) {
        return (s.getBoundary().intersects(this.getBoundary1()) || s.getBoundary().intersects(this.getBoundary2()));
    }
}
