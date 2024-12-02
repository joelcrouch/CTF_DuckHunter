package ducks;


import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Random;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HealerDuckTest {
    RobotController mockRc;
    HealerDuck healerDuck;
    Random mockRng;

    @Before
    public void setUp() {
        mockRc = mock(RobotController.class);
        mockRng = mock(Random.class);
        healerDuck = new HealerDuck(mockRc);
        healerDuck.rng = mockRng;
    }

    @Test
    public void testMoveRandomly_Success() throws GameActionException {
        Direction direction = Direction.NORTH;
        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
        when(mockRc.canMove(direction)).thenReturn(true);
        healerDuck.moveRandomly();
        verify(mockRc, times(1)).move(direction);
    }

    @Test
    public void testRandomMovement_CanMove() throws GameActionException {
        Direction direction = Direction.NORTH;
        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
        when(mockRc.canMove(direction)).thenReturn(true);
        healerDuck.moveRandomly();
        verify(mockRc, times(1)).move(direction);
    }

    @Test
    public void testAttemptToHealAlly_ReturnFalse() throws GameActionException {
        // Mock an ally RobotInfo
        RobotInfo mockAlly = mock(RobotInfo.class);
        MapLocation mockLocation = new MapLocation(1, 1);

        // Mock RobotInfo to return the correct location
        when(mockAlly.getLocation()).thenReturn(mockLocation);

        // Mock RobotController behavior
        when(mockRc.canHeal(mockLocation)).thenReturn(false);

        // Call the method
        boolean result = healerDuck.attemptToHealAlly(mockAlly);

        // Verify the behavior
        assertFalse("Expected attemptToHealAlly to return true", result);
        verify(mockRc, times(0)).canHeal(mockLocation);
        verify(mockRc, times(0)).heal(mockLocation);
    }

    @Test
    public void testIsHigherPriority_FlagCarrierIsHigherPriority() {
        // Mock two RobotInfo instances
        RobotInfo flagCarrier = mock(RobotInfo.class);
        RobotInfo nonFlagCarrier = mock(RobotInfo.class);

        // Mock flag status
        when(flagCarrier.hasFlag()).thenReturn(true);
        when(nonFlagCarrier.hasFlag()).thenReturn(false);

        // Assert flag carrier is higher priority
        assertTrue("Expected flag carrier to be higher priority", healerDuck.isHigherPriority(flagCarrier, nonFlagCarrier));
        assertFalse("Expected non-flag carrier to be lower priority", healerDuck.isHigherPriority(nonFlagCarrier, flagCarrier));
    }

    @Test
    public void testIsHigherPriority_LowerHealthIsHigherPriority() {
        // Mock two RobotInfo instances
        RobotInfo lowHealthAlly = mock(RobotInfo.class);
        RobotInfo highHealthAlly = mock(RobotInfo.class);

        // Mock health values using int instead of double
        when(lowHealthAlly.getHealth()).thenReturn(200);
        when(highHealthAlly.getHealth()).thenReturn(800);

        // Assert lower health is higher priority
        assertTrue("Expected lower-health ally to be higher priority", healerDuck.isHigherPriority(lowHealthAlly, highHealthAlly));
        assertFalse("Expected higher-health ally to be lower priority", healerDuck.isHigherPriority(highHealthAlly, lowHealthAlly));
    }

    @Test
    public void testIsHigherPriority_SamePriority() {
        // Mock two RobotInfo instances with the same priority
        RobotInfo ally1 = mock(RobotInfo.class);
        RobotInfo ally2 = mock(RobotInfo.class);

        // Mock health and flag status
        when(ally1.getHealth()).thenReturn(500);
        when(ally2.getHealth()).thenReturn(500);
        when(ally1.hasFlag()).thenReturn(false);
        when(ally2.hasFlag()).thenReturn(false);

        // Assert neither is higher priority
        assertFalse("Expected same-priority allies to not be higher priority", healerDuck.isHigherPriority(ally1, ally2));
        assertFalse("Expected same-priority allies to not be higher priority", healerDuck.isHigherPriority(ally2, ally1));
    }

    @Test
    public void testHealNearbyAlliesOrMove_NoAlliesNeedHealing() throws GameActionException {
        // Setup: No allies or allies at full health
        RobotInfo[] allies = new RobotInfo[]{
                mock(RobotInfo.class)
        };

        when(mockRc.senseNearbyRobots(-1, mockRc.getTeam())).thenReturn(allies);
        when(allies[0].getHealth()).thenReturn(1000);  // Full health

        // Mock random direction selection
        Direction[] directions = Direction.allDirections();
        when(mockRng.nextInt(directions.length)).thenReturn(0);  // Select first direction
        when(mockRc.canMove(directions[0])).thenReturn(true);

        // Execute
        healerDuck.healNearbyAlliesOrMove();

        // Verify random movement occurred
        verify(mockRc).move(directions[0]);
    }

    //    @Test
//    public void testAttemptToHealAlly_Success() throws GameActionException {
//        // Mock an ally RobotInfo
//        RobotInfo mockAlly = mock(RobotInfo.class);
//        MapLocation mockLocation = new MapLocation(1, 1);
//
//        // Mock RobotInfo to return the correct location
//        when(mockAlly.getLocation()).thenReturn(mockLocation);
//
//        // Mock RobotController behavior
//        when(mockRc.canHeal(mockLocation)).thenReturn(true);
//
//        // Call the method
//        boolean result = healerDuck.attemptToHealAlly(mockAlly);
//
//        // Verify the behavior
//        assertTrue("Expected attemptToHealAlly to return true", result);
//        verify(mockRc, times(1)).canHeal(mockLocation);
//        verify(mockRc, times(1)).heal(mockLocation);
//    }


}
