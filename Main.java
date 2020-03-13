package edu.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Main {
	static boolean serverON = true;
	static HttpServer serv;
	static String index;
	static int temperature=0;
	static long timestamp = new Date().getTime();
	static Thread reciever;
	static double temp;
	public static void main(String[] args) throws Throwable {
		File f = new File("index.html");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		index="";
		while(br.ready()) {
			index += br.readLine();
		}
		serv = new HttpServer(80);
		serv.setIndex(insertAll());
		
		reciever = new Thread(new Runnable() {// поток обрабатывающий пакеты от метеостанций
			@Override
			public void run() {
				DatagramSocket udp;
				try {
					udp = new DatagramSocket(1338);
					
					DatagramPacket packet;
					while(serverON) {
						packet = new DatagramPacket(new byte[5], 5);
						udp.receive(packet);
						byte[] data = packet.getData();
						if(data[4] != 0x13) continue; // идентификатор пакета
						temperature = (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | (data[3]);
						temp = 1 / ( -Math.log((1023.0 / temperature) -1.0) / 3977.0 + 1.0 / 295.0) -273.0; // формула из скетча амперки для ардуино
						System.out.println("message: " + new String(packet.getData()));
						timestamp = System.currentTimeMillis();
						serv.setIndex(insertAll());
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		});
		reciever.start();

		
	}
	static void close() {
		serverON = false;
		
		if(reciever.isAlive()) {
			DatagramSocket s;
			try {
				s = new DatagramSocket(1338);
				s.connect(InetAddress.getLocalHost(),1337);
				byte[] v = new byte[] {0x00,0x00,0x00,0x00,0x13};
				s.send(new DatagramPacket(v, 5));
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	public static String insertAll() {
		Date date = new Date(timestamp);
		String index = Main.index;
		index = index.replaceAll("%t%", Double.toString(temp));
		index = index.replaceAll("%date%", date.toString());
		return index;
	}
	
	
}
