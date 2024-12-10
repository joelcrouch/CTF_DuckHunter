package robots;

import battlecode.common.*;
//import robots.RobotNavigator;
//import ducks.RobotState;
import ducks.BuilderDuck;
import robots.RobotState;
import java.util.Map;
import java.util.Random;
import static java.lang.Math.min;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;
    static RobotState state = new RobotState();
    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
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
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotNavigator  navigator = new RobotNavigator(rc);
        BuilderDuck builder = new BuilderDuck(rc);
        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        System.out.println("I'm a robot and IM alive");

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");

        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // Make sure you spawn your robot in before you attempt to take any actions!
                // Robots not spawned in do not have vision of any tiles and cannot perform any actions.
                if (rc.getRoundNum()==1){ if (rc.canWriteSharedArray(0, rc.getID())){ rc.writeSharedArray(0,rc.getID());}}
                if (rc.readSharedArray(0)==rc.getID()) {
                    if (!rc.isSpawned()) {
                        System.out.println("ID: " + rc.getID());
                        System.out.println("item at 0 in array: " + rc.readSharedArray(0));
                        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
                        // Pick a random spawn location to attempt spawning in.
                        MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
                        if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
//                        MapLocation dest = new MapLocation(6, 6);
//                        navigator.bugNav(rc, dest);
                    }
                    else if (rc.isSpawned()){
                        MapLocation dest = new MapLocation(29, 1);
                        navigator.bugNav(rc, dest);
                    }
                }
