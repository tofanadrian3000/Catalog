package sample;

import Model.Student;
import Service.StudentService;
import Utils.MyData;
import Validation.StudentValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyData.initialize();
        //MyData.currentWeek=3;

        Parent root = FXMLLoader.load(getClass().getResource("../Grades.fxml"));
        primaryStage.setTitle("Grades");
        primaryStage.setScene(new Scene(root, 1980,1013));
        primaryStage.show();

    }

    public static void main(String[] args) { launch(args);}

}