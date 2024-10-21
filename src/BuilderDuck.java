import battlecode.common.*;

public class BuilderDuck extends Duck {

    public BuilderDuck(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        RobotInfo[] enemies = senseNearbyEnemies();

        if (enemies.length > 0) {
            Direction awayFromEnemies = rc.getLocation().directionTo(enemies[0].location).opposite();
            move(awayFromEnemies);
        }
        else {
            if (rc.hasFlag()) {
                moveToAllySpawnLocation();
            } else {
                findAndPickupFlag();
                move(randomDirection());
            }
        }
    }

    private Direction randomDirection() {
        return Direction.values()[(int) (Math.random() * Direction.values().length)];
    }
}
