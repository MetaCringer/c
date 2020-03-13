package edu.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class debug {

	public static void main(String[] args) throws IOException {
		DatagramSocket s = new DatagramSocket(1600);
		DatagramPacket p = new DatagramPacket(new byte[] {0x00,0x00,0x01,0x06,0x13}, 5);
		s.connect(InetAddress.getLocalHost(), 1338);
		s.send(p);
		s.close();
	}

}
