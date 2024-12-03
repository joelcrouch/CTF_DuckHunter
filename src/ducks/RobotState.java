package ducks;
import battlecode.common.*;
public class RobotState {
    private int teamNumber=-1;
    private MapLocation targetLocation =null;
    private MapLocation currentLocation;
    private MapLocation lastLocation;
    private boolean moved =false;


    public MapLocation getCurrentLocation() throws GameActionException {
        return currentLocation;
    }

    public MapLocation getLastLocation() throws GameActionException {
        return lastLocation;
    }

    public void setCurrentLocation(MapLocation location) throws GameActionException{
        this.currentLocation = location;
    }

    public void setLastLocation(MapLocation location) throws GameActionException{
        this.lastLocation = location;
    }



    public boolean getMoved() {
        return moved;
    }

    // Setter for moved
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public MapLocation getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(MapLocation targetLocation) {
        this.targetLocation = targetLocation;
    }

    public void checkAndBuyUpgrades(int roundNumber, RobotController rc) throws GameActionException {
        //int roundNumber = rc.getRoundNum();

        // Check which upgrade should be prioritized based on the round
        if (roundNumber == 601 ) {
            attemptToBuyUpgrade(GlobalUpgrade.ATTACK, rc);
        } else if (roundNumber == 1201 ) {
            attemptToBuyUpgrade(GlobalUpgrade.HEALING, rc);
        } else if (roundNumber == 1801) {
            attemptToBuyUpgrade(GlobalUpgrade.CAPTURING, rc);
        }
    }

    private void attemptToBuyUpgrade(GlobalUpgrade upgrade, RobotController rc) throws GameActionException {
        if (rc.canBuyGlobal(upgrade)) {
            rc.buyGlobal(upgrade);
            System.out.println("Purchased upgrade: " + upgrade);
        } //else {
            //System.out.println("Not enough points for upgrade: " + upgrade);
        //}
    }

    public  boolean hasMoved ()  throws GameActionException {
        if (this.currentLocation.isWithinDistanceSquared(this.lastLocation, 2)) {
            return false;
        }
        return true;
    }


}
