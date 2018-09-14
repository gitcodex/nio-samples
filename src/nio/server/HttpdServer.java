package nio.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import nio.test.NioUtils;

public class HttpdServer {
    ServerSocketChannel serverSocketChannel = null;
    int port = 5555;
    String fileName;

    public HttpdServer (String fileName) throws Exception {
    	this.fileName = fileName;
        startServer ();
    }

    private void startServer () throws Exception {        
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        NioUtils.log ("Server started on " + port);

        SocketChannel socketChannel = serverSocketChannel.accept();
        sendFile2(socketChannel);
        
        serverSocketChannel.close();
    }
    private void sendFile2(SocketChannel socketChannel) {
    	File file = new File (fileName); 
    	FileInputStream fis = null;    	
    	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1000*1024);    		
    	try {    		
    		fis = new FileInputStream(file);
    		FileChannel fileChannel = fis.getChannel();
    		int bytesRead = 0;  byte [] buff = new byte [1000]; 
    		ByteBuffer bb2 = ByteBuffer.allocateDirect(1000*1024);
    		StringBuilder httpRequest = new StringBuilder();
    		String [] httpRequestLines = new String[10];
    		boolean requestRead = false;
    		int lineCtr = 0;
    		while ((bytesRead = socketChannel.read(bb2)) != -1) {
    			bb2.flip();
    			while(bb2.hasRemaining() && !requestRead) {
    				char ch1 = (char)bb2.get();    				
    				httpRequest.append(ch1);
    				if (ch1 == '\r') {
    					httpRequestLines [lineCtr++] = httpRequest.toString().trim();
    					httpRequest = new StringBuilder();
    					char ch2 = (char)bb2.get();
    					char ch3 = (char)bb2.get();
    					char ch4 = (char)bb2.get();
    					if (ch2 == '\n' && ch3 == '\r' && ch4 == '\n') {
    						requestRead = true;
    					} else {
    						httpRequest.append(ch2);
    						httpRequest.append(ch3);
    						httpRequest.append(ch4);
    					}    					
    				}    				 
    			}
    			bb2.clear();
    			if(requestRead) {
    				break;
    			}
    		}
    		
    		//NioUtils.log("requestRead:" + requestRead);
    		//NioUtils.log("req:"+httpRequest);
    		httpRequestLines = Arrays.copyOf(httpRequestLines, lineCtr);
    		for (String line : httpRequestLines) {
    			System.out.println(line);
			}
    		
    		while ((bytesRead = fileChannel.read(byteBuffer)) != -1) {
    			byteBuffer.flip();
    			while (byteBuffer.hasRemaining()) {    				
    				socketChannel.write(byteBuffer);
    			}
    			byteBuffer.clear();
    		}  
    		//byteBuffer.clear();
    		//byteBuffer.put(-1);
    		//socketChannel.write();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
			try {
				//bos.close();
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}	
    }

    private void sendFile1(SocketChannel socketChannel) {
    	File file = new File (fileName); 
    	FileInputStream fis = null;    	
    	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10*1024);    		
    	try {    		
    		fis = new FileInputStream(file);
    		FileChannel fileChannel = fis.getChannel();
    		int bytesRead = 0; 
    		    		
    		while ((bytesRead = fileChannel.read(byteBuffer)) != -1) {
    			byteBuffer.flip();
    			while (byteBuffer.hasRemaining()) {
    				socketChannel.write(byteBuffer);
    			}
    			byteBuffer.clear();
    		}  
    		//byteBuffer.clear();
    		//byteBuffer.put(-1);
    		//socketChannel.write();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
			try {
				//bos.close();
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}	
    }
    
    public static void main (String [] args) throws Exception {
        new HttpdServer(args [0]);
    }

}