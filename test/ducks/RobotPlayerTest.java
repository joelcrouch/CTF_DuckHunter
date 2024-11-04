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
        MapLocation mockLocation = new MapLocation(0, 0); // Example location
        when(mockRc.getLocation()).thenReturn(mockLocation); // Mock the location
        when(mockRc.canPickupFlag(mockLocation)).thenReturn(true); // Can pickup the flag

        robotPlayer.findAndPickupFlag();

        verify(mockRc, times(1)).pickupFlag(mockLocation);
        verify(mockRc, times(1)).setIndicatorString("Picked up a flag!");
        verify(mockRc, times(2)).getLocation(); // Ensure getLocation is called
    }

    @Test
    public void testFindAndPickupFlagFailure() throws GameActionException {

        MapLocation mockLocation = new MapLocation(0, 0); // Example location
        when(mockRc.getLocation()).thenReturn(mockLocation); // Mock the location
        when(mockRc.canPickupFlag(mockLocation)).thenReturn(false); // Cannot pickup the flag

        robotPlayer.findAndPickupFlag();

        verify(mockRc, times(0)).pickupFlag(mockLocation); // Should not call pickupFlag
        verify(mockRc, times(0)).setIndicatorString(anyString()); // Should not call setIndicatorString
        verify(mockRc, times(1)).getLocation(); // Ensure getLocation is called
    }

    @Test
    public void testMoveToAllySpawnLocation_NoFlag() throws GameActionException {
        when(mockRc.hasFlag()).thenReturn(false);  // Robot does not have a flag

        robotPlayer.moveToAllySpawnLocation();

        verify(mockRc, never()).getAllySpawnLocations();
        verify(mockRc, never()).move(any(Direction.class));
    }


    @Test
    public void testMoveToAllySpawnLocation_WithFlagAndSpawnLocations() throws GameActionException {
        // Setup
        when(mockRc.hasFlag()).thenReturn(true); // Robot has a flag

        // Create fakes locations
        MapLocation mockLocation = new MapLocation(0, 0); // Robot's current location
        MapLocation spawnLocation = new MapLocation(1, 0); // Example spawn location
        MapLocation[] spawnLocs = { spawnLocation }; // Non-empty spawn array

        // Set up micito behavior
        when(mockRc.getAllySpawnLocations()).thenReturn(spawnLocs); // Provide spawn locations
        when(mockRc.getLocation()).thenReturn(mockLocation); // Set robot's current location

        Direction expectedDirection = mockLocation.directionTo(spawnLocation); // Expected direction

        // Mock canMove to return true for the direction to spawnnfor underlyig canmove() funciton
        when(mockRc.canMove(expectedDirection)).thenReturn(true);

        // call the methood
        robotPlayer.moveToAllySpawnLocation();

        // Verify
        verify(mockRc, times(1)).getAllySpawnLocations(); // Called to get spawn locations
        verify(mockRc, times(1)).getLocation(); // Called to get current location
        verify(mockRc, times(1)).move(expectedDirection); // Move called in expected direction
    }


}