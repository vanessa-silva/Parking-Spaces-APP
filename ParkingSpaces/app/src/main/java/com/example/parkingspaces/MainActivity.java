package com.example.parkingspaces;

import android.support.v7.app.AppCompatActivity;
import android.app.DialogFragment;
import android.app.FragmentManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.Button;

import android.content.Intent;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static Socket socket;
    static MyClientTask myClientTask;

    private static final int SERVER_PORT = 5560;
    private static final String SERVER_IP = "pluthus.ddns.net";

    private static boolean reserve = false;                 //To know if I should give a "I arrived"

    private Button _reserveButton;
    private Button _arrivedButton;

    private MyDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClientTask = new MyClientTask("");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        _reserveButton = (Button) findViewById(R.id.btn_reserve);
        _arrivedButton = (Button) findViewById(R.id.btn_arrived);

        _arrivedButton.setEnabled(false);
    }

    public void onClickReserve(View view) {
        FragmentManager fm = getFragmentManager();
        dialogFragment = new MyDialogFragment ();
        dialogFragment.show(fm, "Manage Reservation");
    }

    public void onClickArrived(View view) {
        //Enviar mensagem para o servido para ele "avisar" o arduino da chegada e atualizar respetivos androids

        _arrivedButton.setEnabled(false);

    }

    public void onClickOk(View view) {
        //colocar variavel de reserva booliana a TRUE e enviar para o servidor para desativar tudo e avisar tudo
        reserve = true;
        _reserveButton.setEnabled(false);
        _arrivedButton.setEnabled(true);

        // return to main activity and do nothing
        dialogFragment.dismiss();
    }

    public void onClickCancel(View view) {
        // return to main activity and do nothing
        dialogFragment.dismiss();
    }

    class MyDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dialog_reserve, container, false);
            getDialog().setTitle("Manage Reservation");
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

            Socket socket = null;
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