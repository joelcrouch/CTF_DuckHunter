package ducks;

import battlecode.common.*;

import java.util.Random;

public  class HealerDuck {
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();

    public HealerDuck(RobotController rc) {
        this.rc = rc;
    }

    public void healNearbyAlliesOrMove() throws GameActionException {
        System.out.println("Healing NearbyAlliesOrMove");
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


}



