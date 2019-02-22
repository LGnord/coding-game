package caribbean;



import org.junit.Test;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class PlayerTest {

    @Test
    public void ensure() {
        String input = "1\n" + //
                "1\n"+//
                "1 SHIP 4 4 0 0 0 1";//
        InputStream is = new ByteArrayInputStream(input.getBytes());
        Player.parse(is, new StringBuffer());
        int[][] direction0 = Player.move(1, 0);

        int[][] expected = {{4,2}, {4,3}, {4,4}};

        Assert.assertTrue(Arrays.deepToString(direction0)+" =? "+Arrays.deepToString(expected), Arrays.deepEquals(direction0, expected));
    }
}
