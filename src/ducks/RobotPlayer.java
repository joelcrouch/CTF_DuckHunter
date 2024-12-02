package ducks;

import battlecode.common.*;
import ducks.AttackDuck;
import ducks.HealerDuck;
import ducks.BuilderDuck;
import ducks.RobotNavigator;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Random;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public abstract class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;
    protected RobotController rc;
    int teamNumber = 0;
    private static RobotState robotState;
    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /**
     * Array containing all the possible movement directions.
     */
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

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc The RobotController object. You use it to perform actions from this robot, and to get
     *           information on its current status. Essentially your portal to interacting with the world.
     **/

    //SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        Random rng = new Random();
        int turnCount = 0;
        if (robotState == null) {
            robotState = new RobotState();  // Initialize it once on the first turn
        }

        BuilderDuck builderDuck = new BuilderDuck(rc);
        HealerDuck healerDuck = new HealerDuck(rc);
        AttackDuck attackDuck = new AttackDuck(rc);
        RobotNavigator navigator = new RobotNavigator(rc);

        while (true) {
            turnCount++;
            try {
//                if (rc.getRoundNum() < 201 && rc.getRoundNum() >189){
//                System.out.println("Current Round: " + rc.getRoundNum());}
                // 1. Spawn if necessary
                if (!rc.isSpawned()) {
                    MapLocation[] spawnLocs = rc.getAllySpawnLocations();
                    MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
                    if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
                }
                // 2. Get spawns/map dimensions enemy spawns
                else if (rc.getRoundNum() == 30) {
                    storeAllySpawns(rc);
                    storeMapDimensions(rc);
                    storeEnemySpawns(rc);
                }
                // 3. Assign robot to team
                else if (rc.getRoundNum() == 50) {
                    assignTeam(rc, robotState);
                }else if (findAndPickupFlag(rc)) {
                    navigator.returnToBase(rc);
                }
                else if(rc.hasFlag()) {
                    navigator.returnToBase(rc);
                }
                else if (rc.getRoundNum() == 100) {
                    // Assign a random destination if not already set
                    if (robotState.getTargetLocation() == null) {
                        MapLocation randomDestination = navigator.generateRandomLocation(rc);
                        robotState.setTargetLocation(randomDestination);
                        System.out.println("Assigned random destination: " + randomDestination);
                    }
                }


                // 4. Build stuff up to 200
                else if (rc.getRoundNum() < 190) {
                    builderDuck.doBuilderDuckActions();
                }
//                if (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS){
//                    MapLocation[] spawnLocs = rc.getAllySpawnLocations();
//                    MapLocation firstLoc = spawnLocs[0];
//                    Direction dir = rc.getLocation().directionTo(firstLoc);
//                    if (rc.canMove(dir)) rc.move(dir);
//
//                } // 5. Check and buy upgrades
                else if (rc.getRoundNum() == 601 || rc.getRoundNum() == 1201 || rc.getRoundNum() == 1801) {
                    robotState.checkAndBuyUpgrades(rc.getRoundNum(), rc);
                } else if (rc.getRoundNum() % 100 == 0){
                    MapLocation test = robotState.getTargetLocation();
                    System.out.println("Target Dest =: " + test.x +"," + test.y);

                }

                // 6. Attack (priority #1)
//                else if (1==1) {
//                    boolean readytoheal = false;
//                    //System.out.println("AttackDuck check...");
//                    if (!attackDuck.selectiveAttack(rc)) {
//                        //System.out.println("Attack performed.");
//                        readytoheal = true;
//                        continue; // Skip the rest of the turn
//                    } else if (readytoheal){
//                        System.out.println("healer Check");
//                        healerDuck.healDuck(rc);
//                    }
//
//                }
//                    attackDuck.selectiveAttack(rc);
//                    System.out.println("attackDuck check");
               // }
                // 7. Heal (priority #2)
//                else if (rc.isActionReady()) {
//                    System.out.println("healer Check");
//                    healerDuck.healDuck(rc);
//                }
                // 8. Line up between 190 and 200 rounds
//                else if ( 1==1) {
//                    System.out.println("Current round before if statements: " + rc.getRoundNum());
                //}
//                else if (rc.getRoundNum() == 190  ) {
//                    System.out.println("Lining up");
//                    System.out.println(rc.getRoundNum());
//                    System.out.println("Finding lane");
//                    navigator.navigateToLane(robotState);
//                }
                // 9. Move across after round 200

                else if (rc.getRoundNum() > 200) {
                    //navigator.moveTo(rc.getLocation(), robotState.getTargetLocation(), rc);
                    attackDuck.moveToTargetMapLocation(robotState.getTargetLocation());
                    navigator.validateAndReassignDestination(rc, robotState);
                }
            } catch (GameActionException e) {
                System.out.println("GameActionException occurred!");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Exception occurred!");
                e.printStackTrace();
            } finally {
                Clock.yield(); // End the turn
            }
        }
    }
