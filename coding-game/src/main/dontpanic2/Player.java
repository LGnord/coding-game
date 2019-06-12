package dontpanic2;

import java.util.*;



enum Action {
    WAIT(1), BLOCK(3), ELEVATOR(3);

    int cost;

    Action(int cost) {
        this.cost = cost;
    }
}

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Board b = new Board(in);

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            String direction = in.next(); // direction of the leading clone: LEFT or RIGHT
            Position current = new Position(cloneFloor, clonePos, direction);
            Action action = b.getBestAction(current);
            System.out.println(action); // action: WAIT or BLOCK or ELEVATOR
        }
    }
}

class Board {
    int width;
    int exitFloor;
    int nbRounds;
    int exitPos;
    int nbFloors;
    int nbTotalClones;
    int nbAdditionalElevators;
    int nbElevators;
    Floor[] floors;
    Map<Position, Action> bestAction;

    public Board(Scanner in) {
        nbFloors = in.nextInt(); // number of floors
        width = in.nextInt(); // width of the area
        nbRounds = in.nextInt(); // maximum number of rounds
        exitFloor = in.nextInt(); // floor on which the exit is found
        exitPos = in.nextInt(); // position of the exit on its floor
        nbTotalClones = in.nextInt(); // number of generated clones
        nbAdditionalElevators = in.nextInt(); // number of additional elevators that you can build
        nbElevators = in.nextInt(); // number of elevators
        floors = new Floor[nbFloors];
        Arrays.fill(floors, new Floor());
        for (int i = 0; i < nbElevators; i++) {
            int elevatorFloor = in.nextInt(); // floor on which this elevator is found
            int elevatorPos = in.nextInt(); // position of the elevator on its floor
            floors[elevatorFloor].add(elevatorPos);
        }
        bestAction = bestAction();
    }

    private Map<Position,Action> bestAction() {
        Map<Position,Action> toBeReturned = new HashMap<>();

        return toBeReturned;
    }
    

    public Action getBestAction(Position p) {
        return bestAction.getOrDefault(p, Action.WAIT);
    }
}

class Position {
    int cloneFloor;
    int clonePos;
    String direction;

    public Position(int cloneFloor, int clonePos, String direction) {
        this.cloneFloor = cloneFloor;
        this.clonePos = clonePos;
        this.direction = direction;
    }
}

class Floor {

    List<Integer> positions = new ArrayList<>();

    public void add(int elevatorPos) {
        positions.add(elevatorPos);
    }
}