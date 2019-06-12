package dontpanic2;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

    @Test
    public void ensure_move_prev_are_symetric() {
        State p = new State(2, 2, Direction.RIGHT, 5,1);
        for (Action a : Action.values()) {
            State next = p.move(a, BoardTest.getBoardLevel1());
            Assert.assertTrue(next.prev(a, BoardTest.getBoardLevel1()).contains(p));
        }
    }

    @Test
    public void ensure_move_wait_no_elevator() {
        State p = new State(2, 2, Direction.RIGHT, 5, 1);
        State next = p.move(Action.WAIT, BoardTest.getBoardLevel1());
        Assert.assertEquals(p.cloneFloor, next.cloneFloor);
    }
}
