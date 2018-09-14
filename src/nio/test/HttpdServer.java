package nio.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HttpdServer {
    ServerSocket serverSocket = null;
    int port = 5555;
    String fileName;

    public HttpdServer (String fileName) throws Exception {
    	this.fileName = fileName;
        startServer ();
    }

    private void startServer () throws Exception {        
        serverSocket = new ServerSocket (port);
        NioUtils.log ("Server started on " + port);

        Socket socket = serverSocket.accept();
        NioUtils.log ("Processing started");
        long t1 = System.currentTimeMillis();
        sendFile1(socket);
        long timeTaken = System.currentTimeMillis() - t1;
        NioUtils.log  ("Processing completed. time taken: " + timeTaken/1000);
        
        serverSocket.close();
    }

    void sendFile1(Socket socket) {
    	File file = new File (fileName);
    	BufferedInputStream bis = null;
    	BufferedOutputStream bos = null;
    	try {
    		bis = new BufferedInputStream (new FileInputStream(file));
    		bos = new BufferedOutputStream(socket.getOutputStream());
    		int i = 0;
    		while ((i = bis.read()) != -1) {
    			bos.write((byte)i);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
			try {
				bos.close();
				bis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
    	
    }
    void sendFile2(Socket socket) {
    	File file = new File (fileName);
    	BufferedInputStream bis = null;
    	BufferedOutputStream bos = null;
    	try {
    		bis = new BufferedInputStream (new FileInputStream(file));
    		bos = new BufferedOutputStream(socket.getOutputStream());
    		int i = 0; 
    		byte [] buff = new byte [10000]; 
    		while ((i = bis.read(buff, 0, buff.length)) != -1) {
    			bos.write(buff, 0, i);
    		}  bos.write(-1);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
			try {
				bos.close();
				bis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
    	
    }

    
    public static void main (String [] args) throws Exception {
        new HttpdServer(args [0]);
    }

}