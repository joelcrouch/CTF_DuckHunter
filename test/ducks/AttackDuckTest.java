package ducks;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

//write the public class AttackDuckTest
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
    public void testMoveToOppositeSpawnLocation() throws GameActionException {
        // Setup mock behavior
        MapLocation[] mockSpawnLocations = { new MapLocation(0, 0), new MapLocation(10, 10) };
        when(mockRc.getAllySpawnLocations()).thenReturn(mockSpawnLocations);
        when(mockRc.getMapWidth()).thenReturn(20);
        when(mockRc.getMapHeight()).thenReturn(20);
        when(mockRc.getLocation()).thenReturn(new MapLocation(5, 5));
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Calculate opposite spawn locations
        attackDuck.calculateOppositeSpawnLocations();

        // Move to opposite spawn location
        attackDuck.moveToOppositeSpawnLocation();

        // Verify that the robot moved
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testExploreMove() throws GameActionException {
        // Setup mock behavior
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test
        attackDuck.exploreMove();

        // Verify that the robot moved in a valid direction
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testMovingDecision() throws GameActionException {
        // Setup mock behavior
        FlagInfo flag1 = new FlagInfo(new MapLocation(1, 1), Team.A, false, 1);
        FlagInfo flag2 = new FlagInfo(new MapLocation(2, 2), Team.A, false, 2);
        FlagInfo[] flags = { flag1, flag2 };

        when(mockRc.getID()).thenReturn(15); // Not divisible by 7
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);

        // Call the method under test
        attackDuck.movingDecision(flags);

        // Verify that the robot moved towards the flag's location
        verify(mockRc, atLeastOnce()).move(any(Direction.class));

        // Test with robot ID divisible by 7
        when(mockRc.getID()).thenReturn(21); // Divisible by 7

        // Call the method under test
        attackDuck.movingDecision(flags);

        // Verify that the robot explored the map
        verify(mockRc, atLeastOnce()).move(any(Direction.class));
    }
}
