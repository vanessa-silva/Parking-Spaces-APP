package com.example.parkingspaces;

import android.app.Activity;
import android.app.ProgressDialog;

import android.util.Log;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class SignupActivity extends Activity {

    private static final String TAG = "SignupActivity";

    private EditText _nameText;
    private EditText _lastnameText;
    public static EditText _emailText;
    private EditText _passwordText;

    private Button _signupButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _lastnameText = (EditText) findViewById(R.id.input_last_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);

    }

    // Create a Account
    public void onClickCreateAccount(View view) {
        signup();
    }

    // Remember Account? Then Login...
    public void onClickRememberAccount(View view) {
        // Finish the registration screen and return to the Login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        // Finish the Login activity
        finish();
        // Specify an explicit transition animation to perform next
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String str = _nameText.getText().toString() + ", " + _lastnameText.getText().toString() + ", " +
                _emailText.getText().toString() + ", " + _passwordText.getText().toString();
        MainActivity.myClientTask.msgToServer = str;

        //pode ser retirado? acho que não...
        while(true){
            if(!MainActivity.myClientTask.response.equals(""))
                break;
        }

        if(!MainActivity.myClientTask.response.equals("OK_SINGUP")) {
            MainActivity.myClientTask.response = "";
            onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // Register logic
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 500);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        //MainActivity.CheckStatus();

        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Register failed...", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String lastname = _lastnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        char c;
        int d = 0, l = 0, u = 0;

        for(int i = 0; i < name.length(); i++){
            c = name.charAt(i);

            if(Character.isDigit(c))
                d++;
        }

        if (name.isEmpty() || name.length() < 3 || d > 0) {
            _nameText.setError("At least 3 characters e/ou contain numbers...");
            valid = false;
        }
        else {
            _nameText.setError(null);
        }

        d = 0;
        for(int i = 0; i < lastname.length(); i++){
            c = lastname.charAt(i);

            if(Character.isDigit(c))
                d++;
        }

        if (lastname.isEmpty() || lastname.length() < 3 || d > 0) {
            _lastnameText.setError("At least 3 characters e/ou contain numbers...");
            valid = false;
        }
        else {
            _lastnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address...");
            valid = false;
        }
        else {
            _emailText.setError(null);
        }

        d = 0;
        for(int i = 0; i < password.length(); i++){
            c = password.charAt(i);

            if(Character.isDigit(c))
                d++;

            if(Character.isLowerCase(c))
                l++;

            if(Character.isUpperCase(c))
                u++;
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 18 || password.matches("\\s")
                || d == 0 || l == 0 || u == 0) {
            _passwordText.setError("Between 8 and 18 alphanumeric characters and at least one digit, an lowercase and an uppercase letter...");
            valid = false;
        }
        else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
