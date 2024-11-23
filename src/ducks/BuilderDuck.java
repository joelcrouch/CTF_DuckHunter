package ducks;

import battlecode.common.*;

import java.util.Random;

public class BuilderDuck {  // Extending RobotPlayer if necessary
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();

    public BuilderDuck(RobotController rc) {
        this.rc = rc;
    }

    // Method to randomly move the builder duck
    protected void randomMovement() throws GameActionException {

        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    // Method to build a random trap

    protected void buildRandomTrap() throws GameActionException {

        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canBuild(TrapType.EXPLOSIVE, rc.adjacentLocation(dir))) {
            rc.build(TrapType.EXPLOSIVE, rc.adjacentLocation(dir));
            System.out.println("Built an explosive trap at " + rc.adjacentLocation(dir));
        }
    }

    public void doBuilderDuckActions() throws GameActionException {
        randomMovement();
        buildRandomTrap();
    }
}