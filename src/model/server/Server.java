package model.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import protocol.Protocol;
import control.GameManager;

public class Server extends Thread {
	Server() throws IOException {
		gameManager = new GameManager(this);
		socket = new ServerSocket(8081);
		clients = new LinkedList<ClientHandler>();
		active = true;
		close = false;
	}

	/**
	 * Decides Server can accept connection from client or not.
	 * 
	 * @param b
	 *            - true if can accept connection, false if cannot accept
	 *            connection.
	 */
	public void setActive(boolean b) {
		active = b;
	}

	/**
	 * Closes this server.
	 */
	public void close() {
		close = true;
	}

	/**
	 * Receives data from Client Handler and pass it to GameManager for
	 * calculation.
	 * 
	 * @param data
	 *            - the data to be calculated.
	 * @throws IOException
	 */
	public void receive(int id, int[] data) throws IOException {
		gameManager.calculate(id, data);
	}

	/**
	 * Broadcasts data to ClientHandler.
	 * 
	 * @param id
	 *            - the identification number of ClientHandler. -1 for
	 *            "don't care", otherwise, send response to specific
	 *            ClientHandler.
	 * @param protocol
	 *            - the protocol corresponding to the value
	 * @param value
	 *            - The data to be broadcasted
	 * @throws IOException
	 */
	public void broadcast(int id, int[] data) throws IOException {
		System.out.println("Server broadcasted:\tid = " + id + "\tprotocol = "
				+ data[0] + "\tposition = " + data[1] + "\ttype = " + data[2]
				+ "\n");
		if (id == -1) {
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).send(data);
			}
		} else {
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).getID() == id) {
					clients.get(i).send(data);
					break;
				}
			}
		}
	}

	/**
	 * Removes specific element in clients list.
	 * 
	 * @param c
	 *            - the element to be removed
	 */
	public void remove(ClientHandler c) {
		clients.remove(c);
	}

	/**
	 * Main part of this Thread Keep receiving data from Client while this
	 * Server is active Finish and close this Server while the close boolean is
	 * true
	 */
	public void run() {
		try {
			int idRange = Protocol.TYPE_CIRCLE;
			while (!close) {
				if (active) {
					Socket s = socket.accept();
					ClientHandler client = new ClientHandler(idRange, this, s);
					clients.add(client);
					client.start();
					idRange += 1;
					System.out.println("Accepted a Client");
				}
			}

			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).setActive(false);
			}

			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Entry of program.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.start();
	}

	private boolean active;
	private boolean close;
	private ServerSocket socket;
	private GameManager gameManager;
	private LinkedList<ClientHandler> clients;
}
