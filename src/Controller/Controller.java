package Controller;

import Model.Catalog;
import Service.GradeService;
import Service.HomeworkService;
import Service.StudentService;
import Utils.MyData;
import Validation.GradeValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class Controller {

    private Catalog catalog = new Catalog();
    static StudentService studentService= new StudentService();
    static HomeworkService homeworkService= new HomeworkService(MyData.currentWeek);
    static GradeService gradeService= new GradeService();

    void studentsButtonHandler(Window window){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Students.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Students");
        primaryStage.setScene(new Scene(root, 1920,1013));
        primaryStage.show();
        window.hide();
    }

    public void homeworksButtonHandler(Window window){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Homeworks.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Homeworks");
        primaryStage.setScene(new Scene(root, 1920,1013));
        primaryStage.show();
        window.hide();
    }

    public void gradesButtonHandler(Window window){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Grades.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Grades");
        primaryStage.setScene(new Scene(root, 1920,1013));
        primaryStage.show();
        window.hide();
    }

    public void writeFinalGradesButtonHandler(){
        catalog.writeFinalGradeForAllStudents(gradeService.getFinalGrades());
        Alert message=new Alert(Alert.AlertType.INFORMATION);
        message.setHeaderText("PDF File Generated");
        message.setContentText("FinalGrades.pdf was generated successfully!");
        message.showAndWait();
    }

    public void writeHardestHomeworksButtonHandler(){
        catalog.writeHardestHomeworks(gradeService.getHardestHomeworks());
        Alert message=new Alert(Alert.AlertType.INFORMATION);
        message.setHeaderText("PDF File Generated");
        message.setContentText("HardestHomework.pdf was generated successfully!");
        message.showAndWait();
    }

    public void writePassableStudetsButtonHandler(){
        catalog.writePassableStudents(gradeService.getPassableStudents());
        Alert message=new Alert(Alert.AlertType.INFORMATION);
        message.setHeaderText("PDF File Generated");
        message.setContentText("PassableStudents.pdf was generated successfully!");
        message.showAndWait();
    }

    public void writeTopGroupsButtonHandler(){
        catalog.writeTopGroups(gradeService.getTopGroups());
        Alert message=new Alert(Alert.AlertType.INFORMATION);
        message.setHeaderText("PDF File Generated");
        message.setContentText("TopGroups.pdf was generated successfully!");
        message.showAndWait();
    }

    public void showMessage(Alert.AlertType type, String header, String text) {
        Alert message = new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.showAndWait();
    }

    static void showErrorMessage(String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.showAndWait();
    }
}
