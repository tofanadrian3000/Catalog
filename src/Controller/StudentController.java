package Controller;

import Model.Student;
import Utils.Event.ChangeEventType;
import Utils.Event.StudentChangeEvent;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentController extends Controller implements Observer<StudentChangeEvent> {
    private ObservableList<Student> model= FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> studentsPerGroupPieChartList= FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> studentsPerGradePieChartList= FXCollections.observableArrayList();
    private ObservableList<Student> paginationModel= FXCollections.observableArrayList();
    private Student selectedStudent;
    private Integer itemsPerPage=15;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField groupTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField professorTextField;
    @FXML
    private TextField filterByTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button finalGradesButton;
    @FXML
    private Button hardestHomeworkButton;
    @FXML
    private Button passableStudentsButton;
    @FXML
    private Button topGroupsButton;
    @FXML
    private PieChart studentsPerGroupPieChart;
    @FXML
    private PieChart studentsPerGradePieChart;
    @FXML
    private Pagination pagination;
    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TableColumn<Student,String> nameTableColumn;
    @FXML
    private TableColumn<Student,String> groupTableColumn;
    @FXML
    private TableColumn<Student,String> emailTableColumn;
    @FXML
    private TableColumn<Student,String> professorTableColumn;
    @FXML
    private Button gradesButton;
    @FXML
    private Button homeworksButton;

    @FXML
    void initialize(){
        initializeModel();
        initializeTableView();
        initialisePieChart();
        studentService.addObserver(this);
        initializePaginationStudent();

    }

    private void initializeTableView(){
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        groupTableColumn.setCellValueFactory(new PropertyValueFactory<>("Group"));
        emailTableColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        professorTableColumn.setCellValueFactory(new PropertyValueFactory<>("Professor"));

        studentTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showStudentDetails(newValue));

        studentTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> this.selectedStudent=newValue);

        filterByTextField.textProperty().addListener(x -> textStudentHandleValueChange());

    }

    private void textStudentHandleValueChange() {
        if(!filterByTextField.getText().isEmpty()) {
            model.setAll(studentService.getFilteredByNameStudents(filterByTextField.getText()));
            initializePaginationStudent();
        }
        else{
            initializeModel();
            initializePaginationStudent();
        }
    }

    private Node createPageStudent(int pageIndex) {
        Integer from = pageIndex * itemsPerPage;
        int to = (from + itemsPerPage) < model.size() ? (from + itemsPerPage) : model.size();
        paginationModel.clear();
        for (int i = from; i < to; ++i) {
            this.paginationModel.add(this.model.get(i));
        }
        studentTableView.setItems(paginationModel);
        return studentTableView;
    }

    private void initializePaginationStudent() {
        int count = (model.size() / itemsPerPage) + 1;
        pagination.setPageCount(count);
        pagination.setPageFactory(this::createPageStudent);
    }

    public void addStudentButtonHandler() {
        Student newValue=getStudentFromTextFields();

        try {
            Student student=studentService.addStudent(newValue.getName(),newValue.getGroup(),newValue.getEmail(),newValue.getProfessor());
            if(student!=null){
                showErrorMessage( "The student is not valid!");
            }
            else{
                showMessage(Alert.AlertType.INFORMATION, "Successfully added", "The student was saved!");
            }
        }
        catch (Exception ex){
            showErrorMessage( ex.getMessage());
        }
    }

    public void updateStudentButtonHandler(){
        Student newValue=getStudentFromTextFields();
        try {
            Student student=studentService.updateStudent(selectedStudent.getID(),newValue.getGroup(),newValue.getProfessor());
            if(student==null){
                showMessage(Alert.AlertType.INFORMATION, "Successfully added", "The student was update!");
            }
            else{
                showErrorMessage("Please, select a student from the table to update!");
            }
        }
        catch (Exception ex){
            showErrorMessage( ex.getMessage());
        }
    }

    public void removeStudentButtonHandler(){

        try {
            Student student=studentService.removeStudent(selectedStudent.getID());
            if(student==null){
                showMessage(Alert.AlertType.INFORMATION, "Successfully removed", "The student was removed!");
            }
            else{
                showErrorMessage("Please, select a student from the table to update!");
            }
        }
        catch (Exception ex){
            showErrorMessage( ex.getMessage());
        }
    }

    public void gradesButtonHandler(){
        super.gradesButtonHandler(gradesButton.getScene().getWindow());
    }

    public void homeworksButtonHandler(){
        super.homeworksButtonHandler(homeworksButton.getScene().getWindow());
    }

    private void showStudentDetails(Student newValue) {
        if(newValue==null)
        {
            nameTextField.setText("");
            groupTextField.setText("");
            emailTextField.setText("");
            professorTextField.setText("");
        }
        else
        {
            nameTextField.setText(newValue.getName());
            groupTextField.setText(newValue.getGroup());
            emailTextField.setText(newValue.getEmail());
            professorTextField.setText(newValue.getProfessor());
        }
    }

    private void initializeModel(){
        model.setAll(studentService.getActiveStudents());
    }

    private Student getStudentFromTextFields(){
        String name,group,email,professor;
        name = nameTextField.getText();
        group = groupTextField.getText();
        email = emailTextField.getText();
        professor = professorTextField.getText();

        return new Student(1,name,group,email,professor);
    }

    @Override
    public void update(StudentChangeEvent studentChangeEvent) {
        if(studentChangeEvent.getType()== ChangeEventType.UPDATE){
            initializeModel();
            initialisePieChart();
            initializePaginationStudent();
        }
        if(studentChangeEvent.getType()==ChangeEventType.ADD){
            model.add(studentChangeEvent.getData());
            initialisePieChart();
            initializePaginationStudent();
        }
        if(studentChangeEvent.getType()==ChangeEventType.DELETE){
            initializeModel();
            initialisePieChart();
            initializePaginationStudent();
        }
    }

    private void initialisePieChartList(){
        studentsPerGradePieChartList= FXCollections.observableArrayList();
        studentsPerGroupPieChartList= FXCollections.observableArrayList();
        studentService.getStudentsPerGroups().forEach(x->studentsPerGroupPieChartList.add(new PieChart.Data(x.getKey(),x.getValue().size())));
        Controller.gradeService.getStudentsPerGrades().forEach(x->studentsPerGradePieChartList.add(new PieChart.Data(x.getKey().toString(),x.getValue().size())));
    }

    private void initialisePieChart(){
        initialisePieChartList();
        studentsPerGroupPieChart.setData(studentsPerGroupPieChartList);
        studentsPerGroupPieChart.setTitle("Students per Group");
        studentsPerGradePieChart.setData(studentsPerGradePieChartList);
        studentsPerGradePieChart.setTitle("Students per Grade");

    }
}
