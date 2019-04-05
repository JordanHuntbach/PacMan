import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class PacManController {

    static String getNextDirection(Sprite pacman) {

        Position junction = null;

        for (Position position : Position.junctions.keySet()) {
            if (pacman.positionX == position.getPositionX() && pacman.positionY == position.getPositionY()) {
                junction = position;
                break;
            }
        }

        if (junction != null) {
            ArrayList<String> directions = new ArrayList<>(Arrays.asList("UP", "DOWN", "LEFT", "RIGHT"));

            int type = Position.junctions.get(junction);
            boolean[] options = Position.directionOptions[type];
            ArrayList<String> validDirections = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (options[i]) {
                    validDirections.add(directions.get(i));
                }
            }
            Random random = new Random();
            int randomIndex = random.nextInt(validDirections.size());
            return validDirections.get(randomIndex);
        } else {
            return null;
        }

    }

}
