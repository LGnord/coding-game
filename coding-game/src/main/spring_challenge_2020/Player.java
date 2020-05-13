package spring_challenge_2020;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
        }


        // game loop
        while (true) {

            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight

            System.err.println("visiblePacCount = " + visiblePacCount);

            List<Pac> pacs = new ArrayList<Pac>();

            int pacIndex = 0;
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
                    System.err.println("pac = " + pac);
                    pacs.add(pac);
                    pacIndex++;
                }
            }


            int visiblePelletCount = in.nextInt(); // all pellets in sight
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth
                for (Pac pac : pacs) {
                    pac.newPellet(x, y, value);
                }
            }

            for (Pac p1 : pacs) {
                for (Pac p2 : pacs) {
                    if (p1 != p2) {
                        if (p1.sameGoal(p2)) {
                            p1.bestX = (int) Math.random() * 10;
                        }
                    }
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            StringBuffer commands = new StringBuffer();
            for (Pac pac : pacs) {
                commands.append(pac.move() + "|"); // MOVE <pacId> <x> <y>
            }
            System.out.println(commands);
        }
    }

    static class Pac {
        int id;
        int myX;
        int myY;


        int bestX;
        int bestY;
        int bestScore;
        int bestDistance;


        public void newPellet(int x, int y, int value) {
            int distance = (myX - x) * (myX - x) + (myY - y) * (myY - y);
            if (value > bestScore) {
                bestDistance = distance;
                bestScore = value;
                bestX = x;
                bestY = y;
            }
            if (value == bestScore) {
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestScore = value;
                    bestX = x;
                    bestY = y;
                }
            }
        }

        public String move() {
            return "MOVE " + id + " " + bestX + " " + bestY;
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
                    '}';
        }

        public boolean sameGoal(Pac p2) {
            return bestX == p2.bestX && bestY == p2.bestY;
        }
    }
}
