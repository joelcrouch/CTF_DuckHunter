//package ducks;
//
//
//import battlecode.common.*;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//import java.util.Random;
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//public class HealerDuckTest {
//    RobotController mockRc;
//    HealerDuck healerDuck;
//    Random mockRng;
//
//    @Before
//    public void setUp() {
//        mockRc = mock(RobotController.class);
//        mockRng = mock(Random.class);
//        healerDuck = new HealerDuck(mockRc);
//        healerDuck.rng = mockRng;
//    }
//
//    @Test
//    public void testMoveRandomly_Success() throws GameActionException {
//        Direction direction = Direction.NORTH;
//        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
//        when(mockRc.canMove(direction)).thenReturn(true);
//        healerDuck.moveRandomly();
//        verify(mockRc, times(1)).move(direction);
//    }
//
//    @Test
//    public void testRandomMovement_CanMove() throws GameActionException {
//        Direction direction = Direction.NORTH;
//        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
//        when(mockRc.canMove(direction)).thenReturn(true);
//        healerDuck.moveRandomly();
//        verify(mockRc, times(1)).move(direction);
//    }
//
//
//
////    @Test
////    public void testAttemptToHealAlly_Success() throws GameActionException {
////        // Mock an ally RobotInfo
////        RobotInfo mockAlly = mock(RobotInfo.class);
////        MapLocation mockLocation = new MapLocation(1, 1);
////
////        // Mock RobotInfo to return the correct location
////        when(mockAlly.getLocation()).thenReturn(mockLocation);
////
////        // Mock RobotController behavior
////        when(mockRc.canHeal(mockLocation)).thenReturn(true);
////
////        // Call the method
////        boolean result = healerDuck.attemptToHealAlly(mockAlly);
////
////        // Verify the behavior
////        assertTrue("Expected attemptToHealAlly to return true", result);
////        verify(mockRc, times(1)).canHeal(mockLocation);
////        verify(mockRc, times(1)).heal(mockLocation);
////    }
//
//
//    @Test
//    public void testAttemptToHealAlly_ReturnFalse() throws GameActionException {
//        // Mock an ally RobotInfo
//        RobotInfo mockAlly = mock(RobotInfo.class);
//        MapLocation mockLocation = new MapLocation(1, 1);
//
//        // Mock RobotInfo to return the correct location
//        when(mockAlly.getLocation()).thenReturn(mockLocation);
//
//        // Mock RobotController behavior
//        when(mockRc.canHeal(mockLocation)).thenReturn(false);
//
//        // Call the method
//        boolean result = healerDuck.attemptToHealAlly(mockAlly);
//
//        // Verify the behavior
//        assertFalse("Expected attemptToHealAlly to return true", result);
//        verify(mockRc, times(0)).canHeal(mockLocation);
//        verify(mockRc, times(0)).heal(mockLocation);
//    }
//
//
//
//}
