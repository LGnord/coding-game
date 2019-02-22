package caribbean;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    static final int[][][] NEIGHBOUR = { { { 1, 0 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } }, //
            { { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 1 } } };
    static final int X_MAX = 23;
    static final int Y_MAX = 21;

    // id -> debut,millieu,fin -> x,y(,speed)
    static final int[][][] myShips = { { { 0, 0 }, { 0, 0, 0 }, { 0, 0 } }, { { 0, 0 }, { 0, 0, 0 }, { 0, 0 } },
            { { 0, 0 }, { 0, 0, 0 }, { 0, 0 } } };
    static final int[][] myDirection = { { 0, 0, 0 } };

    // x,y,direction
    static int[][] lastMoves = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };

    static double[][] paths = new double[X_MAX][Y_MAX];
    static double[][] fires = new double[X_MAX][Y_MAX];

    static int roundId = 1;

    public static void main(String args[]) {
        InputStream in = System.in;

        // game loop
        while (true) {
            System.out.println(play(in));
        }
    }

    static String play(InputStream is) {
        StringBuffer buffer = new StringBuffer();
        try {

            int myShipCount = parse(is, buffer);

            String res = "";

            for (int shipId = 0; shipId < myShipCount; shipId++) {
                final String shipRes;

                System.err.println("Is :" + buffer);
                buffer.setLength(0);
                System.err.println("Map :" + deepToString(paths, myShips, shipId));
                System.err.println("\n");

                if (roundId % 2 == 0 && myShips[shipId][1][2] != 0) {
                    shipRes = fire(shipId);
                } else {
                    shipRes = move(shipId);
                }
                res += shipRes + (shipId == myShipCount - 1 ? "" : "\n");

            }
            System.err.println("Return'" + res + "'");
            roundId++;
            return res;
        } catch (Exception e) {
            System.err.println("** Exception **");
            System.err.println("Is :" + buffer);
            buffer.setLength(0);
            System.err.println(Arrays.toString(e.getStackTrace()));
            throw e;
            // return "MOVE 10 10";
        }
    }

    static int parse(InputStream is, StringBuffer buffer) {
        Scanner in = new Scanner(is);

        int myShipCount = in.nextInt();
        append(buffer, myShipCount);
        int entityCount = in.nextInt();
        append(buffer, entityCount);

        int myShipId = 0;
        initPaths(paths);
        for (int i = 0; i < entityCount; i++) {
            int entityId = in.nextInt();
            String entityType = in.next();
            int x = in.nextInt();
            int y = in.nextInt();
            int arg1 = in.nextInt();
            int arg2 = in.nextInt();
            int arg3 = in.nextInt();
            int arg4 = in.nextInt();
            append(buffer, entityId, entityType, x, y, arg1, arg2, arg3, arg4);

            if ("SHIP".equals(entityType)) {
                if (isMyShip(arg4)) {
                    myShips[myShipId][1][0] = x;
                    myShips[myShipId][1][1] = y;
                    myShips[myShipId][1][2] = arg2; // speed
                    myShipId++;
                    update(paths, x, y, 0.1, 1, 40);
                    update(fires, x, y, 0.9, -50, 3);
                } else {
                    int orientation = arg1;
                    update(paths, x, y, 0.1, -10, 40);
                    updateWithOrientation(fires, x, y, 0.5, 50, 5, orientation);
                }
            }

            if ("MINE".equals(entityType)) {
                update(paths, x, y, 0.8, -25, 2);
                // try to delete mine
                update(fires, x, y, 0, 50, 0);
            }

            if ("CANNONBALL".equals(entityType)) {
                update(paths, x, y, 0.5, -50, 2);
            }

            if ("BARREL".equals(entityType)) {
                update(paths, x, y, 0.9, arg1, 20);
            }

        }
        System.err.println("My Ships :" + Arrays.deepToString(myShips));
        return myShipCount;
    }

    private static boolean isMyShip(int arg4) {
        return arg4 == 1;
    }

    private static String fire(int shipId) {
        int[] firePos = { 0, 0 };
        double fireMax = 0;
        for (int i = 0; i < paths.length; i++) {
            for (int j = 0; j < paths[i].length; j++) {
                int d = distance(new int[] { i, j }, myShips[shipId][1]);
                if (d <= 10) {
                    double localScore = fires[i][j] / (d / 3);
                    if (localScore >= fireMax) {
                        fireMax = localScore;
                        firePos[0] = i;
                        firePos[1] = j;
                    }
                }
            }
        }
        return "FIRE " + firePos[0] + " " + firePos[1];
    }

    static String move(int shipId) {
        double maxScore = -1_000_000;
        int movX = X_MAX / 2, movY = Y_MAX / 2, direction = 0;
        int[][] movedShip = {};
        for (int orientation = 0; orientation < 6; orientation++) {
            int[][] candidateShip = move(shipId, orientation);
            if (isInMap(candidateShip)) {
                double candidateScore = score(paths, candidateShip);
                if (candidateScore >= maxScore) {
                    movX = candidateShip[1][0] + NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][0];
                    movY = candidateShip[1][1] + NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][1];
                    maxScore = candidateScore;
                    movedShip = candidateShip;
                    direction = orientation;
                }
            }
        }

        final String shipRes;
        if (isReplay(shipId, movX, movY)) {
            System.err.println("Replay");
            movX += Math.round(Math.random() * 3);
        }
        shipRes = "MOVE " + movX + " " + movY;
        lastMoves[shipId][0] = movX;
        lastMoves[shipId][1] = movY;
        lastMoves[shipId][2] = direction;
        for (int[] shipP : movedShip) {
            paths[shipP[0]][shipP[1]] -= 1_000; // my ship will be here
            update(fires, shipP[0], shipP[1], 0.99, -50, 1);
        }
        System.err.println("Return'" + shipRes + "' with maxScore=" + maxScore);
        return shipRes;
    }

    // XXX speed
    static int[][] move(int shipId, int orientation) {
        int candidateShip[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 } };
        candidateShip[1][0] = myShips[shipId][1][0] + NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][0];
        candidateShip[1][1] = myShips[shipId][1][1] + NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][1];
        candidateShip[0][0] = candidateShip[1][0] - NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][0];
        candidateShip[0][1] = candidateShip[1][1] - NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][1];
        candidateShip[2][0] = candidateShip[1][0] + NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][0];
        candidateShip[2][1] = candidateShip[1][1] + NEIGHBOUR[myShips[shipId][1][0] % 2][orientation][1];
        return candidateShip;
    }

    private static boolean isReplay(int shipId, int movX, int movY) {
        return movX == lastMoves[shipId][0] && movY == lastMoves[shipId][1];
    }

    private static double score(double[][] map, int[][] ship) {
        double score = 0;
        for (int[] shipP : ship) {
            score += map[shipP[0]][shipP[1]];
        }
        return score;
    }

    private static boolean isInMap(int[][] ship) {
        for (int[] shipP : ship) {
            if (!isInMap(shipP)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isInMap(int[] shipP) {
        return shipP[0] >= 0 && shipP[1] >= 0 && shipP[0] < X_MAX && shipP[1] < Y_MAX;
    }

    private static String deepToString(double[][] paths, int[][][] myships, int shipId) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < paths.length; i++) {
            boolean newLine = false;
            for (int j = 0; j < paths[i].length; j++) {
                int d = distance(new int[] { i, j }, myships[shipId][1]);
                if (d == 0) {
                    b.append("*");
                }
                if (d <= 5) {
                    b.append(paths[i][j]);
                    b.append(", ");
                    newLine = true;
                }
            }
            if (newLine) {
                b.append("\n");
            }
        }
        return b.toString();
    }

    private static void initPaths(double[][] paths) {
        int[] center = { X_MAX / 2, Y_MAX / 2 };
        for (int i = 0; i < paths.length; i++) {
            for (int j = 0; j < paths[i].length; j++) {
                int d = distance(new int[] { i, j }, center);
                paths[i][j] = 1.0 / (1 + d);
                if (i == 0 || j == 0 || i == X_MAX - 1 || j == Y_MAX - 1) {
                    paths[i][j] -= 100;
                }
            }
        }
    }

    static void append(StringBuffer buffer, Object... values) {
        boolean isFirst = true;
        for (Object value : values) {
            if (!isFirst) {
                buffer.append(" ");
            }
            buffer.append(value);
            isFirst = false;
        }
        buffer.append(" ");

    }

    static void updateWithOrientation(double[][] map, int x, int y, double rate, int score, int distMax,
                                      int orientation) {
        double[] scores = scores(rate, score, distMax);
        int[] currentPos = { x, y };
        for (int i = 0; i <= distMax; i++) {
            if (isInMap(currentPos)) {
                fires[currentPos[0]][currentPos[1]] += scores[i];
                currentPos[1] += NEIGHBOUR[currentPos[0] % 2][orientation][1];
                currentPos[0] += NEIGHBOUR[currentPos[0] % 2][orientation][0];
            } else {
                return;
            }
        }
    }

    static void update(double[][] map, int x, int y, double rate, int score, int distMax) {
        assert rate < 1;
        double[] scores = scores(rate, score, distMax);
        int[] xy = { x, y };
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                int d = distance(new int[] { i, j }, xy);
                if (d <= distMax) {
                    map[i][j] += scores[d];
                }
            }
        }
    }

    private static double[] scores(double rate, int score, int distMax) {
        double[] scores = new double[distMax + 1];
        scores[0] = score;
        for (int i = 1; i < scores.length; i++) {
            scores[i] = scores[i - 1] * rate;
        }
        return scores;
    }

    static int distance(int[] a, int[] b) {
        return (Math.abs(a[0] - b[0]) + Math.abs(a[0] + a[1] - b[0] - b[1]) + Math.abs(a[1] - b[1])) / 2;
    }
}
