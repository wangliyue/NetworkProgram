package com.mycompany.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 提供同一个文件的HTTP服务器
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/9/6 16:07 $
 */
public class SingleFileHTTPServer {

    public static final Logger logger = Logger.getLogger("SingleFileHTTPServer");

    private final byte[] content;

    private final byte[] header;

    private final int port;

    private final String encoding;

    public SingleFileHTTPServer(String data, String encoding,String mimeType,int port)
            throws UnsupportedEncodingException {
        this(data.getBytes(encoding),encoding,mimeType,port);
    }

    public SingleFileHTTPServer(byte[] data, String encoding,String mimeType,int port) {
        this.content = data;
        this.port = port;
        this.encoding = encoding;
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\r\n");
        header.append("Server: OneFile 2.0\r\n");
        header.append("Content-length: "+this.content.length+"\r\n");
        header.append("Content-type: "+mimeType+";charset="+encoding+"\r\n");
        header.append("\r\n");
        this.header = header.toString().getBytes(Charset.forName("US-ASCII"));
    }

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if(port < 1 || port > 65535){
                port = 80;
            }
        }catch (RuntimeException e){
            port = 80;
        }

        String encoding = "UTF-8";
        if(args.length > 2){
            encoding = args[2];
        }

        try {
            Path path = Paths.get(args[0]);
            byte[] data = Files.readAllBytes(path);
            String contentType = URLConnection.getFileNameMap().getContentTypeFor(args[0]);
            SingleFileHTTPServer server = new SingleFileHTTPServer(data,encoding,contentType,port);
            server.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try (ServerSocket server = new ServerSocket(this.port)){
            logger.info("Accepting connections on port " + server.getLocalPort());
            logger.info("Data to be send:");
            logger.info(new String(this.content,encoding));

            while (true){
                try {
                    Socket connection = server.accept();
                    pool.submit(new HTTPHandler(connection));
                }catch (IOException e){
                    logger.log(Level.WARNING,"Error accepting connection",e);
                }
            }

        }
        catch (IOException e) {
            logger.log(Level.SEVERE,"Could not start server",e);
        }

    }

    private class HTTPHandler implements Callable<Void>{

        private final Socket connection;

        public HTTPHandler(Socket socket){
            this.connection = socket;
        }

        @Override
        public Void call() throws Exception {
            try {
                BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                StringBuilder request = new StringBuilder(80);
                //只读取请求的请求行信息
                while(true){
                    int c = in.read();
                    if(c == '\r' || c == '\n' || c == -1){
                        break;
                    }
                    request.append((char)c);
                }

                if(request.toString().indexOf("HTTP/") != -1){
                    out.write(header);
                }
                out.write(content);
                out.flush();
            }catch (IOException e){
                logger.log(Level.WARNING,"Error writing to client",e);
            }finally {
                connection.close();
            }
            return null;
        }
    }
}
