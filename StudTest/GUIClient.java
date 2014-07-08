import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces

import javax.swing.*;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;

public class GUIClient {
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	
	private JPanel MPanel;
	private JFrame MFrame = new JFrame("English test for students.");;
	private JPanel panelQuestion;
	private Button Binfo_stud;
	private Button BOK_ToAnswer;
	private TextField textF_name;
	private TextField textF_surname;
	private TextField textF_group;
	private JRadioButton[] rbs;
	private ButtonGroup RBAnswer;
	
	public GUIClient() {
		this.createInfoPanel();//(clientNumber, num_of_questions);
		this.createMainFrame();
	}
	
	private void connectToServer() throws IOException {
		System.out.println("The client is connecting...");//
		socket = new Socket("localhost", 1080);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        System.out.println("Writing student info...");//
        try {
			this.getStudentInfo();
		} catch (InterruptedException e) {
			System.out.println("Something interrupted me! -> " + e.getMessage());
			socket.close();
		}
		socket.close();
	}
	
	private void createInfoPanel() {	
		MPanel = new JPanel(new GridLayout(2, 1));
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new GridLayout(4, 1));
		JPanel pnlT = new JPanel();
		
		pnlT.add(new Label("Your name:"));
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		textF_name = new TextField(20);
		pnlT.add(textF_name);
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		pnlT.add(new Label("Your surname:"));
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		textF_surname = new TextField(30);
		pnlT.add(textF_surname);
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		pnlT.add(new Label("Your group:"));
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		textF_group = new TextField(10);
		pnlT.add(textF_group);
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		Binfo_stud = new Button("OK");
		ActionListener i_al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonOK_studInfoActionPerfomed(e);
			}
		};
		Binfo_stud.addActionListener(i_al);
		Binfo_stud.setPreferredSize(new Dimension(80, 25));
		pnlT.add(Binfo_stud);
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.TRAILING);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		MPanel.add(panelInfo);
		//-----------------------------------------------------------------------------------------
		panelQuestion = new JPanel();
		/*panelQuestion.setLayout(new GridLayout(5,1));//
		int len_ans = super.db.selectFromQuestions(1).var_answers.length;//?
		RBAnswer = new ButtonGroup();
		rbs = new JRadioButton[len_ans];
		for (int i = 0; i < len_ans ; i++) {
			rbs[i] = new JRadioButton();
			RBAnswer.add(rbs[i]);
			pnlT.add(rbs[i]);
			((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
			panelQuestion.add(pnlT);
			pnlT = new JPanel();
		}
		BOK_ToAnswer = new Button("OK");
		ActionListener q_al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BOK_ToAnswerActionPerformed(arg0, idStudent, num_of_questions);
			}
		};
		BOK_ToAnswer.addActionListener(q_al);
		BOK_ToAnswer.setPreferredSize(new Dimension(80, 25));
		pnlT.add(BOK_ToAnswer);
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.LEFT);
		panelQuestion.add(pnlT);
		
		panelQuestion.hide();*/
		MPanel.add(panelQuestion);
		//-----------------------------------------------------------------------------------------
		MFrame.add(MPanel);
	}
	
	private void createMainFrame() {
		MFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MFrame.pack();
		MFrame.setResizable(false);
		MFrame.setLocation(300, 300);
		MFrame.setVisible(true);
	}
	
	private void getStudentInfo() throws InterruptedException {
		String info = this.textF_name.getText() + "\n" + this.textF_surname.getText() + "\n" + this.textF_group.getText() + "\n";
		out.write(info);
		out.flush();
		out.wait();
	}
	
	@SuppressWarnings("deprecation")
	private void ButtonOK_studInfoActionPerfomed(ActionEvent e) {
		textF_name.enable(false);
		textF_surname.enable(false);
		textF_group.enable(false);
		Binfo_stud.enable(false);
		try {
			this.connectToServer();
		} catch (IOException e1) {
			System.out.println("Can not run server: " + e1.getMessage());
			try {
				socket.close();
			} catch (IOException e2) {
				System.out.println("Couldnt close socket :" + e2.getMessage());
			}
		}
		//try {
		//	AddToProtocolFile(idStudent, textF_name.getText(), textF_surname.getText(),textF_group.getText(), num_of_questions);
		//} catch (Exception e1) {
		//	e1.printStackTrace();
		//}
		//stud_st.setStudentMark(0);
    	//super.db.updateStudentMarkIntoProtocol(idStudent, stud_st.getStudentMark());
		//this.askQuestion(idStudent, num_of_questions);
	}
	
	private void BOK_ToAnswerActionPerformed(ActionEvent arg0) {
		/*int n_answer = 0;
		for (int i = 0 ; i < rbs.length ; i++) {
			if (rbs[i].isSelected()) {
				n_answer = i;	//номер ответа
				break;
			}
    	}
    		long PastTime = System.currentTimeMillis() - stud_st.getStartTimeAnswering(); //конец отсчета времени ввода ответа
    		String timePast = Double.toString(PastTime/1000.0) + "s";
    		//добавляем данные ответа студента на вопрос
    		if (super.AddInfoToProtocolFile(idStudent, stud_st.getIdAnswerStudent(), n_answer, curr_question, timePast)) {
    			stud_st.setStudentMark(stud_st.getStudentMark() + 1);;
    			db.updateStudentMarkIntoProtocol(idStudent, stud_st.getStudentMark());
    		}
    		stud_st.getNQuestions().add(curr_question);
    		stud_st.setIdAnswerStudent(stud_st.getIdAnswerStudent() + 1);
			this.askQuestion(idStudent, num_of_questions);*/
	}
	
	@SuppressWarnings("deprecation")
	protected void EndTest() {
		MFrame.hide();
		MFrame.dispose();
		JPanel finalPanel = new JPanel();
		
		JTextArea textArea = new JTextArea();
		//textArea.setText(super.db.selectAndOutputFromProtocol(idStudent, num_of_questions));
		textArea.setEditable(false);
		finalPanel.setSize(textArea.getWidth(),textArea.getHeight());
		finalPanel.add(textArea);
		finalPanel.setBorder(BorderFactory.createTitledBorder("RESULTS:"));
		
		this.createFinalFrame(finalPanel);
	}
	
	private void createFinalFrame(JPanel finalPanel) {
		JFrame finalFrame = new JFrame("Results of your test");
		finalFrame.add(finalPanel);
		finalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		finalFrame.pack();
		finalFrame.setResizable(false);
		finalFrame.setLocation(300, 300);
		finalFrame.setVisible(true);
	}

}
