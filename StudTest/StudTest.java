import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author funkysprings
*/public class StudTest {
	/** главный вход в программу
	 * @param args Параметры командной строки -setup: создать пустую БД
	 * 										  -run: провести тест с помощью консолм
	 * 										  -gui: провести тест с помощью граф. интерфейса
	 * 										  -guics: провести тест на основе "клиент-сервер"*/
    public static void main(String[] args) {
    	Testing test;
    	String db_name = "test.db";
    	File file = new File(db_name);
    	boolean is_exist_db = false;
    	switch (args[0]) {
    		case "-setup":
    			if (!file.exists()) {
    				test = new Testing(db_name, is_exist_db);
    				System.out.println("Empty test database created successfully!");
    			} else {
    				System.out.println("File " + db_name + " is already exist.");
    			}
    			break;
    		case "-run":
    			if (!file.exists()) {
    				test = new Testing(db_name,"Test","TestAnswers", is_exist_db);// создаем объект Testing с входными файлами-вопросами и файлом-ответом
    			} else {
    				is_exist_db = true;
    				test = new Testing(db_name,"Test","TestAnswers", is_exist_db);
    			}
    			StartTesting(test);
    			break;
    		case "-gui":
    			if (!file.exists()) {
    				test = new Testing(db_name,"Test","TestAnswers", is_exist_db);// создаем объект Testing с входными файлами-вопросами и файлом-ответом
    			} else {
    				is_exist_db = true;
    				test = new Testing(db_name,"Test","TestAnswers", is_exist_db);
    			}
    			StartGuiTesting(test);
    		case "-guics":
    			GUIServer server = new GUIServer(1, 5);
    			server.start();
    			GUIClient client = new GUIClient();
    			break;
    		default: System.out.println("Invalid argument(-s) of a command line!");
    	}
    }
    
    /**
     * Начало тестирования с использованием граф. интерфейса.
     * @param test Объект типа Testing
     */
    private static void StartGuiTesting(Testing test) {
    	GUITesting gui_test = new GUITesting(test.db.getNameDB(), true);
    	gui_test.StartGUITestStudent(1, 5);//StartGUITestStudent(<ИН студента>, <количество вопросов для студента>)
    }
    
    /**
     * Начало тестирования: вводим имя, фамилию и группу, также задаем количество вопросов для студента, добавляем данные о студенте в таблицу "протокол" и начинаем тест.
     * @param test Объект типа Testing
     */
    private static void StartTesting(Testing test) {
    	Scanner input = new Scanner(System.in);
        
        System.out.println("---Please enter your name, sirname and group.");
        System.out.print("->Name: ");
        String name = input.nextLine();//читаем строку с входного потока и записываем в строку
        System.out.print("->Surname: ");
        String sirname = input.nextLine();//читаем строку с входного потока и записываем в строку
        System.out.print("->Group: ");
        String group = input.nextLine();//читаем строку с входного потока и записываем в строку
        
        int num_of_questions_to_ask = 5;
        try {
			test.AddToProtocolFile(1, name, sirname, group, num_of_questions_to_ask);//добавляем имя, фамилию и группу в файл-протокол
			System.out.println("---Please answer on questions below:");
	        test.TestStudent(1, num_of_questions_to_ask);//проводится тестирование из 5 вопросов
	        input.close();
        } catch (Exception e) {
			input.close();
			System.out.println("An error occured!");
			e.printStackTrace();
		}
    }
}
