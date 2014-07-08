import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces

import javax.swing.*;

import java.net.Socket;

public class GUIClient {
	private JPanel MPanel;
	private JFrame MFrame = new JFrame("English for students");;
	private JPanel panelQuestion;
	private Button Binfo_stud;
	private Button BOK_ToAnswer;
	private TextField textF_name;
	private TextField textF_surname;
	private TextField textF_group;
	private JRadioButton[] rbs;
	private ButtonGroup RBAnswer;
	
	public GUIClient(int clientNumber, int num_of_questions) {
		GUIServer server = new GUIServer();
		server.runServer(clientNumber, num_of_questions);
	}

}
