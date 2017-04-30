package awalk.app.smartvalvetest;

/**
 * Created by Vamsi on 12-03-2017.
 */

public class Utils {

    public Utils() {

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
