package edu.tacoma.uw.hungt94.calculator.authenticate;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.tacoma.uw.hungt94.calculator.FrontEnd.MainActivity;
import edu.tacoma.uw.hungt94.calculator.R;
import edu.tacoma.uw.hungt94.calculator.model.User;


public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener
, RegisterFragment.RegisterFragmentListener {
    private JSONObject mUserJSON;
    private SharedPreferences mSharedPreferences;

    private class AuthenticateSyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog pdLoading = new ProgressDialog(SignInActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // This method will be running on UI thread
            pdLoading.setMessage(getString(R.string.wait_msg));
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection = null;
            for (String url : strings) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                    outputStreamWriter.write(mUserJSON.toString());
                    outputStreamWriter.flush();
                    outputStreamWriter.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = bufferedReader.readLine()) != null) {
                        response.append(s);
                    }
                    bufferedReader.close();
                } catch (Exception e) {
                    response.append("An exception occurred:  ");
                    response.append(e.getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            // Disable modal message
            pdLoading.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    String mode = jsonObject.optString("mode", "undefined");
                    switch(mode) {
                        case "login":
                            mSharedPreferences
                                    .edit()
                                    .putBoolean(getString(R.string.LOGGEDIN), true)
                                    .putString(getString(R.string.USERNAME),
                                            mUserJSON.getString("username"))
                                    .apply();
                            launchMainMenu();
                            break;
                        case "register":
                            Toast.makeText(getApplicationContext(),
                                    "Registration successful!",
                                    Toast.LENGTH_SHORT).show();
                            getSupportFragmentManager().popBackStack();
                            break;
                        default:
                            Log.e("AuthenticationActivity",
                                    "Invalid mode passed to AsyncTask");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("error")
                            , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON Parsing error: "
                        + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }





    /**
     * launchMainMenu will start the calculator
     */
    private void launchMainMenu() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void login(String username, String password) {

       StringBuilder url = new StringBuilder(R.string.post_login);
       try{
           mUserJSON = new User(username,password).getUserJson();
           new AuthenticateSyncTask().execute(url.toString());
       } catch (JSONException e) {
           e.printStackTrace();
       }
    }

    @Override
    public void launchRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sign_in_fragment_id,new RegisterFragment()).addToBackStack(null)
                .commit();
    }

    @Override
    public void register(String firstName, String lastName, String email, String username, String password) {
        StringBuilder url = new StringBuilder(getString(R.string.post_register));
        try{
            mUserJSON = new User(firstName,lastName,email,username,password).getUserJson();
            new AuthenticateSyncTask().execute(url.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        if(!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN),false)){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sign_in_fragment_id, new LoginFragment())
                    .commit();
        }else{
            launchMainMenu();
        }
    }
}