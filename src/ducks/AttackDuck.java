package ducks;

import battlecode.common.*;

import java.util.Random;

import javax.naming.directory.DirContext;

public strictfp class AttackDuck extends RobotPlayer {  // Extending RobotPlayer if necessary
    RobotController rc;
    static final Direction[] directions = Direction.allDirections();
    Random rng = new Random();

    public AttackDuck(RobotController rc) {
        this.rc = rc;
    }

    @Override
    public void run() throws GameActionException {
        while (true) {
            turnCount += 1;
            try {
                // Use the methods from RobotPlayer
                if (!rc.isSpawned()) {
                    attemptToSpawn();
                } else {
                    FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
                    for (FlagInfo flag:flags){

                        //findAndPickupFlag();
                        if(rc.canPickupFlag(flag.getLocation())){
                            rc.pickupFlag(flag.getLocation());
                            break;
                        }
                    }

                    // If carrying a flag, move toward the nearest ally spawn location
                    if (rc.hasFlag()) {
                        moveToAllySpawnLocation();
                    } else {
                        // Do some attacking and move about
                        // Move and attack randomly if no objective.

                        //generate a random dir
                        Direction dir = directions[rng.nextInt(directions.length)];

                        MapLocation nextLoc = rc.getLocation().add(dir);

                        //try to move forward as much as possible before take a turn( useful for exploring)
                        /* 
                        if(rc.isMovementReady()){
                            if(dir != null && rc.canMove(dir)){
                                rc.move(dir);
                            }
                            else{
                                dir = directions[rng.nextInt(directions.length)];
                            }
                        }
                            */
                        
                        //try to move forward
                        /* 
                        Direction dirTo = rc.getLocation().directionTo(nextLoc)
                        if(rc.canMove(dirTo)){
                            rc.move(dirTo);
                        }else if(rc.canFill(rc.getLocation().add(dirTo))){
                            rc.fill(rc.getLocation().add(dirTo));
                        }else{
                            Direction randomDir = directions[rng.nextInt(directions.length)];
                            if(rc.canMove(randomDir)){
                                rc.move(randomDir);
                            }
                        }
                        */

                        if (rc.canMove(dir)) {
                            rc.move(dir);
                        } else if (rc.canAttack(nextLoc)) {
                            rc.attack(nextLoc);
                            System.out.println("Take that! Attack duck hit an enemy that was in our way!");
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
}