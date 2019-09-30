package com.mycompany.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/8/23 17:06 $
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",6666);
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF("连接成功！");
        dos.flush();
        dos.close();
        socket.close();

    }
}
