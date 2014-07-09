import java.util.Random;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс для создания сервера, запись данных в БД, обработки данных из БД и взаимодействия с клиентом(граф. интерфейс)
 * @author funkysprings
 *
 */
public class GUIServer extends Thread {
	/**
	 * База данных для обработки данных
	 */
	private SQLiteDB db;
	/**
	 * Сокет для взаимодействия с клиентом
	 */
	private Socket socket;
	/**
	 * Поток для получения данных от клиента
	 */
	private BufferedReader in;
	/**
	 * Поток для отправки данных клиенту
	 */
	private PrintWriter out;
	/**
	 * Информация о текущей успеваемости студента при проведении тестирования
	 */
	private StudentStorage stud_st;
	/**
	 * Количество задаваемых вопросов
	 */
	private int numQuestions;
	/**
	 * Текущий номер вопроса
	 */
	private int curr_question;//??
	
	/**
	 * Подключение к БД, чтение из файлов и запись в БД
	 * @param clientNumber ИН клиента
	 * @param num_of_questions_to_ask Количество задаваемых вопросов
	 */
 	public GUIServer(int clientNumber ,int num_of_questions_to_ask) {
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
    	this.numQuestions = num_of_questions_to_ask;
    	stud_st = new StudentStorage();
    	stud_st.setIdStudent(clientNumber);
 		System.out.println("The server set up successfully.");
 	}
 	
    /**
     * Создание и подключение к БД
     * @param db_name имя БД
     * @param is_created создана ли уже БД
     */
    private void createDB(String db_name, boolean is_created) {
    	db = new SQLiteDB(db_name, is_created);
    }

    /**
     * Метод для запуска потока, запуск сервера
     */
    @Override
    public void run() {
    	try {
			this.runServer();
		} catch (IOException e) {
			System.out.println("Couldnt run server: " + e.getMessage());
		}
    }
    
    /**
     * Проводим анализ файла с ответами: считываем файл с ответами, создаем объект TestAnswers и добавляем в него информацию про номер ответа(вопроса) и сам ответ,
     *  записываем объект TestAnswers в таблицу БД с ответами
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
     * Проводим анализ файла с вопросами: считываем файл с ответами, создаем объект TestQuestion и добавляем в него информацию про номер вопроса,
     *  вопрос и варианты ответов на вопрос, записываем объект TestQuestion в таблицу БД с вопросами
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
    
    /**
     * Проведени тестирования: получаем от клиента имя, фамилию и группу студента, запись этих данных в БД.
     * Пока еще есть вопросы: отправляем клиенту сообщение о том, что вопросы еще есть, получаем вопрос и варианты ответов -> отправляем клиенту.
     * Ждем от клиента номер ответа на вопрос, считаем время на ответ и добавляем в БД
     * @throws IOException
     */
    private void goTesting() throws IOException {
    	String Name = in.readLine();
    	String Surname = in.readLine();
    	String Group = in.readLine();
    	this.AddToProtocolFile(this.stud_st.getIdStudent(), Name, Surname, Group, this.numQuestions);
    	this.stud_st.setStudentMark(0);
    	this.db.updateStudentMarkIntoProtocol(this.stud_st.getIdStudent(), this.stud_st.getStudentMark());
    	
    	int n_answer; //ответ студента
    	while (this.stud_st.getIdAnswerStudent() < this.numQuestions) { //пока определенное количество вопросов не было задано
    		out.write(1);
        	out.flush();
    		this.getQuestion(); //отправляем вопрос и варианты ответов клиенту
        	n_answer = in.read(); //ждем ответ от студента на вопрос
        	long PastTime = System.currentTimeMillis() - this.stud_st.getStartTimeAnswering(); //конец отсчета времени ввода ответа
        	String timePast = Double.toString(PastTime/1000.0) + "s";
        	//добавляем данные ответа студента на вопрос
        	if (this.AddInfoToProtocolFile(this.stud_st.getIdStudent(), this.stud_st.getIdAnswerStudent(), n_answer, curr_question, timePast)) {
        		this.stud_st.setStudentMark(stud_st.getStudentMark() + 1);;
        		this.db.updateStudentMarkIntoProtocol(this.stud_st.getIdStudent(), this.stud_st.getStudentMark());
        	}
    		stud_st.setIdAnswerStudent(stud_st.getIdAnswerStudent() + 1);
    	}
    }
    
    /**
     * Создаем таблицу протокол и добавляем данные о студенте в таблицу протокол
     * @param idStudent ИН студента
     * @param Name Имя студента
     * @param Surname Фамилия студента
     * @param Group Группа студента
     * @param num_of_questions_to_ask количество вопросов для ответа
     *  */
    private void AddToProtocolFile(int idStudent, String Name, String Surname, String Group, int num_of_questions_to_ask){
    	if (db.isExistsProtocol(idStudent)) {
    		db.deleteStatementProtocol(idStudent);
    	} else {
    		db.createTableProtocol(num_of_questions_to_ask);
    	}
    	db.insertDataStudentIntoProtocol(idStudent, Name, Surname, Group);
    }
    
    /**
     * Добавляем информацию об ответе студента в таблицу с протоколом:
     *  лексически сравниваем ответ на вопрос из таблицы БД с ответами с ответом студента,
     *  и если ответ верный в таблицу БД протокол записывается: "true <answer> <timePast>", иначе "false <answer> <timePast>"
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
    
    /**
     * Получаем вопрос и варианты ответов на него.
     * Отправляем вопрос и ответы клиенту.
     */
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
    		stud_st.setStartTimeAnswering(System.currentTimeMillis()); //начало отсчета времени ввода ответа
    		out.write(qa);
    		out.flush();
	}
    
	/**
	 * Запуск сервера: создаем подключение, ждем подключения.
	 * Старт тестирование и окончание тестирования.
	 * @throws IOException
	 */
    public void runServer() throws IOException {
    	System.out.println("The server was run.");
    	
		stud_st.initNQuestions();
    	stud_st.setIdAnswerStudent(0);
    	
    	ServerSocket listener = new ServerSocket(1080);
    	System.out.println("Waiting for a client...");
    	socket = listener.accept();//
    	listener.close();
    	System.out.println("The client is connected.");
    	in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        System.out.println("Go test...");
        this.goTesting();
        System.out.println("The test ended.");
        this.endTesting();
    }
    
    /**
     * Окончание тестирования: отправляем клиенту результаты теста и отключаемся от сервера.
     */
    private void endTesting() {
    	out.write(this.db.selectAndOutputFromProtocol(this.stud_st.getIdStudent(), this.numQuestions));
    	out.flush();
    	stud_st = null;
    	this.closeServer();
    }
    
    /**
     * Завершение работы с сервером и БД.
     */
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
