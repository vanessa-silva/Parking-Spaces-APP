package com.example.appparkingspaces;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Build;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    static Socket socket;

    private static final int SERVER_PORT = 5560;
    private static final String SERVER_IP = "pluthus.ddns.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClick(View view) {

        EditText msg = (EditText) findViewById(R.id.EditText01);

        String msgToServer = msg.getText().toString();      //Message to server

        if(msgToServer.equals("")){
            msgToServer = null;
            Toast.makeText(MainActivity.this, "No message!", Toast.LENGTH_SHORT).show();
        }

        MyClientTask myClientTask = new MyClientTask(msgToServer);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myClientTask.execute();

    }

    public static class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress = SERVER_IP;
        int dstPort = SERVER_PORT;
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

        // Code for a message received by the server in our space (Text View (if you have one)):
        /*
        protected void onPostExecute(Void result) {
            //(Test View) textResponse.setText(response);
            super.onPostExecute(result);
        }*/

    }

}
