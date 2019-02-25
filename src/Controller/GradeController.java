package Controller;

import DTO.StudentGradeDTO;
import Model.Catalog;
import Model.Grade;
import Model.Homework;
import Model.Student;
import Notification.GradeNotification;
import Utils.Event.GradeChangeEvent;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GradeController extends Controller implements Observer<GradeChangeEvent> {
    private StudentGradeDTO studentGradeDTOSelected;
    private ObservableList<Integer> homeworks = FXCollections.observableArrayList();
    private ObservableList<String> groups = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> gradesPerGroupPieChartList = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> gradesPerHomeworkPieChartList = FXCollections.observableArrayList();
    private Catalog catalog = new Catalog();
    private ObservableList<StudentGradeDTO> modelGrade = FXCollections.observableArrayList();
    private ObservableList<StudentGradeDTO> paginationModel = FXCollections.observableArrayList();
    private Integer itemsPerPage = 10;
    private ArrayList<TableColumn<StudentGradeDTO, String>> tableColumns = new ArrayList<>();
    private Integer lastHomeworkFilteredBy = null;
    private String lastGroupFilteredBy = null;
    private Boolean lastMonthFilteredBy = false;
    @FXML
    private TableView gradeTableView;
    @FXML
    private TableColumn<StudentGradeDTO, String> studentColumn;
    @FXML
    private TextField studentTextField;
    @FXML
    private ComboBox<Integer> homeworkComboBox;
    @FXML
    private ComboBox<String> groupComboBox;
    @FXML
    private TextArea feedbackTextArea;
    @FXML
    private TextField gradeTextField;
    @FXML
    private TextField noExemptionsTextField;
    @FXML
    private PieChart gradesPerGroupPieChart;
    @FXML
    private PieChart gradesPerLastHomeworkPieChart;
    @FXML
    private Button addButton;
    @FXML
    private Button homeworkGradesButton;
    @FXML
    private Button homeworkGroupGradesButton;
    @FXML
    private Button groupGradesButton;
    @FXML
    private Button currenMonthGradesButton;
    @FXML
    private Button resetButton;
    @FXML
    private Pagination pagination;
    @FXML
    private Button homeworksButton;
    @FXML
    private Button studentsButton;
    @FXML
    private Button finalGradesButton;
    @FXML
    private Button hardestHomeworkButton;
    @FXML
    private Button PassableStudentsButton;
    @FXML
    private Button TopGroupsButton;

    @FXML
    void initialize() {
        gradeService.addObserver(this);
        //initializeColumns();
        initializeModels(getArrayListOfDTOS(studentService.getActiveStudents()));
        initializeTextFields();
        initializeReceivedComboBox();
        initializeGradePagination();
        initialisePieChart();
        initializeTableView();
        initColumnsListeners();
    }

    private void initializeTableView() {
        pagination.setPrefWidth(950);
        gradeTableView.getColumns().clear();

        TableColumn<StudentGradeDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<StudentGradeDTO, String>("Name"));
        nameColumn.getStyleClass().add("homeworkTableColumn");
        nameColumn.setPrefWidth(178);
        nameColumn.setMaxWidth(178);
        nameColumn.setMinWidth(178);
        if (tableColumns.size() == 0) {
            List<Homework> homeworks = homeworkService.getHomeworks();
            for (Homework homework : homeworks) {
                TableColumn<StudentGradeDTO, String> column = new TableColumn<>("H" + Integer.toString(homework.getID()));

                column.getStyleClass().add("homeworkTableColumn");
                column.setMaxWidth((950.0 - 180) / homeworks.size());
                column.setMinWidth((950.0 - 180) / homeworks.size());
                column.setCellValueFactory(new PropertyValueFactory<StudentGradeDTO, String>("GradeH" + Integer.toString(homework.getID())));

                tableColumns.add(column);
            }

            Callback<TableColumn<StudentGradeDTO, String>, TableCell<StudentGradeDTO, String>> cellFactory =
                    p -> new EditingCell();
            for (TableColumn<StudentGradeDTO, String> column : tableColumns) {
                column.setCellFactory(cellFactory);
            }
        }
        gradeTableView.getColumns().add(nameColumn);
        gradeTableView.getColumns().addAll(tableColumns);
        gradeTableView.setEditable(true);
        gradeTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.studentGradeDTOSelected = (StudentGradeDTO) newValue;
                });
        initializeGradePagination();
        initColumnsListeners();
    }

    private void initColumnsListeners() {

        for (TableColumn<StudentGradeDTO, String> column : tableColumns) {
            column.setOnEditCommit(
                    t -> onTextCellChange(t.getRowValue().getStudentId(), t.getTablePosition().getColumn(), t.getNewValue())
            );
        }
    }

    private void onTextCellChange(Integer idStudent, Integer idHomework, String grade) {
        Grade foundGrade = gradeService.getGrade(idStudent, idHomework);
        if (lastHomeworkFilteredBy != null || (lastMonthFilteredBy != null && lastHomeworkFilteredBy != null)) {
            setModelByName("");
            return;
        }
        if (foundGrade != null) {
            tableColumns.clear();
            initializeTableView();
            showErrorMessage("You can not change the grade of a student!");
            return;
        }
        try {
            Double doubleGrade = Double.parseDouble(grade);
            if (doubleGrade < 1 || doubleGrade > 10) {
                tableColumns.clear();
                initializeTableView();
                showErrorMessage("Grade must be between 1 and 10!");
            } else displayPopup(idStudent, idHomework, doubleGrade);
        } catch (NumberFormatException e) {
            showErrorMessage("Grade must be between 1 and 10!");
        }

    }

    private void displayPopup(Integer studentId, Integer idHomework, Double gradeGrade) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Grade");
        window.setWidth(300);
        window.setHeight(400);

        ComboBox<Integer> noOfExemptionsComboBox = new ComboBox<>();
        ObservableList<Integer> numbers = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        TextArea feedbackTextArea = new TextArea();
        Button add = new Button("Add");
        Button close = new Button("Cancel");
        HBox hBox = new HBox();
        VBox layout = new VBox();
        Text text = new Text();

        feedbackTextArea.setPromptText("Feedback");
        feedbackTextArea.setPrefWidth(250);
        feedbackTextArea.setMaxWidth(250);
        feedbackTextArea.setPrefHeight(100);
        noOfExemptionsComboBox.setMinWidth(250);
        noOfExemptionsComboBox.setMaxWidth(250);
        noOfExemptionsComboBox.setPromptText("No. of exemptions");
        noOfExemptionsComboBox.setItems(numbers);
        add.setMinWidth(100);

        add.setOnAction(x -> {
            Integer noExemptions = noOfExemptionsComboBox.getValue();
            if (noExemptions == null)
                noExemptions = 0;
            String feedback = feedbackTextArea.getText();
            Grade grade = new Grade(studentId, idHomework, gradeGrade, LocalDate.now(), feedback);

            Double maximumGrade = gradeService.getMaximumGradeFor(grade.getID().getIdHomework(), grade.getGrade(), noExemptions);

            Grade maxGrade = new Grade(grade.getID().getIdStudent(), grade.getID().getIdHomework(), maximumGrade, grade.getData(), grade.getFeedback());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Grade Preview");
            alert.setHeaderText(catalog.writeData(maxGrade, grade, studentService.getStudent(grade.getID().getIdStudent()), noExemptions));
            alert.setContentText("Are you ok with this?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK && grade.getGrade() - maximumGrade != 0) {
                grade.setFeedback(grade.getFeedback() + "\nNota a fost diminuata cu " +
                        String.valueOf(grade.getGrade() - maximumGrade) + " puncte datorita intarzierilor.");
                alert.setHeaderText(catalog.writeData(maxGrade, grade, studentService.getStudent(grade.getID().getIdStudent()), noExemptions));
                alert.setContentText("Are you ok with this?");
                if (gradeService.addGrade(grade.getID().getIdStudent(), grade.getID().getIdHomework(),
                        grade.getGrade(), grade.getFeedback(), noExemptions) == null) {
                    (new GradeNotification(studentService.getStudent(grade.getID().getIdStudent()),
                            grade,homeworkService.getHomework(grade.getID().getIdHomework()))).start();
                    showMessage(Alert.AlertType.INFORMATION, "Confirmation", "Successfully added!");
                } else showErrorMessage("The add has failed!");
            } else if (result.get() == ButtonType.OK && grade.getGrade() - maximumGrade == 0) {
                alert.setHeaderText(catalog.writeData(maxGrade, grade, studentService.getStudent(grade.getID().getIdStudent()), noExemptions));
                alert.setContentText("Are you ok with this?");
                if (gradeService.addGrade(grade.getID().getIdStudent(), grade.getID().getIdHomework(),
                        grade.getGrade(), grade.getFeedback(), noExemptions) == null) {
                    (new GradeNotification(studentService.getStudent(grade.getID().getIdStudent()),
                            grade,homeworkService.getHomework(grade.getID().getIdHomework()))).start();
                    showMessage(Alert.AlertType.INFORMATION, "Confirmation", "Successfully added!");
                } else showErrorMessage("The add has failed");
            }
        });

        close.setMinWidth(100);
        close.setOnAction(x -> {
            window.close();
            initializeModels(getArrayListOfDTOS(studentService.getActiveStudents()));
            initializeTableView();
        });

        hBox.getChildren().addAll(add, close);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        text.setText("Please, complete the feedback\n" +
                "and the number of exemptions!\n" +
                "Student: " + studentService.getStudent(studentId).getName() + "\n" +
                "Homework: " + homeworkService.getHomework(idHomework).getID().toString());
        text.setFont(new Font(18));
        layout.getChildren().addAll(text, noOfExemptionsComboBox, feedbackTextArea, hBox);
        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }

    private void initializeColumns(Integer idHomework, List<StudentGradeDTO> students) {
        pagination.setPrefWidth(950);
        gradeTableView.getColumns().clear();
        tableColumns.clear();
        TableColumn<StudentGradeDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        modelGrade.setAll(students);
        nameColumn.getStyleClass().add("homeworkTableColumn");
        nameColumn.setPrefWidth(550);

        if (tableColumns.size() == 0) {
            TableColumn<StudentGradeDTO, String> column = new TableColumn<>("H" + idHomework.toString());

            column.getStyleClass().add("homeworkTableColumn");
            column.setPrefWidth(400);
            column.setCellValueFactory(new PropertyValueFactory<>("GradeH" + idHomework.toString()));

            tableColumns.add(column);
        }
        Callback<TableColumn<StudentGradeDTO, String>, TableCell<StudentGradeDTO, String>> cellFactory =
                p -> new EditingCell();
        for (TableColumn<StudentGradeDTO, String> column : tableColumns) {
            column.setCellFactory(cellFactory);
        }

        gradeTableView.getColumns().add(nameColumn);
        gradeTableView.getColumns().addAll(tableColumns);
        gradeTableView.setEditable(true);
        initializeGradePagination();
        initColumnsListeners();
    }

    private void initializeColumns(Integer idHomework, String group, ArrayList<StudentGradeDTO> students) {
        pagination.setPrefWidth(950);
        gradeTableView.getColumns().clear();
        tableColumns.clear();
        TableColumn<StudentGradeDTO, String> nameColumn = new TableColumn<>("Group " + group);
        nameColumn.setCellValueFactory(new PropertyValueFactory<StudentGradeDTO, String>("Name"));
        nameColumn.getStyleClass().add("homeworkTableColumn");
        nameColumn.setPrefWidth(540);
        modelGrade.setAll(students);
        if (tableColumns.size() == 0) {
            TableColumn<StudentGradeDTO, String> column = new TableColumn<>("H" + idHomework.toString());
            column.getStyleClass().add("homeworkTableColumn");
            column.setPrefWidth(410);
            column.setCellValueFactory(new PropertyValueFactory<StudentGradeDTO, String>("GradeH" + idHomework.toString()));

            tableColumns.add(column);
        }
        Callback<TableColumn<StudentGradeDTO, String>, TableCell<StudentGradeDTO, String>> cellFactory =
                p -> new EditingCell();
        for (TableColumn<StudentGradeDTO, String> column : tableColumns) {
            column.setCellFactory(cellFactory);
        }

        gradeTableView.getColumns().add(nameColumn);
        gradeTableView.getColumns().addAll(tableColumns);
        gradeTableView.setEditable(true);
        initializeGradePagination();
        initColumnsListeners();
    }

    private void initializeReceivedComboBox() {
        homeworks.clear();
        groups.clear();
        homeworkService.getHomeworks().forEach(x -> homeworks.add(x.getID()));
        studentService.getActiveStudents().stream().collect(Collectors.groupingBy(x -> x.getGroup()))
                .forEach((key, value) -> groups.add(key));
        this.homeworkComboBox.setItems(homeworks);
        this.groupComboBox.setItems(groups);
    }

    private void initializeTextFields() {
        studentTextField.textProperty().addListener(x -> textStudentHandleValueChange());
    }

    public void addButtonHandler() {
        Grade grade = getGradeFromTextFields();
        if (grade != null)
            try {
                int noExemptions = 0;
                if (!noExemptionsTextField.getText().isEmpty())
                    try {
                        noExemptions = Integer.parseInt(noExemptionsTextField.getText());
                    } catch (NumberFormatException e) {
                        showErrorMessage("The number of the exemptions for the student is invalid!");
                        noExemptions = 0;
                    }

                Double maximumGrade = gradeService.getMaximumGradeFor(grade.getID().getIdHomework(), grade.getGrade(), noExemptions);
                Grade maxGrade = new Grade(grade.getID().getIdStudent(), grade.getID().getIdHomework(), maximumGrade, grade.getData(), grade.getFeedback());
                Optional<ButtonType> result = showConfirmationMessage(catalog.writeData(maxGrade, grade,
                        studentService.getStudent(grade.getID().getIdStudent()), noExemptions), "Are you sure with this?");

                if (result.get() == ButtonType.OK) {
                    if (grade.getGrade() - maximumGrade != 0) {
                        grade.setFeedback(grade.getFeedback() + "\nThe grade has been reduced by " +
                                String.valueOf(grade.getGrade() - maximumGrade) + " because of the delay.");
                        addGradeResultMessage(grade, noExemptions);
                    } else if (grade.getGrade() - maximumGrade == 0) {
                        addGradeResultMessage(grade, noExemptions);
                    }
                }
            } catch (Exception ex) {
                showErrorMessage(ex.getMessage());
            }
    }

    private void addGradeResultMessage(Grade grade, int noExemptions) {
        if (gradeService.addGrade(grade.getID().getIdStudent(), grade.getID().getIdHomework(),
                grade.getGrade(), grade.getFeedback(), noExemptions) == null) {
            (new GradeNotification(studentService.getStudent(grade.getID().getIdStudent()),
                    grade,homeworkService.getHomework(grade.getID().getIdHomework()))).start();
            showMessage(Alert.AlertType.INFORMATION, "Confirmation!", "Successfully added!");
        } else
            showErrorMessage(studentGradeDTOSelected.getName() + " already has a grade for homework " + grade.getID().getIdHomework() + "!");
    }

    public Optional<ButtonType> showConfirmationMessage(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Grade Preview");
        alert.setHeaderText(header);
        alert.setContentText(text);
        return alert.showAndWait();
    }

    public void homeworksButtonHandler() {
        super.homeworksButtonHandler(homeworksButton.getScene().getWindow());
    }

    public void studentsButtonHandler() {
        super.studentsButtonHandler(studentsButton.getScene().getWindow());
    }

    public void getHomeworkGradesHandler() {
        if (homeworkComboBox.getValue() == null){
            showErrorMessage("Please, select the homework you want the grades for!");
            return;
        }
        List<Grade> grades = gradeService.getGradesForHomework(homeworkComboBox.getValue());
        ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
        grades.forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID().getIdStudent(),
                studentService.getStudent(x.getID().getIdStudent()).getName(), new ArrayList<>(Collections.singletonList(x)))));
        lastHomeworkFilteredBy = homeworkComboBox.getValue();
        lastGroupFilteredBy = null;
        lastMonthFilteredBy = false;
        initializeColumns(homeworkComboBox.getValue(), studentGradeDTOS);
    }

    public void getHomeworkGroupGradesHandler() {
        if (homeworkComboBox.getValue() == null || groupComboBox.getValue() == null){
            showErrorMessage("Please, select the homework and the group you want the grades for!");
            return;
        }
        List<Grade> grades = gradeService.getGradesForHomeworkGroup(homeworkComboBox.getValue(), groupComboBox.getValue());
        ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
        grades.forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID().getIdStudent(),
                studentService.getStudent(x.getID().getIdStudent()).getName(), new ArrayList<>(Collections.singletonList(x)))));
        lastHomeworkFilteredBy = homeworkComboBox.getValue();
        lastGroupFilteredBy = groupComboBox.getValue();
        lastMonthFilteredBy = false;
        initializeColumns(homeworkComboBox.getValue(), groupComboBox.getValue(), studentGradeDTOS);
    }

    public void getGroupGradesHandler() {
        if (groupComboBox.getValue() == null) {
            showErrorMessage("Please, select the group you want the grades for!");
            tableColumns.clear();
            initializeModels(getArrayListOfDTOS(studentService.getActiveStudents()));
        } else {
            tableColumns.clear();
            initializeModels(getArrayListOfDTOS(studentService.getGroupStudents(groupComboBox.getValue())));
        }
        lastHomeworkFilteredBy = null;
        lastGroupFilteredBy = groupComboBox.getValue();
        lastMonthFilteredBy = false;
        initializeTableView();
        initColumnsListeners();
    }

    public void getCurrentMonthGradesHandler() {
        ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
        studentService.getActiveStudents().forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID(),
                x.getName(), (ArrayList<Grade>) gradeService.getCurrentMonthGrades(x.getID()))));
        initializeModels(studentGradeDTOS);
        lastHomeworkFilteredBy = null;
        lastGroupFilteredBy = null;
        lastMonthFilteredBy = true;
        tableColumns.clear();
        initializeTableView();
        initColumnsListeners();
    }

    public void resetHandler() {
        gradeTableView.getColumns().clear();
        tableColumns.clear();
        initializeModels(getArrayListOfDTOS(studentService.getActiveStudents()));
        initializeTableView();
        initColumnsListeners();
        lastMonthFilteredBy = false;
        lastGroupFilteredBy = null;
        lastHomeworkFilteredBy = null;
    }

    private Grade getGradeFromTextFields() {
        Integer idStudent = null;
        String errorMessage = "";
        if (studentGradeDTOSelected != null)
            idStudent = studentGradeDTOSelected.getStudentId();
        else errorMessage += "Please, select a student from the table first!(Just click on it!)\n";

        String feedback = feedbackTextArea.getText();
        Integer idHomework = homeworkComboBox.getValue();
        if (idHomework == null)
            errorMessage += "Please, enter the homework number!\n";
        try {
            double grade = Double.parseDouble(gradeTextField.getText());
            if (grade < 1 || grade > 10)
                errorMessage += "Grade must be between 1 and 10!\n";
            if (!errorMessage.isEmpty()) {
                showErrorMessage(errorMessage);
                return null;
            } else return new Grade(idStudent, idHomework, grade, LocalDate.now(), feedback);
        } catch (NumberFormatException ex) {
            showErrorMessage(errorMessage + "Please enter a valid grade!");
            return null;
        }
    }

    private void initializeModels(ArrayList<StudentGradeDTO> students) {
        modelGrade.setAll(students);
    }

    private void textStudentHandleValueChange() {
        String s = studentTextField.getText();
        if (s != null) {
            setModelByName(s);
        } else {
            initializeModels(getArrayListOfDTOS(studentService.getActiveStudents()));
        }
    }

    private ArrayList<StudentGradeDTO> getArrayListOfDTOS(List<Student> students) {
        ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
        students.forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID(), x.getName(), gradeService.getStudentGrades(x.getID()))));
        return studentGradeDTOS;
    }

    private void setModelByName(String start) {
        List<Grade> grades;
        List<Student> studentsByName = studentService.getFilteredByNameStudents(start);
        if (!lastMonthFilteredBy) {
            if (lastHomeworkFilteredBy != null) {
                if (lastGroupFilteredBy != null) {
                    grades = gradeService.getGradesForHomeworkGroup(lastHomeworkFilteredBy, lastGroupFilteredBy);
                    ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
                    grades.stream().filter(x -> studentsByName.contains(studentService.getStudent(x.getID().getIdStudent())))
                            .forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID().getIdStudent(),
                                    studentService.getStudent(x.getID().getIdStudent()).getName(), new ArrayList<>(Collections.singletonList(x)))));
                    initializeColumns(lastHomeworkFilteredBy, lastGroupFilteredBy, studentGradeDTOS);
                } else {
                    grades = gradeService.getGradesForHomework(lastHomeworkFilteredBy);
                    ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
                    grades.stream().filter(x -> studentsByName.contains(studentService.getStudent(x.getID().getIdStudent())))
                            .forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID().getIdStudent(),
                                    studentService.getStudent(x.getID().getIdStudent()).getName(), new ArrayList<>(Collections.singletonList(x)))));
                    initializeColumns(lastHomeworkFilteredBy, studentGradeDTOS);
                }
            } else {
                if (lastGroupFilteredBy != null) {
                    ArrayList<Student> students = (ArrayList<Student>) studentService.getGroupStudents(groupComboBox.getValue()).stream()
                            .filter(studentsByName::contains).collect(Collectors.toList());
                    initializeModels(getArrayListOfDTOS(students));
                } else {
                    modelGrade.setAll(getArrayListOfDTOS(studentsByName));
                }
                tableColumns.clear();
                initializeTableView();
            }
        } else {
            ArrayList<StudentGradeDTO> studentGradeDTOS = new ArrayList<>();
            studentsByName.forEach(x -> studentGradeDTOS.add(new StudentGradeDTO(x.getID(),
                    x.getName(), (ArrayList<Grade>) gradeService.getCurrentMonthGrades(x.getID()))));
            tableColumns.clear();
            initializeModels(studentGradeDTOS);
            initializeTableView();
        }
    }

    @Override
    public void update(GradeChangeEvent gradeChangeEvent) {
        //initializeModels(getArrayListOfDTOS(studentService.getActiveStudents()));
        textStudentHandleValueChange();
        initialisePieChart();
        initializeGradePagination();
    }

    private Node createPageGrade(int pageIndex) {
        Integer from = pageIndex * itemsPerPage;
        int to = (from + itemsPerPage) < modelGrade.size() ? (from + itemsPerPage) : modelGrade.size();
        paginationModel.clear();

        for (int i = from; i < to; ++i) {
            this.paginationModel.add(this.modelGrade.get(i));
        }
        gradeTableView.setItems(paginationModel);
        return gradeTableView;
    }

    private void initializeGradePagination() {
        int count = (modelGrade.size() / itemsPerPage) + 1;
        pagination.setPageCount(count);
        pagination.setPageFactory(this::createPageGrade);

    }

    private void initialisePieChartList() {
        gradesPerGroupPieChartList = FXCollections.observableArrayList();
        gradesPerHomeworkPieChartList = FXCollections.observableArrayList();
        gradeService.getAllGrades().stream().collect(Collectors.groupingBy(x -> studentService.getStudent(x.getID().getIdStudent()).getGroup()))
                .forEach((key, value) -> gradesPerGroupPieChartList.add(new PieChart.Data(key, value.size())));
        gradeService.getAllGrades().stream().collect(Collectors.groupingBy(x -> x.getID().getIdHomework()))
                .forEach((key, value) -> gradesPerHomeworkPieChartList.add(new PieChart.Data(key.toString(), value.size())));
    }

    private void initialisePieChart() {
        initialisePieChartList();
        gradesPerGroupPieChart.setData(gradesPerGroupPieChartList);
        gradesPerGroupPieChart.setTitle("Grades per Groups");
        gradesPerLastHomeworkPieChart.setData(gradesPerHomeworkPieChartList);
        gradesPerLastHomeworkPieChart.setTitle("Grades per Homeworks");
    }
}