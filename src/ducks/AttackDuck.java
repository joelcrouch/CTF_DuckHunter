package ducks;

import battlecode.common.*;

import java.util.Random;

import javax.naming.directory.DirContext;

public strictfp class AttackDuck extends RobotPlayer { // Extending RobotPlayer if necessary
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();
    private Direction lastDirection; // Declare the lastDirection field
    private MapLocation[] oppositeSpawnLocations; // Array to store opposite spawn locations

    public AttackDuck(RobotController rc) {
        this.rc = rc;
        this.lastDirection = null;
    }

    @Override
    public void run() throws GameActionException {
        while (true) {
            turnCount += 1;
            try {
                // Use the methods from RobotPlayer
                // Check if the robot ready to spawn a unit, then spawn it
                if (!rc.isSpawned()) {
                    attemptToSpawn();

                } else {

                    FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());

                    // if there are flags nearby, pickup the flag
                    for (FlagInfo flag : flags) {
                        // findAndPickupFlag();
                        if (rc.canPickupFlag(flag.getLocation())) {
                            rc.pickupFlag(flag.getLocation());
                            break;
                        }
                    }
                    if (rc.hasFlag()) {
                        // If the robot has a flag, move towards the ally spawn location
                        moveToAllySpawnLocation();
                    }
                    // if robot has no flag, check if there are enemy robots nearby
                    else {
                        updateEnemyRobots(rc);
                        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
                        // if there are enemy robots nearby, attack the lowest health robot
                        if (enemyRobots.length > 0) {
                            attackLowestHealthRobot(enemyRobots);
                        } else {
                            movingDecision(flags);
                        }

                    }
                }

            } catch (GameActionException e) {
                System.out.println("Exception caught in AttackDuck run method.");
                e.printStackTrace();
            } finally {
                // End the turn
                Clock.yield();
            }
        }
    }

    public void attackLowestHealthRobot(RobotInfo[] enemyRobots) throws GameActionException {
        // RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        RobotInfo lowestHealthRobot = enemyRobots[0];
        for (RobotInfo robot : enemyRobots) {
            if (robot.getHealth() < lowestHealthRobot.getHealth()) {
                lowestHealthRobot = robot;
            }
        }
        if (rc.canAttack(lowestHealthRobot.getLocation())) {
            rc.attack(lowestHealthRobot.getLocation());
        }
    }

    // function for moving in a specific direction, moving forward as much as
    // possible
    public void exploreMove() throws GameActionException {
        Direction dir;
        if (lastDirection != null && rc.canMove(lastDirection)) {
            dir = lastDirection; // Prioritize the same direction as the last turn
        } else {
            dir = directions[rng.nextInt(directions.length)]; // Choose a random direction
        }

        if (rc.canMove(dir)) {
            rc.move(dir);
            lastDirection = dir; // Update lastDirection to the current direction
        }
    }

    // function for moving towards the flag's position
    public void moveToFlagsLocation(FlagInfo[] flags) throws GameActionException {
        MapLocation flagLocation = flags[0].getLocation();
        Direction toFlag = rc.getLocation().directionTo(flagLocation);
        move(toFlag);
    }

    // function to return if the robot id divisible by 7
    public boolean isDivisibleBySeven(int robotID) {
        return robotID % 7 == 0;
    }

    // function to make decision of moving depending on if there is a flag or not
    public void movingDecision(FlagInfo[] flags) throws GameActionException {
        if (flags.length > 0) {
            // if the robot id is divisible by 7, move explore the map
            if (isDivisibleBySeven(rc.getID())) {
                exploreMove();
            } else {
                // if the robot id is not divisible by 7, move towards the opposite spawn
                // location
                moveToOppositeSpawnLocation();
            }
        } else {
            // move randomly
            moveRandomly();

        }
    }

    // Function to calculate the opposite locations of all ally spawn positions
    public MapLocation[] calculateOppositeSpawnLocations() throws GameActionException {
        MapLocation[] allySpawnLocations = rc.getAllySpawnLocations();
        int mapWidth = rc.getMapWidth();
        int mapHeight = rc.getMapHeight();
        MapLocation[] oppositeLocations = new MapLocation[allySpawnLocations.length];

        for (int i = 0; i < allySpawnLocations.length; i++) {
            MapLocation spawnLocation = allySpawnLocations[i];
            MapLocation oppositeLocation = new MapLocation(mapWidth - spawnLocation.x - 1,
                    mapHeight - spawnLocation.y - 1);
            oppositeLocations[i] = oppositeLocation;
        }

        return oppositeLocations;
    }

    // Function to move to the opposite spawn location
    public void moveToOppositeSpawnLocation() throws GameActionException {
        if (oppositeSpawnLocations != null && oppositeSpawnLocations.length > 0) {
            // Pick a random opposite spawn location
            int randomIndex = rng.nextInt(oppositeSpawnLocations.length);
            MapLocation targetLocation = oppositeSpawnLocations[randomIndex];
            moveToTargetMapLocation(targetLocation);
        }
    }

    // Function to move to the opposite side of the map
    public void moveToTargetMapLocation(MapLocation targetLocation) throws GameActionException {
        Direction toTarget = rc.getLocation().directionTo(targetLocation);
        if (rc.canMove(toTarget)) {
            rc.move(toTarget);
        } else {
            // If the direct path is blocked, try to move in a random direction
            moveRandomly();
        }
    }

    // Function to move randomly if the direct path is blocked
    public void moveRandomly() throws GameActionException {
        Direction randomDirection = directions[rng.nextInt(directions.length)];
        if (rc.canMove(randomDirection)) {
            rc.move(randomDirection);
        }
    }

    // Gets the position of the flag from the shared array.
    public MapLocation getFlagPosition(int index) throws GameActionException {
        int encodedLocation = rc.readSharedArray(index);
        return decodeLocation(encodedLocation);
    }

    // Decodes an integer from the shared array back into a MapLocation.
    private MapLocation decodeLocation(int encodedLocation) {
        int x = (encodedLocation >> 16) & 0xFFFF;
        int y = encodedLocation & 0xFFFF;
        return new MapLocation(x, y);
    }

    // Helper function to encode a MapLocation into an integer
    private int encodeFlagLocation(MapLocation location) {
        return (location.x << 16) | (location.y & 0xFFFF);
    }

    // Function to store flag information for the rest of the team
    public void updateFlagInfo(FlagInfo[] flags) throws GameActionException {
        for (int i = 0; i < flags.length; i++) {
            MapLocation flagLocation = flags[i].getLocation();
            int flagData = encodeFlagLocation(flagLocation);
            if (rc.canWriteSharedArray(i, flagData)) {
                rc.writeSharedArray(i, flagData);
            }
        }
    }
}