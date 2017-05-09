package com.example.parkingspaces;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.content.Context;
import android.content.Intent;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity{

    private static Context context;

    public static boolean start_here;

    static MyClientTask myClientTask = null;

    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP = "10.0.2.2";

    public static boolean reserve;                 // To know if I should give a "I arrived"

    public static Button _reserveButton;
    public static Button _arrivedButton;

    private static TextView _stringState;

    private static ImageView _imagState;

    private static MyDialogFragmentFire dialogFragmentFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        myClientTask = new MyClientTask();
        myClientTask.start();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        _reserveButton = (Button) findViewById(R.id.btn_reserve);
        _arrivedButton = (Button) findViewById(R.id.btn_arrived);

        _stringState = (TextView) findViewById(R.id.stateString);

        _imagState = (ImageView) findViewById(R.id.state);

        reserve = false;

        start_here = false;

        _reserveButton.setEnabled(false);                   // Initially I can not make reservation soon, before I know the status
        _arrivedButton.setEnabled(false);                   // Initially I can not say "I arrived" because there is no reservation
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public static void CheckStatus() {
       // myClientTask.msgToServer = "STATE";

        //pode ser retirado? acho que não...
        /*while(true){
            if(!myClientTask.response.equals(""))
                break;
        }*/

       // myClientTask.msgToServer = "OLA " + myClientTask.response + "   AQUI";
/*
        if (myClientTask.response.equals("FREE")) {
            stateFree();
        } else if (myClientTask.response.equals("RESERVED")) {
            stateReserved();
        } else if (myClientTask.response.equals("BUSY")) {
            stateBusy();
        } else {
            Toast.makeText(MainActivity.getAppContext(), "Connection failed...", Toast.LENGTH_LONG).show();

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.getAppContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Reconnecting...");
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            MainActivity.CheckStatus();
                            progressDialog.dismiss();
                        }
                    }, 800);
        }

        myClientTask.response = "";

        start_here = true;*/
    }

    public static void stateFree() {
        _imagState.setImageDrawable(context.getResources().getDrawable(R.drawable.green));
        _stringState.setText("'Free'");

        _reserveButton.setEnabled(true);
        _arrivedButton.setEnabled(false);
    }

    public static void stateReserved() {
        _imagState.setImageDrawable(context.getResources().getDrawable(R.drawable.yellow));
        _stringState.setText("'Reserved'");

        if(reserve) {
            _reserveButton.setEnabled(false);
            _arrivedButton.setEnabled(true);
        }
        else {
            _reserveButton.setEnabled(false);
            _arrivedButton.setEnabled(false);
        }
    }

    public static void stateBusy() {
        _imagState.setImageDrawable(context.getResources().getDrawable(R.drawable.red));
        _stringState.setText("'Busy'");

        _reserveButton.setEnabled(false);
        _arrivedButton.setEnabled(false);
    }

    public void onClickReserve(View view) {
        Intent intent = new Intent(this, ReserveManag.class);
        startActivity(intent);
    }

    public void onClickArrived(View view) {

        String str = "";

        if(LoginActivity._emailText.getText().toString().contains("@"))
            str = "ARRIVED, " + LoginActivity._emailText.getText().toString() + ", " + ReserveManag._licensePlate.getText().toString();
        else if (SignupActivity._emailText.getText().toString().contains("@"))
            str = "ARRIVED, " + SignupActivity._emailText.getText().toString() + ", " + ReserveManag._licensePlate.getText().toString();
        else {
            onArrivedFailed();
            return;
        }

        myClientTask.msgToServer = str;

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(myClientTask.response.equals("OK"))
                            onArrivedSuccess();
                        else
                            onArrivedFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);

    }

    public void onArrivedSuccess() {
        reserve = false;
        stateFree();
        // return to main activity and do nothing
        //dialogFragment.dismiss();
    }

    public void onArrivedFailed() {
        Toast.makeText(getBaseContext(), "Sending failed...", Toast.LENGTH_LONG).show();

        _reserveButton.setEnabled(false);
        _arrivedButton.setEnabled(true);
    }

    public void fireOn() {
        FragmentManager fm = getFragmentManager();
        dialogFragmentFire = new MyDialogFragmentFire();
        dialogFragmentFire.show(fm, "FIRE");
    }

    static class MyDialogFragmentFire extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dialog_fire, container, false);
            getDialog().setTitle("FIRE");
            return rootView;
        }
    }


    public static class MyClientTask extends Thread {
        String dstAddress = MainActivity.SERVER_IP;
        int dstPort = MainActivity.SERVER_PORT;
        String response;                    //Message from server
        String msgToServer;                 //Message to server

        boolean aqui = false;

        MyClientTask() {
            response = "";
            msgToServer = "";
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                while (true) {

                    if (dataInputStream.available() > 0) {
                        response = dataInputStream.readUTF();

                        /*if(MainActivity.start_here) {

                        }*/
                    }

                    if (aqui) {
                        msgToServer = "Ola111...";
                        MainActivity.stateReserved();
                        aqui = false;
                    }

                    if (!msgToServer.equals("")) {
                        dataOutputStream.writeUTF(msgToServer);
                        dataOutputStream.flush();

                        if(msgToServer.equals("STATE"))
                            aqui = true;
                        else
                            msgToServer = "";
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

}