package cadeau;

import java.util.*;


/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int nbPlayers = in.nextInt();
        log(nbPlayers);
        int total = in.nextInt();
        log(total);
        List<Integer> budget = new ArrayList<>();


        for (int i = 0; i < nbPlayers; i++) {
            int B = in.nextInt();
            budget.add(B);
        }

        int playerId = 0;
        List<Integer> participation = new ArrayList<>();
        while ( budget.get(playerId) >= total) {
            participation.add(budget.get(playerId));
            total = total - budget.get(playerId);
            playerId++;
        }

        int restToPay = nbPlayers - playerId;

        while (playerId < nbPlayers) {
            participation.add(total/restToPay);
            playerId++;
        }


        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        if (total != 0) {
            System.out.println("IMPOSSIBLE");
        } else {
            for (Integer p : participation) {
                System.out.println(participation.get(p));
            }
        }
    }



    private static void log(Number message) {
        log(message+"");
    }

    private static void log(String message) {
        System.err.println(message);
    }

}