package control;

import java.io.IOException;
import java.util.LinkedList;

import model.server.Server;
import protocol.Protocol;

public class GameManager {
	public GameManager(Server server) {
		PROTOCOL = 0;
		POSITION = 1;
		TYPE = 2;

		this.server = server;
		chessboard = new int[9];
		for (int i = 0; i < 9; i++) {
			chessboard[i] = Protocol.TYPE_NONE;
		}
		playerCounter = 0;
		gameStarted = false;
		idList = new LinkedList<Integer>();
		responseData = new int[3];
	}

	/**
	 * Calculates and tells Server to broadcast the result.
	 * 
	 * @param protocol
	 *            - the protocol corresponding to the value
	 * @param value
	 *            - content of protocol (data)
	 * @throws IOException
	 */
	public void calculate(int id, int[] data) throws IOException {
		int responseID = -1;

		for (int i = 0; i < responseData.length; i++) {
			responseData[i] = -1; // -1 means the ith contain of response is a
									// "don't care"
		}

		switch (data[PROTOCOL]) {
		case Protocol.GAME_JOIN:
			playerCounter += 1;
			idList.add(id);
			responseID = id;
			responseData[PROTOCOL] = data[PROTOCOL];

			if (playerCounter == 1) { // Checks how many player joined and
										// assigns a type to player
				responseData[TYPE] = Protocol.TYPE_CIRCLE;
			} else {
				server.setActive(false);
				responseData[TYPE] = Protocol.TYPE_CROSS;
			}

			if (playerCounter == 2) { // Two player joined then start the game
				responseID = -1;
				responseData[PROTOCOL] = Protocol.GAME_STARTED;
				responseData[POSITION] = -1;
				responseData[TYPE] = -1;
				server.broadcast(responseID, responseData);

				playerCounter = 0; // Give first player the chance to talk

				gameStarted = true;
			}
			break;
		case Protocol.GAME_UPDATE:
			// If the player who talks and holds the token then do things to update
			// chessboard
			System.out.println("id = " + id + "\nToken = "
					+ idList.get(playerCounter));
			if (id == idList.get(playerCounter)
					&& chessboard[data[POSITION]] == Protocol.TYPE_NONE) {
				chessboard[data[POSITION]] = data[TYPE];

				responseData[PROTOCOL] = data[PROTOCOL];
				responseData[POSITION] = data[POSITION];
				responseData[TYPE] = data[TYPE];
			}
			break;
		default:
			for (int i = 0; i < responseData.length; i++) {
				responseData[i] = -1;
			}
			System.out.println("Default");
			break;
		}

		server.broadcast(responseID, responseData);
		
		int result = checkGameResult();
		responseData[PROTOCOL] = Protocol.GAME_RESULT;
		responseData[POSITION] = -1;
		
		if (result == 1) { // Check whether there's a winner or not
			responseID = id;
			responseData[TYPE] = Protocol.GAME_WIN;
			server.broadcast(responseID, responseData);

			responseID = -1;
			responseData[TYPE] = Protocol.GAME_LOSE;
			
			server.broadcast(responseID, responseData);
		} else if (result == 2 && data[PROTOCOL] != Protocol.GAME_JOIN) {System.out.println("DUAL");
			responseData[TYPE] = Protocol.GAME_DUAL;
			
			responseID = id;
			server.broadcast(responseID, responseData);
			
			responseID = -1;
			server.broadcast(responseID, responseData);
		} else {
			shiftToken();
		}
	}

	/**
	 * Checks whether there's anyone has win this game or not.
	 * 
	 * @throws IOException
	 */
	private int checkGameResult() throws IOException {
		boolean dual = true;
		
		if (chessboard[0] != Protocol.TYPE_NONE
				&& chessboard[0] == chessboard[4]
				&& chessboard[4] == chessboard[8]) {
			return 1;
		} else if (chessboard[2] != Protocol.TYPE_NONE
				&& chessboard[2] == chessboard[4]
				&& chessboard[4] == chessboard[6]) {
			return 1;
		} else {
			for (int i = 0; i < 2; i++) {
				if (chessboard[i * 3] != Protocol.TYPE_NONE
						&& chessboard[i * 3] == chessboard[i * 3 + 1]
						&& chessboard[i * 3 + 1] == chessboard[i * 3 + 2]) {
					return 1;
				} else {
					if (chessboard[i] != Protocol.TYPE_NONE
							&& chessboard[i] == chessboard[i + 3]
							&& chessboard[i + 3] == chessboard[i + 6]) {
						return 1;
					}
				}
			}
		}
		
		for (int i = 0; i < 9; i ++) {
			if (chessboard[i] == Protocol.TYPE_NONE) {
				dual = false;
			}
		}
		
		if (dual) {
			return 2;
		} else {
			return 0;
		}
	}

	/**
	 * Shift token to next player
	 * 
	 * @throws IOException
	 */
	private void shiftToken() throws IOException {
		if (gameStarted) {
			playerCounter += 1;
			if (playerCounter == idList.size()) {
				playerCounter = 0;
			}

			responseData[PROTOCOL] = Protocol.GAME_TOKEN;
			responseData[POSITION] = -1;
			responseData[TYPE] = 0;
			server.broadcast(-1, responseData);
			responseData[TYPE] = 1;
			server.broadcast(idList.get(playerCounter), responseData);

		}

	}

	private Server server;
	private int[] chessboard;
	private int playerCounter;
	int[] responseData;
	private LinkedList<Integer> idList;
	private final int PROTOCOL, POSITION, TYPE;
	private boolean gameStarted;
}
