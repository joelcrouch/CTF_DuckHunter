import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class Duck {
    protected RobotController rc;
    protected Random rng;
    protected int turnCount = 0;

    public Duck(RobotController rc) {
        this.rc = rc;
        this.rng = new Random(6147);
    }

    // Abstract method that must be implemented by subclasses
    public abstract void run() throws GameActionException;

    // Common movement or helper/escape flag/pickupflags methods can be added here
    // All the stuff in common
    protected void moveRandomly() throws GameActionException {
        Direction[] directions = Direction.values();
        Direction randomDir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(randomDir)) {
            rc.move(randomDir);
        }
    }

    public void move(Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    public RobotInfo[] senseNearbyEnemies() throws GameActionException {
        return rc.senseNearbyRobots(-1, rc.getTeam().opponent());
    }
}
