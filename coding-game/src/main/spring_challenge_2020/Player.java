package spring_challenge_2020;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {
        new Player().play();
    }

    void play() {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
        }

        Map<Integer, Pac> cachePac = new HashMap<>();
        // game loop
        while (true) {

            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight

            System.err.println("visiblePacCount = " + visiblePacCount);

            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours

                System.err.println("mine pac = " + pacId);
                Pac pac = new Pac();
                pac.id = pacId;
                pac.myX = in.nextInt(); // position in the grid
                pac.myY = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues

                if (mine) {
                    if (cachePac.containsKey(pacId)) {
                        cachePac.get(pacId).update(pac);
                    } else {
                        cachePac.put(pacId, pac);
                    }
                }
            }


            int visiblePelletCount = in.nextInt(); // all pellets in sight
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth
                for (Pac pac : cachePac.values()) {
                    pac.newPellet(x, y, value);
                }
            }

            for (Pac p1 : cachePac.values()) {
                for (Pac p2 : cachePac.values()) {
                    if (p1 != p2) {
                        if (p1.sameGoal(p2)) {
                            System.err.println("Same goal");
                            if (p1.bestDistance > p2.bestDistance) {
                                p1.bestX = (int) (Math.random() * 10);
                            } else {
                                p2.bestX = (int) (Math.random() * 10);
                            }
                        }
                    }
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            StringBuffer commands = new StringBuffer();
            for (Pac pac : cachePac.values()) {
                System.err.println("pac = " + pac);
                commands.append(pac.move() + "|"); // MOVE <pacId> <x> <y>
            }
            System.out.println(commands);
        }
    }

    class Pac {
        int id;
        int myX;
        int myY;


        int bestX = -1;
        int bestY = -1;
        int bestScore = 0;
        int bestDistance = Integer.MAX_VALUE;

        int roundToChange = 0;


        public void newPellet(int x, int y, int value) {
            int distance = (myX - x) * (myX - x) + (myY - y) * (myY - y);
            if (value > bestScore) {
                bestDistance = distance;
                bestScore = value;
                this.bestX = x;
                this.bestY = y;
                System.err.println("new best score (id, x, y, value, distance): " + id + ", " + x + ", " + y + ", " + value + ", " + distance);
                System.err.println(this);
            }
            if (value == bestScore) {
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestScore = value;
                    this.bestX = x;
                    this.bestY = y;
                    System.err.println("new best distance (id, x, y, value, distance): " + id + ", " + x + ", " + y + ", " + value + ", " + distance);
                }
            }
        }

        public String move() {
            if (roundToChange == 0) {
                roundToChange = 10;
                return "SPEED " + id;
            } else {
                roundToChange--;
                return "MOVE " + id + " " + bestX + " " + bestY;
            }
        }

        public boolean sameGoal(Pac p2) {
            return bestX == p2.bestX && bestY == p2.bestY;
        }

        @Override
        public String toString() {
            return "Pac{" +
                    "id=" + id +
                    ", myX=" + myX +
                    ", myY=" + myY +
                    ", bestX=" + bestX +
                    ", bestY=" + bestY +
                    ", bestScore=" + bestScore +
                    ", bestDistance=" + bestDistance +
                    ", roundToChange=" + roundToChange +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pac pac = (Pac) o;
            return id == pac.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        public void update(Pac pac) {
            myX = pac.myX;
            myY = pac.myY;
            bestScore = 0;
            bestDistance = Integer.MAX_VALUE;
            bestY = -1;
            bestX = -1;
        }
    }
}
