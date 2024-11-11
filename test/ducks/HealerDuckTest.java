package ducks;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HealerDuckTest {
    RobotController mockRc;
    HealerDuck healerDuck;

    @Before
    public void setup() throws GameActionException {
        // Initialize mock RobotController
        mockRc = mock(RobotController.class);

        // Set common expectations for mockRc methods
        when(mockRc.isSpawned()).thenReturn(false);
        when(mockRc.canMove(any(Direction.class))).thenReturn(true);
        when(mockRc.senseNearbyRobots(-1, mockRc.getTeam())).thenReturn(new RobotInfo[0]);

        // Create a testable instance of HealerDuck with the mocked RobotController
        healerDuck = new HealerDuckTestable(mockRc);
    }

    @Test
    public void testAttemptToSpawn() throws GameActionException {
        when(mockRc.isSpawned()).thenReturn(false);

        healerDuck.run();

        // Verify that a spawn attempt is made
        verify(mockRc, times(1)).isSpawned();
    }

    @Test
    public void testHealsAllyWhenInRange() throws GameActionException {
        // Mock an ally robot with health less than 1000
        RobotInfo ally = mock(RobotInfo.class, withSettings().lenient());
        when(ally.getHealth()).thenReturn(900);
        when(ally.location).thenReturn(new MapLocation(1, 1));
        when(mockRc.senseNearbyRobots(-1, mockRc.getTeam())).thenReturn(new RobotInfo[]{ally});
        when(mockRc.canHeal(ally.location)).thenReturn(true);

        healerDuck.run();

        // Verify that the heal method is called for the ally in range
        verify(mockRc, times(1)).heal(ally.location);
    }


    @Test
    public void testMoveTowardsAllyWhenCannotHeal() throws GameActionException {
        // Mock an ally robot out of healing range
        RobotInfo ally = mock(RobotInfo.class);
        when(ally.getHealth()).thenReturn(900);
        when(ally.location).thenReturn(new MapLocation(5, 5));
        when(mockRc.senseNearbyRobots(-1, mockRc.getTeam())).thenReturn(new RobotInfo[]{ally});
        when(mockRc.canHeal(ally.location)).thenReturn(false);
        when(mockRc.getLocation()).thenReturn(new MapLocation(0, 0));
        Direction directionToAlly = new MapLocation(0, 0).directionTo(ally.location);
        when(mockRc.canMove(directionToAlly)).thenReturn(true);

        healerDuck.run();

        // Verify that the duck moves toward the ally when unable to heal directly
        verify(mockRc, times(1)).move(directionToAlly);
    }

    @Test
    public void testRandomMovementWhenNoAlliesToHeal() throws GameActionException {
        when(mockRc.senseNearbyRobots(-1, mockRc.getTeam())).thenReturn(new RobotInfo[0]);
        Direction randomDirection = Direction.NORTH; // Specify a direction for simplicity
        when(mockRc.canMove(randomDirection)).thenReturn(true);

        healerDuck.run();

        // Verify that the duck moves randomly when no allies need healing
        verify(mockRc, atLeastOnce()).move(randomDirection);
    }

    @Test
    public void testNoMovementWhenFlagHeld() throws GameActionException {
        when(mockRc.isSpawned()).thenReturn(true);
        when(mockRc.hasFlag()).thenReturn(true);

        healerDuck.run();

        // Verify that movement toward ally spawn location occurs when holding a flag
        verify(mockRc, atLeastOnce()).move(any(Direction.class)); // Customize if needed
    }

    // Custom subclass to bypass Clock.yield() during tests
    private class HealerDuckTestable extends HealerDuck {
        public HealerDuckTestable(RobotController rc) {
            super(rc);
        }

        @Override
        public void run() throws GameActionException {
            turnCount += 1;
            if (!rc.isSpawned()) {
                attemptToSpawn();
            }
        }
    }
}
