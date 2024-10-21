import battlecode.common.*;

public class HealerDuck extends Duck {

    public HealerDuck(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        // sense nearby ducks from our team
        RobotInfo[] allies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());

        // Check if there are allies to heal from our team
        RobotInfo allyToHeal = null;
        for (RobotInfo ally : allies) {
            if (ally.health < ally.type.getMaxHealth()) {
                allyToHeal = ally;
                break;
            }
        }

        if (allyToHeal != null) {
            // Heal the damaged ally
            if (rc.canHeal(allyToHeal.location)) {
                rc.heal(allyToHeal.location);
            } else {
                // Move towards the ally if not in range to heal
                move(rc.getLocation().directionTo(allyToHeal.location));
            }
        } else {
            // if no allies to heal move randomly
            move(randomDirection());
        }
    }

    private Direction randomDirection() {
        return Direction.values()[(int) (Math.random() * Direction.values().length)];
    }
}

