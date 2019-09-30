package com.mycompany.http.jhttp;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JHTTP web服务器
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/9/7 15:57 $
 */
public class JHTTP {

    public static final Logger logger = Logger.getLogger(JHTTP.class.getCanonicalName());

    public static final int NUM_THREADS = 50;

    public static final String INDEX_FILE = "index.html";

    private final File rootDirectory;

    private final int port;

    public JHTTP(File rootDirectory,int port) throws IOException {
        if(!rootDirectory.isDirectory()){
            throw new IOException(rootDirectory+" does not exist as a directory.");
        }
        this.rootDirectory = rootDirectory;
        this.port = port;
    }

    public void start(){
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)){
            logger.info("Accepting connections on port "+server.getLocalPort());
            logger.info("Document Root:"+rootDirectory);

            while(true){
                Socket request = server.accept();
                Runnable task = new RequestProcessor(rootDirectory, INDEX_FILE, request);
                pool.submit(task);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //设置文档根目录
        File docroot;
        try {
            docroot = new File(args[0]);
        }catch (ArrayIndexOutOfBoundsException ex){
            System.out.println("Usage: java JHTTP docroot port");
            return;
        }
        //设置监听的端口
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if(port < 0 || port > 65535){
                port = 80;
            }
        }catch (RuntimeException ex){
            port = 80;
        }

        try {
            JHTTP server = new JHTTP(docroot,port);
            server.start();
        }
        catch (IOException e) {
            logger.log(Level.SEVERE,"Server could not start",e);
        }
    }


}
