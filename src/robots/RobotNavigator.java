package robots;
import battlecode.common.*;
import ducks.RobotState;

import java.util.Random;
public class RobotNavigator {
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    private static int bugState=0;
    private static MapLocation closestObstacle=null;
    private static int closestDistance = 10000;
    private static Direction bugDir=null;

    public  void reset () {
        int bugState=0;
        closestObstacle=null;
        int closestDistance = 10000;
        bugDir = null;
    }

    public static void bugNav (RobotController rc, MapLocation dest) throws GameActionException {
        if(bugState==0){
            System.out.println("bugnav bugState:" + bugState);
            if (dest==null){System.out.println("bugnav dest is null");}
            bugDir= rc.getLocation().directionTo((dest));
            if(rc.canMove(bugDir)){
                rc.move(bugDir);
            } else {
                bugState =1;
                closestObstacle = null;
                closestDistance =10000;
            }
        } else {
            if (rc.getLocation().equals(closestObstacle)) {
                bugState=0;

            }
            if (rc.getLocation().distanceSquaredTo(dest) < closestDistance) {
                closestDistance  = rc.getLocation().distanceSquaredTo(dest);
                closestObstacle=rc.getLocation();
            }
            for (int i=0; i<8; i++){
                if (rc.canMove(bugDir)){
                    rc.move(bugDir);
                    bugDir=bugDir.rotateRight();
                    break;
                } else{
                    bugDir=bugDir.rotateLeft();
                }
            }
        }
    }

    RobotController rc;

    public RobotNavigator(RobotController rc) {
        this.rc = rc;
    }

    // Random movement function
    public void moveRandomly(RobotController rc) throws GameActionException {
        Direction[] directions = Direction.values();
        Direction randomDir = directions[new Random().nextInt(directions.length)];
        if (rc.canMove(randomDir)) {
            rc.move(randomDir);
            //  System.out.println("Moved randomly in direction: " + randomDir);
        } //else {
//            System.out.println("Could not move randomly. Staying in place.");
//        }
    }
}
