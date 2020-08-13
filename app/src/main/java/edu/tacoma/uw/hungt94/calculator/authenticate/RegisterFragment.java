package edu.tacoma.uw.hungt94.calculator.authenticate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.tacoma.uw.hungt94.calculator.R;

public class RegisterFragment extends Fragment {

private RegisterFragmentListener mRegistrationFragmentListener;
    public RegisterFragment() {
        // Required empty public constructor
    }

    public interface RegisterFragmentListener {

        void register(String firstName, String lastName, String email, String username, String password);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegistrationFragmentListener = (RegisterFragmentListener) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_register,container,false);
       mRegistrationFragmentListener = (RegisterFragmentListener)getActivity();

       final EditText firstname = view.findViewById(R.id.r_firstname);
       final EditText lastname = view.findViewById(R.id.r_lastname);
       final EditText email = view.findViewById(R.id.r_email);
       final EditText username = view.findViewById(R.id.r_username);
       final EditText password = view.findViewById(R.id.r_password);
        Button btnRegister = view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_nameText = firstname.getText().toString();
                String last_nameText = lastname.getText().toString();
                String emailText = email.getText().toString();
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if (TextUtils.isEmpty(first_nameText)) {
                    Toast.makeText(v.getContext(), "Enter a first name",
                            Toast.LENGTH_SHORT).show();
                    firstname.requestFocus();
                } else if (TextUtils.isEmpty(last_nameText)) {
                    Toast.makeText(v.getContext(), "Enter a last name",
                            Toast.LENGTH_SHORT).show();
                    lastname.requestFocus();
                } else if (TextUtils.isEmpty(emailText)) {
                    Toast.makeText(v.getContext(), "Enter a email",
                            Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                } else if (TextUtils.isEmpty(usernameText)) {
                    Toast.makeText(v.getContext(), "Enter a username",
                            Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                } else if (TextUtils.isEmpty(passwordText) || passwordText.length() < 6) {
                    Toast.makeText(v.getContext(),
                            "Enter a password at least 6 characters long",
                            Toast.LENGTH_SHORT).show();
                    password.requestFocus();

                } else {
                    mRegistrationFragmentListener.register(first_nameText, last_nameText, emailText, usernameText, passwordText);
                }
            }
        });
        return view;
    }

}