package Service;

import DTO.GroupTeacherDTO;
import Model.*;
import Notification.GradeNotification;
import Repository.CrudRepository;
import Repository.GradeDBRepository;
import Utils.Event.ChangeEventType;
import Utils.Event.GradeChangeEvent;
import Utils.MyData;
import Utils.Observable;
import Utils.Observer;
import Validation.GradeValidator;
import Validation.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

public class GradeService implements Observable<GradeChangeEvent> {

    private CrudRepository<GradeId, Grade> gradeRepository;
    private StudentService studentService;
    private HomeworkService homeworkService;
    private ArrayList<Observer<GradeChangeEvent>> observers = new ArrayList<>();


    public GradeService() {
        this.gradeRepository = new GradeDBRepository(new GradeValidator());
        this.studentService = new StudentService();
        this.homeworkService = new HomeworkService(MyData.currentWeek);
    }

    /**
     * @return the maximum grade for a homework at this current week
     * @throws ServiceException if the homework doesn't exist
     */
    public Double getMaximumGradeFor(Integer idHomework,Double grade,Integer noExemptions) throws ServiceException{
        Homework homework = this.homeworkService.getHomework(idHomework);
        if(homework==null)
            throw new ServiceException("The homework doesn't exist!");
        Integer currentWeek = MyData.currentWeek;
        int delay = currentWeek-this.homeworkService.getHomework(idHomework).getDeadline()-noExemptions;
        if( delay == 1)
            return grade-2.5;
        else if (delay == 2)
            return grade-5;
        else if (delay>2)
            return 1.0;
        else return grade;
    }

    /**
     * @return null if the grade was added with succes, or the grade entered otherwise
     * @throws ServiceException if the student is not found
     * @throws ServiceException if the homework is not found
     * @throws ServiceException if the grade is invalid
     * @throws ServiceException if the grade exists
     * @throws IllegalArgumentException for null parameters
     */
    public Grade addGrade(Integer idStudent, Integer idHomework, Double inputGrade, String feedback, Integer noExemptions){

        String errors= "";
        Student student = this.studentService.getStudent(idStudent);
        if(student==null)
            errors+="The student that you've entered doesn't exist!\n";

        Homework homework = this.homeworkService.getHomework(idHomework);
        if(homework==null)
            errors+="The homework that you've entered doesn't exist!\n";

        if(!errors.isEmpty())
            throw new ServiceException(errors);

        try {
            Double maximumGrade = getMaximumGradeFor(idHomework,inputGrade,noExemptions);
            Grade grade = new Grade(idStudent, idHomework, maximumGrade, LocalDate.now(), feedback);

            if(this.gradeRepository.save(grade)==null) {
                notifyObservers(new GradeChangeEvent(ChangeEventType.ADD, grade));
                return null;
            }
            return grade;
        } catch (ValidationException | IllegalArgumentException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public Grade getGrade(Integer idStudent, Integer idHomework){
        return this.gradeRepository.findOne(new GradeId(idStudent,idHomework));
    }

    public ArrayList<Grade> getAllGrades() {
        return (ArrayList<Grade>) gradeRepository.findAll();
    }

    public List<Grade> getStudentGrades(Integer idStudent) {
        return getAllGrades().stream().filter(x->x.getID().getIdStudent().equals(idStudent))
                .collect(Collectors.toList());
    }

    private double getMedium(List<Grade> grades){
        AtomicReference<Integer> gainedWeight= new AtomicReference<>(0);
        AtomicReference<Integer> totalWeight= new AtomicReference<>(0);
        homeworkService.getHomeworks().forEach(x-> totalWeight.updateAndGet(v -> v + x.getDeadline() - x.getReceived()));

        AtomicReference<Double> finalGrade= new AtomicReference<>((double) 0);

        grades.forEach(grade->{
            Homework homework = homeworkService.getHomework(grade.getID().getIdHomework());
            Integer weight = homework.getDeadline() - homework.getReceived();
            finalGrade.updateAndGet(v -> v + grade.getGrade() * weight);
            gainedWeight.updateAndGet(v -> v + weight);
        });

        return (finalGrade.get() + (totalWeight.get() - gainedWeight.get())) / totalWeight.get();
    }

    public List<Grade> getGradesForHomework(Integer idHomework){
        return getAllGrades().stream().filter(x-> x.getID().getIdHomework().equals(idHomework) &&
                studentService.getStudent(x.getID().getIdStudent()).getStatus()).collect(Collectors.toList());
    }

    public List<Grade> getGradesForHomeworkGroup(Integer idHomework, String group){
        return getAllGrades().stream().filter (x-> x.getID().getIdHomework().equals(idHomework) &&
                studentService.getStudent(x.getID().getIdStudent()).getGroup().equals(group)&&
                studentService.getStudent(x.getID().getIdStudent()).getStatus()).collect(Collectors.toList());
    }

    public List<Grade> getCurrentMonthGrades(Integer idStudent){
        return getStudentGrades(idStudent).stream().filter(x->x.getData().getMonth()==LocalDateTime.now().getMonth()).collect(Collectors.toList());
    }

    public List<Entry<Student, Double>> getFinalGrades(){
        return studentService.getActiveStudents().stream().collect(Collectors.toMap(x->x,
                x->getMedium(getStudentGrades(x.getID()))))
            .entrySet().stream().sorted((o1, o2) -> o2.getValue().isNaN() ? -1: o2.getValue().compareTo(o1.getValue().isNaN() ? 0.0: o1.getValue())).collect(Collectors.toList())
                ;
    }

    public List<Entry<Homework, Double>> getHardestHomeworks(){
        return getAllGrades().stream().collect(Collectors.groupingBy(x->homeworkService.getHomework(x.getID().getIdHomework())))
                    .entrySet().stream().collect(Collectors.toMap(Entry::getKey, x->(x.getValue()
                        .stream().mapToDouble(Grade::getGrade).sum()+studentService.getActiveStudents().size()-
                        x.getValue().size())/studentService.getActiveStudents().size()))
                    .entrySet().stream().sorted(comparing(Entry::getValue)).collect(Collectors.toList());
    }

    public List<Student> getPassableStudents(){
        return getFinalGrades().stream().filter(x->x.getValue()>=5).map(Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Entry<GroupTeacherDTO, Double>> getTopGroups(){
        return getAllGrades().stream().collect(Collectors.groupingBy(x->new GroupTeacherDTO(studentService.getStudent(x.getID().getIdStudent()).getGroup(),
                studentService.getStudent(x.getID().getIdStudent()).getProfessor())))
                .entrySet().stream().collect(Collectors.toMap(Entry::getKey, x->x.getValue().stream().mapToDouble(Grade::getGrade).average().getAsDouble()))
                .entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())).collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<GradeChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<GradeChangeEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(GradeChangeEvent event) {
        observers.forEach(x->x.update(event));
    }

    public List<Entry<Integer, List<Entry<Student, Double>>>> getStudentsPerGrades(){
        return new ArrayList<>(getFinalGrades().stream().collect(Collectors.groupingBy(x -> Grade.getRoundedGrade(x.getValue())))
                .entrySet());
    }
    public List<Entry<Homework, Double>> getMediumGrades(){
        return new ArrayList<>(homeworkService.getHomeworks().stream().collect(Collectors.toMap(x -> x, x -> homeworkService.getMediumForHomeworks(
                getGradesForHomework(x.getID())))).entrySet());
    }
}