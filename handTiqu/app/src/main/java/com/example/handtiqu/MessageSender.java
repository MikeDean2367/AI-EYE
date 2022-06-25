package com.example.handtiqu;
import android.os.AsyncTask;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String,Void,Void>
{
    Socket s;
    DataOutputStream dos;
    PrintWriter pw;

    @Override
    protected Void doInBackground(String... voids){

        String message = voids[0];
//        boolean flag=false;
        String []ip=message.split("#");
        try {
            s=new Socket(ip[0],Integer.parseInt(voids[1]));
//            s=new Socket("192.168.43.242",8888);
            pw=new PrintWriter(s.getOutputStream());
            pw.write(ip[1]);
            pw.flush();
            pw.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
/*
package com.example.myapplication;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String,Void,Void>
{
    Socket s;
    DataOutputStream dos;
    PrintWriter pw;

    @Override
    protected Void doInBackground(String... voids){

        String message = voids[0];

        try {
            s=new Socket("192.168.43.242",8888);
            pw=new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}

 */
