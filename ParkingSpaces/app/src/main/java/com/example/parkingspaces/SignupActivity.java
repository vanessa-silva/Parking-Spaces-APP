package com.example.parkingspaces;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;

import android.util.Log;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;

import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText _nameText;
    private EditText _lastnameText;
    private EditText _emailText;
    private EditText _passwordText;

    private TextView _loginLink;

    private Button _signupButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //VER SE IDS COM MESMO NOME EM FICHEIROS XML DIFERENTE FAZ CONFUSAO!!!!
        _nameText = (EditText) findViewById(R.id.input_name);
        _lastnameText = (EditText) findViewById(R.id.input_last_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _signupButton = (Button) findViewById(R.id.btn_signup);

    }

    public void onClickCreateAccount(View view) {
        signup();
    }

    public void onClickRememberAccount(View view) {
        // Finish the registration screen and return to the Login activity
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            MainActivity.myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            MainActivity.myClientTask.execute();

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // Register logic
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 1500);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

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

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters...");
            valid = false;
        }
        else {
            _nameText.setError(null);
        }

        if (lastname.isEmpty() || lastname.length() < 3) {
            _lastnameText.setError("At least 3 characters...");
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

        char c;
        int d = 0, l = 0, u = 0;
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
        //finishAffinity();
        //super.onBackPressed();
        // Finish the registration screen and return to the Login activity
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        // Finish the Login activity
        finish();
        // Specify an explicit transition animation to perform next
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
