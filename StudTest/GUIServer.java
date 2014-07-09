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
 		File file = new File("test.db"); //���� ��
 		String db_name = file.getName();
 		String testFile = "Test"; //����-���� � ��������
 		String test_answerFile = "TestAnswers"; ////����-���� � �������� �� �������
 		
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
    
    private void goTesting() throws IOException {
    	String Name = in.readLine();
    	String Surname = in.readLine();
    	String Group = in.readLine();
    	this.AddToProtocolFile(this.stud_st.getIdStudent(), Name, Surname, Group, this.numQuestions);
    	this.stud_st.setStudentMark(0);
    	this.db.updateStudentMarkIntoProtocol(this.stud_st.getIdStudent(), this.stud_st.getStudentMark());
    	
    	char[] cbuf = new char[3]; //��������� ����� ��� 3 int
    	int n_answer; //����� ��������
    	while (this.stud_st.getIdAnswerStudent() != this.numQuestions) { //���� ������������ ���������� �������� �� ���� ������
        	this.getQuestion();
        	n_answer = in.read(cbuf); //���� ������ �� �������� �� ������
        	long PastTime = System.currentTimeMillis() - this.stud_st.getStartTimeAnswering(); //����� ������� ������� ����� ������
        	String timePast = Double.toString(PastTime/1000.0) + "s";
        	//��������� ������ ������ �������� �� ������
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
     * ��������� ���������� �� ������ �������� � ������� � ����������: ���������� ���������� ����� �� ������ �� ������� �� � �������� � ������� ��������, ���� ����� ������ � ������� �� �������� ������������: "true <answer> <timePast>", ����� "false <answer> <timePast>"
     * @param idStudent �� ��������
     * @param idAnswerStud ���������� ����� ������� �������� ��� ������
     * @param AnswerStud ����� ��������
     * @param n_question ����� �������
     * @param TimePast �����, ����������� �� �����
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
     * ������� ������ ������� "��������" � ������ ������ ���������
     * @param idStud �� ��������
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
    		stud_st.setStartTimeAnswering(System.currentTimeMillis()); //������ ������� ������� ����� ������
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
        
        this.goTesting();
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
    }

}
