package robots;
import java.util.*;
import battlecode.common.*;
public class RobotState {
    private MapLocation oppSpawn1;
    private MapLocation oppSpawn2;
    private MapLocation oppSpawn3;
    private MapLocation target;
    private MapLocation tempTarget;
    RobotController rc;

    public RobotState() {
        //this.rc = rc;
    }

    public void setOppSpawn1(MapLocation oppSpawn1) {
        this.oppSpawn1 = oppSpawn1;
    }

    public MapLocation getOppSpawn1() {
        return oppSpawn1;
    }

    public boolean found1 = false;

    public void setFound1 (boolean delta) {
        found1 = delta;
    }

    public boolean getFound1() {
        return found1;
    }


    public void setOppSpawn2(MapLocation oppSpawn2) {
        this.oppSpawn2 = oppSpawn2;
    }

    public MapLocation getOppSpawn2() {
        return oppSpawn2;
    }

    public boolean found2 = false;

    public void setFound2 (boolean delta) {
        found2 = delta;
    }

    public boolean getFound2() {
        return found2;
    }



    public void setOppSpawn3(MapLocation oppSpawn3) {
        this.oppSpawn3 = oppSpawn3;
    }

    public MapLocation getOppSpawn3() {
        return oppSpawn3;
    }

    public boolean found3 = false;

    public void setFound3 (boolean delta) {
        found3 = delta;
    }

    public boolean getFound3() {
        return found3;
    }

    public void setTarget(MapLocation target) {
        this.target = target;
    }


    public MapLocation getTarget() {
        return target;
    }

    public void setTempTarget(MapLocation tempTarget) {
        this.tempTarget = tempTarget;
    }

    public MapLocation getTempTarget() {
        return tempTarget;
    }
}
