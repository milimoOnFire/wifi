package com.shmj.wifidirectdemo;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Shahriar on 3/21/2018.
 */

public class Server extends Thread {
    TextView mMesseges;
    InetAddress address;
    public static  int PORT = 1234;
    String msgToSend;
    Context mContext;


    public Server(InetAddress groupOwnerAddress, Context context){
        address = groupOwnerAddress;
        mContext = context;
    }

    /*public void sendFromServer(String str){
        try {
            ServerSocket serverSocket = new ServerSocket(PORT,5,address);
            Socket socket;
            socket = serverSocket.accept();

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(str);
            out.close();

        }catch (Exception e){
            System.out.println("server run abnormal: " + e.getMessage());
        }
    }*/

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT,5,address);
            Socket socket = null;
            while (true){
                socket = serverSocket.accept();
                System.out.println("Add connection："+socket.getInetAddress()+":"+socket.getPort());
                //address = socket.getInetAddress();
                //PORT = socket.getPort();
                new HandlerThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class HandlerThread implements Runnable {
        private Socket socket;
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }



        public void run() {
            Log.i("in ane doros shod",Chat.msgToSend );

            try {
                // Read client data
                DataInputStream input = new DataInputStream(socket.getInputStream());
                //This should pay attention to the write method of the client output stream,
                // otherwise it will throw EOFException
                String clientInputStr = input.readUTF();
                // Processing client data
                System.out.println("Client sent over the content:" + clientInputStr);
                TextView txtView = (TextView) ((Chat)mContext).findViewById(R.id.messages);
                // txtView.setText(clientInputStr);
                txtView.setText(txtView.getText() + "\n" + "client: " + clientInputStr);
                //TextView tt = (TextView) tt.findViewById();
                // mMesseges.setText("milad");
                //chat.messages.setText(messages.getText() + "\n" + "client: " + clientInputStr);

                // Reply to the client
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                //System.out.print("please enter:\t");
//                // Send a line of keyboard input
//                String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                msgToSend = Chat.getMsgToSend();
                if( msgToSend != null) {
                    out.writeUTF(msgToSend);
                }
                msgToSend = "";


                //out.writeUTF("test back");

                //out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("server run abnormal: " + e.getMessage());
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("server finally abnormal:" + e.getMessage());
                    }
                }
            }
        }
    }
}