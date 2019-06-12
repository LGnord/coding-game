package dontpanic2;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class BoardTest {

    @Test
    public void ensure_in_board() {
        String input = "2\n" +
                "13\n" +
                "100\n" +
                "1\n" +
                "2\n" +
                "10\n" +
                "1\n" +
                "0";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Board b = new Board(new Scanner(is));
        Assert.assertTrue(b.isInBoard(new Position(0, 9, Direction.RIGHT)));
        Assert.assertTrue(b.isInBoard(new Position(0, 10, Direction.RIGHT)));
        Assert.assertTrue(b.isInBoard(new Position(0, 9, Direction.LEFT)));
        Assert.assertTrue(b.isInBoard(new Position(1, 9, Direction.RIGHT)));
    }

    @Test
    public void ensure_cost_exit_exist() {
        String input = "2\n" +
                "13\n" +
                "100\n" +
                "1\n" +
                "2\n" +
                "10\n" +
                "1\n" +
                "0";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Board b = new Board(new Scanner(is));
        Map<Position, Integer> cost = b.getCost(new Position(0, 9, Direction.RIGHT));
        Assert.assertTrue(cost.containsKey(b.getExitPosition(cost)));

    }


    @Test
    public void ensure_compute_path_no_error() {
        String input = "2\n" +
                "13\n" +
                "100\n" +
                "1\n" +
                "2\n" +
                "10\n" +
                "1\n" +
                "0";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Board b = new Board(new Scanner(is));
        b.path2Exit(new Position(0, 9, Direction.RIGHT));

    }
}
