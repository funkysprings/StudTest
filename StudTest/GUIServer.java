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
        
        this.goTesting();
        //this.endTesting();
    }
    
    private void goTesting() throws IOException {
    	String Name = in.readLine();
    	String Surname = in.readLine();
    	String Group = in.readLine();
    	this.db.insertDataStudentIntoProtocol(this.stud_st.getIdStudent(), Name, Surname, Group);
    	System.out.println("Student info was written.");
    	in.notify();
    }
    
    public void closeServer() {
    	this.db.endConnection();
    	try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Error: Couldn't disconnect from server. Something going wrong!");
			e.printStackTrace();
		}
    }

}
