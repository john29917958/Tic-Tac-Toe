package model.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JOptionPane;

import protocol.Protocol;
import view.GUI;

public class Client extends Thread {
	public Client(GUI g) throws IOException {
		active = true;
		socket = new Socket();
		gui = g;
	}

	/**
	 * Sets is client to active or not If this client is inactive, then this
	 * thread will end.
	 * 
	 * @param b
	 *            - active or inactive
	 */
	public void setActive(boolean b) {
		active = b;
	}

	/**
	 * Send request to Server
	 * 
	 * @param data
	 *            - the request to be sent.
	 * @throws IOException
	 */
	public void send(int[] data) throws IOException {
		output.writeByte(1); // Connection test
		output.write(data.length);
		for (int i = 0; i < data.length; i++) { // Main data
			output.write(data[i]);
		}
		output.flush();
	}

	/**
	 * The main part of this thread This method keep checking whether Server is
	 * shutdown or is there any data is sent by the Server. Once data is sent by
	 * Server, Client send this data to GUI to update the Graphic.
	 */
	public void run() {
		try {
			init();
			byte connectionTest;
			while (active) {
				if ((connectionTest = input.readByte()) == -1) { // Connection
																	// test
					JOptionPane.showMessageDialog(null,
							"Connection to Server lost!", "Tic-Tac-Toe",
							JOptionPane.ERROR_MESSAGE);
					gui.dispose();
					active = false;
				} else {
					if (connectionTest > (byte) 0) { // Pass value to GUI for
														// updating
						int[] data = new int[input.read()];
						for (int i = 0; i < data.length; i++) {
							data[i] = input.read();
						}
						gui.update(data);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Try to connect to Server.
	 * 
	 * @throws IOException
	 */
	private void init() throws IOException {
		socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8081));
		//socket.connect(new InetSocketAddress("140.115.207.57", 8081));
		output = new DataOutputStream(socket.getOutputStream());
		output.flush();
		input = new DataInputStream(socket.getInputStream());
		System.out.println("Connected to Server");
		
		int[] gameJoinRequest = { Protocol.GAME_JOIN, -1, -1 };
		send(gameJoinRequest);
	}

	private boolean active;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private GUI gui;
}
