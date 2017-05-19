package com.example.parkingspaces;

import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;

import android.view.View;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReserveManag extends Activity {

    public static EditText _licensePlate;
    public static String _standbyTime;
    public static Spinner _spinner;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        _licensePlate = (EditText) findViewById(R.id.input_license_plate);
        _standbyTime = "15";
        _spinner = (Spinner) findViewById(R.id.input_standby_time);
    }

    public void onClickOk(View view) {

        if (!validate()) {
            onReserveFailed();
            return;
        }

        MainStatus.reserve = true;

        _standbyTime = _spinner.getSelectedItem().toString();

        String str = "";

        if(LoginActivity._emailText.getText().toString().contains("@"))
            str = "INS_BOOKING " + LoginActivity._emailText.getText().toString() + "," + _licensePlate.getText().toString() + "," + _standbyTime;
        else if (SignupActivity._emailText.getText().toString().contains("@"))
            str = "INS_BOOKING " + SignupActivity._emailText.getText().toString() + "," + _licensePlate.getText().toString() + "," + _standbyTime;
        else {
            onReserveFailed();
            return;
        }

        MainActivity.myClientTask.msgToServer = str;

        final ProgressDialog progressDialog = new ProgressDialog(ReserveManag.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending Reserve...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        while(true){
                            if(!MainActivity.myClientTask.response.equals(""))
                                break;
                        }
                        if(MainActivity.myClientTask.response.equals("OK_RESERVE")) {
                            MainActivity.myClientTask.response = "";
                            onReserveSuccess();
                        }
                        else {
                            MainActivity.myClientTask.response = "";
                            onReserveFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 500);

    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onReserveSuccess() {
        MainStatus.stateReserved();

        finish();
    }

    public void onReserveFailed() {
        Toast.makeText(getBaseContext(), "Reserve failed...", Toast.LENGTH_LONG).show();

        MainStatus._reserveButton.setEnabled(true);
        MainStatus._arrivedButton.setEnabled(false);
    }

    public boolean validate() {
        // License plate format:
        // CC-NN-NN, NN-CC-NN, NN-NN-CC

        String plate = _licensePlate.getText().toString();

        if(plate.isEmpty() || plate.length() < 8)
            return false;

        else if(plate.charAt(2) == '-' || plate.charAt(5) == '-') {

            if (Character.isUpperCase(plate.charAt(0)) && Character.isUpperCase(plate.charAt(1))) {         // first case
                if (Character.isDigit(plate.charAt(3)) && Character.isDigit(plate.charAt(4))
                        && Character.isDigit(plate.charAt(6)) && Character.isDigit(plate.charAt(7)))
                    return true;
            }

            else if(Character.isUpperCase(plate.charAt(3)) && Character.isUpperCase(plate.charAt(4))) {     // second case
                if (Character.isDigit(plate.charAt(0)) && Character.isDigit(plate.charAt(1))
                        && Character.isDigit(plate.charAt(6)) && Character.isDigit(plate.charAt(7)))
                    return true;
            }

            else if(Character.isUpperCase(plate.charAt(6)) && Character.isUpperCase(plate.charAt(7))) {     // third case
                if (Character.isDigit(plate.charAt(0)) && Character.isDigit(plate.charAt(1))
                        && Character.isDigit(plate.charAt(3)) && Character.isDigit(plate.charAt(4)))
                    return true;
            }

            else
                return false;

        }

        return false;
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
