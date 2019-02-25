package Controller;

import Model.Homework;
import Notification.NewHomeworkNotification;
import Notification.UpdateHomeworkNotification;
import Utils.Event.ChangeEventType;
import Utils.Event.HomeworkChangeEvent;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.stream.Collectors;


public class HomeworkController extends Controller implements Observer<HomeworkChangeEvent> {

    private ObservableList<Homework> homeworkModel = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> homeworksPerGradePieChartList = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> homeworksPerWeightPieChartList = FXCollections.observableArrayList();
    private ObservableList<Homework> paginationModel = FXCollections.observableArrayList();
    private Integer itemsPerPage = 12;

    @FXML
    private TextField homeworkTextField;

    @FXML
    private TextField deadlineTextField;
    @FXML
    private TextField receivedTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button finalGradesButton;
    @FXML
    private Button hardestHomeworkButton;
    @FXML
    private Button passableStudentsButton;
    @FXML
    private Button topGroupsButton;
    @FXML
    private PieChart homeworksPerGradePieChart;
    @FXML
    private PieChart homeworksPerWeightPieChart;
    @FXML
    private TableView<Homework> homeworkTableView;
    @FXML
    private TableColumn<Homework, Integer> homeworkTableColumn;
    @FXML
    private TableColumn<Homework, Integer> receivedTableColumn;
    @FXML
    private TableColumn<Homework, Integer> deadlineTableColumn;
    @FXML
    private TableColumn<Homework, String> descriptionTableColumn;
    @FXML
    private Button gradesButton;
    @FXML
    private Button studentsButton;
    @FXML
    private Pagination pagination;


    @FXML
    void initialize() {

        initializeModel();
        initializeTableView();
        homeworkService.addObserver(this);
        initialisePieChart();
        initializeGradePagination();

    }

    private void initializeTableView() {
        homeworkTableColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        receivedTableColumn.setCellValueFactory(new PropertyValueFactory<>("Received"));
        deadlineTableColumn.setCellValueFactory(new PropertyValueFactory<>("Deadline"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));
        homeworkTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showHomeworkDetails(newValue));

    }

    public void addHomeworkButtonHandler() {
        Homework newValue = GetHomeworkFromTextFields();

        try {
            Homework homework = homeworkService.addHomework(newValue.getID(), newValue.getReceived(), newValue.getDeadline(), newValue.getDescription());
            if (homework != null) {
                showErrorMessage("This homework exists!");
            } else {
                studentService.getActiveStudents().forEach(x->(new NewHomeworkNotification(newValue,x)).start());
                showMessage(Alert.AlertType.INFORMATION, "Successfully added", "The homework was saved!");
            }
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    public void updateHomeworkButtonHandler() {

        Homework newValue = GetHomeworkFromTextFields();

        try {
            Homework homework = homeworkService.updateHomework(newValue.getID(), newValue.getDeadline());
            if (homework == null) {
                studentService.getActiveStudents().forEach(x->(new UpdateHomeworkNotification(newValue,x)).start());
                showMessage(Alert.AlertType.INFORMATION, "Successfully update", "The homework was update!");
            } else {
                showErrorMessage("The homework does not exist!");
            }
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    public void gradesButtonHandler() {
        super.gradesButtonHandler(gradesButton.getScene().getWindow());
    }

    public void studentsButtonHandler() {
        super.studentsButtonHandler(studentsButton.getScene().getWindow());
    }

    private void showHomeworkDetails(Homework newValue) {
        if (newValue == null) {
            receivedTextField.setText("");
            deadlineTextField.setText("");
            descriptionTextArea.setText("");
        } else {
            homeworkTextField.setText(newValue.getID().toString());
            receivedTextField.setText(newValue.getReceived().toString());
            deadlineTextField.setText(newValue.getDeadline().toString());
            descriptionTextArea.setText(newValue.getDescription());
        }
    }

    private void initializeModel() {
        homeworkModel.setAll(homeworkService.getHomeworks());
    }

    private Homework GetHomeworkFromTextFields() {
        Integer homework, received, deadline;
        try {
            homework = Integer.valueOf(homeworkTextField.getText());
            received = Integer.valueOf(receivedTextField.getText());
            deadline = Integer.valueOf(deadlineTextField.getText());
        } catch (NumberFormatException ex) {
            homework = null;
            received = null;
            deadline = null;
        }
        String description = descriptionTextArea.getText();
        return new Homework(homework, received, deadline, description);
    }

    @Override
    public void update(HomeworkChangeEvent homeworkChangeEvent) {
        if (homeworkChangeEvent.getType() == ChangeEventType.UPDATE) {
            initializeModel();
            initialisePieChart();
            initializeGradePagination();
        }
        if (homeworkChangeEvent.getType() == ChangeEventType.ADD) {
            homeworkModel.add(homeworkChangeEvent.getData());
            initialisePieChart();
            initializeGradePagination();
        }
    }

    private Node createPageHomework(int pageIndex) {
        Integer from = pageIndex * itemsPerPage;
        int to = (from + itemsPerPage) < homeworkModel.size() ? (from + itemsPerPage) : homeworkModel.size();
        paginationModel.clear();

        for (int i = from; i < to; ++i) {
            this.paginationModel.add(this.homeworkModel.get(i));
        }
        homeworkTableView.setItems(paginationModel);
        return homeworkTableView;
    }

    private void initializeGradePagination() {
        int count = (homeworkModel.size() / itemsPerPage) + 1;
        pagination.setPageCount(count);
        pagination.setPageFactory(this::createPageHomework);

    }

    private void initialisePieChartList() {
        homeworksPerGradePieChartList = FXCollections.observableArrayList();
        homeworksPerWeightPieChartList= FXCollections.observableArrayList();
        gradeService.getMediumGrades().stream().collect(Collectors.groupingBy(x -> x.getValue().toString()))
                .forEach((key, value) -> homeworksPerGradePieChartList.add(new PieChart.Data(key, value.size())));
        homeworkService.getHomeworks().stream().collect(Collectors.groupingBy(x->x.getDeadline()-x.getReceived()))
                .forEach((key,value)->homeworksPerWeightPieChartList.add(new PieChart.Data(key.toString(),value.size())));
    }

    private void initialisePieChart() {
        initialisePieChartList();
        homeworksPerWeightPieChart.setTitle("Homework per Weight");
        homeworksPerWeightPieChart.setData(homeworksPerWeightPieChartList);
        homeworksPerGradePieChart.setData(homeworksPerGradePieChartList);
        homeworksPerGradePieChart.setTitle("Homework per Grades");

    }
}