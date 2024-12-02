package ducks;
import battlecode.common.*;
public class RobotState {
    private int teamNumber=-1;
    private MapLocation targetLocation =null;

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
}
