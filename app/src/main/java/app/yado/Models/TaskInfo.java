package app.yado.Models;

import java.io.Serializable;
import java.util.HashMap;

public class TaskInfo implements Serializable {
    private String authorUID;
    private String taskTitle;
    private String taskDescription;
    private String taskStartDate;
    private String taskStartTime;
    private String taskAwardAmount;
    private String taskLocationAddress;
    private String taskLocationLatLong;
    private String taskStatus;
    private String taskKey;


    public TaskInfo(String authorUID, String taskTitle, String taskDescription,
                    String taskStartDate, String taskStartTime, String taskAwardAmount,
                    String taskLocation, String taskStatus, String taskKey, String taskLocationLatLong) {

        this.authorUID = authorUID;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStartDate = taskStartDate;
        this.taskStartTime = taskStartTime;
        this.taskAwardAmount = taskAwardAmount;
        this.taskLocationAddress = taskLocation;
        this.taskLocationLatLong = taskLocationLatLong;

        this.taskStatus = taskStatus;
        this.taskKey = taskKey;
    }

    public TaskInfo() {


    }

    public String getTaskLocationAddress() {
        return taskLocationAddress;
    }

    public void setTaskLocationAddress(String taskLocationAddress) {
        this.taskLocationAddress = taskLocationAddress;
    }

    public String getTaskLocationLatLong() {
        return taskLocationLatLong;
    }

    public void setTaskLocationLatLong(String taskLocationLatLong) {
        this.taskLocationLatLong = taskLocationLatLong;
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(String taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskAwardAmount() {
        return taskAwardAmount;
    }

    public void setTaskAwardAmount(String taskAwardAmount) {
        this.taskAwardAmount = taskAwardAmount;
    }

    public String taskLocationAddress() {
        return taskLocationAddress;
    }

    public void taskLocationAddress(String taskLocation) {
        this.taskLocationAddress = taskLocation;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }



    public HashMap<String, String> toFireBaseObject() {
        HashMap<String, String> taskHashMap = new HashMap<>();

        taskHashMap.put("authorUID", authorUID);
        taskHashMap.put("taskTitle", taskTitle);
        taskHashMap.put("taskDescription", taskDescription);
        taskHashMap.put("taskStartDate", taskStartDate);
        taskHashMap.put("taskStartTime", taskStartTime);
        taskHashMap.put("taskAwardAmount", taskAwardAmount);
        taskHashMap.put("taskLocationAddress", taskLocationAddress);
        taskHashMap.put("taskLocationLatLong", taskLocationLatLong);
        taskHashMap.put("taskStatus", taskStatus);
        taskHashMap.put("taskKey", taskKey);

        return taskHashMap;
    }
}
