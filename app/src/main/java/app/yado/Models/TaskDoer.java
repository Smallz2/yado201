package app.yado.Models;

import java.util.HashMap;
import java.util.Map;

public class TaskDoer {
    private String UID;
    private String timeStamp;

    public TaskDoer(String UID, String timeStamp) {
        this.UID = UID;
        this.timeStamp = timeStamp;
    }

    public TaskDoer() {

    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Map<String, Object> toFireBase() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("UID", UID);
        result.put("timeStamp", timeStamp);

        return result;
    }

}
