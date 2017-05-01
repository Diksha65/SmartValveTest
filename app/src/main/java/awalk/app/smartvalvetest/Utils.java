package awalk.app.smartvalvetest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vamsi on 12-03-2017.
 */

public class Utils {

    public Utils() {

    }

    enum STATUS {
        OPENING,
        OPENED,
        CLOSING,
        CLOSED,
        LODGED,
        OVER_HEATING
    }

    private String[] Status = {
            "Opening",
            "Opened",
            "Closing",
            "Closed",
            "Lodged",
            "Over Heating"
    };
    public String decodeStatus(int num){
        return Status[num];
    }
}
