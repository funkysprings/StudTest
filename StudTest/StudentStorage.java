import java.util.ArrayList;

/**
 * Класс для хранения информации об ответах студента на вопросы и т.п.
 * @author funkysprings
 *
 */
public class StudentStorage {
	private int idStudent; //ИН студента
	private int student_mark; //оценка студента
	private int idAnswerStudent; //количество уже заданных вопросов
	private ArrayList<Integer> n_question; //список уже заданных вопросов
	private long startTime; //начало отсчета времени от задания вопроса
	
	/**
	 * Задать ИН студента
	 * @param IdStudent ИН студента
	 */
	public void setIdStudent(int IdStudent) {
			this.idStudent = IdStudent;
	}
	
	/**
	 * Задать оценку студента
	 * @param StudentMark оценка студента
	 */
	public void setStudentMark(int StudentMark) {
		this.student_mark = StudentMark;
	}
	
	/**
	 * Задать порядковый номер ответа студента
	 * @param IdAnswerStudent порядковый номер ответа студента
	 */
	public void setIdAnswerStudent(int IdAnswerStudent) {
		this.idAnswerStudent = IdAnswerStudent;
	}
	
	/**
	 * Инициализировать поле "уже заданные вопросы"
	 */
	public void initNQuestions() {
		n_question = new ArrayList<Integer>();
	}

	/**
	 * Задать начало отсчета времени от задания вопроса 
	 * @param StartTime начальное время
	 */
	public void setStartTimeAnswering(long StartTime) {
		this.startTime = StartTime;
	}
	
	/**
	 * Получить ИН студента
	 * @return ИН студента
	 */
	public int getIdStudent() {
		try {
			return this.idStudent;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * Получить оценку студента
	 * @return Оценка студента
	 */
	public int getStudentMark() {
		try {
			return this.student_mark;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * Получить порядковый номер ответа на вопрос студента
	 * @return Порядковый номер ответа на вопрос студента
	 */
	public int getIdAnswerStudent() {
		try {
			return this.idAnswerStudent;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * Получить список уже заданых вопросов
	 * @return Список уже заданых вопросов
	 */
	public ArrayList<Integer> getNQuestions() {
		try {
			return this.n_question;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
	
	/**
	 * Получить начало отсчета времени ответа студента на вопрос
	 * @return Начало отсчета времени ответа студента на вопрос
	 */
	public long getStartTimeAnswering() {
		try {
			return this.startTime;
		} catch(Exception e) {
			throw new NullPointerException(e.getMessage());
		}
	}
}
