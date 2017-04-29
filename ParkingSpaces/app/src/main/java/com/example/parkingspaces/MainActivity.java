package com.example.parkingspaces;

import android.support.v7.app.AppCompatActivity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.content.Intent;

import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static Socket socket;
    static MyClientTask myClientTask;

    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP = "10.0.2.2";

    private static boolean reserve = false;                 //To know if I should give a "I arrived"

    private static boolean fire = false;                    //Label to see if there is fire or not

    private Button _reserveButton;
    private Button _arrivedButton;

    private EditText _licensePlate;
    private EditText _standbyTime;

    private MyDialogFragmentReserve dialogFragment;
    private MyDialogFragmentFire dialogFragmentFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClientTask = new MyClientTask("");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        _reserveButton = (Button) findViewById(R.id.btn_reserve);
        _arrivedButton = (Button) findViewById(R.id.btn_arrived);

        _licensePlate = (EditText) findViewById(R.id.input_license_plate);
        _standbyTime = (EditText) findViewById(R.id.input_standby_time);

        //_arrivedButton.setEnabled(false);
    }

    public void onClickReserve(View view) {
        FragmentManager fm = getFragmentManager();
        dialogFragment = new MyDialogFragmentReserve();
        dialogFragment.show(fm, "Manage Reservation");
    }

    public void onClickArrived(View view) {
        //Enviar mensagem para o servido para ele "avisar" o arduino da chegada e atualizar respetivos androids,
        // para comprovativo de que sou "eu", envio a matricula do carro

        FragmentManager fm = getFragmentManager();
        dialogFragmentFire = new MyDialogFragmentFire();
        dialogFragmentFire.show(fm, "Manage Reservation");

        /*String str = _licensePlate.getText().toString();
        myClientTask.msgToServer = str;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myClientTask.execute();

        //Provavelmente isto dialog pode ser retirado:
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
        progressDialog.show();
        progressDialog.dismiss();       //?

        _arrivedButton.setEnabled(false);*/

    }

    public void onClickOk(View view) {
        //colocar variavel de reserva booliana a TRUE e enviar para o servidor para desativar tudo e avisar tudo

        if (!validate()) {
            onReserveFailed();
            return;
        }

        reserve = true;
        _reserveButton.setEnabled(false);
        _arrivedButton.setEnabled(true);

        String str = _licensePlate.getText().toString() + ", " + _standbyTime.getText().toString();
        myClientTask.msgToServer = str;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myClientTask.execute();

        //Pode ser tirado
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending Data...");
        progressDialog.show();

        // Register reserve
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onReserveSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);

    }

    public void onClickCancel(View view) {
        // return to main activity and do nothing
        dialogFragment.dismiss();
    }

    public void onReserveSuccess() {
        // return to main activity and do nothing
        dialogFragment.dismiss();
    }

    public void onReserveFailed() {
        Toast.makeText(getBaseContext(), "Reserve failed...", Toast.LENGTH_LONG).show();

        _reserveButton.setEnabled(true);
        _arrivedButton.setEnabled(false);
    }

    public boolean validate() {
        boolean valid = true;

        //Verificar formato da matricula e do tempo de reserva
        //O TEMPO DE RESREVA ate poderia por um escolher tempo uma vez que ESTE TEMPO DEVE TER LIMIT MINIMO E MÁXIMO

        return valid;
    }

    class MyDialogFragmentReserve extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dialog_reserve, container, false);
            getDialog().setTitle("Manage Reservation");
            return rootView;
        }
    }

    class MyDialogFragmentFire extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dialog_fire, container, false);
            getDialog().setTitle("FIRE");
            return rootView;
        }
    }

    static class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress = MainActivity.SERVER_IP;
        int dstPort = MainActivity.SERVER_PORT;
        String response = "";               //Message from server
        String msgToServer;                 //Message to server

        //constructor
        MyClientTask(String msgTo) {
            msgToServer = msgTo;
        }

        protected Void doInBackground(Void... arg0) {

            socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }

                response = dataInputStream.readUTF();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //response = "IOException: " + e.toString();
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
            return null;
        }

        protected void onPostExecute(Void result) {
            // Code for a message received by the server in our space (Text View (if you have one)):
            //(Test View) textResponse.setText(response);
            super.onPostExecute(result);
        }

    }

    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

}