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

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    static final int REQUEST_SIGNUP = 0;

    public static EditText _emailText;
    private EditText _passwordText;

    private Button _loginButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _loginButton = (Button) findViewById(R.id.btn_login);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);

    }

    public void onClickLogin(View view) {
        login();
    }

    public void onClickSignup(View view) {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        // Finish the Signup activity
        finish();
        // Specify an explicit transition animation to perform next
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void login() {
        Log.d(TAG, "Login");

        _loginButton.setEnabled(false);

        String str = "GET_USER "+_emailText.getText().toString() + ", " + _passwordText.getText().toString();
        MainActivity.myClientTask.msgToServer = str;

        if (!validate(MainActivity.myClientTask)) {
            onLoginFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // Authentication logic
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 700);
    }

    public void onLoginSuccess() {
        finish();

        Intent intent2 = new Intent(this, MainStatus.class);
        startActivity(intent2);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed...", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    // This function will check if the server recognizes in the database the authentication data
    public boolean validate(MainActivity.MyClientTask myClientTask) {
        boolean valid = true;

        while(true){
            if(!myClientTask.response.equals(""))
                break;
        }

        String email = _emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || myClientTask.response.equals("KO_EMAIL")) {
            _emailText.setError("Enter a valid email address...");
            valid = false;
        }
        else {
            _emailText.setError(null);
        }

        if(myClientTask.response.equals("KO_PASS")){
            _passwordText.setError("Incorrect password...");
            valid = false;
        }
        else {
            _passwordText.setError(null);
        }

        if(myClientTask.response.isEmpty() || !myClientTask.response.equals("OK_LOG")){
            valid = false;
        }

        myClientTask.response = "";

        return valid;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //successful signup logic
                //by default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}
