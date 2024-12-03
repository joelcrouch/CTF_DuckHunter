package ducks;
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

    RobotController rc;

    public RobotNavigator(RobotController rc) {
        this.rc = rc;
    }

    public void returnToBase(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation firstLoc = spawnLocs[0];
        Direction dir = rc.getLocation().directionTo(firstLoc);
        if (rc.canMove(dir)) rc.move(dir);

        System.out.println("Rnning home!");
    }


    public void moveTo(MapLocation current, MapLocation destination, RobotController rc) throws GameActionException {
        // If already at the destination, do nothing
        if (current.equals(destination)) {
           // System.out.println("Already at the destination: " + destination);
            return;
        }
//        if ( checkStateForNewDest(state, rc)) {
//            int tm = state.getTeamNumber();
//            dx = (rc.readSharedArray(14 +(tm *5) +2))- rc.getLocation().x;
//            dy = (rc.readSharedArray(14 +(tm *5) +3))- rc.getLocation().y;
//

      //  }
//        else if (!rc.hasFlag()){
//            if (senseFlags(rc, state)) {
//                System.out.println("Found a flag");
//                int tm = state.getTeamNumber();
//                dx = (rc.readSharedArray(14 +(tm *5) +2))- rc.getLocation().x;
//                dy = (rc.readSharedArray(14 +(tm *5) +3))- rc.getLocation().y;
//
//            }
//            else{
//                dx = destination.x - current.x;
//                dy = destination.y - current.y;
//            }
//        }
        // Compute the x and y differences
        int dx = destination.x - current.x;
        int dy = destination.y - current.y;
        //System.out.println("Moving " + dx + " " + dy);
        // Determine the primary direction to move
        Direction primaryDirection = computeDirection(dx, dy);

        // Attempt to move in the primary direction
        if (primaryDirection != null && rc.canMove(primaryDirection)) {
            rc.move(primaryDirection);
            //System.out.println("Moved: " + primaryDirection);
        } else {
            // If movement in the primary direction is blocked, try alternative directions
            moveAroundObstacle(primaryDirection);
        }
    }

    private Direction computeDirection(int dx, int dy) {
        if (dx == 0 && dy == 0) return null; // No movement needed

        if (dx > 0 && dy > 0) return Direction.SOUTHEAST;
        if (dx > 0 && dy == 0) return Direction.EAST;
        if (dx > 0 && dy < 0) return Direction.NORTHEAST;
        if (dx == 0 && dy > 0) return Direction.SOUTH;
        if (dx == 0 && dy < 0) return Direction.NORTH;
        if (dx < 0 && dy > 0) return Direction.SOUTHWEST;
        if (dx < 0 && dy == 0) return Direction.WEST;
        if (dx < 0 && dy < 0) return Direction.NORTHWEST;

        return null; // Shouldn't reach here
    }

    private String getSpawnOrientation(RobotController rc) throws GameActionException {

        MapLocation[] allySpawns = rc.getAllySpawnLocations();
        int left = Integer.MAX_VALUE, right = Integer.MIN_VALUE, top = Integer.MAX_VALUE, bottom = Integer.MIN_VALUE;

        // Determine the edges of the spawn locations
        for (MapLocation loc : allySpawns) {
            left = Math.min(left, loc.x);
            right = Math.max(right, loc.x);
            top = Math.min(top, loc.y);
            bottom = Math.max(bottom, loc.y);
        }

        int width = rc.getMapWidth();
        int height = rc.getMapHeight();

        // Classify orientation based on spawn position
        if (top < height / 4) return "TOP";
        if (bottom > 3 * height / 4) return "BOTTOM";
        if (left < width / 4) return "LEFT";
        if (right > 3 * width / 4) return "RIGHT";
        return "CORNER";
    }

    public void navigateToLane(RobotState robotState) throws GameActionException {
        // Get the spawn orientation and map dimensions
        System.out.println("in navigate fucntion");
        String orientation = getSpawnOrientation(rc);
        int height = rc.getMapHeight();
        int width = rc.getMapWidth();

        // Get team number and determine lane spacing
        int teamNum = robotState.getTeamNumber();
        int laneSpacing = (orientation.equals("TOP") || orientation.equals("BOTTOM")) ? width / 50 : height / 50;

        // Compute starting and target locations
        MapLocation startLocation = null;
        MapLocation targetLocation = null;

        switch (orientation) {
            case "TOP":
                startLocation = new MapLocation(teamNum * laneSpacing, 0); // Start near the top
                targetLocation = new MapLocation(teamNum * laneSpacing, height - 1);
                break;
            case "BOTTOM":
                startLocation = new MapLocation(teamNum * laneSpacing, height - 1); // Start near the bottom
                targetLocation = new MapLocation(teamNum * laneSpacing, 0);
                break;
            case "LEFT":
                startLocation = new MapLocation(0, teamNum * laneSpacing); // Start near the left
                targetLocation = new MapLocation(width - 1, teamNum * laneSpacing);
                break;
            case "RIGHT":
                startLocation = new MapLocation(width - 1, teamNum * laneSpacing); // Start near the right
                targetLocation = new MapLocation(0, teamNum * laneSpacing);
                break;
        }

        // Update robotState with the new target location

        robotState.setTargetLocation(targetLocation);



        // Start navigating toward the target
        if (startLocation != null) {
            moveTo(rc.getLocation(), startLocation, rc);
        }
    }

    private void moveAroundObstacle(Direction primaryDirection) throws GameActionException {
        if (primaryDirection == null) return;

        // Look for alternative directions
        for (Direction dir : directions) {
            if (rc.canMove(dir)) {
                rc.move(dir);
                return;
            }
        }

        // If no alternative direction is available, stay in place
        //System.out.println("Obstacle in all directions. Unable to move.");
    }

    public  MapLocation generateRandomLocation(RobotController rc) {
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        long seed = ((long) rc.getID() * 31) + rc.getRoundNum();
        Random random = new Random(seed); // Use robot ID as seed for consistency

        int x = random.nextInt(width);  // Random x-coordinate within map width
        int y = random.nextInt(height); // Random y-coordinate within map height

        return new MapLocation(x, y);
    }


    public void validateAndReassignDestination(RobotController rc,  RobotState robotState) throws GameActionException {
        MapLocation currentTarget = robotState.getTargetLocation();
        if (currentTarget == null) {
            System.out.println("No current target. Assigning new target.");
            robotState.setTargetLocation(generateRandomLocation(rc));
            return; // Exit early to avoid null pointer issues
        }

        MapInfo[] locations = rc.senseNearbyMapInfos();
        // Check if the current target is in the array of locations
        boolean targetInArray = false;
        for (MapInfo loc : locations) {
            if (loc.getMapLocation().equals(currentTarget)) {
                targetInArray = true;
                break;
            }

        }

        // If target is in the array, check for opposing flags nearby
        if (targetInArray) {
            MapLocation[] flags = rc.senseBroadcastFlagLocations();
            if (flags.length > 0) {
                robotState.setTargetLocation(flags[0]);
            } else {
                MapLocation newTarget = generateRandomLocation(rc);
                robotState.setTargetLocation(newTarget);
            }
        }
    }

    public static MapLocation  senseFlags(RobotController rc, RobotState state) throws GameActionException {
        MapLocation[] flagLocs= rc.senseBroadcastFlagLocations();
        if (flagLocs.length != 0) {
            int x = flagLocs[0].x;
            int y = flagLocs[1].y;
            int foundAFlag=0;
            int tm = state.getTeamNumber();
            int startIndex= 14 + (tm *5);
            rc.writeSharedArray(startIndex +2,x);
            rc.writeSharedArray(startIndex +3,y);
            rc.writeSharedArray(startIndex, foundAFlag);
            return flagLocs[0];
        }
        return new MapLocation(-1, -1);
    }

    public static boolean checkStateForNewDest(RobotState state, RobotController rc) throws GameActionException {
        int tm = state.getTeamNumber();
        int checkIndex = 14 + (tm *5);
        if(rc.readSharedArray(checkIndex) == 0){
            return true;
        }
        return false;
    }

    public void moveTowardsTarget(RobotController rc, RobotState robotState) throws GameActionException {
        MapLocation target = robotState.getTargetLocation();
        if (target == null) {
            System.out.println("No target location set. Moving randomly.");
            moveRandomly(rc);
            return;
        }

        Direction dir = rc.getLocation().directionTo(target);
        MapLocation adjacentLocation = rc.adjacentLocation(dir);

        // Check if the adjacent location is passable
        if (rc.canMove(dir) && rc.senseMapInfo(adjacentLocation).isPassable()) {
            rc.move(dir);
            //System.out.println("Moved towards target: " + target);
        } else {
           // System.out.println("Target direction not passable. Moving randomly.");
            moveRandomly(rc);
        }
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
