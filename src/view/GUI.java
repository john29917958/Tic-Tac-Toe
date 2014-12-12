package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import protocol.Protocol;
import model.client.Client;

public class GUI extends JFrame {
	public GUI() throws IOException {
		JPanel chessboard = new JPanel();
		chessboard.setLayout(new GridLayout(3, 3));
		chessboard.setBorder(new TitledBorder("Chessboard"));

		grid = new Grid[9];
		for (int i = 0; i < 9; i++) {
			grid[i] = new Grid(i, this);
			chessboard.add(grid[i]);
		}

		JPanel textPanel = new JPanel();
		textArea = new JTextArea("Waiting");
		textArea.setColumns(5);
		textArea.setFont(Font.getFont(Font.SANS_SERIF));
		textPanel.setBorder(new TitledBorder("Game status"));
		textPanel.add(textArea, BorderLayout.CENTER);
		
		add(chessboard, BorderLayout.CENTER);
		add(textPanel, BorderLayout.SOUTH);

		setTitle("Tic-Tac-Toe");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(true);

		client = new Client(this);
		client.start();

		PROTOCOL = 0;
		POSITION = 1;
		TYPE = 2;
		type = -1;
		started = false;
		active = false;
	}

	public void send(int position) throws IOException {
		if (started && active) {
			int[] data = { Protocol.GAME_UPDATE, position, type };
			client.send(data);
		}
	}

	/**
	 * Updates information or graphic of grids.
	 * 
	 * @param protocol
	 *            - the protocol corresponding to the value
	 * @param value
	 *            - content of protocol (data)
	 */
	public void update(int[] data) {
		int protocol = data[PROTOCOL];
		int position = data[POSITION];
		int type = data[TYPE];
		System.out.println("GUI received:\tprotocol = " + protocol
				+ "\tposition = " + position + "\ttype = " + type);
		switch (protocol) {
		case Protocol.GAME_JOIN:
			this.type = type;
			break;
		case Protocol.GAME_STARTED:
			started = true;
			textArea.setText("Game started!!");
			break;
		case Protocol.GAME_TOKEN:
			if (type == 0) {
				active = false;
				textArea.setText("Game started!!\nOther players' turn, please wait.");
			} else {
				active = true;
				textArea.setText("Game started!!\nYour turn.");
			}
			break;
		case Protocol.GAME_UPDATE:
			grid[position].setType(type);
			repaint();
			break;
		case Protocol.GAME_RESULT:
			if (type == Protocol.GAME_WIN) {
				textArea.setText("Win");
				JOptionPane.showMessageDialog(null,
						"You win!!\nCongratulations!!", "Tic-Tac-Toe",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (type == Protocol.GAME_DUAL) {
				textArea.setText("Dual");
				JOptionPane.showMessageDialog(null,
						"You and your opponent are well-matched!!\nHope you come back again soon!!", "Tic-Tac-Toe",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				textArea.setText("Lose");
				JOptionPane.showMessageDialog(null,
						"You lose!!\nBut you are still good!!", "Tic-Tac-Toe",
						JOptionPane.INFORMATION_MESSAGE);
			}
			client.setActive(false);
			this.dispose();
			break;
		default:
			break;
		}

	}

	/**
	 * Program main start execution. Entry of program.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		GUI g = new GUI();
	}

	private Client client;
	private Grid grid[];
	private JTextArea textArea;
	private int type;
	private boolean started, active;
	private final int PROTOCOL, POSITION, TYPE;
}
