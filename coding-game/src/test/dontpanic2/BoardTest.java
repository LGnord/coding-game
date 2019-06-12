package dontpanic2;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class BoardTest {

    static Board getBoardLevel1() {
        String input = "5\n" +
                "13\n" +
                "100\n" +
                "1\n" +
                "2\n" +
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

    @Test
    public void ensure_cost_exit_exist_level4() {
        Board b = getBoardLevel4();
        Assert.assertEquals(2, b.nbAdditionalElevators);
        Map<Position, Integer> cost = b.getCost(new Position(0, 10, Direction.RIGHT, b.nbAdditionalElevators));
        Assert.assertTrue(cost.containsKey(b.getExitPosition(cost)));
    }

    @Test
    public void ensure_cost_path2exit_exist_level4() {
        Board b = getBoardLevel4();
        Assert.assertEquals(2, b.nbAdditionalElevators);

        Assert.assertNotNull(b.getBestAction(new Position(0, 10, Direction.RIGHT, b.nbAdditionalElevators)));
    }

    @Test
    public void ensure_in_board() {
        Board b = getBoardLevel1();
        Assert.assertTrue(b.isInBoard(new Position(0, 9, Direction.RIGHT, 10)));
        Assert.assertTrue(b.isInBoard(new Position(0, 10, Direction.RIGHT, 10)));
        Assert.assertTrue(b.isInBoard(new Position(0, 9, Direction.LEFT, 10)));
        Assert.assertTrue(b.isInBoard(new Position(1, 9, Direction.RIGHT, 10)));
    }

    @Test
    public void ensure_cost_exit_exist() {
        Board b = getBoardLevel1();
        Map<Position, Integer> cost = b.getCost(new Position(0, 2, Direction.RIGHT, 1));
        Position exitPosition = b.getExitPosition(cost);
        Assert.assertNotNull(exitPosition);
        Assert.assertTrue(cost.containsKey(exitPosition));
    }

    @Test
    public void ensure_compute_path_no_error() {
        Board b = getBoardLevel1();
        b.path2Exit(new Position(0, 9, Direction.RIGHT, 1));
    }

    @Test
    public void ensure_compute_path_no_error_2_elevators() {
        Board b = getBoardLevel1();
        b.path2Exit(new Position(0, 9, Direction.RIGHT, 2));
    }
}


