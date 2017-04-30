package awalk.app.smartvalvetest;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vamsi on 03-03-2017.
 */

public class Valve {
    private String valveName;
    private int valveState,valveStatus;

    public Valve() {

    }

    public Valve(String valveName, int valveState, int valveStatus) {
        this.valveName = valveName;
        this.valveState = valveState;
        this.valveStatus = valveStatus;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("valveName", valveName);
        result.put("valveState", valveState);
        result.put("valveStatus", valveStatus);
        return result;
    }

    public String getValveName() {
        return valveName;
    }

    public void setValveName(String valveName) {
        this.valveName = valveName;
    }

    public int getValveState() {
        return valveState;
    }

    public void setValveState(int valveState) {
        this.valveState = valveState;
    }

    public int getValveStatus() {
        return valveStatus;
    }

    public void setValveStatus(int valveStatus) {
        this.valveStatus = valveStatus;
    }
}
