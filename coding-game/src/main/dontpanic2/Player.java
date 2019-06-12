package dontpanic2;

import java.util.*;


enum Action {
    WAIT(1), BLOCK(3), ELEVATOR(3);

    int cost;

    Action(int cost) {
        this.cost = cost;
    }
}

enum Direction {

    LEFT, RIGHT, NONE;

    Direction inverse() {
        if (this == NONE) {
            return NONE;
        }
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

}

class Player {

    public static void main(String args[]) {
        log("Start new game");

        Scanner in = new Scanner(System.in);
        Board b = new Board(in);

        log("Board is ready\n" + b);
        int buildAdditionalElevator = 0;

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            String direction = in.next(); // direction of the leading clone: LEFT or RIGHT
            Position current = new Position(cloneFloor, clonePos, direction, b.nbAdditionalElevators - buildAdditionalElevator);
            Player.log("Clone position: " + current);
            Action action = b.getBestAction(current);
            if (action == Action.ELEVATOR) {
                buildAdditionalElevator++;
            }
            System.out.println(action); // action: WAIT or BLOCK or ELEVATOR
        }
    }

    static void log(String message) {
        System.err.println(message);
    }
}

class Board {
    final int exitPos;
    int width;
    int exitFloor;
    int nbRounds;
    int nbFloors;
    int nbTotalClones;
    int nbAdditionalElevators;
    int nbElevators;
    Floor[] floors;
    Map<Position, Action> path2Exit;

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
        for (int i = 0; i < nbFloors; i++) {
            floors[i] = new Floor();
        }
        for (int i = 0; i < nbElevators; i++) {
            int elevatorFloor = in.nextInt(); // floor on which this elevator is found
            int elevatorPos = in.nextInt(); // position of the elevator on its floor
            floors[elevatorFloor].add(elevatorPos);
        }
    }

    Map<Position, Action> path2Exit(Position startPos) {

        Player.log("start path2Exit: " + startPos);

        Map<Position, Integer> cost = getCost(startPos);

        Position exit = getExitPosition(cost);

        Map<Position, Action> path = new HashMap<>();
        while (!exit.equals(startPos)) {
            int minCost = Integer.MAX_VALUE;
            Position minPos = null;
            Action minAction = null;
            for (Action a : Action.values()) {
                for (Position prev : exit.prev(a, this)) {
                    if (isInBoard(prev)) {
                        // assert  cost.containsKey(prev) : prev.toString() + a;
                        int prevCost = cost.getOrDefault(prev, Integer.MAX_VALUE);
                        if (prevCost < minCost) {
                            minCost = prevCost;
                            minPos = prev;
                            minAction = a;
                        }
                    }
                }
            }
            exit = minPos;
            assert exit != null;
            assert minAction != null;
            Player.log("Exit: " + minPos + ", " + minAction);
            path.put(minPos, minAction);
        }

        Player.log("end path2Exit");
        return path;
    }

    Position getExitPosition(Map<Position, Integer> cost) {
        Position exit = null;
        int minExitCost = Integer.MAX_VALUE;
        for (int i = 0; i <= nbAdditionalElevators; i++) {
            for (Direction d : Direction.values()) {
                Position candidate = new Position(exitFloor, exitPos, d, i);
                int candidateCost = cost.getOrDefault(candidate, Integer.MAX_VALUE);
                if (candidateCost < minExitCost) {
                    minExitCost = candidateCost;
                    exit = candidate;
                }
            }
        }
        assert exit != null;
        return exit;
    }

    Map<Position, Integer> getCost(Position startPos) {
        Map<Position, Integer> cost = new HashMap<>();
        Stack<Position> todo = new Stack<>();

        todo.push(startPos);
        cost.put(startPos, 0);

        while (!todo.isEmpty()) {
            Position current = todo.pop();
            //Player.log("\nCurrent: " + current);
            int currentCost = cost.get(current);
            for (Action a : Action.values()) {
                int nextCost = currentCost + a.cost;
                Position nextPos = current.move(a, this);
                // Player.log("Next: " + nextPos);
                if (isInBoard(nextPos)) {
                    int bestCost = cost.getOrDefault(nextPos, Integer.MAX_VALUE);
                    if (nextCost <= nbRounds && nextCost < bestCost) {
                        // Player.log("Add as todo: " + nextPos);
                        cost.put(nextPos, nextCost);
                        todo.push(nextPos);
                    }
                }
            }
        }
        return cost;
    }

    boolean isInBoard(Position pos) {
        return pos.nbAdditionalElevators >= 0 && pos.cloneFloor >= 0 && pos.cloneFloor < nbFloors && pos.clonePos >= 0 && pos.clonePos < width;
    }


    public Action getBestAction(Position p) {
        if (path2Exit == null) {
            path2Exit = path2Exit(p);
        }
        Action toBeReturned = path2Exit.remove(p);
        if (toBeReturned == null) {
            toBeReturned = Action.WAIT;
        }
        return toBeReturned;
    }

    @Override
    public String toString() {
        StringBuffer bf = new StringBuffer(nbFloors +
                "\n" + width +
                "\n" + nbRounds +
                "\n" + exitFloor +
                "\n" + exitPos +
                "\n" + nbTotalClones +
                "\n" + nbAdditionalElevators +
                "\n" + nbElevators);
        for (int floor = 0; floor < nbFloors; floor++) {
            for (int pos : floors[floor].positions) {
                bf.append("\n" + floor + " " + pos);
            }
        }
        return bf.toString();
    }

    boolean isElevatorAt(int cloneFloor, int clonePos) {
        return cloneFloor >= 0 && floors[cloneFloor].contains(clonePos);
    }
}

