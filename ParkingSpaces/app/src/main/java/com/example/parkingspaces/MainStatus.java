package com.example.parkingspaces;


import android.app.Activity;
import android.app.ProgressDialog;

import android.graphics.Color;
import android.os.Bundle;

import android.view.View;

import android.content.Context;
import android.content.Intent;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainStatus extends Activity{

    private static Context contextStatus;

    public static String _currentState;

    public static boolean reserve;                 // To know if I should give a "I arrived"

    public static Button _reserveButton;
    public static Button _arrivedButton;
    public static Button _cancelReservationButton;

    private static Button _stringState;

    private static ImageView _imagState;

    private static LinearLayout _backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        MainStatus.contextStatus = getApplicationContext();

        _reserveButton = (Button) findViewById(R.id.btn_reserve);
        _arrivedButton = (Button) findViewById(R.id.btn_arrived);
        _cancelReservationButton = (Button) findViewById(R.id.btn_cancel_reservation);

        _stringState = (Button) findViewById(R.id.stateString);

        _imagState = (ImageView) findViewById(R.id.state);

        _backgroundView = (LinearLayout) findViewById(R.id.viewState);

        reserve = false;

        _currentState = "";

        MainActivity.myClientTask.msgToServer = "GET_STATE PARK";

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        CheckStatus();
                    }
                }, 800);

    }

    public static Context getAppContext() {
        return MainStatus.contextStatus;
    }

    public static void CheckStatus() {
        while(true){
            if(!MainActivity.myClientTask.response.equals(""))
                break;
        }

        if (MainActivity.myClientTask.response.equals("FREE")) {
            stateFree();
        } else if (MainActivity.myClientTask.response.equals("RESERVED")) {
            stateReserved();
        } else if (MainActivity.myClientTask.response.equals("BUSY")) {
            stateBusy();
        } else {
            Toast.makeText(MainStatus.getAppContext(), "Connection failed...\n     Reconnecting", Toast.LENGTH_LONG).show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            MainActivity.myClientTask.response = "";
                            MainActivity.myClientTask.msgToServer = "GET_STATE PARK";
                            CheckStatus();
                        }
                    }, 500);
        }

        MainActivity.myClientTask.response = "";

        MainActivity.start_here = false;
    }

    public static void stateFree() {
        _currentState = "free";

        _imagState.setImageDrawable(getAppContext().getResources().getDrawable(R.drawable.green));
        _stringState.setText("'Free'");

        _reserveButton.setClickable(true);
        _arrivedButton.setClickable(false);
        _cancelReservationButton.setClickable(false);
    }

    public static void stateReserved() {
        _currentState = "reserved";

        _imagState.setImageDrawable(getAppContext().getResources().getDrawable(R.drawable.yellow));
        _stringState.setText("'Reserved'");

        if (reserve) {
            _reserveButton.setClickable(false);
            _arrivedButton.setClickable(true);
            _cancelReservationButton.setClickable(true);
        } else {
            _reserveButton.setClickable(false);
            _arrivedButton.setClickable(false);
            _cancelReservationButton.setClickable(false);
        }
    }

    public static void stateBusy() {
        _currentState = "busy";

        _imagState.setImageDrawable(getAppContext().getResources().getDrawable(R.drawable.red));
        _stringState.setText("'Busy'");

        _reserveButton.setClickable(false);
        _arrivedButton.setClickable(false);
        _cancelReservationButton.setClickable(false);
    }

    public void onClickReserve(View view) {
        Intent intent = new Intent(MainStatus.this, ReserveManag.class);
        startActivity(intent);
    }

    public void onClickArrived(View view) {

        String str = "";

        if(LoginActivity._emailText.getText().toString().contains("@"))
            str = "SET_ARRIVED " + LoginActivity._emailText.getText().toString() + "," + ReserveManag._licensePlate.getText().toString();
        else if (SignupActivity._emailText.getText().toString().contains("@"))
            str = "SET_ARRIVED " + SignupActivity._emailText.getText().toString() + "," + ReserveManag._licensePlate.getText().toString();
        else {
            onArrivedFailed();
            return;
        }

        MainActivity.myClientTask.msgToServer = str;

        final ProgressDialog progressDialog = new ProgressDialog(MainStatus.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    while(true){
                        if(!MainActivity.myClientTask.response.equals(""))
                            break;
                    }
                    if(MainActivity.myClientTask.response.equals("OK_ARRIVED")) {
                        MainActivity.myClientTask.response = "";
                        onArrivedSuccess();
                    }
                    else {
                        MainActivity.myClientTask.response = "";
                        onArrivedFailed();
                    }
                    progressDialog.dismiss();
                }
            }, 500);

    }

    public void onArrivedSuccess() {
        reserve = false;
        stateFree();
    }

    public void onArrivedFailed() {
        Toast.makeText(getBaseContext(), "Sending failed...", Toast.LENGTH_LONG).show();

        _reserveButton.setClickable(false);
        _arrivedButton.setClickable(true);
        _cancelReservationButton.setClickable(true);
    }

    public void onClickCancelReservation(View view) {
        String str = "";

        if(LoginActivity._emailText.getText().toString().contains("@"))
            str = "CANCEL_BOOKING " + LoginActivity._emailText.getText().toString() + ", " + ReserveManag._licensePlate.getText().toString();
        else if (SignupActivity._emailText.getText().toString().contains("@"))
            str = "CANCEL_BOOKING " + SignupActivity._emailText.getText().toString() + ", " + ReserveManag._licensePlate.getText().toString();
        else {
            onCancelReservationFailed();
            return;
        }

        MainActivity.myClientTask.msgToServer = str;

        final ProgressDialog progressDialog = new ProgressDialog(MainStatus.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        while(true){
                            if(!MainActivity.myClientTask.response.equals(""))
                                break;
                        }
                        if(MainActivity.myClientTask.response.equals("OK_CANCEL")) {
                            MainActivity.myClientTask.response = "";
                            onCancelReservationSuccess();
                        }
                        else {
                            MainActivity.myClientTask.response = "";
                            onArrivedFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 500);
    }

    public void onCancelReservationFailed() {
        Toast.makeText(getBaseContext(), "Sending failed...", Toast.LENGTH_LONG).show();

        _reserveButton.setClickable(false);
        _arrivedButton.setClickable(true);
        _cancelReservationButton.setClickable(true);
    }

    public void onCancelReservationSuccess() {
        reserve = false;
        stateFree();
    }

    public static void fireOn() {
        _imagState.setImageDrawable(getAppContext().getResources().getDrawable(R.drawable.fire));
        _stringState.setText("'FIRE!!!'");
        _backgroundView.setBackgroundColor(Color.parseColor("#4Dff0000"));

        _stringState.setClickable(true);
        _reserveButton.setClickable(false);
        _arrivedButton.setClickable(false);
        _cancelReservationButton.setClickable(false);
    }

    public static void fireOf() {
        _stringState.setClickable(false);
        MainActivity.myClientTask.msgToServer = "FIRE_TERMIN PARK";

        _backgroundView.setBackground(getAppContext().getResources().getDrawable(R.drawable.register_main));

        if (_currentState.equals("free"))
            stateFree();
        else if (_currentState.equals("reserved"))
            stateReserved();
        else if (_currentState.equals("busy"))
            stateBusy();
        else
            stateFree();
    }

    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

}
