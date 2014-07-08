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
