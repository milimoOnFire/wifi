package com.shmj.wifidirectdemo;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static com.shmj.wifidirectdemo.Server.PORT;

/**
 * Created by Shahriar on 3/21/2018.
 */

public class Client extends Thread {
    InetAddress address;
    String msgToSend;

    public Client(InetAddress address){
        this.address = address;
    }
    @Override
    public void run() {
        communication();
    }

    /*public void sendFromClient(String str){
        try {
            //ServerSocket serverSocket = new ServerSocket(PORT,5,address);
            Socket socket;
            socket = new Socket(address, PORT);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(str);
            out.close();

        }catch (Exception e){
            System.out.println("Client run abnormal: " + e.getMessage());
        }
    }*/

    private void communication(){
        Socket socket = null;
        try {
            //Create a stream socket and connect it to the
            //specified port number on the specified host
            //socket = new Socket(address, PORT);
            socket = new Socket(address, Server.PORT);


            //Read server data
            DataInputStream input = new DataInputStream(socket.getInputStream());

            //Send data to the server
            msgToSend = Chat.getMsgToSend();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.print("please enter: \t");
            if(msgToSend != null ) {
                out.writeUTF(msgToSend);
            }
            msgToSend = null;
//            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
//            out.writeUTF(str);
            //out.writeUTF("test");



            String ret = input.readUTF();
            Log.i("server returns: " , ret);
            Log.i("in ane doros shod",Chat.msgToSend );

            // 如接收到 "OK" 则断开连接
            if ("OK".equals(ret)) {
                System.out.println("Client will close the connection");
                Thread.sleep(500);
            }

            Chat.updateMessagesfromClient(ret);
            //messages.setText(messages.getText() + "\n" + "Server: " + ret);

            //out.close();
            input.close();
        } catch (Exception e) {
            System.out.println("Client exception:" + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                    System.out.println("Clients finally abnormal:" + e.getMessage());
                }
            }
        }
    }
}
