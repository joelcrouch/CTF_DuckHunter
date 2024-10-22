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
}