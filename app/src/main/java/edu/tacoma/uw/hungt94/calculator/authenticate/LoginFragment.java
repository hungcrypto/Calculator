package edu.tacoma.uw.hungt94.calculator.authenticate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.tacoma.uw.hungt94.calculator.R;
import edu.tacoma.uw.hungt94.calculator.model.User;


public class LoginFragment extends Fragment {
    private LoginFragmentListener mLoginFragmentListener;
    public interface LoginFragmentListener {
        void login(String email, String pwd);
        void launchRegistration();

    }
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginFragmentListener = (LoginFragmentListener) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        mLoginFragmentListener = (LoginFragmentListener)getActivity();

        final EditText username = view.findViewById(R.id.r_username);
        final EditText password = view.findViewById(R.id.r_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                if (TextUtils.isEmpty(usernameText)) {
                    Toast.makeText(v.getContext(), "Enter a username",
                            Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                } else if (TextUtils.isEmpty(passwordText) || passwordText.length() < 6) {
                    Toast.makeText(v.getContext(),
                            "Enter a password at least 6 characters long",
                            Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                } else {
                    mLoginFragmentListener.login(usernameText, passwordText);
                }
            }
        });

        return view;
    }

}