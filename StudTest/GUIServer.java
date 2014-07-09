import java.util.Random;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GUIServer extends Thread {
	private SQLiteDB db;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private StudentStorage stud_st;
	private int numQuestions;
	private int curr_question;//??
	
 	public GUIServer() {
 		boolean is_created = false;
 		File file = new File("test.db"); //файл БД
 		String db_name = file.getName();
 		String testFile = "Test"; //файл-тест с вопросам
 		String test_answerFile = "TestAnswers"; ////файл-тест с ответами на вопросы
 		
 		if (file.exists()) {
 			is_created = true;
 		}
    	this.createDB(db_name, is_created);
    	if (!is_created) {
    		File TestFile = new File(testFile);
    		ParseTestFile(TestFile);
    		File TestAnswers = new File(test_answerFile);
    		ParseTestAnswers(TestAnswers);
    	}
 		System.out.println("The server set up successfully.");
 	}
 	
    private void createDB(String db_name, boolean is_created) {
    	db = new SQLiteDB(db_name, is_created);
    }

    @Override
    public void run() {
    	try {
			this.runServer(1, 5);//?!
		} catch (IOException e) {
			System.out.println("Couldnt run server: " + e.getMessage());
		}
    }
    
    /**
     * Проводим анализ файла с ответами: считываем файл с ответами, создаем объект TestAnswers и добавляем в него информацию про номер ответа(вопроса) и сам ответ, записываем объект TestAnswers в таблицу БД с ответами
     * @param TestAnswers файл с ответами
     *  */
    private void ParseTestAnswers(File TestAnswers) {
    	try {
			BufferedReader br = new BufferedReader(new FileReader(TestAnswers));
			TestAnswer t_answer;
			String s;
			int n = 1, i;
			while ((s = br.readLine()) != null) {
				i = 0;
				t_answer = new TestAnswer();
				t_answer.n_answer = n;
		    	while(Character.isDigit(s.charAt(i))) {
		    		i++;
		    	}
		    	t_answer.answer = s.substring(i);
		    	db.insertOperation(t_answer);
				n++;
			}
			br.close();
		} catch (IOException e) {
			System.out.println("The test was dropped!.");
			throw new RuntimeException(e);
		}
    }
    
    /**
     * Проводим анализ файла с вопросами: считываем файл с ответами, создаем объект TestQuestion и добавляем в него информацию про номер вопроса, вопрос и варианты ответов на вопрос, записываем объект TestQuestion в таблицу БД с вопросами
     * @param TestFile файл с вопросами
     *  */
    private void ParseTestFile(File TestFile) {
    	try {
			BufferedReader br = new BufferedReader(new FileReader(TestFile));
	            //В цикле построчно считываем файл
	            String s;
	            int n = 1;//номер вопроса
	            while ((s = br.readLine()) != null) {
	            	ParseStringFromTestFile(s,n);
	            	n++;
	            }
	            br.close();
		} catch (IOException e) {
			System.out.println("The test was dropped!.");
			throw new RuntimeException(e);
		}
    	
    }
    
    /**
     * Проводим анализ каждой строки из файла с вопросами, сохраняя в в таблицу БД с вопросами номер вопроса, сам вопрос и варианты ответов 
     * @param input_str Входная строка
     * @param n Номер вопроса
     *  */
    private void ParseStringFromTestFile(String input_str, int n) {
    	TestQuestion tq = new TestQuestion();
    	tq.n_question = n;
    	int i = 0;
    	while(Character.isDigit(input_str.charAt(i))) {
    		i++;
    	}
    	tq.question = input_str.substring(i, input_str.indexOf('('));
    	i = input_str.indexOf('(') + 1;
    	tq.var_answers = new String[4];
    	int n_ans = 0;
    	n = i;	//запоминаем индекс элемента после символа "("
    	while(input_str.charAt(i - 1) != ')') {
    		if (input_str.charAt(i) == ',' || input_str.charAt(i) == ')') {
    			tq.var_answers[n_ans] = input_str.substring(n, i);
    			n = i + 1;
    			n_ans++;
    		}
    		i++;
    	}
    	db.insertOperation(tq);
    }
    
    private void goTesting() throws IOException {
    	String Name = in.readLine();
    	String Surname = in.readLine();
    	String Group = in.readLine();
    	this.AddToProtocolFile(this.stud_st.getIdStudent(), Name, Surname, Group, this.numQuestions);
    	this.stud_st.setStudentMark(0);
    	this.db.updateStudentMarkIntoProtocol(this.stud_st.getIdStudent(), this.stud_st.getStudentMark());
    	
    	char[] cbuf = new char[1]; //выделеяем место под ответ студента
    	int n_answer; //ответ студента
    	while (this.stud_st.getIdAnswerStudent() != this.numQuestions) { //пока определенное количество вопросов не было задано
        	this.getQuestion();
        	n_answer = in.read(cbuf); //ждем ответа от студента на вопрос
        	cbuf = new char[1];
        	//out.write(cbuf); //даем знать серверу, что вопросы еще не закончились
        	//out.flush();
        	long PastTime = System.currentTimeMillis() - this.stud_st.getStartTimeAnswering(); //конец отсчета времени ввода ответа
        	String timePast = Double.toString(PastTime/1000.0) + "s";
        	//добавляем данные ответа студента на вопрос
        	if (this.AddInfoToProtocolFile(this.stud_st.getIdStudent(), this.stud_st.getIdAnswerStudent(), n_answer, curr_question, timePast)) {
        		this.stud_st.setStudentMark(stud_st.getStudentMark() + 1);;
        		this.db.updateStudentMarkIntoProtocol(this.stud_st.getIdStudent(), this.stud_st.getStudentMark());
        	}
    	}
    }
    
    private void AddToProtocolFile(int idStudent, String Name, String Surname, String Group, int num_of_questions_to_ask){
    	if (db.isExistsProtocol(idStudent)) {
    		db.deleteStatementProtocol(idStudent);
    	} else {
    		db.createTableProtocol(num_of_questions_to_ask);
    	}
    	db.insertDataStudentIntoProtocol(idStudent, Name, Surname, Group);
    }
    
    /**
     * Добавляем информацию об ответе студента в таблицу с протоколом: лексически сравниваем ответ на вопрос из таблицы БД с ответами с ответом студента, если ответ верный в таблицу БД протокол записывается: "true <answer> <timePast>", иначе "false <answer> <timePast>"
     * @param idStudent ИН студента
     * @param idAnswerStud порядковый номер вопроса студента для ответа
     * @param AnswerStud Ответ студента
     * @param n_question Номер вопроса
     * @param TimePast время, потраченное на ответ
     *  */
    private boolean AddInfoToProtocolFile(int idStudent, int idAnswerStudent, int AnswerStud, int n_question, String TimePast) {
    	    try {
    	    	String StudentAnswer = this.db.selectFromQuestions(n_question).var_answers[AnswerStud];
    	    	if (StudentAnswer.equals(this.db.selectFromAnswers(n_question).answer)) {
    	    		this.db.updateAnswerStudentIntoProtocol(idStudent, idAnswerStudent , StudentAnswer, 1, TimePast);
    	    		return true;
    	    	}
    	    	else {
    	    		this.db.updateAnswerStudentIntoProtocol(idStudent, idAnswerStudent , StudentAnswer, 0, TimePast);
    	    		return false;
    	    	}
			} catch (Exception e) {
				System.out.println("The test was dropped!.");
				this.DeleteFileProtocol(idStudent);
				return false;
			}
    	}
    
    /**
     * Очищаем кортеж таблицы "протокол" в случае ошибки программы
     * @param idStud ИН студента
     *  */
    private void DeleteFileProtocol(int idStud){
    	db.deleteStatementProtocol(idStud);
    }
    
	private void getQuestion() {
			String qa = "";
	    	Random rand = new Random();
	    	int num_rows = this.db.countRowsQuestions();
    		while (true) {
    			curr_question = rand.nextInt(num_rows);
    			if (curr_question != 0 && !stud_st.getNQuestions().contains(curr_question))
    				break;
    		}
    		qa = qa + this.db.selectFromQuestions(curr_question).question + "\n";
    		int len_var_ans = this.db.selectFromQuestions(curr_question).var_answers.length;
    		for (int i = 0; i < len_var_ans; i ++) {
    			qa = qa + db.selectFromQuestions(curr_question).var_answers[i] + "\n";
    		}
    		stud_st.getNQuestions().add(curr_question);
    		stud_st.setIdAnswerStudent(stud_st.getIdAnswerStudent() + 1);
    		stud_st.setStartTimeAnswering(System.currentTimeMillis()); //начало отсчета времени ввода ответа
    		out.write(qa);
    		out.flush();
	}
    
    public void runServer(int clientNumber, int num_of_questions) throws IOException {
    	System.out.println("The server was run.");
    	
    	this.numQuestions = num_of_questions;
    	stud_st = new StudentStorage();
    	stud_st.setIdStudent(clientNumber);
		stud_st.initNQuestions();
    	stud_st.setIdAnswerStudent(0);
    	
    	ServerSocket listener = new ServerSocket(1080);
    	System.out.println("Waiting for a client...");
    	socket = listener.accept();//
    	System.out.println("The client is connected.");
    	in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        System.out.println("Go test...");
        this.goTesting();
        System.out.println("The test ended.");
        this.endTesting();
    }
    
    private void endTesting() {
    	out.write(this.db.selectAndOutputFromProtocol(this.stud_st.getIdStudent(), this.numQuestions));
    	out.flush();
    	stud_st = null;
    	this.closeServer();
    }
    
    private void closeServer() {
    	this.db.endConnection();
    	try {
			this.socket.close();
			this.in.close();
			this.out.close();
		} catch (IOException e) {
			System.out.println("Error: Couldn't disconnect from server. Something going wrong!");
			e.printStackTrace();
		}
        System.out.println("The server was closed.");
    }

}
