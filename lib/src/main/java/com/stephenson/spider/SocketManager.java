package com.stephenson.spider;

import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SocketManager {

	
	/**
	 * Method to create a socket
	 * @param host
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public SSLSocket makeSocketForConnection(String host, int port) throws IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		return socket;
	};
};
