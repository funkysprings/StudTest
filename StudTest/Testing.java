
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * ����� ��� ������������ ���������
 * @author funkysprings
 */
public class Testing {
	
	/** 
	 * ������ ���� SQLiteDB, �.�. ������ � ���� ��
	 */
    protected SQLiteDB db;
    
    /** 
    *������� ������ ��
    *@param db_name ��� ��
    *@param is_created ���������� �� ��
    *@see Testing#Testing(String, String, String, boolean)
    */
    public Testing(String db_name, boolean is_created) {
    	this.createDB(db_name, is_created);
    }
    
    /** 
     *������� �� �� ����� � ��������� ������� �� ������
     *@param db_name ��� ��
     *@param TestF ���� � ���������  
     *@param TestAns ���� � ��������
     *@param is_created ���������� �� ��
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
     * �������� � ����������� � ��
     * @param db_name ��� ��
     */
    private void createDB(String db_name, boolean is_created) {
    	db = new SQLiteDB(db_name, is_created);
    }

    /**
     *  ������� ����� ���������: ���������� ���������� �����=����� �������, ������� ��������� ������ � ������ ������� �� ����, ��������� � ������� ����� �������� � ��������� ������� � ����������, � � ����� ������� ��� ������ �� ������� ��������� � ������ ��������
     *@param idStudent �� ��������, ������� �������� �� ������
     *@param num_of_questions ���������� ��������� ��������
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
    		String your_answer; 	//����� ������ � ��������� �����
    		int n_answer = 0; 	//����� ������
    		long startTime = System.currentTimeMillis(); //������ ������� ������� ����� ������
    		while (isIncorrectInput) {     		//���� �������� ����
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
    		long PastTime = System.currentTimeMillis() - startTime; //����� ������� ������� ����� ������
    		String timePast = Double.toString(PastTime/1000.0) + "s";
    		//��������� ������ ������ �������� �� ������
    		if (AddInfoToProtocolFile(idStudent, idAnswerStudent, n_answer, curr_r, timePast)) {
    			student_mark++;
    			db.updateStudentMarkIntoProtocol(idStudent, student_mark);
    		}
    		n_question.add(curr_r);
    		num_of_questions--;
    		idAnswerStudent++;
    	}
    	num_of_questions = idAnswerStudent; //�������� ���������� ��������
    	EndTest(idStudent, num_of_questions);
    }
    
    /** 
     * �����-��������� �����: �������� � ������� ��� ������ �� ������� ��������� � ������������� ������
     *@paran idStudent �� ��������
     *@param num_of_questions ���������� ��������
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
     * �������� ������ ����� � ��������: ��������� ���� � ��������, ������� ������ TestAnswers � ��������� � ���� ���������� ��� ����� ������(�������) � ��� �����, ���������� ������ TestAnswers � ������� �� � ��������
     * @param TestAnswers ���� � ��������
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
     * �������� ������ ����� � ���������: ��������� ���� � ��������, ������� ������ TestQuestion � ��������� � ���� ���������� ��� ����� �������, ������ � �������� ������� �� ������, ���������� ������ TestQuestion � ������� �� � ���������
     * @param TestFile ���� � ���������
     *  */
    private void ParseTestFile(File TestFile) {
    	try {
			BufferedReader br = new BufferedReader(new FileReader(TestFile));
	            //� ����� ��������� ��������� ����
	            String s;
	            int n = 1;//����� �������
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
     * �������� ������ ������ ������ �� ����� � ���������, �������� � � ������� �� � ��������� ����� �������, ��� ������ � �������� ������� 
     * @param input_str ������� ������
     * @param n ����� �������
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
    	n = i;	//���������� ������ �������� ����� ������� "("
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
     * ������� ������� �������� � ��������� ������ � �������� � ������� ��������
     * @param idStudent �� ��������
     * @param Name ��� ��������
     * @param Surname ������� ��������
     * @param Group ������ ��������
     * @param num_of_questions_to_ask ���������� �������� ��� ������
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
     * ��������� ���������� �� ������ �������� � ������� � ����������: ���������� ���������� ����� �� ������ �� ������� �� � �������� � ������� ��������, ���� ����� ������ � ������� �� �������� ������������: "true <answer> <timePast>", ����� "false <answer> <timePast>"
     * @param idStudent �� ��������
     * @param idAnswerStud ���������� ����� ������� �������� ��� ������
     * @param AnswerStud ����� ��������
     * @param n_question ����� �������
     * @param TimePast �����, ����������� �� �����
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
     * ������� ������ ������� "��������" � ������ ������ ���������
     * @param idStud �� ��������
     *  */
    private void DeleteFileProtocol(int idStud){
    	db.deleteStatementProtocol(idStud);
    }
    
}