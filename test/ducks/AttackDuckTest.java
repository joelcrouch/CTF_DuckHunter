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

    @Test
    public void testIsDivisibleBySeven() {
        // Test cases where the ID is divisible by 7
        assertTrue(attackDuck.isDivisibleBySeven(7));
        assertTrue(attackDuck.isDivisibleBySeven(14));
        assertTrue(attackDuck.isDivisibleBySeven(21));

        // Test cases where the ID is not divisible by 7
        assertFalse(attackDuck.isDivisibleBySeven(1));
        assertFalse(attackDuck.isDivisibleBySeven(8));
        assertFalse(attackDuck.isDivisibleBySeven(15));
    }

    @Test
    public void testMoveRandomly() throws GameActionException {
        // Setup mock behavior
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test
        attackDuck.moveRandomly();

        // Verify that the robot moved in a valid direction
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testMoveToTargetMapLocation() throws GameActionException {
        // Setup mock behavior
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = new MapLocation(3, 3);
        Direction toTarget = currentLocation.directionTo(targetLocation);

        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toTarget)).thenReturn(true);

        // Call the method under test
        attackDuck.moveToTargetMapLocation(targetLocation);

        // Verify that the robot moved towards the target location
        verify(mockRc).move(toTarget);

        // Test the case where the direct path is blocked
        when(mockRc.canMove(toTarget)).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test again
        attackDuck.moveToTargetMapLocation(targetLocation);

        // Verify that the robot moved randomly
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testCalculateOppositeSpawnLocations() throws GameActionException {
        // Setup mock behavior
        MapLocation[] allySpawnLocations = {
                new MapLocation(1, 1),
                new MapLocation(2, 2),
                new MapLocation(3, 3)
        };
        int mapWidth = 10;
        int mapHeight = 10;

        when(mockRc.getAllySpawnLocations()).thenReturn(allySpawnLocations);
        when(mockRc.getMapWidth()).thenReturn(mapWidth);
        when(mockRc.getMapHeight()).thenReturn(mapHeight);

        // Call the method under test
        MapLocation[] oppositeSpawnLocations = attackDuck.calculateOppositeSpawnLocations();

        // Expected opposite spawn locations
        MapLocation[] expectedOppositeSpawnLocations = {
                new MapLocation(8, 8),
                new MapLocation(7, 7),
                new MapLocation(6, 6)
        };

        // Verify the results
        assertArrayEquals(expectedOppositeSpawnLocations, oppositeSpawnLocations);
    }

}
