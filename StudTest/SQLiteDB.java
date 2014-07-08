import java.sql.*;

/**
 * Класс БД для работы с SQLite
 * @author Misha Yaskov
 *
 */
public class SQLiteDB {
	/**
	 *  Имя БД
	 */
	private String DBName;
	/**
	 *  Объект - соединение с БД
	 */
	private Connection c;
	
	/**
	 * Подключаемся к БД, создаем таблицы ответов и вопросов
	 * @param db_name имя БД
	 * @param is_exists существует ли БД
	 */
	public SQLiteDB(String db_name, boolean is_exists) {
		if (!is_exists) {
			this.DBName = db_name;
			this.getConnection();
			this.createTableQuestions();//вопросы с 4 ответами
			this.createTableAnswers();
		} else {
			this.DBName = db_name;
			this.getConnection();	
		}
	}
	
	/**
	 * Создание подключения к БД
	 */
	private void getConnection() {
	    c = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + this.DBName);
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Opened database successfully");//d
	  }
	
	/**
	 * Отсоединение от БД
	 */
	public void endConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Создание таблицы с вопросами и 4-мя ответами
	 */
	private void createTableQuestions() {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String sql = "CREATE TABLE QUESTIONS " +
	                   "(QID INT PRIMARY KEY     NOT NULL," +
	                   " QNAME           TEXT    NOT NULL, " +
	                   " QANSWER0         TEXT    NOT NULL, " +
	                   " QANSWER1         TEXT    NOT NULL, " +
	                   " QANSWER2         TEXT    NOT NULL, " +
	                   " QANSWER3         TEXT    NOT NULL)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Table questions created successfully");//d
	  }

	/**
	 * Создание таблицы с ответами
	 */
	private void createTableAnswers() {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String sql = "CREATE TABLE ANSWERS " +
	                   "(AID INT PRIMARY KEY     NOT NULL," +
	                   " ANAME           TEXT    NOT NULL)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Table answers created successfully");//d
	  }
	
	/**
	 * Создание таблицы с протоколом
	 * @param num_of_questions количество вопросов 
	 */
	public void createTableProtocol(int num_of_questions) {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String str = "";
	      for (int i = 0; i < num_of_questions; i++) {
	    	  str = str + " PANSWER" + i +  " TEXT," +
	    				  " PISCORRECT" + i + " TEXT," +
	    				  " PPASTTIME" + i + " TEXT,";
	      }
	      String sql = "CREATE TABLE PROTOCOL " +
	                   "(PID INT PRIMARY KEY     NOT NULL," +
	                   " PNAME         TEXT      NOT NULL," +
	                   " PSURNAME	   TEXT	     NOT NULL," +
	                   " PGROUP        CHAR(20)	 NOT NULL," + str +
	                   " PMARK         INT)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Table protocol created successfully");//d
	  }
	
	/**
	 * Добавление информации в таблицу с вопросами
	 * @param tq объект с вопросами и ответами на них
	 */
	public void insertOperation(TestQuestion tq) {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String str = "";
	      for (int i = 0; i < tq.var_answers.length; i++) {
	    	  if (i != tq.var_answers.length - 1)
	    		  str = str + " '" + tq.var_answers[i] + "',";
	    	  else
	    		  str = str + " '" + tq.var_answers[i] + "'"; 
	      }
	      String sql = "INSERT INTO QUESTIONS (QID,QNAME,QANSWER0,QANSWER1,QANSWER2,QANSWER3) " +
	                   "VALUES (" + tq.n_question + ", '" + tq.question + "', " + str + ")"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Records question" + tq.question + "created successfully");//d
	  }

	/**
	 * Добавление информации в таблицу с ответами
	 * @param tа объект с ответами
	 */
	public void insertOperation(TestAnswer ta) {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String sql = "INSERT INTO ANSWERS (AID,ANAME) " +
	                   "VALUES (" + ta.n_answer + ", '" + ta.answer + "')"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Records answer" + ta.answer + "created successfully");//d
	  }

	/**
	 * Добавление данных о студенте в таблицу с протоколом
	 * @param idStudent ИН студента
	 * @param Name имя студента
	 * @param Surname фамилия студента 
	 * @param Group группа студента
	 */
	public void insertDataStudentIntoProtocol(int idStudent, String Name, String Surname, String Group) {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String sql = "INSERT INTO PROTOCOL (PID,PNAME,PSURNAME,PGROUP) " +
	                   "VALUES (" + Integer.toString(idStudent) + ", '" + Name + "', '" + Surname + "', '" + Group + "')"; 
	      stmt.executeUpdate(sql);

	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Records data successfully");//d
	  }

	/**
	 * Добавляем данные об ответе студента на вопрос
	 * @param idStudent ИН студента
	 * @param IdAnswer порядковый номер вопроса 
	 * @param Answer ответ студента
	 * @param isCorrect правильный ли ответ студента
	 * @param TimePast время, потраченное на ответ студента
	 */
	public void updateAnswerStudentIntoProtocol(int idStudent ,int IdAnswer, String Answer, int isCorrect, String TimePast) {
		Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String id_ans = Integer.toString(IdAnswer);
	      String true_false;
	      if (isCorrect == 1) {
	    	  true_false = "true";
	      } else {
	    	  true_false = "false";
	      }
	      String sql = "UPDATE PROTOCOL set PANSWER" + id_ans + "='" + Answer + "', " +
	    		  	   "PISCORRECT" + id_ans + "='" + true_false + "', "+
	    		  	   "PPASTTIME" + id_ans + "='" + TimePast + "' where PID=" + Integer.toString(idStudent) +";";
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Update answer done successfully");
	  }
    	  
	/**
	 * Добавление оценки студента в таблицу
	 * @param idStudent ИН студента
	 * @param mark текущая оценка студента
	 */
	public void updateStudentMarkIntoProtocol(int idStudent ,int mark) {
		Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String sql = "UPDATE PROTOCOL set PMARK=" + Integer.toString(mark) + " where PID=" + Integer.toString(idStudent) +";";
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Update mark " + Integer.toString(mark) + " done successfully!!!!!!!!!!!!!!!!!!");
	}
	
	/**
	 * Удаление из талицы протокола информации о студенте
	 * @param idStud ИН студента
	 */
	public void deleteStatementProtocol(int idStud) {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      String sql = "DELETE from PROTOCOL where PID=" + Integer.toString(idStud) + ";";
	      stmt.executeUpdate(sql);
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Deleting table in protocol done successfully");
	  }
	
	/**
	 * Выборка всех данных о вопросе
	 * @param idQuestion номер вопроса
	 * @return объект типа TestQuestion с вопросами и ответами на них
	 */
	public TestQuestion selectFromQuestions(int idQuestion ) {
		TestQuestion tq = new TestQuestion();
		tq.var_answers = new String[4];
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM QUESTIONS WHERE QID=" + Integer.toString(idQuestion) + ";" );
	    	 tq.n_question = idQuestion;
	    	 tq.question = rs.getString("QNAME");
	    	 tq.var_answers[0] = rs.getString("QANSWER0");
	    	 tq.var_answers[1] = rs.getString("QANSWER1");
	    	 tq.var_answers[2] = rs.getString("QANSWER2");
	    	 tq.var_answers[3] = rs.getString("QANSWER3");
	      rs.close();
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Selection question " + Integer.toString(idQuestion) + " done successfully");
	    return tq;
	  }

	/**
	 * Выборка всех данных об ответе
	 * @param idAnswer номер вопроса
	 * @return объект типа TestAnswer с вопросами и ответами на них
	 */
	public TestAnswer selectFromAnswers(int idAnswer) {
		TestAnswer ta = new TestAnswer();
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM ANSWERS WHERE AID=" + Integer.toString(idAnswer) + ";" );
	    	 ta.n_answer = idAnswer;
	    	 ta.answer = rs.getString("ANAME");
	      rs.close();
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Selection answer " + Integer.toString(idAnswer) + "  done successfully");
	    return ta;
	  }

	/**
	 * Выбираем и выводим все данные о студенте и его ответах, а также оценку из таблицы с протоколом
	 * @param idStudent ИН студента
	 * @param num_of_questions количество вопросов
	 */
	public String selectAndOutputFromProtocol(int idStudent, int num_of_questions) {
		String results = "";
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM PROTOCOL WHERE PID=" + Integer.toString(idStudent) + ";" );
	          results = results + "Name : " + rs.getString("PNAME") ;
	          results = results + "\nSurname : " + rs.getString("PSURNAME") ;
	          results = results + "\nGroup : " + rs.getString("PGROUP") ;
	          for (int i = 0; i < num_of_questions; i++) {
	        	  results = results + "\n" + rs.getString("PANSWER" + i) + "\t" + rs.getString("PISCORRECT" + i) + "\t" + rs.getString("PPASTTIME" + i);
	          }
	          results = results +  "\nResult mark = " + rs.getInt("PMARK");
	      rs.close();
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return results;
	    //System.out.println("Output done successfully");
	  }
	
	/**
	 * Получение количества строк в таблице с вопросами
	 * @return количество строк в таблице с вопросами
	 */
	public int countRowsQuestions() {
	    Statement stmt = null;
	    int count = 0;
	    try {
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM QUESTIONS;" );
	      while (rs.next()) {
	    	  count++;
	      }
	      rs.close();
	      stmt.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Count rows table questions done successfully");
	    return count;
	  }
	
	public boolean isExistsProtocol(int idStudent) {
	    Statement stmt = null;
	    try {
	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM PROTOCOL WHERE PID=" + Integer.toString(idStudent) + ";" );
	      while (rs.next()) {
	    	  rs.close();
		      stmt.close();
	    	  return true;
	      }
	      rs.close();
	      stmt.close();
	    } catch ( Exception e ) {
	      return false;
	    }
	    //System.out.println("Count rows table questions done successfully");
	    return false;
	  }
	
	/**
	 * Получем имя БД
	 * @return имя БД
	 */
	public String getNameDB() {
		return this.DBName;
	}
	
}
