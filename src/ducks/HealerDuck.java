package ducks;

import battlecode.common.*;

import java.util.Random;

public strictfp class HealerDuck extends RobotPlayer{
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();

    public HealerDuck(RobotController rc) {
        this.rc = rc;
    }

        @Override
        public void run() throws GameActionException {
            while (true) {
                turnCount += 1;
                try {
                    if (!rc.isSpawned()) {
                        attemptToSpawn();
                    } else {
                        findAndPickupFlag();

                        if (rc.hasFlag()) {
                            moveToAllySpawnLocation();
                        } else {
                            healNearbyAlliesOrMove();
                        }
                    }
                } catch (GameActionException e) {
                    System.out.println("Exception caught in HealerDuck run method.");
                    e.printStackTrace();
                } finally {
                    Clock.yield();
                }
            }
        }

    public void healNearbyAlliesOrMove() throws GameActionException {
        RobotInfo[] allies = rc.senseNearbyRobots(-1, rc.getTeam());

        // Sort allies by priority: flag carriers first, then by health
        RobotInfo bestAlly = null;
        for (RobotInfo ally : allies) {
            if (ally.getHealth() < 1000) {
                if (bestAlly == null || isHigherPriority(ally, bestAlly)) {
                    bestAlly = ally;
                }
            }
        }

        if (bestAlly != null) {
            // Try to heal the best ally
            if (attemptToHealAlly(bestAlly)) {
                return; // Successfully healed
            }

            // Move closer if not in range
            moveTowardAlly(bestAlly);
        } else {
            // No allies need healing; move randomly
            //moveRandomly();
            moveSmartly();
        }
    }


        public boolean attemptToHealAlly(RobotInfo ally) throws GameActionException {
            if (rc.canHeal(ally.location)) {
                rc.heal(ally.location);
                System.out.println("Healed ally at location: " + ally.location);
                return true;
            }
            return false;
        }

        private void moveTowardAlly(RobotInfo ally) throws GameActionException {
            Direction toAlly = rc.getLocation().directionTo(ally.location);
            if (rc.canMove(toAlly)) {
                rc.move(toAlly);
            }
        }

        public void moveRandomly() throws GameActionException {
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        }

        public boolean isHigherPriority(RobotInfo ally, RobotInfo bestAlly) {
            // Priority 1: Flag carriers are more important
            if (ally.hasFlag() && !bestAlly.hasFlag()) {
                return true;
            }
            if (!ally.hasFlag() && bestAlly.hasFlag()) {
                return false;
            }

            // Priority 2: Lower health is more important
            return ally.getHealth() < bestAlly.getHealth();
        }

        public void moveSmartly() throws GameActionException {
            // Check for nearby crumbs
            MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
            if (crumbs.length > 0) {
                moveTowardLocation(crumbs[0]); // Move toward the nearest crumb
                return;
            }

            // Check for nearby activity (flags or robots)
            FlagInfo[] flags = rc.senseNearbyFlags(-1);
            if (flags.length > 0) {
                moveTowardLocation(flags[0].getLocation()); // Move toward the nearest flag
                return;
            }
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1);
            if (nearbyRobots.length > 0) {
                moveTowardLocation(nearbyRobots[0].location); // Move toward a nearby robot
                return;
            }

            // Fallback: Systematic exploration (e.g., clockwise movement)
            moveClockwise();
        }

        private void moveTowardLocation(MapLocation target) throws GameActionException {
            Direction toTarget = rc.getLocation().directionTo(target);
            if (rc.canMove(toTarget)) {
                rc.move(toTarget);
                System.out.println("Moved toward target at: " + target);
            }
        }

        private void moveClockwise() throws GameActionException {
            for (Direction dir : directions) { // Loop through all directions
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    System.out.println("Moved clockwise in direction: " + dir);
                    return;
                }
            }
            System.out.println("No valid movement options.");
        }
}



