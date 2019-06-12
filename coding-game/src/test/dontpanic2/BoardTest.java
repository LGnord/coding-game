package dontpanic2;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BoardTest {

    static Board getBoardLevel1() {
        String input = "2\n" +
                "13\n" +
                "100\n" +
                "1\n" +
                "11\n" +
                "10\n" +
                "1\n" +
                "0";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        return new Board(new Scanner(is));
    }

    static Board getBoardLevel4() {
        String input = "6\n" +
                "13\n" +
                "100\n" +
                "5\n" +
                "1\n" +
                "10\n" +
                "2\n" +
                "3\n" +
                "0 4\n" +
                "2 7\n" +
                "4 1\n";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        return new Board(new Scanner(is));
    }

    static Board getBoardLevel9() {
        String input = "13\n" +
                "69\n" +
                "79\n" +
                "11\n" +
                "39\n" +
                "8\n" +
                "5\n" +
                "30\n" +
                "1 50\n" +
                "1 17\n" +
                "1 4\n" +
                "1 24\n" +
                "1 34\n" +
                "2 24\n" +
                "2 23\n" +
                "2 3\n" +
                "2 58\n" +
                "3 17\n" +
                "5 4\n" +
                "5 46\n" +
                "6 65\n" +
                "6 13\n" +
                "6 34\n" +
                "6 57\n" +
                "7 17\n" +
                "8 34\n" +
                "8 56\n" +
                "8 1\n" +
                "8 66\n" +
                "8 9\n" +
                "8 23\n" +
                "10 23\n" +
                "10 3\n" +
                "11 4\n" +
                "11 13\n" +
                "11 42\n" +
                "11 11\n" +
                "11 38";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        return new Board(new Scanner(is));
    }


    @Test
    public void ensure_cost_exit_exist_level9() {
        Board b = getBoardLevel9();
        Assert.assertEquals(8, b.nbTotalClones);
        Map<State, Integer> cost = b.getCost(new State(0, 33, Direction.RIGHT, b.nbAdditionalElevators, b.nbTotalClones));
        Assert.assertTrue(cost.containsKey(b.getExitPosition(cost)));
    }

    @Test
    public void ensure_last_move_level9() {
        Board b = getBoardLevel9();

        State s = new State(10, 39, Direction.RIGHT, 1, 2);
        Assert.assertTrue(b.isInBoard(s.move(Action.ELEVATOR, b)));

        // cloneFloor=11, clonePos=38, direction=RIGHT, nbAdditionalElevators=0, nbClones=1
        s = new State(11, 38, Direction.RIGHT, 0, 1);
        Assert.assertEquals(12, s.move(Action.WAIT, b).cloneFloor);

        System.out.print(s.prev(Action.WAIT, b));
    }


    @Test
    public void ensure_cost_exit_exist_level4() {
        Board b = getBoardLevel4();
        Assert.assertEquals(2, b.nbAdditionalElevators);
        Map<State, Integer> cost = b.getCost(new State(0, 10, Direction.RIGHT, b.nbAdditionalElevators, b.nbTotalClones));
        Assert.assertTrue(cost.containsKey(b.getExitPosition(cost)));
    }

    @Test
    public void ensure_cost_path2exit_exist_level4() {
        Board b = getBoardLevel4();
        Assert.assertEquals(2, b.nbAdditionalElevators);

        Assert.assertNotNull(b.getBestAction(new State(0, 10, Direction.RIGHT, b.nbAdditionalElevators, b.nbTotalClones)));
    }

    @Test
    public void ensure_get_state_level4() {
        Board b = getBoardLevel4();
        Assert.assertEquals(2, b.nbAdditionalElevators);

        State s = new State(5, 1, Direction.LEFT, 0, 5);
        State expected = new State(4, 1, Direction.LEFT, 0, 5);
        Assert.assertTrue(s.prev(Action.WAIT, b).contains(expected));
    }

    @Test
    public void ensure_cost_path2exit_exist_level9() {
        Board b = getBoardLevel9();
        State s = new State(0, 33, Direction.RIGHT, b.nbAdditionalElevators, b.nbTotalClones);
        b.path2Exit = b.path2Exit(s);
        Assert.assertNotNull(b.path2Exit.get(s));
    }

    @Test
    public void ensure_in_board() {
        Board b = getBoardLevel1();
        Assert.assertTrue(b.isInBoard(new State(0, 9, Direction.RIGHT, 10, b.nbTotalClones)));
        Assert.assertTrue(b.isInBoard(new State(0, 10, Direction.RIGHT, 10, b.nbTotalClones)));
        Assert.assertTrue(b.isInBoard(new State(0, 9, Direction.LEFT, 10, b.nbTotalClones)));
        Assert.assertTrue(b.isInBoard(new State(1, 9, Direction.RIGHT, 10, b.nbTotalClones)));
    }

    @Test
    public void ensure_cost_exit_exist() {
        Board b = getBoardLevel1();
        Assert.assertEquals(10, b.nbTotalClones);
        Map<State, Integer> cost = b.getCost(new State(0, 2, Direction.RIGHT, 1, b.nbTotalClones));
        State exitPosition = b.getExitPosition(cost);
        Assert.assertNotNull(exitPosition);
        Assert.assertTrue(cost.containsKey(exitPosition));
    }

    @Test
    public void ensure_compute_path_no_error() {
        Board b = getBoardLevel1();
        b.path2Exit(new State(0, 9, Direction.RIGHT, 1, b.nbTotalClones));
    }

    @Test
    public void ensure_compute_path_no_error_2_elevators() {
        Board b = getBoardLevel1();
        Assert.assertEquals(1, b.exitFloor);
        Assert.assertEquals(11, b.exitPos);
        b.path2Exit(new State(0, 9, Direction.RIGHT, 2, b.nbTotalClones));
    }

    @Test
    public void ensure_prev_elevators() {
        Board b = getBoardLevel1();
        State s = new State(1, 9, Direction.RIGHT, 1, 9);
        State expected = new State(0, 9, Direction.RIGHT, 2, 10);
        List<State> actualPrev = s.prev(Action.ELEVATOR, b);
        Assert.assertEquals(s, expected.move(Action.ELEVATOR, b));
        Assert.assertTrue(actualPrev.toString(), actualPrev.contains(expected));
    }


}