//    public void attemptToSpawn() throws GameActionException {
//        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
//        MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
//        if (spawnLocs.length > 0) {
//                        MarandomLoc1 = spawnLocs[rng.nextInt(spawnLocs.length)];
//                        if (rc.canSpawn(randomLoc)) {
//                            rc.spawn(randomLoc);
//                        } else {
//                            System.out.println("Cannot spawn at: " + randomLoc);
//                        }
//    }
     public MapLocation getTeamDest(int teamNumber, RobotController rc) throws GameActionException{
                // Determine the appropriate shared array indices
        int enemyFlagX = rc.readSharedArray(8 + (2 * teamNumber));
        int enemyFlagY = rc.readSharedArray(9 + (2 * teamNumber));
        MapLocation enemyFlag = new MapLocation(enemyFlagX, enemyFlagY);
        return enemyFlag;

    }




    public static void updateEnemyRobots(RobotController rc) throws GameActionException {
        // Sensing methods can be passed in a radius of -1 to automatically
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0) {
            rc.setIndicatorString("There are nearby enemy robots! Scary!");
            // Save an array of locations with enemy robots in them for future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++) {
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            // Let the rest of our team know how many enemy robots we see!
            if (rc.canWriteSharedArray(0, enemyRobots.length)) {
                rc.writeSharedArray(0, enemyRobots.length);
                int numEnemies = rc.readSharedArray(0);
            }
        }
    }

    public static void storeAllySpawns(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        rc.writeSharedArray(0, spawnLocs[0].x);
        rc.writeSharedArray(1, spawnLocs[0].y);
        rc.writeSharedArray(2, spawnLocs[9].x);
        rc.writeSharedArray(3, spawnLocs[9].y);
        rc.writeSharedArray(4, spawnLocs[18].x);
        rc.writeSharedArray(5,spawnLocs[18].y);
    }

    public static void storeMapDimensions(RobotController rc) throws GameActionException {
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        rc.writeSharedArray(6, width);  // Write map width
        rc.writeSharedArray(7, height); // Write map height

        //System.out.println("Map dimensions (" + width + "x" + height + ") stored in sharedArray.");
    }

    public static void storeEnemySpawns(RobotController rc) throws GameActionException {
        int width = rc.getMapWidth();   // Read map width
        int height = rc.getMapHeight(); // Read map height
        int enemySpawnSitey=0;
        int enemySpawnSitex=0;
        for(int i=0; i<3; i++){
            enemySpawnSitex = width - rc.readSharedArray(2*i);  // Symmetric x-coordinate
            enemySpawnSitey = height - rc.readSharedArray(2*i+1); // Symmetric y-coordinate
            rc.writeSharedArray(((2*i)+8), enemySpawnSitex);  // Write enemy x-coordinate
            rc.writeSharedArray(((2*i)+9), enemySpawnSitey);  // Write enemy y-coordinate
            //System.out.println("Enemy spawn location (" + enemySpawnSitex + ", " + enemySpawnSitey + ") stored in sharedArray.");
        }
    }

    public static void writeSpawnsites (int x, int y, RobotController rc) throws GameActionException {

    }

    public static boolean allTeamsAssigned(RobotController rc) throws GameActionException {
        for (int team = 0; team < 10; team++) {
            int maxIndex = 14 + team * 5 + 4; // Check last slot for each team
            if (rc.readSharedArray(maxIndex) < 5) {
                return false; // Not all teams are full
            }
        }
        return true; // All teams are full
    }

    public static void assignTeam(RobotController rc, RobotState robotState) throws GameActionException {

//        System.out.println("Assigned to Team " + team);
        for (int team = 0; team < 10; team++) {
            int baseIndex = 14 + team * 5; // Starting index for team's slots
            int maxIndex = baseIndex + 4; // The "extra" slot for tracking count

            // Read the current count of assigned robots for this team
            int memberCount = rc.readSharedArray(maxIndex);

            // If the team has fewer than 5 members
            if (memberCount < 5) {
                // Assign this robot to the team
                rc.writeSharedArray(maxIndex, memberCount + 1); // Increment the team count
                robotState.setTeamNumber(team); // Set team number in RobotState
                ///System.out.println("Assigned to Team " + team + " (Member " + (memberCount + 1) + ")");
                return; // Exit after assigning
            }
        }
    }

    protected static boolean findAndPickupFlag(RobotController rc) throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation())) {
            rc.pickupFlag(rc.getLocation());
            rc.setIndicatorString("Picked up a flag!");
            return true;
        }
        return false;
    }


    public static void findRandomFlag(RobotController rc) throws GameActionException {

    }

    public static void scoutAndAct(RobotController rc, RobotState state, RobotNavigator rn) throws GameActionException {
        // Sense nearby map info
        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();

        for (MapInfo tile : nearbyTiles) {
            if (tile.getCrumbs() > 0) {
                System.out.println("Found crumbs at: " + tile.getMapLocation());
                rn.moveTo(rc.getLocation(), tile.getMapLocation(), rc); // Move towards crumbs
                return;
            }
//            } else if (tile.isWater()) {
//                System.out.println("Water found at: " + tile.getMapLocation());
//                state.recordWaterTile(tile.getMLocation());
//            } else if (tile.isDam() || tile.isWall()) {
//                System.out.println("Obstacle found at: " + tile.getMLocation());
//                state.recordObstacle(tile.getLocation());
//            }
        }

        // If no specific targets, move randomly
        Direction randomDir = Direction.values()[(int) (Math.random() * Direction.values().length)];
        if (rc.canMove(randomDir)) {
            rc.move(randomDir);
        }
    }

}




//
//    public void move(Direction dir) throws GameActionException {
//        if (rc.canMove(dir)) {
//            rc.move(dir);
//        }
//    }
//
//    /**
//     * Attempts to pick up a flag if the robot is on a flag location.
//     */

//
//    // General method to find flag and move toward it
//    protected void moveToAllySpawnLocation() throws GameActionException {
//        if (rc.hasFlag()) {
//            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
//            if (spawnLocs.length > 0) {
//                MapLocation nearestSpawn = spawnLocs[0];  // Choose nearest spawn location
//                Direction toSpawn = rc.getLocation().directionTo(nearestSpawn);
//                move(toSpawn);
//            }
//        }
//
//    }
//
//    public void attemptToSpawn() throws GameActionException {
//        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
//        MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
//        if (rc.canSpawn(randomLoc)) {
//            rc.spawn(randomLoc);
//        }
//    }
//
//    protected int getRandomInt(int bound) {
//        return rng.nextInt(bound);
//    }
