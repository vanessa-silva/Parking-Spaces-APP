package com.example.parkingspaces;

import android.app.Activity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Looper;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity{

    public static boolean start_here;

    static MyClientTask myClientTask = null;

    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClientTask = new MyClientTask();
        myClientTask.start();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        start_here = true;
    }

    public static class MyClientTask extends Thread {
        String dstAddress = MainActivity.SERVER_IP;
        int dstPort = MainActivity.SERVER_PORT;
        String response;                    //Message from server
        String msgToServer;                 //Message to server

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

                    }

                    if (!MainActivity.start_here) {

                        if (response.equals("FIRE")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.fireOn();
                                            response = "";
                                        }
                                    }, 1);
                        } else if (response.equals("FIRE_TERMIN")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.fireOf();
                                            response = "";
                                        }
                                    }, 1);
                        } else if (response.equals("FREE")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.stateFree();
                                            response = "";
                                        }
                                    }, 1);
                        } else if (response.equals("RESERVED")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.stateReserved();
                                            response = "";
                                        }
                                    }, 1);
                        } else if (response.equals("BUSY")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.stateBusy();
                                            response = "";
                                        }
                                    }, 1);
                        } else if(response.equals("EXPIRED")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.reserve = false;
                                            MainStatus.stateFree();
                                            response = "";
                                        }
                                    }, 1);
                        }
                    }

                    if (!msgToServer.equals("")) {
                        dataOutputStream.write(msgToServer.getBytes("UTF-8"));
                        dataOutputStream.flush();
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