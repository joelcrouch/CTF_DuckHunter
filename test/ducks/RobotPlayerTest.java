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

        // Set up micito  behavior
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


    @Test
    public void testAttemptToSpawn_Success() throws GameActionException {
        // Mock spawn locations
        MapLocation spawnLocation1 = new MapLocation(2, 2);
        MapLocation spawnLocation2 = new MapLocation(3, 3);
        MapLocation[] spawnLocations = { spawnLocation1, spawnLocation2 };

        // Set up mocked behavior
        when(mockRc.getAllySpawnLocations()).thenReturn(spawnLocations);
        when(mockRc.canSpawn(spawnLocation1)).thenReturn(true);

        // Create a subclass to override RNG behavior
        robotPlayer = new RobotPlayer() {
            @Override
            public void run() {
            }

            @Override
            protected int getRandomInt(int bound) {
                return 0; // Always pick the first location
            }
        };
        robotPlayer.rc = mockRc; // Inject mocked RC

        // Call the method
        robotPlayer.attemptToSpawn();

        // Verify that spawn was attempted at the correct location
        verify(mockRc, times(1)).spawn(spawnLocation1);
    }

    @Test
    public void testAttemptToSpawn_Failure() throws GameActionException {
        // Mock spawn locations
        MapLocation spawnLocation1 = new MapLocation(2, 2);
        MapLocation spawnLocation2 = new MapLocation(3, 3);
        MapLocation[] spawnLocations = { spawnLocation1, spawnLocation2 };

        // Set up mocked behavior
        when(mockRc.getAllySpawnLocations()).thenReturn(spawnLocations);
        when(mockRc.canSpawn(any(MapLocation.class))).thenReturn(false); // Always fail spawning

        // Create a subclass to override RNG behavior
        robotPlayer = new RobotPlayer() {
            @Override
            public void run() {
            }

            @Override
            protected int getRandomInt(int bound) {
                return 0; // Always pick the first location
            }
        };
        robotPlayer.rc = mockRc; // Inject mocked RC

        // Call the method
        robotPlayer.attemptToSpawn();

        // Verify that no spawn was attempted
        verify(mockRc, never()).spawn(any(MapLocation.class));
    }


    @Test
    public void testUpdateEnemyRobots_WithEnemies() throws GameActionException {
        // Mock enemy robots
        MapLocation location1 = new MapLocation(1, 2);
        MapLocation location2 = new MapLocation(3, 4);
        RobotInfo enemy1 = mock(RobotInfo.class);
        RobotInfo enemy2 = mock(RobotInfo.class);
        when(enemy1.getLocation()).thenReturn(location1);
        when(enemy2.getLocation()).thenReturn(location2);
        when(enemy1.getTeam()).thenReturn(Team.B);
        when(enemy2.getTeam()).thenReturn(Team.B);

        RobotInfo[] enemyRobots = {enemy1, enemy2};

        // Mock RobotController behavior
        when(mockRc.getTeam()).thenReturn(Team.A); // Stub the team's value
        when(mockRc.senseNearbyRobots(-1, Team.B)).thenReturn(enemyRobots); // Use Team.B explicitly
        when(mockRc.canWriteSharedArray(0, enemyRobots.length)).thenReturn(true);

        // Call the method
        RobotPlayer.updateEnemyRobots(mockRc);

        // Verify that the indicator string is set
        verify(mockRc).setIndicatorString("There are nearby enemy robots! Scary!");

        // Verify that enemy locations were processed (we'd assert indirectly here)
        verify(enemy1).getLocation();
        verify(enemy2).getLocation();

        // Verify shared array operations
        verify(mockRc).writeSharedArray(0, enemyRobots.length);
        verify(mockRc).readSharedArray(0);
    }

    @Test
    public void testUpdateEnemyRobots_NoEnemies() throws GameActionException {
        // Mock an empty array of enemy robots
        RobotInfo[] enemyRobots = {};

        // Mock RobotController behavior
        when(mockRc.getTeam()).thenReturn(Team.A); // Stub the team's value
        when(mockRc.senseNearbyRobots(-1, Team.B)).thenReturn(enemyRobots); // No enemy robots

        // Call the method
        RobotPlayer.updateEnemyRobots(mockRc);

        // Verify that no indicator string is set
        verify(mockRc, never()).setIndicatorString(anyString());

        // Verify that no enemy locations are processed
        verify(mockRc, never()).writeSharedArray(anyInt(), anyInt());
        verify(mockRc, never()).readSharedArray(anyInt());
    }

    @Test
    public void check() {
        // Perform the operation and check the result
        assertEquals(4, 2 * 2);
    }


    //this is the first of the run tests.  Just have to walk through it and continue to work it
//    @Test
//    public void testRun_InitialLines() throws GameActionException {
//        // Mock RobotController
//        RobotController mockRc = mock(RobotController.class);
//
//        // Mock turn count (static variable needs to be reset for tests)
//        RobotPlayer.turnCount = 0;
//
//        // Run the method in a separate thread to simulate its behavior
//        new Thread(() -> {
//            try {
//                RobotPlayer.run(mockRc); // Will go into the loop
//            } catch (GameActionException ignored) {
//            }
//        }).start();
//
//        // Verify initial output
//        verify(mockRc, timeout(1000)).setIndicatorString("Hello world!");
//        assertEquals(1, RobotPlayer.turnCount); // Check turnCount increment after one iteration
//
//        // Stop the loop after one iteration
//        Thread.currentThread().interrupt(); // Simulate stopping the loop
//    }

}