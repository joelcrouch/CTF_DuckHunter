package ducks;

import battlecode.common.*;

import java.util.Random;

public strictfp class BuilderDuck extends RobotPlayer {  // Extending RobotPlayer if necessary
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();

    public BuilderDuck(RobotController rc) {
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
                    // Attempt to pick up a flag if there's one available
                    findAndPickupFlag();

                    // If carrying a flag, move toward the nearest ally spawn location
                    if (rc.hasFlag()) {
                        moveToAllySpawnLocation();
                    } else {
                        // Update its position in shared array for AttackDuck
                        updateLocationInSharedArray();
                        // Continue with the builder duck's usual behavior if not carrying a flag
                        buildRandomTrap();
                        randomMovement();
                    }
                }
            } catch (GameActionException e) {
                System.out.println("Exception caught in BuilderDuck run method.");
                e.printStackTrace();
            } finally {
                // End the turn
                Clock.yield();
            }
        }
    }

    // Method to update its location in a shared array
    private void updateLocationInSharedArray() throws GameActionException {
        MapLocation currentLoc = rc.getLocation();
        // Write the BuilderDuck's location in shared array (assuming index 1)
        if (rc.canWriteSharedArray(1, currentLoc.x)) {
            rc.writeSharedArray(1, currentLoc.x);
            rc.writeSharedArray(2, currentLoc.y);
        }
    }

    // Method to randomly move the builder duck
    private void randomMovement() throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    // Method to build a random trap
    private void buildRandomTrap() throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canBuild(TrapType.EXPLOSIVE, rc.adjacentLocation(dir))) {
            rc.build(TrapType.EXPLOSIVE, rc.adjacentLocation(dir));
            System.out.println("Built an explosive trap at " + rc.adjacentLocation(dir));
        }
    }
}
