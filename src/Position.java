import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Position {
    private double positionX;
    private double positionY;

    Position(double x, double y) {
        positionX = x;
        positionY = y;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public String toString() {
        return "[" + positionX + "," + positionY + "]";
    }

    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Position)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Position p = (Position) o;

        // Compare the data members and return accordingly
        return Double.compare(positionX, p.positionX) == 0 && Double.compare(positionY, p.positionY) == 0;
    }

    // Some code

    static HashMap<Position, Integer> junctions = new HashMap<Position, Integer>() {{
        put(new Position(27, 27), 4);
        put(new Position(127, 27), 8);
        put(new Position(247, 27), 3);
        put(new Position(307, 27), 4);
        put(new Position(427, 27), 8);
        put(new Position(527, 27), 3);
        put(new Position(27, 107), 6);
        put(new Position(127, 107), 9);
        put(new Position(187, 107), 8);
        put(new Position(247, 107), 7);
        put(new Position(307, 107), 7);
        put(new Position(367, 107), 8);
        put(new Position(427, 107), 9);
        put(new Position(527, 107), 5);
        put(new Position(27, 167), 2);
        put(new Position(127, 167), 5);
        put(new Position(187, 167), 2);
        put(new Position(247, 167), 3);
        put(new Position(307, 167), 4);
        put(new Position(367, 167), 1);
        put(new Position(427, 167), 6);
        put(new Position(527, 167), 1);
        put(new Position(187, 227), 4);
        put(new Position(247, 227), 7);
        put(new Position(307, 227), 7);
        put(new Position(367, 227), 3);
        put(new Position(127, 287), 8);
        put(new Position(187, 287), 5);
        put(new Position(367, 287), 6);
        put(new Position(427, 287), 8);
        put(new Position(187, 347), 6);
        put(new Position(367, 347), 5);
        put(new Position(27, 407), 4);
        put(new Position(127, 407), 9);
        put(new Position(187, 407), 7);
        put(new Position(247, 407), 3);
        put(new Position(307, 407), 4);
        put(new Position(367, 407), 7);
        put(new Position(427, 407), 8);
        put(new Position(527, 407), 3);
        put(new Position(27, 467), 2);
        put(new Position(67, 467), 3);
        put(new Position(127, 467), 6);
        put(new Position(187, 467), 8);
        put(new Position(247, 467), 7);
        put(new Position(307, 467), 7);
        put(new Position(367, 467), 8);
        put(new Position(427, 467), 5);
        put(new Position(487, 467), 4);
        put(new Position(527, 467), 1);
        put(new Position(27, 527), 4);
        put(new Position(67, 527), 7);
        put(new Position(127, 527), 1);
        put(new Position(187, 527), 2);
        put(new Position(247, 527), 3);
        put(new Position(307, 527), 4);
        put(new Position(367, 527), 1);
        put(new Position(427, 527), 2);
        put(new Position(487, 527), 7);
        put(new Position(527, 527), 3);
        put(new Position(27, 587), 2);
        put(new Position(247, 587), 7);
        put(new Position(307, 587), 7);
        put(new Position(527, 587), 1);
        put(new Position(277, 227), 0);
    }};

    // Each array specifies if a junction is connected via the up, down, left, and right directions respectively.
    static boolean[][] directionOptions = new boolean[][] {
            // Special case for the start location.
            new boolean[] {false, false, true, true}, // 0: START
            // Corners
            new boolean[] {true, false, true, false}, // 1: UP + LEFT
            new boolean[] {true, false, false, true}, // 2: UP + RIGHT
            new boolean[] {false, true, true, false}, // 3: DOWN + LEFT
            new boolean[] {false, true, false, true}, // 4: DOWN + RIGHT
            // T-Junctions
            new boolean[] {true, true, true, false}, // 5: UP, DOWN, LEFT
            new boolean[] {true, true, false, true}, // 6: UP, DOWN, RIGHT
            new boolean[] {true, false, true, true}, // 7: UP, LEFT, RIGHT
            new boolean[] {false, true, true, true}, // 8: DOWN, LEFT, RIGHT
            // Cross-Roads
            new boolean[] {true, true, true, true}, // 9: CROSS ROADS
    };


    // A* Search Code

    Position pathParent;
    float costFromStart;
    float estimatedCostToGoal;
    private ArrayList<Position> neighbours = new ArrayList<>();

    private float getSumCost() {
        return costFromStart + estimatedCostToGoal;
    }

    public int compareTo(Position other) {
        return Float.compare(this.getSumCost(), other.getSumCost());
    }

    public float getCost(Position node){
        return (float) Math.abs(node.getPositionX() - this.positionX) + (float) Math.abs(node.getPositionY() - this.positionY);
    }

    //The estimated cost should never exceed the true cost. The better the estimate, the more efficient the search.
    public float getEstimatedCost(Position node){
        return (float) Math.abs(node.getPositionX() - this.positionX) + (float) Math.abs(node.getPositionY() - this.positionY);
    }

    public List<Position> getNeighbours(){
        return neighbours;
    }

    private void addNeighbour(Position position){
        neighbours.add(position);
    }

    private void addNeighbourAndBack(Position position){
        neighbours.add(position);
        position.addNeighbour(this);
    }

    static List<Position> initialisePositions() {
        Position position1 = new Position(27, 27);
        Position position2 = new Position(127, 27);
        Position position3 = new Position(247, 27);
        Position position4 = new Position(307, 27);
        Position position5 = new Position(427, 27);
        Position position6 = new Position(527, 27);
        Position position7 = new Position(27, 107);
        Position position8 = new Position(127, 107);
        Position position9 = new Position(187, 107);
        Position position10 = new Position(247, 107);
        Position position11 = new Position(307, 107);
        Position position12 = new Position(367, 107);
        Position position13 = new Position(427, 107);
        Position position14 = new Position(527, 107);
        Position position15 = new Position(27, 167);
        Position position16 = new Position(127, 167);
        Position position17 = new Position(187, 167);
        Position position18 = new Position(247, 167);
        Position position19 = new Position(307, 167);
        Position position20 = new Position(367, 167);
        Position position21 = new Position(427, 167);
        Position position22 = new Position(527, 167);
        Position position23 = new Position(187, 227);
        Position position24 = new Position(247, 227);
        Position position25 = new Position(307, 227);
        Position position26 = new Position(367, 227);
        Position position27 = new Position(127, 287);
        Position position28 = new Position(187, 287);
        Position position29 = new Position(367, 287);
        Position position30 = new Position(427, 287);
        Position position31 = new Position(187, 347);
        Position position32 = new Position(367, 347);
        Position position33 = new Position(27, 407);
        Position position34 = new Position(127, 407);
        Position position35 = new Position(187, 407);
        Position position36 = new Position(247, 407);
        Position position37 = new Position(307, 407);
        Position position38 = new Position(367, 407);
        Position position39 = new Position(427, 407);
        Position position40 = new Position(527, 407);
        Position position41 = new Position(27, 467);
        Position position42 = new Position(67, 467);
        Position position43 = new Position(127, 467);
        Position position44 = new Position(187, 467);
        Position position45 = new Position(247, 467);
        Position position46 = new Position(307, 467);
        Position position47 = new Position(367, 467);
        Position position48 = new Position(427, 467);
        Position position49 = new Position(487, 467);
        Position position50 = new Position(527, 467);
        Position position51 = new Position(27, 527);
        Position position52 = new Position(67, 527);
        Position position53 = new Position(127, 527);
        Position position54 = new Position(187, 527);
        Position position55 = new Position(247, 527);
        Position position56 = new Position(307, 527);
        Position position57 = new Position(367, 527);
        Position position58 = new Position(427, 527);
        Position position59 = new Position(487, 527);
        Position position60 = new Position(527, 527);
        Position position61 = new Position(27, 587);
        Position position62 = new Position(247, 587);
        Position position63 = new Position(307, 587);
        Position position64 = new Position(527, 587);
        Position position65 = new Position(277, 227);

        // Row 1
        position1.addNeighbourAndBack(position2);
        position1.addNeighbourAndBack(position7);
        position2.addNeighbourAndBack(position3);
        position2.addNeighbourAndBack(position8);
        position3.addNeighbourAndBack(position10);

        position4.addNeighbourAndBack(position5);
        position4.addNeighbourAndBack(position11);
        position5.addNeighbourAndBack(position6);
        position5.addNeighbourAndBack(position13);
        position6.addNeighbourAndBack(position14);

        // Row 2
        position7.addNeighbourAndBack(position8);
        position7.addNeighbourAndBack(position15);
        position8.addNeighbourAndBack(position9);
        position8.addNeighbourAndBack(position16);
        position9.addNeighbourAndBack(position10);
        position9.addNeighbourAndBack(position17);
        position10.addNeighbourAndBack(position11);

        position11.addNeighbourAndBack(position12);
        position12.addNeighbourAndBack(position13);
        position12.addNeighbourAndBack(position20);
        position13.addNeighbourAndBack(position14);
        position13.addNeighbourAndBack(position21);
        position14.addNeighbourAndBack(position22);

        // Row 3
        position15.addNeighbourAndBack(position16);
        position16.addNeighbourAndBack(position27);
        position17.addNeighbourAndBack(position18);
        position18.addNeighbourAndBack(position24);

        position19.addNeighbourAndBack(position20);
        position19.addNeighbourAndBack(position25);
        position21.addNeighbourAndBack(position22);
        position21.addNeighbourAndBack(position30);

        // Row 4
        position23.addNeighbourAndBack(position24);
        position23.addNeighbourAndBack(position28);
        position24.addNeighbourAndBack(position25);
        position25.addNeighbourAndBack(position26);
        position26.addNeighbourAndBack(position29);

        position65.addNeighbourAndBack(position24);
        position65.addNeighbourAndBack(position25);

        // Row 5
        position27.addNeighbourAndBack(position28);
        position27.addNeighbourAndBack(position30);
        position27.addNeighbourAndBack(position34);
        position28.addNeighbourAndBack(position31);

        position29.addNeighbourAndBack(position30);
        position29.addNeighbourAndBack(position32);
        position30.addNeighbourAndBack(position39);

        // Row 6
        position31.addNeighbourAndBack(position32);
        position31.addNeighbourAndBack(position35);
        position32.addNeighbourAndBack(position38);

        // Row 7
        position33.addNeighbourAndBack(position34);
        position33.addNeighbourAndBack(position41);
        position34.addNeighbourAndBack(position35);
        position34.addNeighbourAndBack(position43);
        position35.addNeighbourAndBack(position36);
        position36.addNeighbourAndBack(position45);

        position37.addNeighbourAndBack(position38);
        position37.addNeighbourAndBack(position46);
        position38.addNeighbourAndBack(position39);
        position39.addNeighbourAndBack(position40);
        position39.addNeighbourAndBack(position48);
        position40.addNeighbourAndBack(position50);

        // Row 8
        position41.addNeighbourAndBack(position42);
        position42.addNeighbourAndBack(position52);
        position43.addNeighbourAndBack(position44);
        position43.addNeighbourAndBack(position53);
        position44.addNeighbourAndBack(position45);
        position44.addNeighbourAndBack(position54);
        position45.addNeighbourAndBack(position46);

        position46.addNeighbourAndBack(position47);
        position47.addNeighbourAndBack(position48);
        position47.addNeighbourAndBack(position57);
        position48.addNeighbourAndBack(position58);
        position49.addNeighbourAndBack(position50);
        position49.addNeighbourAndBack(position59);

        // Row 9
        position51.addNeighbourAndBack(position52);
        position51.addNeighbourAndBack(position61);
        position52.addNeighbourAndBack(position53);
        position54.addNeighbourAndBack(position55);
        position55.addNeighbourAndBack(position62);

        position56.addNeighbourAndBack(position57);
        position56.addNeighbourAndBack(position63);
        position58.addNeighbourAndBack(position59);
        position59.addNeighbourAndBack(position60);
        position60.addNeighbourAndBack(position64);

        // Row 10
        position61.addNeighbourAndBack(position62);
        position62.addNeighbourAndBack(position63);
        position63.addNeighbourAndBack(position64);

        List<Position> positions = new ArrayList<>();
        positions.add(position1);
        positions.add(position2);
        positions.add(position3);
        positions.add(position4);
        positions.add(position5);
        positions.add(position6);
        positions.add(position7);
        positions.add(position8);
        positions.add(position9);
        positions.add(position10);
        positions.add(position11);
        positions.add(position12);
        positions.add(position13);
        positions.add(position14);
        positions.add(position15);
        positions.add(position16);
        positions.add(position17);
        positions.add(position18);
        positions.add(position19);
        positions.add(position20);
        positions.add(position21);
        positions.add(position22);
        positions.add(position23);
        positions.add(position24);
        positions.add(position25);
        positions.add(position26);
        positions.add(position27);
        positions.add(position28);
        positions.add(position29);
        positions.add(position30);
        positions.add(position31);
        positions.add(position32);
        positions.add(position33);
        positions.add(position34);
        positions.add(position35);
        positions.add(position36);
        positions.add(position37);
        positions.add(position38);
        positions.add(position39);
        positions.add(position40);
        positions.add(position41);
        positions.add(position42);
        positions.add(position43);
        positions.add(position44);
        positions.add(position45);
        positions.add(position46);
        positions.add(position47);
        positions.add(position48);
        positions.add(position49);
        positions.add(position50);
        positions.add(position51);
        positions.add(position52);
        positions.add(position53);
        positions.add(position54);
        positions.add(position55);
        positions.add(position56);
        positions.add(position57);
        positions.add(position58);
        positions.add(position59);
        positions.add(position60);
        positions.add(position61);
        positions.add(position62);
        positions.add(position63);
        positions.add(position64);
        positions.add(position65);

        return positions;
    }

    public static Position getNearestPosition(double x, double y){
        Position best = null;
        double minDistance = 1000;
        for (Position position : initialisePositions()) {
            double posX = position.getPositionX();
            double posY = position.getPositionY();
            double distance = Math.abs(posX - x) + Math.abs(posY - y);
            if (distance == 0) {
                return position;
            } else if (distance < minDistance) {
                best = position;
                minDistance = distance;
            }
        }
        return best;
    }

    public static boolean isJunction(double x, double y) {
        for (Position position : junctions.keySet()) {
            if (x == position.getPositionX() && y == position.getPositionY()) {
                return true;
            }
        }
        return false;
    }
}
