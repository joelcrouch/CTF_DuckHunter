package ducks;

import battlecode.common.*;

import java.util.Random;

public strictfp class AttackDuck extends RobotPlayer {  // Extending RobotPlayer if necessary
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();

    public AttackDuck(RobotController rc) {
        this.rc = rc;
    }

    @Override
    public void run() throws GameActionException {
        while (true) {
            turnCount += 1;
            try {
                // Use the methods from RobotPlayer
                if (!rc.isSpawned()) {
                    attemptToSpawn();
                } else {
                    findAndPickupFlag();

                    // Check the location of the BuilderDuck
                    MapLocation builderLoc = getBuilderLocationFromSharedArray();
                    if (builderLoc != null) {
                        // Move towards the BuilderDuck if it's nearby
                        moveToBuilder(builderLoc);
                    }

                    // If carrying a flag, move toward the nearest ally spawn location
                    if (rc.hasFlag()) {
                        moveToAllySpawnLocation();
                    } else {
                        // Do some attacking and move about
                        // Move and attack randomly if no objective.
                        Direction dir = directions[rng.nextInt(directions.length)];
                        MapLocation nextLoc = rc.getLocation().add(dir);
                        if (rc.canMove(dir)) {
                            rc.move(dir);
                        } else if (rc.canAttack(nextLoc)) {
                            rc.attack(nextLoc);
                            System.out.println("Take that! Attack duck hit an enemy that was in our way!");
                        }
                    }
                }
            } catch (GameActionException e) {
                System.out.println("Exception caught in AttackDuck run method.");
                e.printStackTrace();
            } finally {
                // End the turn
                Clock.yield();
            }
        }
    }

    // Method to get the BuilderDuck's location from the shared array
    private MapLocation getBuilderLocationFromSharedArray() throws GameActionException {
        int x = rc.readSharedArray(1);
        int y = rc.readSharedArray(2);
        if (x != -1 && y != -1) {
            return new MapLocation(x, y);
        }
        return null; // Return null if no valid location
    }

    // Move towards the BuilderDuck's location
    private void moveToBuilder(MapLocation builderLoc) throws GameActionException {
        Direction toBuilder = rc.getLocation().directionTo(builderLoc);
        if (rc.canMove(toBuilder)) {
            rc.move(toBuilder);
        }
    }
}
