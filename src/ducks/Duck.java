package ducks;
import battlecode.common.*;

import java.util.Random;


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

    protected void moveRandomly() throws GameActionException {
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

    protected void findAndPickupFlag() throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation())) {
            rc.pickupFlag(rc.getLocation());
            rc.setIndicatorString("Picked up a flag!");
        }
    }

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













