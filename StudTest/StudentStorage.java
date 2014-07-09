import java.util.ArrayList;

/**
 * ����� ��� �������� ���������� �� ������� �������� �� ������� � �.�.
 * @author funkysprings
 *
 */
public class StudentStorage {
	private int idStudent; //�� ��������
	private int student_mark; //������ ��������
	private int idAnswerStudent; //���������� ��� �������� ��������
	private ArrayList<Integer> n_question; //������ ��� �������� ��������
	private long startTime; //������ ������� ������� �� ������� �������
	
	/**
	 * ������ �� ��������
	 * @param IdStudent �� ��������
	 */
	public void setIdStudent(int IdStudent) {
			this.idStudent = IdStudent;
	}
	
	/**
	 * ������ ������ ��������
	 * @param StudentMark ������ ��������
	 */
	public void setStudentMark(int StudentMark) {
		this.student_mark = StudentMark;
	}
	
	/**
	 * ������ ���������� ����� ������ ��������
	 * @param IdAnswerStudent ���������� ����� ������ ��������
	 */
	public void setIdAnswerStudent(int IdAnswerStudent) {
		this.idAnswerStudent = IdAnswerStudent;
	}
	
	/**
	 * ���������������� ���� "��� �������� �������"
	 */
	public void initNQuestions() {
		n_question = new ArrayList<Integer>();
	}

	/**
	 * ������ ������ ������� ������� �� ������� ������� 
	 * @param StartTime ��������� �����
	 */
	public void setStartTimeAnswering(long StartTime) {
		this.startTime = StartTime;
	}
	
	/**
	 * �������� �� ��������
	 * @return �� ��������
	 */
	public int getIdStudent() {
		try {
			return this.idStudent;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * �������� ������ ��������
	 * @return ������ ��������
	 */
	public int getStudentMark() {
		try {
			return this.student_mark;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * �������� ���������� ����� ������ �� ������ ��������
	 * @return ���������� ����� ������ �� ������ ��������
	 */
	public int getIdAnswerStudent() {
		try {
			return this.idAnswerStudent;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * �������� ������ ��� ������� ��������
	 * @return ������ ��� ������� ��������
	 */
	public ArrayList<Integer> getNQuestions() {
		try {
			return this.n_question;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * �������� ������ ������� ������� ������ �������� �� ������
	 * @return ������ ������� ������� ������ �������� �� ������
	 */
	public long getStartTimeAnswering() {
		try {
			return this.startTime;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
}
