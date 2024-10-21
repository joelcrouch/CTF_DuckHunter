import battlecode.common.*;

public class AttackDuck extends Duck {
    private Direction lastDirection = null;

    public AttackDuck(RobotController rc) {
        super(rc); // Call the constructor of the Duck class
    }

    @Override
    public void run() throws GameActionException {
        // Example behavior for the AttackDuck
        RobotInfo[] enemies = senseNearbyEnemies(); // Use inherited method to sense enemies

        if (enemies.length > 0) {
            // If there are enemies nearby, attack or move towards them??
            ///////////////////////////////////////////////
            // Try to attack the closest enemy
            attackEnemies(enemies);
        } else {
            // If no enemies are nearby, move randomly
            move(randomDirection());
        }
    }

    private void attackEnemies(RobotInfo[] enemies) throws GameActionException {
        for (RobotInfo enemy : enemies) {
            MapLocation enemyLocation = enemy.location;

            // Check if the robot can attack the enemy at its location
            if (rc.canAttack(enemyLocation)) {
                rc.attack(enemyLocation); // Attack the enemy at the location
                break; // Exit after attacking one enemy
            }
        }
    }

    // Method to get a random direction
    private Direction randomDirection() {
        Direction[] directions = Direction.values();
        Direction nextDirection;

        do {
            nextDirection = directions[(int) (Math.random() * directions.length)];
        } while (nextDirection == lastDirection); // Avoid repeating the last direction

        lastDirection = nextDirection; // Update last direction
        return nextDirection;
    }

}
