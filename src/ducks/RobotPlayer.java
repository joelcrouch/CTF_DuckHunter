package ducks;

import battlecode.common.*;
import ducks.AttackDuck;
import ducks.HealerDuck;
import ducks.BuilderDuck;

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
        System.out.println("I'm the base class and I'm alive");
        rc.setIndicatorString("Hello world!");

        Random rng = new Random();
        int turnCount = 0;
        BuilderDuck builderDuck = new BuilderDuck(rc);
        HealerDuck healerDuck = new HealerDuck(rc);
        AttackDuck attackDuck = new AttackDuck(rc);

        while (true) {
            turnCount++;
            try {
                if (!rc.isSpawned()) {
                    // Try to spawn if not spawned
                    MapLocation[] spawnLocs = rc.getAllySpawnLocations();
                    if (spawnLocs.length > 0) {
                        MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
                        if (rc.canSpawn(randomLoc)) {
                            rc.spawn(randomLoc);
                        } else {
                            System.out.println("Cannot spawn at: " + randomLoc);
                        }
                    }
                } else {
                    // we watn to determine our spawn points, write into shared array, [1,2,3]
                    //the first 1
                    int roundNumber = rc.getRoundNum();
                    if(roundNumber == 1) {
                        storeAllySpawns(rc);
                        storeMapDimensions(rc);
                        storeEnemySpawns(rc);
                    }

 //the first 6 slots in the array are reserved for spawn locations
                    //on first round
                    // size of the map and write similarily int sa[6] and sa[7] wher 6=x and  7 =y


// determine opposing euqivalent spawn (shoudld be near flags)


//sa 8 and 9 give us a maplocation to got to and start hunting for flags sa 8 and 9 (xandy respectiveley)

                    // assuming opponents spawn points are in similar locations, then use sa 0-5 to determine maplocationattack
                    //maplocationattack in sa8

                    //divide into teams: on spawn, read sa[10], then based upon that nubmer (divide it by 5)
                    //assign itself a team number (there will be 10 teams 0-4 on team 0, 5-9 team 1, 10-14team 2 etc
                    //then we assign one as the team leader and generlly that one makes the decisions (if that one in in jail
                    // we suss that out.     



                    //determine the size[sa4] , at round 201 use attackduck gotompalocation to send all the ducks
                    // to those three locations
                    // Alternate between builder and healer behavior
                    //if depending upon results of sensenearbyMaplocation determine behavior: if there watersf fill, if
                    //traps of oppposite fill
                    // sensenearbyrobots if the enemey is near attack themm
                    // sensenearbrobots so if they are friendly and no robots, find out who has lowest health, heal them
                    if (turnCount % 2 == 0) {  //setup phase 200 rounds. explore and build then
                        //at rnd 201 most ducks behave like attakc ducks 2/3 rounds, healer duck1/3
                        //if there are water feature fill with buiilder   fucntion sensenearbyMaplocation
                        builderDuck.doBuilderDuckActions();
                    } else {
                        healerDuck.healNearbyAlliesOrMove();
                    }
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

        for (int i = 0; i < 3; i++) {
            int x = spawnLocs[i].x;
            int y = spawnLocs[i].y;
            rc.writeSharedArray(2 * i, x);       // Write x-coordinate
            rc.writeSharedArray(2 * i + 1, y);  // Write y-coordinate
        }

        System.out.println("Ally spawn locations stored in sharedArray.");
    }

    public static void storeMapDimensions(RobotController rc) throws GameActionException {
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        rc.writeSharedArray(6, width);  // Write map width
        rc.writeSharedArray(7, height); // Write map height

        System.out.println("Map dimensions (" + width + "x" + height + ") stored in sharedArray.");
    }

    public static void storeEnemySpawns(RobotController rc) throws GameActionException {
        int width = rc.readSharedArray(6);   // Read map width
        int height = rc.readSharedArray(7); // Read map height

        int enemySpawnSitex = width - rc.readSharedArray(0);  // Symmetric x-coordinate
        int enemySpawnSitey = height - rc.readSharedArray(1); // Symmetric y-coordinate
        rc.writeSharedArray(8, enemySpawnSitex);  // Write enemy x-coordinate
        rc.writeSharedArray(9, enemySpawnSitey);  // Write enemy y-coordinate

        System.out.println("Enemy spawn location (" + enemySpawnSitex + ", " + enemySpawnSitey + ") stored in sharedArray.");
    }
}


    //@SuppressWarnings("unused")
    // Abstract run method that subclasses must implement
    //public abstract void run() throws GameActionException;

//    }
////    public static void run(RobotController rc) throws GameActionException {
////
////        // Hello world! Standard output is very useful for debugging.
////        // Everything you say here will be directly viewable in your terminal when you run a match!
////        System.out.println("I'm the base Duck and Im alive");
////
////        // You can also use indicators to save debug notes in replays.
////        rc.setIndicatorString("Hello world!");
////
////        while (true) {
////            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
////            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
////            // loop, we call Clock.yield(), signifying that we've done everything we want to do.
////
////            turnCount += 1;  // We have now been alive for one more turn!
////
////            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
////            try {
////                // Make sure you spawn your robot in before you attempt to take any actions!
////                // Robots not spawned in do not have vision of any tiles and cannot perform any actions.
////                if (!rc.isSpawned()){
////                    MapLocation[] spawnLocs = rc.getAllySpawnLocations();
////                    // Pick a random spawn location to attempt spawning in.
////                    MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
////                    if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
////                }
////                else{
////                    if (rc.canPickupFlag(rc.getLocation())){
////                        rc.pickupFlag(rc.getLocation());
////                        rc.setIndicatorString("Holding a flag!");
////                    }
////                    // If we are holding an enemy flag, singularly focus on moving towards
////                    // an ally spawn zone to capture it! We use the check roundNum >= SETUP_ROUNDS
////                    // to make sure setup phase has ended.
////                    if (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS){
////                        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
////                        MapLocation firstLoc = spawnLocs[0];
////                        Direction dir = rc.getLocation().directionTo(firstLoc);
////                        if (rc.canMove(dir)) rc.move(dir);
////                    }
////                    // Move and attack randomly if no objective.
////                    Direction dir = directions[rng.nextInt(directions.length)];
////                    MapLocation nextLoc = rc.getLocation().add(dir);
////                    if (rc.canMove(dir)){
////                        rc.move(dir);
////                    }
////                    else if (rc.canAttack(nextLoc)){
////                        rc.attack(nextLoc);
////                        System.out.println("Take that! BASE Duck Damaged an enemy that was in our way!");
////                    }
////
////                    // Rarely attempt placing traps behind the robot.
////                    MapLocation prevLoc = rc.getLocation().subtract(dir);
////                    if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc) && rng.nextInt() % 37 == 1)
////                        rc.build(TrapType.EXPLOSIVE, prevLoc);
////                    // We can also move our code into different methods or classes to better organize it!
////                    updateEnemyRobots(rc);
////                }
////
////            } catch (GameActionException e) {
////                // Oh no! It looks like we did something illegal in the Battlecode world. You should
////                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
////                // world. Remember, uncaught exceptions cause your robot to explode!
////                System.out.println("GameActionException");
////                e.printStackTrace();
////
////            } catch (Exception e) {
////                // Oh no! It looks like our code tried to do something bad. This isn't a
////                // GameActionException, so it's more likely to be a bug in our code.
////                System.out.println("Exception");
////                e.printStackTrace();
////
////            } finally {
////                // Signify we've done everything we want to do, thereby ending our turn.
////                // This will make our code wait until the next turn, and then perform this loop again.
////                Clock.yield();
////            }
////            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
////        }
////
////        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
////    }
//
//    protected static Class<? extends RobotPlayer> determineDuckType(RobotController rc) {
//        int roundNumber =rc.getRoundNum();
//        if (roundNumber <= 20) {
//            return AttackDuck.class;
//        } else if ( roundNumber <= 35) {
//            return BuilderDuck.class;
//        } else {
//            return HealerDuck.class;
//        }
//
//    }
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
//    protected void findAndPickupFlag() throws GameActionException {
//        if (rc.canPickupFlag(rc.getLocation())) {
//            rc.pickupFlag(rc.getLocation());
//            rc.setIndicatorString("Picked up a flag!");
//        }
//    }
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
//
//
//
//
//    public static void updateEnemyRobots(RobotController rc) throws GameActionException{
//        // Sensing methods can be passed in a radius of -1 to automatically
//        // use the largest possible value.
//        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
//        if (enemyRobots.length != 0){
//            rc.setIndicatorString("There are nearby enemy robots! Scary!");
//            // Save an array of locations with enemy robots in them for future use.
//            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
//            for (int i = 0; i < enemyRobots.length; i++){
//                enemyLocations[i] = enemyRobots[i].getLocation();
//            }
//            // Let the rest of our team know how many enemy robots we see!
//            if (rc.canWriteSharedArray(0, enemyRobots.length)){
//                rc.writeSharedArray(0, enemyRobots.length);
//                int numEnemies = rc.readSharedArray(0);
//            }
//        }
//    }
//
//  //  public abstract void run(RobotController rc) throws GameActionException;
//}