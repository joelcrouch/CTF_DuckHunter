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

            for (RobotInfo ally : allies) {
                if (ally.getHealth() < 1000) {
                    if (attemptToHealAlly(ally)) {
                        return; // Successfully healed; stop further actions this turn.
                    }
                    moveTowardAlly(ally);
                    return; // Moved closer to an ally; stop further actions this turn.
                }
            }

            // If no allies to heal or move towards, move randomly.
            moveRandomly();
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
//    @Override
//    public void run() throws GameActionException {
//        while (true) {
//            turnCount += 1;
//            try {
//                // Use the methods from RobotPlayer
//                if (!rc.isSpawned()) {
//                    attemptToSpawn();
//                } else {
//                    findAndPickupFlag();
//
//                    // If carrying a flag, move toward the nearest ally spawn location
//                    if (rc.hasFlag()) {
//                        moveToAllySpawnLocation();
//                        break;
//                    } else {
//                        // Sense nearby ducks from our team
//                        RobotInfo[] allies = rc.senseNearbyRobots(-1, rc.getTeam());
//
//                        for (RobotInfo ally : allies) {
//                            if (ally.getHealth() < 1000) {
//                                // If the ally's health is below 1000, try to heal them
//                                if (rc.canHeal(ally.location)) {
//                                    rc.heal(ally.location);
//                                    System.out.println("Healed ally at location: " + ally.location);
//                                } else {
//                                    // Move toward the ally if you can't heal them from the current location
//                                    Direction toAlly = rc.getLocation().directionTo(ally.location);
//                                    if (rc.canMove(toAlly)) {
//                                        rc.move(toAlly);
//                                    }
//                                }
//                            }
//                        }
//                        // If no allies to heal, move randomly
//                        Direction dir = directions[rng.nextInt(directions.length)];
//                        MapLocation nextLoc = rc.getLocation().add(dir);
//                        if (rc.canMove(dir)) {
//                            rc.move(dir);
//                        }
//                    }
//                }
//            } catch (GameActionException e) {
//                System.out.println("Exception caught in HealerDuck run method.");
//                e.printStackTrace();
//            } finally {
//                // End the turn
//                Clock.yield();
//            }
//        }
//    }
}



