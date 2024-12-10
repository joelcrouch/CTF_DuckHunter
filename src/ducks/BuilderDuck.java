package ducks;

import battlecode.common.*;
import ducks.RobotState;
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
            System.out.println(" Ducks Built an explosive trap at " + rc.adjacentLocation(dir));
        }
    }

    public boolean fillWater () throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canFill(rc.adjacentLocation(dir))) {
            rc. fill(rc.adjacentLocation(dir));
            return true;
        }
        return false;
    }

    public void doBuilderDuckActions(RobotState state) throws GameActionException {
        //System.out.println("Doing a builder duck actions");
        //put some if statement here such that if there are traps nearby  move else build a trap
        if (fillWater()) {
            System.out.println("Filled a location ");
            //();
           // state.setMoved(true);
        } else {;
            buildRandomTrap();
        }
    }
}