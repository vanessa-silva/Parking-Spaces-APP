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
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    static final int REQUEST_SIGNUP = 0;

    private EditText _emailText;
    private EditText _passwordText;
/*
    private TextView _signupLink;*/

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

        String str = _emailText.getText().toString() + ", " + _passwordText.getText().toString();
        MainActivity.myClientTask.msgToServer = str;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            MainActivity.myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            MainActivity.myClientTask.execute();

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
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 1500);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed...", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate(MainActivity.MyClientTask myClientTask) {

        //ESTA FUNÇÃO VAI SER DIFERENTE:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //apenas vai verificar que a pass e o email batem certo com
        //o que está guardado no servidor

        //O servidor deve receber:
        //OK
        //KO mail    -- se nao existir este email, se existir verificar pass
        //KO pass

        boolean valid = true;

        /*String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || myClientTask.response == "KO_MAIL") {
            _emailText.setError("Enter a valid email address...");
            valid = false;
        }
        else {
            _emailText.setError(null);
        }

        if(myClientTask.response.isEmpty()){
            valid = false;
        }

        if(myClientTask.response == "KO_PASS"){
            _passwordText.setError("Incorrect password...");
            valid = false;
        }
        else {
            _passwordText.setError(null);
        }
*/
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