class Position {
    int cloneFloor;
    int clonePos;
    Direction direction;
    int nbAdditionalElevators;

    public Position(int cloneFloor, int clonePos, String direction, int nbAdditionalElevators) {
        this(cloneFloor, clonePos, Direction.valueOf(direction), nbAdditionalElevators);
    }

    public Position(int cloneFloor, int clonePos, Direction direction, int nbAdditionalElevators) {
        this.cloneFloor = cloneFloor;
        this.clonePos = clonePos;
        this.direction = direction;
        this.nbAdditionalElevators = nbAdditionalElevators;
    }

    public Position move(Action a, Board b) {
        assert b.isInBoard(this);
        switch (a) {
            case ELEVATOR:
                return new Position(cloneFloor + 1, clonePos, direction, nbAdditionalElevators - 1);
            case BLOCK:
                return new Position(cloneFloor, clonePos, direction.inverse(), nbAdditionalElevators);
            case WAIT:
                if (b.floors[cloneFloor].contains(clonePos)) {
                    return new Position(cloneFloor + 1, clonePos, direction, nbAdditionalElevators);
                }
                switch (direction) {
                    case LEFT:
                        return new Position(cloneFloor, clonePos - 1, direction, nbAdditionalElevators);
                    case RIGHT:
                        return new Position(cloneFloor, clonePos + 1, direction, nbAdditionalElevators);
                }
            default:
                throw new IllegalStateException();
        }
    }

    public List<Position> prev(Action a, Board b) {
        List<Position> positions = new ArrayList<>();
        switch (a) {
            case ELEVATOR:
                positions.add(new Position(cloneFloor - 1, clonePos, direction, nbAdditionalElevators + 1));
                return positions;
            case BLOCK:
                positions.add(new Position(cloneFloor, clonePos, direction.inverse(), nbAdditionalElevators));
                return positions;
            case WAIT:
                if (b.isElevatorAt(cloneFloor - 1, clonePos)) {
                    positions.add(new Position(cloneFloor - 1, clonePos, direction, nbAdditionalElevators));
                }
                switch (direction) {
                    case LEFT:
                        positions.add(new Position(cloneFloor, clonePos + 1, direction, nbAdditionalElevators));
                        return positions;
                    case RIGHT:
                        positions.add(new Position(cloneFloor, clonePos - 1, direction, nbAdditionalElevators));
                        return positions;
                }
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (cloneFloor != position.cloneFloor) return false;
        if (clonePos != position.clonePos) return false;
        if (nbAdditionalElevators != position.nbAdditionalElevators) return false;
        return direction == position.direction;
    }

    @Override
    public int hashCode() {
        int result = cloneFloor;
        result = 31 * result + clonePos;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + nbAdditionalElevators;
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "cloneFloor=" + cloneFloor +
                ", clonePos=" + clonePos +
                ", direction=" + direction +
                ", nbAdditionalElevators=" + nbAdditionalElevators +
                '}';
    }
}

class Floor {

    List<Integer> positions = new ArrayList<>();

    public void add(int elevatorPos) {
        positions.add(elevatorPos);
    }

    boolean contains(int pos) {
        return positions.contains(pos);
    }
}