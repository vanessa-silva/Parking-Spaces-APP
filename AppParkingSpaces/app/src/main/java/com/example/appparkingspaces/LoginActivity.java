package com.example.appparkingspaces;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Build;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                // Finish the Signup activity
                finish();
                // Specify an explicit transition animation to perform next
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        _loginButton.setEnabled(false);

        String str = _emailText.getText().toString() + ", " + _passwordText.getText().toString();
        MainActivity.MyClientTask myClientTask = new MainActivity.MyClientTask(str);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myClientTask.execute();

        if (!validate(myClientTask)) {
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
                }, 3000);
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

        String email = _emailText.getText().toString();
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
        super.onBackPressed();
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

}