package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JPanel;
import protocol.Protocol;

public class Grid extends JPanel implements MouseListener {
	Grid(int id, GUI parent) {
		this.ID = id;
		type = Protocol.TYPE_NONE;
		gui = parent;
		borderColor = new Color(120, 120, 120);
		backgroundColor = new Color(200, 200, 200);
		addMouseListener(this);
	}

	/**
	 * Set the current type of this grid (Circle, cross, or none) .
	 * 
	 * @param type
	 *            - the type to be set (Circle, cross, or none)
	 */
	public void setType(int type) {
		this.type = type;
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0) {
		if (type == Protocol.TYPE_NONE) {
			borderColor = new Color(150, 150, 150);
			backgroundColor = new Color(210, 210, 210);
		}

		repaint();
	}

	public void mouseExited(MouseEvent arg0) {
		if (type == Protocol.TYPE_NONE) {
			borderColor = new Color(120, 120, 120);
			backgroundColor = new Color(200, 200, 200);
		}

		repaint();
	}

	public void mousePressed(MouseEvent arg0) {
		if (type == Protocol.TYPE_NONE) {
			borderColor = new Color(100, 100, 100);
			backgroundColor = new Color(90, 90, 90);
		}

		repaint();
	}

	/**
	 * Tell GUI that this grid is pressed for calculation if the type of this
	 * grid is "none".
	 */
	public void mouseReleased(MouseEvent arg0) {
		if (type == Protocol.TYPE_NONE) {
			try {
				borderColor = new Color(150, 150, 150);
				backgroundColor = new Color(210, 210, 210);	
				gui.send(ID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

		repaint();
	}

	/**
	 * Draw this grid
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(borderColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(backgroundColor);
		g.fillRect(0 + getWidth() / 20, 0 + getWidth() / 20, getWidth()
				- getWidth() / 10, getHeight() - getWidth() / 10);

		if (type != Protocol.TYPE_NONE) {
			if (type == Protocol.TYPE_CIRCLE) {
				g.setColor(borderColor);
				g.fillOval(getWidth() / 10, getHeight() / 10, getWidth()
						- getWidth() / 5, getHeight() - getHeight() / 5);
				g.setColor(backgroundColor);
				g.fillOval(getWidth() / 5, getHeight() / 5, getWidth()
						- getWidth() / (5 / 2), getHeight() - getHeight()
						/ (5 / 2));
			} else {
				g.setColor(borderColor);
				int[] pointX = { getWidth() / 10, getWidth() / 5,
						getWidth() - getWidth() / 10,
						getWidth() - getWidth() / 5, getWidth() / 10 };
				int[] pointY = { getHeight() / 5, getHeight() / 10,
						getHeight() - getHeight() / 5,
						getHeight() - getHeight() / 10, getHeight() / 5 };
				g.fillPolygon(pointX, pointY, 5);

				for (int i = 0; i < pointX.length; i++) {
					pointX[i] = getWidth() - pointX[i];
				}

				g.fillPolygon(pointX, pointY, 5);
			}
		}
	}

	protected final int ID;
	protected int type;
	protected Color borderColor, backgroundColor;
	protected GUI gui;
}
