package edu.main;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class HttpServer {
	private Thread linstening;
	private ServerSocket ss;
	private String index;
    public HttpServer(int port) throws Throwable {
    	ss = new ServerSocket(port);
        linstening = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
		            Socket s;
					try {
						s = ss.accept();
			            System.err.println("Client accepted");
			            new Thread(new SocketProcessor(s,getIndex())).start();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				
			}
		});
        linstening.start();
    }
    
    synchronized public String getIndex() {
		return index;
	}
    synchronized public void setIndex(String i) {
    	index = i;
    }
    
    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;
        private String index;
        private SocketProcessor(Socket s,String index) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            this.index=index;
        }

        public void run() {
            try {
                readInputHeaders();
                writeResponse(index);
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: danilst\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private void readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while(true) {
                String s = br.readLine();
                if(s == null || s.trim().length() == 0) {
                    break;
                }
                System.out.println(s);
            }
        }
    }
}
