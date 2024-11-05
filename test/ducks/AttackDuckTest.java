package ducks;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AttackDuckTest {
    RobotController mockRc;
    AttackDuck attackDuck;

    @Before
    public void setUp() {
        mockRc = Mockito.mock(RobotController.class);
        attackDuck = new AttackDuck(mockRc);
    }

    @Test
    public void testSanity() {
        assertEquals(2, 1 + 1);
    }

}
