package app.yado.Models;

import java.util.HashMap;

public class userModel {
    private String userID;
    private String userName;
    private String userPhotoUrl;
    private String userEmail;
    private String userAbout;

    public userModel(String userID, String userName, String userPhotoUrl, String userEmail, String userAbout) {
        this.userID = userID;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.userEmail = userEmail;
        this.userAbout = userAbout;
    }

    public userModel() {
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public HashMap<String, String> toFirebase() {
        HashMap<String, String> user = new HashMap<>();

        user.put("userID", userID);
        user.put("userName", userName);
        user.put("userPhotoUrl", userPhotoUrl);
        user.put("userEmail", userEmail);
        user.put("userAbout", userAbout);
        return user;
    }

    /**
     * Interface method to save data from fireBase onDataChange Method
     */
    public interface userObjectInterface {
        void onCallBack(userModel userModel);
    }
}
