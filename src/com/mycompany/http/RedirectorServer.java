package com.mycompany.http;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP重定向服务器
 * @author wangly
 * @version $Revision: 1.0 $, $Date: 2019/9/7 14:50 $
 */
public class RedirectorServer {

    private static final Logger logger = Logger.getLogger("RedirectorServer");

    private final int port;

    private final String newSite;

    public RedirectorServer(String theSite, int thePort) {
        this.port = thePort;
        this.newSite = theSite;
    }

    public static void main(String[] args) {
        int thePort;
        String  theSite = args[0];
        if(theSite.endsWith("/")){
            theSite = theSite.substring(0,theSite.length()-1);
        }

        try {
            thePort = Integer.parseInt(args[1]);
        }catch (RuntimeException ex){
            thePort = 80;
        }

        RedirectorServer server = new RedirectorServer(theSite,thePort);
        server.start();
    }

    private void start() {
        try (ServerSocket server = new ServerSocket(port)){
            logger.info("Redirecting connections on port "+server.getLocalPort()+" to "+newSite);

            while (true){
                try {
                    Socket connection = server.accept();
                    Thread t = new RedirectThread(connection);
                    t.start();
                }catch (IOException e){
                    logger.log(Level.WARNING,"Exception accepting connection");
                }
            }
        }catch (IOException e){
            logger.log(Level.SEVERE,"Could not start server",e);
        }
    }

    private class RedirectThread extends Thread{

        private final Socket connection;

        public RedirectThread(Socket socket){
            this.connection = socket;
        }

        @Override
        public void run() {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),
                        "us-ascii"));
                InputStreamReader in = new InputStreamReader(
                        new BufferedInputStream(connection.getInputStream()));
                StringBuilder request = new StringBuilder(80);
                //只读取请求的请求行信息
                while(true){
                    int c = in.read();
                    if(c == '\r' || c == '\n' || c == -1){
                        break;
                    }
                    request.append((char)c);
                }

                String get = request.toString();
                String[] pieces = get.split("\\w*");
                String theFile = pieces[1];

                if(get.indexOf("HTTP") != -1){
                    out.write("HTTP/1.1 302 FOUND\r\n");
                    Date now = new Date();
                    out.write("Date: "+now+"\r\n");
                    out.write("Server: Redirector 1.1\r\n");
                    out.write("Location: "+newSite+theFile+"\r\n");
                    out.write("Content-type: text/html\r\n");
                    out.write("\r\n");
                    out.flush();
                }

                out.write("<html><head><title>Document moved</title></head>\r\n");
                out.write("<body><h1>Document moved</h1>\r\n");
                out.write("The document "+theFile+" has moved to\r\n<a href=\"" + newSite + theFile + "\">"+newSite + theFile + "</a>\r\n Please update your bookmarks");
                out.write("</body></html>\r\n");
                out.flush();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
