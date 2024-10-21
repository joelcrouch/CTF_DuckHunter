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


    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    public Duck(RobotController rc) {
        this.rc = rc;
        this.rng = new Random(6147);
    }

    // Abstract method that must be implemented by subclasses
    public abstract void run() throws GameActionException;



    // Common movement behavior
    protected void moveRandomly() throws GameActionException {
        Direction randomDir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(randomDir)) {
            rc.move(randomDir);
        }
    }

    // General-purpose movement method
    public void move(Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    // Sense nearby enemies
    protected RobotInfo[] senseEnemies() throws GameActionException {
        return rc.senseNearbyRobots(-1, rc.getTeam().opponent());
    }

    // pickup or sensing a flag
    protected void findAndPickupFlag() throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation())) {
            rc.pickupFlag(rc.getLocation());
            rc.setIndicatorString("Picked up a flag!");
        }
    }

    // General method to find flag and move toward it
    protected void moveToAllySpawnLocation() throws GameActionException {
        if (rc.hasFlag()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            if (spawnLocs.length > 0) {
                MapLocation nearestSpawn = spawnLocs[0];  // Choose nearest spawn location
                Direction toSpawn = rc.getLocation().directionTo(nearestSpawn);
                move(toSpawn);
            }
        }
    }
}













