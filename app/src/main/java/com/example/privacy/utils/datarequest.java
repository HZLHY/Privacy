package com.example.privacy.utils;

import com.example.privacy.Pyfunc.py;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class datarequest {
    public Socket socket;
    public String IP;
    public int Port;
    public datarequest(String IP, int Port) throws IOException{
        this.IP = IP;
        this.Port = Port;
    }

    public String getPhotosInfo(FileInputStream[] photos) throws IOException{
        this.socket = new Socket(this.IP,this.Port);
        OutputStream os = this.socket.getOutputStream();
        byte[] tmp = new byte[1024*1024];
        for(int i = 0;i < photos.length;i++){
            tmp = new byte[1024*1024];
            int B = photos[i].read(tmp);
            while(B != -1){
                os.write(tmp);
                tmp = new byte[1024*1024];
                B = photos[i].read(tmp);
            }
            os.write("################".getBytes());
        }
        os.write("Finished".getBytes());
        os.flush();
        this.socket.shutdownOutput();
        // 接收
        String res = this.recv(this.socket);
        os.close();
        return res;
    }

    public String getTextInfo(String[] texts) throws IOException{
        this.socket = new Socket(this.IP,this.Port);
        OutputStream os = this.socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        for(int i = 0;i < texts.length;i++){
            pw.write(texts[i] + "EOF");
        }
        pw.write("Finished");
        pw.flush();
        this.socket.shutdownOutput();
        // 接收
        py.print("start get fallback");
        String res = this.recv(this.socket);
        py.print("get fallback succeed");
        os.close();
        return res;
    }

    public String recv(Socket socket) throws IOException{
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String res = "";
        String info = br.readLine();
        while(info != null){
            res += info + "\n";
            info = br.readLine();
        }
        py.print("close!");
        br.close();
        is.close();
        return res;
    }

    public void close() throws IOException{
        this.socket.close();
    }
}