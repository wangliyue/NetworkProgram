package com.mycompany.daytime;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/9/5 15:56 $
 */
public class DaytimeServer {

    public static final int PORT = 13;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                try (Socket connection = server.accept()){
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    Date now = new Date();
                    writer.write(now.toString()+"\r\n");
                    writer.flush();
                }catch (IOException ex){

                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
