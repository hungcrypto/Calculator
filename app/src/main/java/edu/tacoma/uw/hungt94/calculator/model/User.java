package edu.tacoma.uw.hungt94.calculator.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String mUserFirstName;
    private String mUserLastName;
    private String mEmail;
    private String mUsername;
    private String mPassword;

    public static final String FIRST_NAME = "first";
    public static final String LAST_NAME = "last";
    public static final String EMAIL = "email";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";

    public User(String FirstName, String LastName, String Email, String Username, String Password) {
        this.mUserFirstName = FirstName;
        this.mUserLastName = LastName;
        this.mEmail = Email;
        this.mUsername = Username;
        this.mPassword = Password;
    }

    public User(String Username, String Password) {
        this.mUsername = Username;
        this.mPassword = Password;
    }

    /**
     * This turns the JSON blob into Textbook objects.
     * @return List of textbooks
     * @throws JSONException
     */

    public JSONObject getUserJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        if(!TextUtils.isEmpty(mUserFirstName) && !TextUtils.isEmpty(mUserLastName) &&
        !TextUtils.isEmpty(mEmail)){
            jsonObject.put(FIRST_NAME, mUserFirstName);
            jsonObject.put(LAST_NAME, mUserLastName);
            jsonObject.put(EMAIL, mEmail);
        }
        jsonObject.put(USER_NAME, mUsername);
        jsonObject.put(PASSWORD, mPassword);




        return jsonObject;
    }
}
