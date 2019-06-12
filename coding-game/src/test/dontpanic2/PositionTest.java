package dontpanic2;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

    @Test
    public void ensure_move_prev_are_symetric() {
        Position p = new Position(2,2, Direction.RIGHT);
        for (Action a : Action.values()) {
            Position next = p.move(a);
            Assert.assertTrue(next.prev(a).equals(p));
        }
    }

    @Test
    public void ensure_move_wait_no_elevator() {
        Position p = new Position(2,2, Direction.RIGHT);
        Position next = p.move(Action.WAIT);
        Assert.assertEquals(p.cloneFloor, next.cloneFloor);
    }
}
