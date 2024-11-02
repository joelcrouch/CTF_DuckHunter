package ducks;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RobotPlayerTest {
    RobotController mockRc;
    RobotPlayer robotPlayer;

    @Before  // Use JUnit 4's @Before annotation  b/c we battlecode uses 4 not 5
    public void setup() {
        // Initialize mock RobotController
        mockRc = mock(RobotController.class);

        // Makea concrete instance of RobotPlayer with the mocked RobotController
        robotPlayer = new RobotPlayer() {
            @Override
            public void run() {
                // HEre is  empty implementation for the abstract run() method
            }

            {
                this.rc = mockRc;
            }
        };
    }

    @Test  // Use JUnit 4's @Test annotation again not 5
    public void testMoveSuccess() throws GameActionException {
        Direction direction = Direction.NORTH;
        when(mockRc.canMove(direction)).thenReturn(true);
        robotPlayer.move(direction);
        verify(mockRc, times(1)).move(direction);
    }

    @Test
    public void testMoveFailure() throws GameActionException {
        Direction direction = Direction.SOUTH;
        when(mockRc.canMove(direction)).thenReturn(false);
        robotPlayer.move(direction);
        verify(mockRc, times(0)).move(direction);
    }

    @Test
    public void testFindAndPickupFlagSuccess() throws GameActionException {
        // Mock behavior
        MapLocation mockLocation = new MapLocation(0, 0); // Example location
        when(mockRc.getLocation()).thenReturn(mockLocation); // Mock the location
        when(mockRc.canPickupFlag(mockLocation)).thenReturn(true); // Can pickup the flag

        // Call the method under test
        robotPlayer.findAndPickupFlag();

        // Verify interactions
        verify(mockRc, times(1)).pickupFlag(mockLocation);
        verify(mockRc, times(1)).setIndicatorString("Picked up a flag!");
        verify(mockRc, times(2)).getLocation(); // Ensure getLocation is called
    }

    @Test
    public void testFindAndPickupFlagFailure() throws GameActionException {
        // Mock behavior
        MapLocation mockLocation = new MapLocation(0, 0); // Example location
        when(mockRc.getLocation()).thenReturn(mockLocation); // Mock the location
        when(mockRc.canPickupFlag(mockLocation)).thenReturn(false); // Cannot pickup the flag

        // Call the method under test
        robotPlayer.findAndPickupFlag();

        // Verify interactions
        verify(mockRc, times(0)).pickupFlag(mockLocation); // Should not call pickupFlag
        verify(mockRc, times(0)).setIndicatorString(anyString()); // Should not call setIndicatorString
        verify(mockRc, times(1)).getLocation(); // Ensure getLocation is called
    }

}