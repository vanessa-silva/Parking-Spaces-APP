package com.example.parkingspaces;

import android.app.Activity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {

    public static boolean start_here;

    static MyClientTask myClientTask = null;
    static MyServerTask myListenerTask = null;

    private static final int SERVER_PORT = 5560;
    private static final int SERVER_PORT_LISTENER = 61204;
    private static final String SERVER_IP = "192.168.10.104";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClientTask = new MyClientTask(SERVER_PORT, SERVER_IP);
        myClientTask.start();

        myListenerTask = new MyServerTask(SERVER_PORT_LISTENER, SERVER_IP);
        myListenerTask.start();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        start_here = true;
    }

    public static class MyClientTask extends Thread {
        String dstAddress;
        int dstPort;
        String response;                    //Message from server
        String msgToServer;                 //Message to server
        Boolean closeSocket = false;

        MyClientTask(int porta, String ip_) {
            dstAddress = ip_;
            dstPort = porta;
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
                    if(closeSocket){
                        socket.close();
                    }

                    if (dataInputStream.available() > 0) {
                        BufferedReader in =
                                new BufferedReader(
                                        new InputStreamReader(dataInputStream));
                        response = in.readLine();
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
                        } else if (response.equals("EXPIRED")) {
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

    public static class MyServerTask extends Thread {
        String dstAddress;
        int dstPort;
        String response;                    //Message from server
        String msgToServer;                 //Message to server
        Boolean closeSocket = false;

        MyServerTask(int porta, String ip_) {
            dstAddress = ip_;
            dstPort = porta;
            response = "";
            msgToServer = "";
        }

        @Override
        public void run() {
            Socket connectionSocket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                String clientSentence;
                String capitalizedSentence;
                ServerSocket welcomeSocket = new ServerSocket(dstPort);

                while (true) {
                connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    response = inFromClient.readLine();
                System.out.println("Received: " + response);
                capitalizedSentence = response.toUpperCase() + '\n';
                outToClient.writeBytes(capitalizedSentence);

                    if (!MainActivity.start_here) {

                        if (response.equals("FIRE")) {
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            MainStatus.fireOn();
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
                        } else if (response.equals("EXPIRED")) {
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
                if (connectionSocket != null) {
                    try {
                        connectionSocket.close();
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

    @Override
    protected void onDestroy(){
        try {
            myClientTask.closeSocket = true;
        }finally {
            myClientTask.interrupt();
        }

        super.onDestroy();
    }
}