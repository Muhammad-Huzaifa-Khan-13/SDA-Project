package frontend;
import backend.controllers.CourseController;
import backend.controllers.QuizController;
import backend.controllers.QuestionController;
import backend.controllers.AuthController;
import backend.controllers.AttemptController;
import backend.controllers.EvaluationController;
import backend.models.Grade;
import backend.services.EvaluationService;

import backend.enums.Role;
import backend.models.User;
import backend.models.Course;
import backend.models.Quiz;
import backend.models.Question;
import backend.models.Attempt;


import java.util.List;

public class ConsoleTest {

    public static void main(String[] args) {

        AuthController auth = new AuthController();
        CourseController courseCtrl = new CourseController();
        QuizController quizCtrl = new QuizController();
        QuestionController questionCtrl = new QuestionController();
        AttemptController attemptCtrl = new AttemptController();
        EvaluationController evalCtrl = new EvaluationController();

        System.out.println("==== SDA Project Console Tester ====\n");

        // 1) REGISTER TEACHER
        System.out.println("Registering Teacher...");
        boolean teacherOk = auth.register("Teacher Ali", "ali@fast.edu", "1234", Role.TEACHER);
        System.out.println("Teacher Registered: " + teacherOk);

        // 2) LOGIN TEACHER
        System.out.println("\nLogin Teacher...");
        auth.register("Teacher Ali", "ali@fast.edu", "1234", Role.TEACHER);
        User teacher = auth.login("ali@fast.edu", "1234");

        System.out.println("Teacher Registered: " + (teacher != null ? teacher.getName() : "FAILED"));

        int teacherId = teacher.getUserId();

        // 3) CREATE COURSE
        System.out.println("\nCreating Course...");
        courseCtrl.createCourse("OOP", teacherId);

        List<Course> courses = courseCtrl.getCoursesByTeacher(teacherId);
        System.out.println("Courses Found: " + courses.size());

        int courseId = courses.get(0).getCourseId();
        System.out.println("Course ID = " + courseId);

        // 4) CREATE QUIZ
        System.out.println("\nCreating Quiz...");
        quizCtrl.createQuiz(courseId, "Midterm Quiz", "MCQ");

        List<Quiz> quizzes = quizCtrl.getQuizzesByCourse(courseId);
        int quizId = quizzes.get(0).getQuizId();
        System.out.println("Quiz ID = " + quizId);

        // 5) ADD QUESTION
        System.out.println("\nAdding Question...");
        Question q = new Question(0, quizId,
                "2 + 2 = ?",
                "2", "4", "6", "8",
                "B");

        questionCtrl.addQuestion(q);

        List<Question> questions = questionCtrl.getQuestionsByQuiz(quizId);
        System.out.println("Questions in Quiz: " + questions.size());

        int questionId = questions.get(0).getQuestionId();
        System.out.println("Question ID = " + questionId);

        // 6) REGISTER STUDENT
        System.out.println("\nRegistering Student...");
        auth.register("Student Musab", "musab@gmail.com", "111", Role.STUDENT);

        System.out.println("Login Student...");
        User student = auth.login("musab@gmail.com", "111");
        int studentId = student.getUserId();
        System.out.println("Student Logged In: " + student.getName());

        // 7) START ATTEMPT
        System.out.println("\nStarting Attempt...");
        Attempt attempt = attemptCtrl.startAttempt(studentId, quizId);
        int attemptId = attempt.getAttemptId();
        System.out.println("Attempt ID = " + attemptId);

        // 8) SUBMIT ANSWER — correct = "B"
        System.out.println("\nSubmitting Answer...");
        EvaluationService evalService = new EvaluationService();
        evalService.submitAnswer(attemptId, questionId, "B");

        // 9) EVALUATE ATTEMPT
        System.out.println("\nEvaluating Attempt...");
        Grade g = evalCtrl.evaluateAttempt(attemptId);
        System.out.println("Score = " + g.getScore());

        // 10) FETCH FINAL GRADE
        System.out.println("\nFetching Final Grade...");
        Grade finalGrade = evalCtrl.getGradeForAttempt(attemptId);

        if (finalGrade != null)
            System.out.println("Final Grade = " + finalGrade.getScore());
        else
            System.out.println("NO GRADE FOUND!");

        System.out.println("\n==== TEST COMPLETE ====");
    }
}
