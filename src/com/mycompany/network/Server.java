package com.mycompany.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/8/20 15:01 $
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        while(true){
            Socket socket = serverSocket.accept();
            System.out.println("新连接！");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            System.out.println(dis.readUTF());
            dis.close();
            socket.close();
        }
    }
}
