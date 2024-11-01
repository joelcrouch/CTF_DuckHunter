package ducks;

import battlecode.common.*;

import java.util.Random;

public strictfp class AttackDuck extends RobotPlayer {  // Extending RobotPlayer
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
                        // Implement the avoidBuilderDuck logic
                        avoidBuilderDuck();

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

    private void avoidBuilderDuck() throws GameActionException {
        // Sense nearby robots
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
        boolean hasBuilderDuck = false;
        int attackDuckCount = 0;

        // Check for BuilderDucks and count nearby AttackDucks
        for (RobotInfo robot : nearbyRobots) {
            // Example condition for BuilderDuck (customize this logic)
            if (robot.getTeam() == rc.getTeam() && robot.getID() % 2 == 0) { // Placeholder condition for BuilderDuck
                hasBuilderDuck = true;
            } else if (robot.getTeam() == rc.getTeam() && robot.getID() % 2 != 0) { // Placeholder condition for AttackDuck
                attackDuckCount++;
            }
        }

        // If there's a BuilderDuck and at least one nearby AttackDuck, move away from the BuilderDuck
        if (hasBuilderDuck && attackDuckCount > 0) {
            // Move away from the BuilderDuck
            for (RobotInfo robot : nearbyRobots) {
                // Same condition used here to identify the BuilderDuck
                if (robot.getTeam() == rc.getTeam() && robot.getID() % 2 == 0) { // Example condition for BuilderDuck
                    // Calculate the direction to move away from the BuilderDuck
                    Direction awayFromBuilder = rc.getLocation().directionTo(robot.getLocation()).opposite();
                    if (rc.canMove(awayFromBuilder)) {
                        rc.move(awayFromBuilder);
                        System.out.println("Moving away from BuilderDuck!");
                    }
                    break; // Exit after moving away from the first found BuilderDuck
                }
            }
        }
    }
}
