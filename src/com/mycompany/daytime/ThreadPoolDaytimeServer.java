package com.mycompany.daytime;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用线程池的daytime服务器
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/9/6 14:43 $
 */
public class ThreadPoolDaytimeServer {

    public static final int PORT = 13;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(50);

        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                try {
                    Socket connection = server.accept();
                    Callable<Void> task = new DaytimeTask(connection);
                    pool.submit(task);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static class DaytimeTask implements Callable<Void>{

        private Socket connection;

        public DaytimeTask(Socket socket){
            this.connection = socket;
        }

        @Override
        public Void call() throws Exception {
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
            return null;
        }
    }
}