//                } else if (rc.getRoundNum() == 50) {
//                    for (int i = 0; i < 64; i++) {
//                        if (rc.readSharedArray(i) != 9999) {
//                            rc.writeSharedArray(i, 9999);
//                            System.out.println("Initialized shared array");
//                        }
//                    }
//                    //get size and spawn points and opposing spawn points
//                } else if (rc.getRoundNum() == 59) {
//                    storeAllySpawns(rc);
//                    storeEnemySpawns(rc);
//
//                } else{
//                    if ( !rc.hasFlag()){
//                        if (findAndPickupFlag(rc)) {
//                            System.out.println("Picked up enemy flag");
//                        }
//                    }
////                    if (rc.canPickupFlag(rc.getLocation())){
////                        rc.pickupFlag(rc.getLocation());
////                        rc.setIndicatorString("Holding a flag!");
////                    }
//                    // If we are holding an enemy flag, singularly focus on moving towards
//                    // an ally spawn zone to capture it! We use the check roundNum >= SETUP_ROUNDS
//                    // to make sure setup phase has ended.
//                    if (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS){
////                        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
////                        MapLocation firstLoc = spawnLocs[0];
////                        Direction dir = rc.getLocation().directionTo(firstLoc);
////                        System.out.println("Heading back to spawn location with flag: " + firstLoc);
//                        //i have a flag come towards me!
//                        Direction dir = getClosestAllySpawnDirection(rc);
//                        if(rc.canWriteSharedArray(11, 1)){
//                            rc.writeSharedArray(11, 1);
//                            MapLocation current = rc.getLocation();
//                            int x= rc.getLocation().x;
//                            int y = rc.getLocation().y;
//                            rc.writeSharedArray(12, x);
//                            rc.writeSharedArray(13, y);
//                            System.out.println("At location with flag:"+ current + " Put 1 into shared array, and shared location in 2, 3");
//                        }
//                        if (rc.canMove(dir)) rc.move(dir);
//                    }
//                    if (rc.getRoundNum() >200 ){
//                        if(rc.getID() % 5 ==0){
//                            int x1= rc.readSharedArray(8);
//                            int y1= rc.readSharedArray(9);
//                            System.out.println("opp spawn location1 (" + x1 + ", " + y1 + ")");
//                            MapLocation oppSpawn1 = new MapLocation(x1, y1);
//                            Direction dir = rc.getLocation().directionTo(oppSpawn1);
//                            if (rc.canMove(dir)){
//                                System.out.println(" Headed to opp spawn location1 ");
//                                rc.move(dir);
//                            }
//                        }
//                        if (state.getOppSpawn1() == null) {
//                            getOppSpawns(rc, state);
//                        }
//
//                        if (rc.getID() % 7 ==0 ) {
//                            MapLocation target = state.getOppSpawn1();
//                            if (target !=null) {
//                                state.setTarget(target);
//
//                                Direction dir = rc.getLocation().directionTo(state.getTarget());
//                                if (isObstacle(rc, dir)){
//                                    getAroundObstacle(rc, dir,state);
//
//
//                                }
//                                //                        Direction dir = getClosestEnemySpawnDirection(rc);
//                                System.out.println(" Robot #: " + rc.getID() + " headed to enemy spawn site :" + state.getTarget());
//                                if (rc.canMove(dir)) rc.move(dir);
//                            } else {System.out.println("target is null"); }
//                        }
//                    }
//                    // Move and attack randomly if no objective.
//                    Direction dir = directions[rng.nextInt(directions.length)];
//                    MapLocation nextLoc = rc.getLocation().add(dir);
//                    if (rc.canMove(dir)){
//                        rc.move(dir);
//                    }
//                    else if (rc.canAttack(nextLoc)){
//                        rc.attack(nextLoc);
//                        System.out.println("Take that!  Robots Damaged an enemy that was in our way!");
//                    }
//                    //builder.doBuilderDuckActions(state);
//                    // Rarely attempt placing traps behind the robot.
//                    MapLocation prevLoc = rc.getLocation().subtract(dir);
//                    if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc) && rng.nextInt() % 2 == 1)
//                        rc.build(TrapType.EXPLOSIVE, prevLoc);
//                    // We can also move our code into different methods or classes to better organize it!
//                    updateEnemyRobots(rc);
//                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println("GameActionException");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }
    public static void updateEnemyRobots(RobotController rc) throws GameActionException{
        // Sensing methods can be passed in a radius of -1 to automatically
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0){
            rc.setIndicatorString("There are nearby enemy robots! Scary!");
            // Save an array of locations with enemy robots in them for future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++){
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            // Let the rest of our team know how many enemy robots we see!
            if (rc.canWriteSharedArray(0, enemyRobots.length)){
                rc.writeSharedArray(0, enemyRobots.length);
                int numEnemies = rc.readSharedArray(0);
            }
        }
    }

    public static boolean findAndPickupFlag(RobotController rc) throws GameActionException {
        FlagInfo[] flags = rc.senseNearbyFlags(-1);
        if (flags.length > 0) {
            for (int i = 0; i < flags.length; i++) {
                if (flags[i].getTeam() != rc.getTeam()) {
                    if (flags[i].isPickedUp()) {
                        Direction dir = directions[rng.nextInt(directions.length)];
                        MapLocation nextLoc = rc.getLocation().add(dir);
                        if (rc.canMove(dir)) {
                            rc.move(dir);
                            return true;
                        }
                    }
                    else if (rc.canPickupFlag(flags[i].getLocation())) {
                            rc.pickupFlag(flags[i].getLocation());
                            return true;
                    } else {
                            MapLocation flag = flags[i].getLocation();

                            if (rc.canMove(rc.getLocation().directionTo(flag))) {
                                rc.move(rc.getLocation().directionTo(flag));
                                return true;
                            }
                        }
                }
            }
        }
        return false;
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
//
//    public static void storeMapDimensions(RobotController rc) throws GameActionException {
//        int width = rc.getMapWidth();
//        int height = rc.getMapHeight();
//        rc.writeSharedArray(6, width);  // Write map width
//        rc.writeSharedArray(7, height); // Write map height
//
//        //System.out.println("Map dimensions (" + width + "x" + height + ") stored in sharedArray.");
//    }
//
    public static void storeEnemySpawns(RobotController rc) throws GameActionException {
        int width = rc.getMapWidth();   // Read map width
        int height = rc.getMapHeight(); // Read map height
        int enemySpawnSitey=0;
        int enemySpawnSitex=0;
        for(int i=0; i<3; i++){
            enemySpawnSitex = width - rc.readSharedArray(2*i);  // Symmetric x-coordinate 0,2,4
            enemySpawnSitey = height - rc.readSharedArray(2*i+1); // Symmetric y-coordinate 1,3,,5

            rc.writeSharedArray(((2*i)+8), enemySpawnSitex);  // Write enemy x-coordinate 8, 10, 12
            rc.writeSharedArray(((2*i)+9), enemySpawnSitey);  // Write enemy y-coordinate 9,11,13
            System.out.println("Enemy spawn location (" + enemySpawnSitex + ", " + enemySpawnSitey + ") stored in sharedArray.");
        }
    }

    public static boolean atTarget (RobotController rc) throws GameActionException {
        MapInfo [] locs = rc.senseNearbyMapInfos();
        boolean result = false;
        for (int i = 0; i < locs.length; i++) {
            //am i at the correct target?
            if (state.getTarget().equals(locs[i].getMapLocation())){
                result =true;
                //set result to true
                //check and see which oppSpawn we are at
                if (state.getTarget().equals(state.getOppSpawn1())) {
                    state.setFound1(true);
                } else if (state.getTarget().equals(state.getOppSpawn2())) {
                    state.setFound2(true);
                } else if (state.getTarget().equals(state.getOppSpawn3())) {
                    state.setFound3(true);
                }
                //if any of the other oppspaws found booleans are true, set target location to that one
                if (state.getFound1()) {
                    state.setTarget(state.getOppSpawn1());
                }else if (state.getFound2()) {
                    state.setTarget(state.getOppSpawn2());
                } else if (state.getFound3()) {
                    state.setTarget(state.getOppSpawn3());
                }
            }
        }
        return result;
    }

    ///robot is on its way to some coordinate. There is an obstacle that it meets.
    //how to get around it?
    //1. if is fillable (fill it)
    // 2.  get an array of nearby Mapinfos, and plot a way around it
    //3.  randoom movement  -not best suited.
    //2  is the way?
        // if next location to go to is not passable  -use senseMapInfo(MapLocation)
        // get an array of MapInfos  use senseNearbyMapinfos(this location) to get an array of Mapinfos
        // survey the obstacle
        // does the obstacle go for some time -can we see an edge that we can use as a temp target?
        // if not then pick one of the sides to go towards, log that direction into State
        // and continue in that direction until a) we hit a wall  b) we find the edge,and add a temp target
        //finally remove temp target after rounding edge, and resume toward original target

    public static boolean isObstacle(RobotController rc, Direction dir) throws GameActionException {
        //if the Maplocation in the direction is passable return true else false
        MapLocation next = rc.adjacentLocation(dir);
        MapInfo adj = rc.senseMapInfo(next);
        if (adj.isPassable()) {
            return true;
        }
        return false;
    }


    public static Direction getAroundObstacle (RobotController rc, Direction dir, RobotState state) throws GameActionException {
        //we know we have an obstacle Now we have to find out how big it is?
        //a is it water ?  fill it
        Direction result = null;
        int distanceTo = rc.getLocation().distanceSquaredTo(rc.adjacentLocation(dir));
        if (rc.canFill(rc.adjacentLocation(dir))) {
            rc.fill(rc.adjacentLocation(dir));
            return dir;
        } else {
            MapInfo [] obstacle = rc.senseNearbyMapInfos(rc.getLocation());

            for (int i = 0; i < obstacle.length; i++) {
                if (obstacle[i].isPassable()  && dir == obstacle[i].getMapLocation().directionTo(state.getTarget())) {
                    //set temp target
                    state.setTempTarget(obstacle[i].getMapLocation());
                    result= rc.getLocation().directionTo(obstacle[i].getMapLocation());
                }
                else if (!obstacle[i].isPassable()){
                    if (distanceTo > (rc.getLocation().distanceSquaredTo(obstacle[i].getMapLocation()))){
                        result =rc.getLocation().directionTo(obstacle[i].getMapLocation());
                    }

                }
            }

        }
        return result;
    }

    public static Direction getClosestAllySpawnDirection(RobotController rc) throws GameActionException {
        Direction dir;
        int x1= rc.readSharedArray(0);
        int x2= rc.readSharedArray(2);
        int x3 = rc.readSharedArray(4);
        int y1= rc.readSharedArray(1);
        int y2= rc.readSharedArray(3);
        int y3= rc.readSharedArray(5);
        MapLocation allySpawn1= new MapLocation(x1,y1);
        MapLocation allySpawn2= new MapLocation(x2,y2);
        MapLocation allySpawn3= new MapLocation(x3,y3);
        MapLocation closestSpawn = allySpawn1;
        int minDistance = rc.getLocation().distanceSquaredTo(allySpawn1);

        if (rc.getLocation().distanceSquaredTo(allySpawn2) < minDistance) {
            closestSpawn = allySpawn2;
            minDistance = rc.getLocation().distanceSquaredTo(allySpawn2);
        }
        if (rc.getLocation().distanceSquaredTo(allySpawn3) < minDistance) {
            closestSpawn = allySpawn3;
            minDistance = rc.getLocation().distanceSquaredTo(allySpawn3);
        }
       // System.out.println("Heading to nearest ally spawn point!");

        // Get the direction to the closest spawn
        Direction dirToClosest = rc.getLocation().directionTo(closestSpawn);
        return dirToClosest;

    }

    public static void getOppSpawns (RobotController rc, RobotState state) throws GameActionException {
            int x1= rc.readSharedArray(8);
            int y1= rc.readSharedArray(9);
            System.out.println("opp spawn location1 (" + x1 + ", " + y1 + ")");
            MapLocation oppSpawn1 = new MapLocation(x1, y1);
            state.setOppSpawn1(oppSpawn1);

            x1= rc.readSharedArray(10);
            y1= rc.readSharedArray(11);
            System.out.println("opp spawn location2 (" + x1 + ", " + y1 + ")");
            MapLocation oppSpawn2 = new MapLocation(x1, y1);
            state.setOppSpawn2(oppSpawn2);

            x1= rc.readSharedArray(12);
            y1= rc.readSharedArray(13);
            System.out.println("opp spawn location3 (" + x1 + ", " + y1 + ")");
            MapLocation oppSpawn3 = new MapLocation(x1, y1);
            state.setOppSpawn3(oppSpawn3);
            System.out.println("opp spawn locations (" + state.getOppSpawn1() + ", " + state.getOppSpawn2() + ", " + state.getOppSpawn3() + ")");
    }


    public static Direction getClosestEnemySpawnDirection(RobotController rc) throws GameActionException {
        Direction dir;
        int x1= rc.readSharedArray(8);
        int x2= rc.readSharedArray(9);
        int x3 = rc.readSharedArray(10);
        int y1= rc.readSharedArray(11);
        int y2= rc.readSharedArray(12);
        int y3= rc.readSharedArray(13);
        MapLocation allySpawn1= new MapLocation(x1,y1);
        MapLocation allySpawn2= new MapLocation(x2,y2);
        MapLocation allySpawn3= new MapLocation(x3,y3);
        MapLocation closestSpawn = allySpawn1;
        int minDistance = rc.getLocation().distanceSquaredTo(allySpawn1);

        if (rc.getLocation().distanceSquaredTo(allySpawn2) < minDistance) {
            closestSpawn = allySpawn2;
            minDistance = rc.getLocation().distanceSquaredTo(allySpawn2);
        }
        if (rc.getLocation().distanceSquaredTo(allySpawn3) < minDistance) {
            closestSpawn = allySpawn3;
            minDistance = rc.getLocation().distanceSquaredTo(allySpawn3);
        }
        //System.out.println("Heading to nearest enemy spawn point at : " + closestSpawn);
        rc.setIndicatorString("Heading to nearest enemy spawn point at : " + closestSpawn);
        // Get the direction to the closest spawn
        Direction dirToClosest = rc.getLocation().directionTo(closestSpawn);
        return dirToClosest;

    }
}

//Shared array :  all values initialized to 999 on round 30
//0 allyspawn1x
//1 allyspawn1y
//2 allyspawn2x
//3 allyspawn2y
//4 allyspawn3x
//5 allyspawn1y
//6
//7
//8 spawnsite1x
//9 spawnsite1y
//10 spawnsite2x
//11 spawnsite2y
//12 spawnsite3x
//13 spawnsite3y