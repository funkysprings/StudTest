import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces

import javax.swing.*;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
//import java.net.InetAddress;

/**
 * Класс для создания граф. интерфейса и взаимодействием с сервером
 * @author funkysprings
 *
 */
public class GUIClient {
	/**
	 * Поток для получения данных от сервера
	 */
	private BufferedReader in;
	/**
	 * Поток для отправки данных на сервер
	 */
	private PrintWriter out;
	/**
	 * Сокет взаимодействия с сервером
	 */
	private Socket socket;
	/**
	 * Главная панель
	 */
	private JPanel MPanel;
	/**
	 * Главный фрейм
	 */
	private JFrame MFrame = new JFrame("English test for students.");;
	/**
	 * Панель с вопросами и ответами на него
	 */
	private JPanel panelQuestion;
	/**
	 * Кнопка подтверждения информации о студенте
	 */
	private Button Binfo_stud;
	/**
	 * Кнопка подтверждения ответа на вопрос
	 */
	private Button BOK_ToAnswer;
	/**
	 * Поле ввода для имени студента
	 */
	private TextField textF_name;
	/**
	 * Поле ввода для фамилии студента
	 */
	private TextField textF_surname;
	/**
	 * Поле ввода для группы студента
	 */
	private TextField textF_group;
	/**
	 * Массив радио-кнопок с ответами на вопрос
	 */
	private JRadioButton[] rbs;
	/**
	 * Информация об ответах студента и т.п.
	 */
	private ButtonGroup RBAnswer;
	
	/**
	 * Создание граф. интерфейса
	 */
	public GUIClient() {
		this.createPanels();
		this.createMainFrame();
	}
	
	/**
	 * Соединяемся с сервером
	 * @throws IOException
	 */
	private void connectToServer() throws IOException {
		System.out.println("The client is connecting...");
		socket = new Socket("localhost", 1080);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	/**
	 * Создание панелей для ввода информации о студенте и для ответов на вопросы
	 */
	@SuppressWarnings("deprecation")
	private void createPanels() {	
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
		panelQuestion.setLayout(new GridLayout(5,1));
		int len_ans = 4;
		RBAnswer = new ButtonGroup();
		rbs = new JRadioButton[len_ans];
		for (int i = 0; i < 4 ; i++) {
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
				try {
					BOK_ToAnswerActionPerformed(arg0);
				} catch (IOException e) {
					System.out.println("Error after clicking button : " + e.getMessage());
				}
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
	
	/**
	 * Создание главного фрейма
	 */
	private void createMainFrame() {
		MFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MFrame.pack();
		MFrame.setResizable(false);
		MFrame.setLocation(300, 300);
		MFrame.setVisible(true);
	}
	
	/**
	 * Отправка информации о студенте с фрейма на сервер
	 * @throws InterruptedException
	 */
	private void getStudentInfo() throws InterruptedException {
		String info = this.textF_name.getText() + "\n" + this.textF_surname.getText() + "\n" + this.textF_group.getText() + "\n";
		out.write(info);
		out.flush();
	}
	
	/**
	 * Получаем вопрос и ответ с сервера
	 */
    @SuppressWarnings("deprecation")
	private void getQuestionAndAnswer() {
    	try {
			String q = in.readLine(); //считываем вопрос
			for (int i = 0; i < rbs.length; i++) { //считываем варианты ответов
				rbs[i].setText(in.readLine());
			}
			rbs[0].setSelected(true);
			panelQuestion.setBorder(BorderFactory.createTitledBorder(
			           BorderFactory.createEtchedBorder(), q));
			panelQuestion.show();
		} catch (IOException e) {
			System.out.println("Couldnt read from server: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
     * Получаем результаты тестирования с сервера
     * @return строку с результатами
     * @throws IOException
     */
    private String getResults() throws IOException {
    	String results = "N"; 
    	while(in.ready()) {
    		results = results + in.readLine() + "\n";
    	}
    	return results;
    }
	
    /**
     * Реакция на нажатие кнопки "Добавление информации о студенте": соединяемся с сервером, получаем и отправляем информацию о студенте на сервер и начинаем задавать вопросы
     * @param ae событие на нажатие
     */
	@SuppressWarnings("deprecation")
	private void ButtonOK_studInfoActionPerfomed(ActionEvent ae) {
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
        try {
            System.out.println("Writing student info...");//
			this.getStudentInfo();	
		} catch (InterruptedException e1) {
			System.out.println("Something interrupted me! -> " + e1.getMessage());
			try {
				socket.close();
			} catch (IOException e2) {
				System.out.println("Couldnt close socket : " + e2.getMessage());
			}
		}
    	System.out.println("Student info was written.");
        this.getQuestionAndAnswer();
	}
	
	/**
	 * Реакция на нажатие кнопки "Подтверждение ответа на вопрос": определяем какая из радио-кнопок выбрана, с нее считываем ответ студента и отправляем на сервер.
	 * Далее, если получаем подтверждение от сервера о том, что вопросы еще есть, то получаем еще вопрос и варианты ответов на него. Иначе, заканчиваем тест.
	 * @param arg0
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private void BOK_ToAnswerActionPerformed(ActionEvent arg0) throws IOException {
		for (int i = 0 ; i < rbs.length ; i++) {
			if (rbs[i].isSelected()) {
				out.write(i);	//номер ответа
				out.flush();
				break;
			}
		}
		if (in.read() == 1) { //если в буфере 1, то сервер еще присылает вопросы
			this.getQuestionAndAnswer();
		} else {
			BOK_ToAnswer.enable(false);
			this.EndTest();
		}
	}
	
	/**
	 * Окончание теста: очищаем главную форму и создаем новую форму для вывода результатов тестирования
	 */
	@SuppressWarnings("deprecation")
	private void EndTest() {
		MFrame.hide();
		MFrame.dispose();
		JPanel finalPanel = new JPanel();
		JTextArea textArea = new JTextArea();
		
		try {
			textArea.setText(this.getResults());
			socket.close();
			in.close();
		} catch (IOException e) {
			System.out.println("Error IO : " + e.getMessage());
		}
		out.close();
		
		textArea.setEditable(false);
		finalPanel.setSize(textArea.getWidth(),textArea.getHeight());
		finalPanel.add(textArea);
		finalPanel.setBorder(BorderFactory.createTitledBorder("RESULTS:"));
		
		this.createFinalFrame(finalPanel);
		
	}
	
	/**
	 * Создание формы с результатами тестирования
	 * @param finalPanel панель с результатами тестирования
	 */
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
