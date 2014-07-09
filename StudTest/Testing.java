
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Класс для тестирование студентов
 * @author funkysprings
 */
public class Testing {
	
	/** 
	 * Объект типа SQLiteDB, т.е. методы и сама БД
	 */
    protected SQLiteDB db;
    
    /** 
    *Создаем пустую БД
    *@param db_name имя БД
    *@param is_created существует ли БД
    *@see Testing#Testing(String, String, String, boolean)
    */
    public Testing(String db_name, boolean is_created) {
    	this.createDB(db_name, is_created);
    }
    
    /** 
     *Создаем БД по имени и заполняем данными из файлов
     *@param db_name имя БД
     *@param TestF файл с вопросами  
     *@param TestAns файл с ответами
     *@param is_created существует ли БД
     *@see Testing#Testing(String, boolean)
     */
    public Testing(String db_name, String TestF, String TestAns, boolean is_created) {
    	this.createDB(db_name, is_created);
    	if (!is_created) {
    		File TestFile = new File(TestF);
    		ParseTestFile(TestFile);
    		File TestAnswers = new File(TestAns);
    		ParseTestAnswers(TestAnswers);
    	}
    }
    
    /**
     * Создание и подключение к БД
     * @param db_name имя БД
     */
    private void createDB(String db_name, boolean is_created) {
    	db = new SQLiteDB(db_name, is_created);
    }

    /**
     *  Главный метод программы: генерируем случаейное число=номер вопроса, выводим случайный вопрос и список ответов на него, считываем с консоли ответ студента и обновляем таблицу с протоколом, а в конце выводим все данные из таблицы протокола и оценку студента
     *@param idStudent ИН студента, который отвечает на вопрос
     *@param num_of_questions Количество случайных вопросов
     *  */
    public void TestStudent(int idStudent, int num_of_questions) {
    	int student_mark = 0;
    	db.updateStudentMarkIntoProtocol(idStudent, student_mark);
    	
    	Random r = new Random();
    	int curr_r, idAnswerStudent = 0;
    	ArrayList<Integer> n_question = new ArrayList<Integer>();
    	int num_rows = db.countRowsQuestions();
    	while (num_of_questions != 0) {
    		while (true) {
    			curr_r = r.nextInt(num_rows);
    			if (curr_r != 0 && !n_question.contains(curr_r))
    				break;
    		}
    		System.out.println("--" + db.selectFromQuestions(curr_r).question);
    		int len_var_ans = db.selectFromQuestions(curr_r).var_answers.length;
    		for (int i = 0; i < len_var_ans; i ++) {
    			System.out.println(i + ")" + db.selectFromQuestions(curr_r).var_answers[i]);
    		}
    		boolean isIncorrectInput = true;
    		Scanner input = new Scanner(System.in);
    		String your_answer; 	//номер ответа в строковой форме
    		int n_answer = 0; 	//номер ответа
    		long startTime = System.currentTimeMillis(); //начало отсчета времени ввода ответа
    		while (isIncorrectInput) {     		//пока неверный ввод
	    		System.out.print("Your answer: ");
	    		try {
	    			your_answer = input.nextLine();
	    			n_answer = Integer.parseInt(your_answer);
		    		if (n_answer >= 0 && n_answer < len_var_ans) {
		    			isIncorrectInput = false;
		    		} else {
		    			System.out.println("!!!Invalid character!");
		    		}
	    		} catch (NumberFormatException e) {
	    			System.out.println("!!!Invalid character!");
	    		}	
    		}
    		long PastTime = System.currentTimeMillis() - startTime; //конец отсчета времени ввода ответа
    		String timePast = Double.toString(PastTime/1000.0) + "s";
    		//добавляем данные ответа студента на вопрос
    		if (AddInfoToProtocolFile(idStudent, idAnswerStudent, n_answer, curr_r, timePast)) {
    			student_mark++;
    			db.updateStudentMarkIntoProtocol(idStudent, student_mark);
    		}
    		n_question.add(curr_r);
    		num_of_questions--;
    		idAnswerStudent++;
    	}
    	num_of_questions = idAnswerStudent; //получаем количество вопросов
    	EndTest(idStudent, num_of_questions);
    }
    
    /** 
     * Метод-окончание теста: выодится в консоль все данные из таблицы протокола и окончательную оценку
     *@paran idStudent ИН студента
     *@param num_of_questions количество вопросов
     * */
    protected void EndTest(int idStudent, int num_of_questions) {
    	System.out.println("==============================Results==============================");
    	try {
    		System.out.println(db.selectAndOutputFromProtocol(idStudent, num_of_questions));
		} catch (Exception e) {
			System.out.println("The test was dropped!.");
			e.printStackTrace();
		}
    	db.endConnection();
    	System.out.println("===================================================================");
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
    
    /**
     * Создаем таблицу протокол и добавляем данные о студенте в таблицу протокол
     * @param idStudent ИН студента
     * @param Name Имя студента
     * @param Surname Фамилия студента
     * @param Group Группа студента
     * @param num_of_questions_to_ask количество вопросов для ответа
     * @exception Exception
     *  */
    public void AddToProtocolFile(int idStudent, String Name, String Surname, String Group, int num_of_questions_to_ask) throws Exception {
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
    protected boolean AddInfoToProtocolFile(int idStudent, int idAnswerStudent, int AnswerStud, int n_question, String TimePast) {
    	    try {
    	    	String StudentAnswer = db.selectFromQuestions(n_question).var_answers[AnswerStud];
    	    	if (StudentAnswer.equals(db.selectFromAnswers(n_question).answer)) {
    	    		db.updateAnswerStudentIntoProtocol(idStudent, idAnswerStudent , StudentAnswer, 1, TimePast);
    	    		return true;
    	    	}
    	    	else {
    	    		db.updateAnswerStudentIntoProtocol(idStudent, idAnswerStudent , StudentAnswer, 0, TimePast);
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
    
}