import java.util.ArrayList;

public class StudentStorage {
	private int idStudent;
	private int student_mark;
	private int idAnswerStudent; //количество уже заданных вопросов
	private ArrayList<Integer> n_question; //список уже заданных вопросов
	private long startTime;
	
	public void setIdStudent(int IdStudent) {
		this.idStudent = IdStudent;
	}
	
	public void setStudentMark(int StudentMark) {
		this.student_mark = StudentMark;
	}
	
	public void setIdAnswerStudent(int IdAnswerStudent) {
		this.idAnswerStudent = IdAnswerStudent;
	}
	
	public void initNQuestions() {
		n_question = new ArrayList<Integer>();
	}

	public void setStartTimeAnswering(long StartTime) {
		this.startTime = StartTime;
	}
	
	public int getIdStudent() {
		try {
			return this.idStudent;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	public int getStudentMark() {
		try {
			return this.student_mark;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	public int getIdAnswerStudent() {
		try {
			return this.idAnswerStudent;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	public ArrayList<Integer> getNQuestions() {
		try {
			return this.n_question;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	public long getStartTimeAnswering() {
		try {
			return this.startTime;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
}
