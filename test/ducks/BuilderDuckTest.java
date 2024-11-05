package ducks;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BuilderDuckTest {
    RobotController mockRc;
    BuilderDuck builderDuck;
    Random mockRng;

    @Before
    public void setup() {
        mockRc = mock(RobotController.class);
        mockRng = mock(Random.class);
        builderDuck = new BuilderDuck(mockRc);
        builderDuck.rng = mockRng; // Use a mock random to control direction choice
    }

    @Test
    public void testRandomMovement_CanMove() throws GameActionException {
        Direction direction = Direction.NORTH;
        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
        when(mockRc.canMove(direction)).thenReturn(true);

        builderDuck.randomMovement();

        verify(mockRc, times(1)).move(direction);
    }

    @Test
    public void testRandomMovement_CannotMove() throws GameActionException {
        Direction direction = Direction.NORTH;
        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
        when(mockRc.canMove(direction)).thenReturn(false);

        builderDuck.randomMovement();

        verify(mockRc, times(0)).move(any(Direction.class));
    }

    @Test
    public void testBuildRandomTrap_CanBuild() throws GameActionException {
        Direction direction = Direction.EAST;
        MapLocation adjacentLocation = new MapLocation(1, 0);

        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
        when(mockRc.adjacentLocation(direction)).thenReturn(adjacentLocation);
        when(mockRc.canBuild(TrapType.EXPLOSIVE, adjacentLocation)).thenReturn(true);

        builderDuck.buildRandomTrap();

        verify(mockRc, times(1)).build(TrapType.EXPLOSIVE, adjacentLocation);
    }

    @Test
    public void testBuildRandomTrap_CannotBuild() throws GameActionException {
        Direction direction = Direction.EAST;
        MapLocation adjacentLocation = new MapLocation(1, 0);

        when(mockRng.nextInt(anyInt())).thenReturn(direction.ordinal());
        when(mockRc.adjacentLocation(direction)).thenReturn(adjacentLocation);
        when(mockRc.canBuild(TrapType.EXPLOSIVE, adjacentLocation)).thenReturn(false);

        builderDuck.buildRandomTrap();

        verify(mockRc, times(0)).build(any(TrapType.class), any(MapLocation.class));
    }
}
