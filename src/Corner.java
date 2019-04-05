import javafx.geometry.Rectangle2D;

public class Corner extends Sprite {
    private String type;

    Corner(String cornerType) {
        positionX = 0;
        positionY = 0;
        type = cornerType;
    }

    private Rectangle2D getBoundary1() {
        switch(type){
            case "TR":
            case "BR":
                return new Rectangle2D(positionX, positionY, 2, height);
            case "TL":
            case "BL":
                return new Rectangle2D(positionX + width - 2, positionY, 2, height);
            default:
                return new Rectangle2D(positionX, positionY, width, height);
        }

    }

    private Rectangle2D getBoundary2() {
        switch(type){
            case "TR":
            case "TL":
                return new Rectangle2D(positionX, positionY + height - 2, width, 2);
            case "BR":
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
