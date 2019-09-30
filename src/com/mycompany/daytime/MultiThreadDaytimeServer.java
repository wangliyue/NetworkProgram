package com.mycompany.daytime;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 多线程的daytime服务器
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/9/6 14:35 $
 */
public class MultiThreadDaytimeServer {

    public static final int PORT = 13;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)){
            while(true){
                try {
                    Socket connection = server.accept();
                    Thread task = new DaytimeThread(connection);
                    task.start();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class DaytimeThread extends Thread{
        private Socket connection;

        public DaytimeThread(Socket socket){
            this.connection = socket;
        }

        @Override
        public void run() {
            try {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                Date now = new Date();
                writer.write(now.toString()+"\r\n");
                writer.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    connection.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
