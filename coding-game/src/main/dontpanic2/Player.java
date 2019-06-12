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
        int nbBuildAdditionalElevator = 0;
        int nbBlock = 0;

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            String direction = in.next(); // direction of the leading clone: LEFT or RIGHT
            State current = new State(cloneFloor, clonePos, direction, b.nbAdditionalElevators - nbBuildAdditionalElevator, b.nbTotalClones - nbBuildAdditionalElevator - nbBlock);
            Player.log("Clone position: " + current);
            Action action = b.getBestAction(current);
            if (action == Action.ELEVATOR) {
                nbBuildAdditionalElevator++;
            }
            if (action == Action.BLOCK) {
                nbBlock++;
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
    Map<State, Action> path2Exit;

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

    Map<State, Action> path2Exit(State startPos) {

        Player.log("start path2Exit: " + startPos);

        Map<State, Integer> cost = getCost(startPos);

        State exit = getExitPosition(cost);

        Map<State, Action> path = new HashMap<>();
        while (!exit.equals(startPos)) {
            exit = getState(cost, exit, path);
        }

        Player.log("end path2Exit");
        return path;
    }

    State getState(Map<State, Integer> cost, State exit, Map<State, Action> path) {
        int minCost = Integer.MAX_VALUE;
        State minPos = null;
        Action minAction = null;
        for (Action a : Action.values()) {
            for (State prev : exit.prev(a, this)) {
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
        assert minPos != null : exit;
        exit = minPos;
        assert minAction != null;
        Player.log("Exit: " + minPos + ", " + minAction);
        path.put(minPos, minAction);
        return exit;
    }

    State getExitPosition(Map<State, Integer> cost) {
        State exit = null;
        int minExitCost = Integer.MAX_VALUE;
        for (int i = 0; i <= nbAdditionalElevators; i++) {
            for (Direction d : Direction.values()) {
                for (int nbClone = 1; nbClone <= nbTotalClones; nbClone++) {
                    State candidate = new State(exitFloor, exitPos, d, i, nbClone);
                    int candidateCost = cost.getOrDefault(candidate, Integer.MAX_VALUE);
                    if (candidateCost < minExitCost) {
                        minExitCost = candidateCost;
                        exit = candidate;
                    }
                }
            }
        }
        assert exit != null : cost;
        Player.log("Best exit: " + exit);
        return exit;
    }

    Map<State, Integer> getCost(State startPos) {
        Map<State, Integer> cost = new HashMap<>();
        Stack<State> todo = new Stack<>();

        todo.push(startPos);
        cost.put(startPos, 0);

        while (!todo.isEmpty()) {
            State current = todo.pop();
            //Player.log("\nCurrent: " + current);
            int currentCost = cost.get(current);
            for (Action a : Action.values()) {
                int nextCost = currentCost + a.cost;
                State nextPos = current.move(a, this);
                // Player.log("Next: " + nextPos);
                if (isInBoard(nextPos)) {
                    int bestCost = cost.getOrDefault(nextPos, Integer.MAX_VALUE);
                    if (nextCost <= nbRounds && nextCost < bestCost) {
                       // Player.log("Add a new cost: " + nextPos + ", " + a + ", " + current);
                        cost.put(nextPos, nextCost);
                        todo.push(nextPos);
                    }
                }
            }
        }
        return cost;
    }

    boolean isInBoard(State pos) {
        return pos.nbClones >= 1 && pos.nbAdditionalElevators >= 0 && pos.cloneFloor >= 0 && pos.cloneFloor < nbFloors && pos.clonePos >= 0 && pos.clonePos < width;
    }


    public Action getBestAction(State p) {
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

class State {
    int cloneFloor;
    int clonePos;
    Direction direction;
    int nbAdditionalElevators;
    int nbClones;

    public State(int cloneFloor, int clonePos, String direction, int nbAdditionalElevators, int nbClones) {
        this(cloneFloor, clonePos, Direction.valueOf(direction), nbAdditionalElevators, nbClones);
    }

    public State(int cloneFloor, int clonePos, Direction direction, int nbAdditionalElevators, int nbClones) {
        this.cloneFloor = cloneFloor;
        this.clonePos = clonePos;
        this.direction = direction;
        this.nbAdditionalElevators = nbAdditionalElevators;
        this.nbClones = nbClones;
    }

    public State move(Action a, Board b) {
        assert b.isInBoard(this) : this;
        switch (a) {
            case ELEVATOR:
                return new State(cloneFloor + 1, clonePos, direction, nbAdditionalElevators - 1, nbClones - 1);
            case BLOCK:
                return new State(cloneFloor, clonePos, direction.inverse(), nbAdditionalElevators, nbClones - 1);
            case WAIT:
                if (b.floors[cloneFloor].contains(clonePos)) {
                    return new State(cloneFloor + 1, clonePos, direction, nbAdditionalElevators, nbClones);
                }
                switch (direction) {
                    case LEFT:
                        return new State(cloneFloor, clonePos - 1, direction, nbAdditionalElevators, nbClones);
                    case RIGHT:
                        return new State(cloneFloor, clonePos + 1, direction, nbAdditionalElevators, nbClones);
                }
            default:
                throw new IllegalStateException();
        }
    }

    public List<State> prev(Action a, Board b) {
        List<State> prev = new ArrayList<>();
        for (int deltaFloor = -1; deltaFloor <= 0; deltaFloor++) {
            for (int deltaPos = -1; deltaPos <= 1; deltaPos++) {
                for (int deltaElevator = 0; deltaElevator <= 1; deltaElevator++) {
                    for (int deltaClones = 0; deltaClones <= 1; deltaClones++) {
                        State left = new State(cloneFloor + deltaFloor, clonePos + deltaPos, Direction.LEFT, nbAdditionalElevators + deltaElevator, nbClones + deltaClones);
                        State right = new State(cloneFloor + deltaFloor, clonePos + deltaPos, Direction.RIGHT, nbAdditionalElevators + deltaElevator, nbClones + deltaClones);
                        if (b.isInBoard(left) && left.move(a, b).equals(this)) {
                            prev.add(left);
                        }
                        if (b.isInBoard(right) && right.move(a, b).equals(this)) {
                            prev.add(right);
                        }
                    }
                }
            }
        }
        return prev;


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (cloneFloor != state.cloneFloor) return false;
        if (clonePos != state.clonePos) return false;
        if (nbAdditionalElevators != state.nbAdditionalElevators) return false;
        if (nbClones != state.nbClones) return false;
        return direction == state.direction;
    }

    @Override
    public int hashCode() {
        int result = cloneFloor;
        result = 31 * result + clonePos;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + nbAdditionalElevators;
        result = 31 * result + nbClones;
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "cloneFloor=" + cloneFloor +
                ", clonePos=" + clonePos +
                ", direction=" + direction +
                ", nbAdditionalElevators=" + nbAdditionalElevators +
                ", nbClones=" + nbClones +
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