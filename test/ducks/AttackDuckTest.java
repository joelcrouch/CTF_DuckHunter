package ducks;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import java.lang.reflect.Field;
import java.util.Random;
import battlecode.common.Clock;
import battlecode.common.*;

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

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private Object getPrivateField(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
    
    @Test
    public void testExploreMove_LastDirectionValid() throws GameActionException, NoSuchFieldException, IllegalAccessException {
        // Setup mock behavior
        Direction lastDirection = Direction.NORTH;
        setPrivateField(attackDuck, "lastDirection", lastDirection);
        when(mockRc.canMove(lastDirection)).thenReturn(true);

        // Call the method under test
        attackDuck.exploreMove();

        // Verify that the robot moved in the last direction
        verify(mockRc, times(1)).move(lastDirection);
        assertEquals(lastDirection, getPrivateField(attackDuck, "lastDirection"));
    }

    @Test
    public void testExploreMove_LastDirectionInvalid() throws GameActionException, NoSuchFieldException, IllegalAccessException {
        // Setup mock behavior
        Direction lastDirection = Direction.NORTH;
        setPrivateField(attackDuck, "lastDirection", lastDirection);
        when(mockRc.canMove(lastDirection)).thenReturn(false);

        // Mock random direction
        Direction randomDirection = Direction.EAST;
        Random mockRng = mock(Random.class);
        setPrivateField(attackDuck, "rng", mockRng);
        when(mockRng.nextInt(anyInt())).thenReturn(randomDirection.ordinal());
        when(mockRc.canMove(randomDirection)).thenReturn(true);

        // Call the method under test
        attackDuck.exploreMove();

        // Verify that the robot moved in the random direction
        verify(mockRc, times(1)).move(randomDirection);
        assertEquals(randomDirection, getPrivateField(attackDuck, "lastDirection"));
    }

    @Test
    public void testExploreMove_NoValidMove() throws GameActionException, NoSuchFieldException, IllegalAccessException {
        // Setup mock behavior
        Direction lastDirection = Direction.NORTH;
        setPrivateField(attackDuck, "lastDirection", lastDirection);
        when(mockRc.canMove(lastDirection)).thenReturn(false);

        // Mock random direction
        Direction randomDirection = Direction.EAST;
        Random mockRng = mock(Random.class);
        setPrivateField(attackDuck, "rng", mockRng);
        when(mockRng.nextInt(anyInt())).thenReturn(randomDirection.ordinal());
        when(mockRc.canMove(randomDirection)).thenReturn(false);

        // Call the method under test
        attackDuck.exploreMove();

        // Verify that the robot did not move
        verify(mockRc, never()).move(any(Direction.class));
        assertEquals(lastDirection, getPrivateField(attackDuck, "lastDirection"));
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

    @Test
    public void testMoveToTargetMapLocation_CannotMove() throws GameActionException {
        // Setup mock behavior
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = new MapLocation(3, 3);
        Direction toTarget = currentLocation.directionTo(targetLocation);

        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toTarget)).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test
        attackDuck.moveToTargetMapLocation(targetLocation);

        // Verify that the robot moved randomly
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testMoveToTargetMapLocation_NoValidMove() throws GameActionException {
        // Setup mock behavior
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = new MapLocation(3, 3);
        Direction toTarget = currentLocation.directionTo(targetLocation);

        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toTarget)).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(false);

        // Call the method under test
        attackDuck.moveToTargetMapLocation(targetLocation);

        // Verify that the robot did not move
        verify(mockRc, never()).move(any(Direction.class));
    }

    @Test
    public void testAttackLowestHealthRobot() throws GameActionException {
        // Setup mock behavior
        RobotInfo robot1 = new RobotInfo(1, Team.A, 50, new MapLocation(1, 1), false, 1, 1, 1);
        RobotInfo robot2 = new RobotInfo(2, Team.A, 30, new MapLocation(1, 1), false, 1, 1, 1);
        RobotInfo robot3 = new RobotInfo(3, Team.A, 40, new MapLocation(1, 1), false, 1, 1, 1);

        RobotInfo[] enemyRobots = { robot1, robot2, robot3 };
        when(mockRc.getTeam()).thenReturn(Team.A);
        when(mockRc.senseNearbyRobots(-1, mockRc.getTeam().opponent())).thenReturn(enemyRobots);
        when(mockRc.canAttack(any(MapLocation.class))).thenReturn(true);

        // Call the method under test
        attackDuck.attackLowestHealthRobot(enemyRobots);

        // Verify that the robot attacked the lowest health robot
        verify(mockRc).attack(robot2.getLocation());
    }
    
    @Test
    public void testMoveToOppositeSpawnLocation() throws GameActionException, NoSuchFieldException, IllegalAccessException {
        // Initialize attackDuck and mockRc
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        MapLocation[] oppositeSpawnLocations = {
                new MapLocation(8, 8),
                new MapLocation(7, 7),
                new MapLocation(6, 6)
        };
        int robotID = 1;
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = oppositeSpawnLocations[robotID % oppositeSpawnLocations.length];
        Direction toTarget = currentLocation.directionTo(targetLocation);

        // Set private fields
        setPrivateField(attackDuck, "oppositeSpawnLocations", oppositeSpawnLocations);
        when(mockRc.getID()).thenReturn(robotID);
        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toTarget)).thenReturn(true);

        // Call the method under test
        attackDuck.moveToOppositeSpawnLocation();

        // Verify that the robot moved towards the target location
        verify(mockRc).move(toTarget);

        // Test the case where the direct path is blocked
        when(mockRc.canMove(toTarget)).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test again
        attackDuck.moveToOppositeSpawnLocation();

        // Verify that the robot moved randomly
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testMovingDecision_DivisibleBySeven() throws GameActionException, NoSuchFieldException, IllegalAccessException {
        // Initialize attackDuck and mockRc
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        int robotID = 7; // ID divisible by 7
        when(mockRc.getID()).thenReturn(robotID);

        // Mock exploreMove method
        AttackDuck spyAttackDuck = spy(attackDuck);
        doNothing().when(spyAttackDuck).exploreMove();

        // Call the method under test
        spyAttackDuck.movingDecision();

        // Verify that exploreMove was called
        verify(spyAttackDuck, times(1)).exploreMove();
        verify(spyAttackDuck, never()).moveToOppositeSpawnLocation();
    }

    @Test
    public void testMovingDecision_NotDivisibleBySeven() throws GameActionException, NoSuchFieldException, IllegalAccessException {
    // Initialize attackDuck and mockRc
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        int robotID = 8; // ID not divisible by 7
        when(mockRc.getID()).thenReturn(robotID);

        // Mock moveToOppositeSpawnLocation method
        AttackDuck spyAttackDuck = spy(attackDuck);
        doNothing().when(spyAttackDuck).moveToOppositeSpawnLocation();

        // Call the method under test
        spyAttackDuck.movingDecision();

        // Verify that moveToOppositeSpawnLocation was called
        verify(spyAttackDuck, times(1)).moveToOppositeSpawnLocation();
        verify(spyAttackDuck, never()).exploreMove();
    }

    @Test
    public void testMoveToFlagsLocation_CanMove() throws GameActionException {
        // Initialize attackDuck and mockRc
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation flagLocation = new MapLocation(5, 5);
        Direction toFlag = currentLocation.directionTo(flagLocation);
        FlagInfo mockFlag = mock(FlagInfo.class);
        when(mockFlag.getLocation()).thenReturn(flagLocation);
        FlagInfo[] flags = { mockFlag };

        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toFlag)).thenReturn(true);

        // Call the method under test
        attackDuck.moveToFlagsLocation(flags);

        // Verify that the robot moved towards the flag location
        verify(mockRc).move(toFlag);
    }

    @Test
    public void testMoveToFlagsLocation_CannotMove() throws GameActionException {
        // Initialize attackDuck and mockRc
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation flagLocation = new MapLocation(5, 5);
        Direction toFlag = currentLocation.directionTo(flagLocation);
        FlagInfo mockFlag = mock(FlagInfo.class);
        when(mockFlag.getLocation()).thenReturn(flagLocation);
        FlagInfo[] flags = { mockFlag };

        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toFlag)).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test
        attackDuck.moveToFlagsLocation(flags);

        // Verify that the robot moved randomly
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testMoveToFlagsLocation_NoValidMove() throws GameActionException {
        // Initialize attackDuck and mockRc
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation flagLocation = new MapLocation(5, 5);
        Direction toFlag = currentLocation.directionTo(flagLocation);
        FlagInfo mockFlag = mock(FlagInfo.class);
        when(mockFlag.getLocation()).thenReturn(flagLocation);
        FlagInfo[] flags = { mockFlag };

        when(mockRc.getLocation()).thenReturn(currentLocation);
        when(mockRc.canMove(toFlag)).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(false);

        // Call the method under test
        attackDuck.moveToFlagsLocation(flags);

        // Verify that the robot did not move
        verify(mockRc, never()).move(any(Direction.class));
    }

    @Test
    public void testPerformTurn_NotSpawned_AttemptsToSpawn() throws GameActionException {
        // Initialize mock objects
        RobotController mockRc = mock(RobotController.class);
        AttackDuck attackDuck = new AttackDuck(mockRc);

        // Setup mock behavior
        when(mockRc.isSpawned()).thenReturn(false);

        // Spy on the AttackDuck instance to verify method calls
        AttackDuck spyAttackDuck = spy(attackDuck);

        // Mock any additional dependencies or methods used in attemptToSpawn
        doNothing().when(spyAttackDuck).attemptToSpawn();

        // Call the method under test
        spyAttackDuck.performTurn();

        // Verify that attemptToSpawn was called
        verify(spyAttackDuck, times(1)).attemptToSpawn();
    }

    ///////////////////////
}
