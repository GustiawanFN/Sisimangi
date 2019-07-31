package com.smagar.sisimangi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gustiawan on 1/21/2019.
 */

public class Absensi {


    @SerializedName("success")
    String success;
    @SerializedName("message")
    String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Absensi(String success, String message) {
        this.success = success;
        this.message = message;
    }


}
