import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces
import java.util.Random;

import javax.swing.*;

public class GUITesting extends Testing{//  extends Frame {// implements ActionListener {
	
	private JPanel MPanel;
	private JFrame MFrame;
	private JPanel panelQuestion;
	private Button Binfo_stud;
	private Button BOK_ToAnswer;
	private TextField textF_name;
	private TextField textF_surname;
	private TextField textF_group;
	private JRadioButton[] rbs;
	private StudentStorage stud_st;//
	private ButtonGroup RBAnswer;
	private int curr_question;//
	
	public GUITesting(String db_name, boolean is_created) {
		super(db_name, is_created);
	}
	
	public GUITesting(String db_name, String TestF, String TestAns, boolean is_created) {
		super(db_name, TestF, TestAns, is_created);
	}

	public void StartGUITestStudent(int idStudent, int num_of_questions) {
		//Runnable r = new Runnable() {
		//	@Override
		//	public void run() {
				stud_st = new StudentStorage();
				stud_st.initNQuestions();
		    	stud_st.setIdAnswerStudent(0);
		    	
				MFrame = new JFrame("English testing for students.");
				getStudentInfo(idStudent, num_of_questions);
				createMFrame();
		//	}
		//};
		//EventQueue.invokeLater(r);
	}
	
	public void createMFrame() {
		MFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MFrame.pack();
		MFrame.setResizable(false);
		MFrame.setLocation(300, 300);
		MFrame.setVisible(true);
	}
	
	@SuppressWarnings("deprecation")
	private void getStudentInfo(int idStudent, int num_of_questions) {	
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
				ButtonOK_studInfoActionPerfomed(e, idStudent, num_of_questions);
			}
		};
		Binfo_stud.addActionListener(i_al);
		Binfo_stud.setPreferredSize(new Dimension(80, 25));
		pnlT.add(Binfo_stud);
		((FlowLayout)pnlT.getLayout()).setAlignment(FlowLayout.TRAILING);
		panelInfo.add(pnlT);
		pnlT = new JPanel();
		MPanel.add(panelInfo);
		//--------------------------------------------------------------------------------------
		panelQuestion = new JPanel();
		panelQuestion.setLayout(new GridLayout(5,1));//
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
		
		panelQuestion.hide();
		MPanel.add(panelQuestion);
		//-----------------------------------------------------------------------------------------
		MFrame.add(MPanel);
	}
	

	@SuppressWarnings("deprecation")
	private void ButtonOK_studInfoActionPerfomed(ActionEvent e, int idStudent, int num_of_questions) {
		textF_name.enable(false);
		textF_surname.enable(false);
		textF_group.enable(false);
		Binfo_stud.enable(false);
		try {
			AddToProtocolFile(idStudent, textF_name.getText(), textF_surname.getText(),textF_group.getText(), num_of_questions);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		stud_st.setStudentMark(0);
    	super.db.updateStudentMarkIntoProtocol(idStudent, stud_st.getStudentMark());
		this.askQuestion(idStudent, num_of_questions);
	}
	
	private void BOK_ToAnswerActionPerformed(ActionEvent arg0, int idStudent, int num_of_questions) {
		int n_answer = 0;
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
			this.askQuestion(idStudent, num_of_questions);
	}
	
	@SuppressWarnings("deprecation")
	private void askQuestion(int idStudent, int num_of_questions) {
		if (stud_st.getIdAnswerStudent() != num_of_questions) {
	    	Random rand = new Random();
	    	int num_rows = super.db.countRowsQuestions();
	    	
    		TestQuestion tq;
    		tq = new TestQuestion();
    		while (true) {
    			curr_question = rand.nextInt(num_rows);
    			if (curr_question != 0 && !stud_st.getNQuestions().contains(curr_question))
    				break;
    		}
    		tq.n_question = curr_question;
    		tq.question = super.db.selectFromQuestions(curr_question).question;
    		int len_var_ans = super.db.selectFromQuestions(curr_question).var_answers.length;
    		tq.var_answers = new String[len_var_ans];
    		for (int i = 0; i < len_var_ans; i ++) {
    			tq.var_answers[i] = db.selectFromQuestions(curr_question).var_answers[i];
    		}
    		this.outputQuestionAndAnswers(tq);
    		stud_st.setStartTimeAnswering(System.currentTimeMillis()); //начало отсчета времени ввода ответа
		} else {
			BOK_ToAnswer.enable(false);
			stud_st = null;
			this.EndTest(idStudent, num_of_questions);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void outputQuestionAndAnswers(TestQuestion tq) {
		for (int i = 0; i < tq.var_answers.length; i++) {
			rbs[i].setText(tq.var_answers[i]);
		}
		rbs[0].setSelected(true);
		panelQuestion.setBorder(BorderFactory.createTitledBorder(
		           BorderFactory.createEtchedBorder(), tq.question));
		panelQuestion.show();
	}
	
	@SuppressWarnings("deprecation")
	protected void EndTest(int idStudent, int num_of_questions) {
		MFrame.hide();
		MFrame.dispose();
		JPanel finalPanel = new JPanel();
		
		JTextArea textArea = new JTextArea();
		textArea.setText(super.db.selectAndOutputFromProtocol(idStudent, num_of_questions));
		textArea.setEditable(false);
		finalPanel.setSize(textArea.getWidth(),textArea.getHeight());
		finalPanel.add(textArea);
		finalPanel.setBorder(BorderFactory.createTitledBorder("RESULTS:"));
		
		this.createNewFrame(finalPanel);
		super.db.endConnection();
	}
	
	private void createNewFrame(JPanel finalPanel) {
		JFrame finalFrame = new JFrame("Results of your test");
		finalFrame.add(finalPanel);
		finalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		finalFrame.pack();
		finalFrame.setResizable(false);
		finalFrame.setLocation(300, 300);
		finalFrame.setVisible(true);
	}
}

