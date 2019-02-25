package Service;

import Model.Student;
import Repository.CrudRepository;
import Repository.StudentDBRepository;
import Utils.Event.ChangeEventType;
import Utils.Event.StudentChangeEvent;
import Utils.Observable;
import Utils.Observer;
import Validation.StudentValidator;
import Validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentService implements Observable<StudentChangeEvent> {
    private CrudRepository<Integer,Student> repository;
    private ArrayList<Observer<StudentChangeEvent>> observers;
    private Integer minimumId;

    public StudentService() {
        this.repository = new StudentDBRepository(new StudentValidator());
        observers = new ArrayList<>();
        minimumId=getStudents().isEmpty() ? 1: getStudents().get(getStudents().size()-1).getID()+1;
    }

    /**
     *
     * @param name
     * @param group
     * @param email
     * @param professor
     * @param status
     * @return the Student formed by the given attributes if the Student was already in
     * @return null otherwise
     * @throws ServiceException if the given attributes are incorrect
     */
    private Student addStudent(String name, String group, String email, String professor, boolean status) throws ServiceException{
        Student stud=new Student(minimumId,name,group,email,professor,status);
        minimumId++;
        try {
            Student student =  this.repository.save(stud);
            if(student==null)
                notifyObservers(new StudentChangeEvent(ChangeEventType.ADD,stud));
            return student;
        } catch (ValidationException | IllegalArgumentException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @return the Student formed by the given attributes if the Student was already in
     * @return null otherwise
     * @throws ServiceException if the given attributes are incorrect
     */
    public Student addStudent(String name, String group, String email, String professor) throws ServiceException {
        if(name !=null && group!=null && email !=null && professor!=null)
            return addStudent(name,group,email,professor,true);
        else throw new ServiceException("Atributele sunt incorecte!");
    }


    /**
     * @return null if the student specified has been updated
     * @return the entity if the student doesn't exist
     * @throws ServiceException if the student updated is invalid or if the given parameters are null
     */
    public Student updateStudent(Integer id, String group, String professor) throws ServiceException {
        try {
            Student oldStud = this.repository.findOne(id);
            if(oldStud==null) {
                return null;
            }
            Student stud = new Student(oldStud.getID(),oldStud.getName(),oldStud.getGroup(),oldStud.getEmail(),oldStud.getProfessor(),oldStud.getStatus());
            if(group!=null && professor!=null){
                stud.setGroup(group);
                stud.setProfessor(professor);
            }
            else throw new ServiceException("Atributele sunt incorecte!");
            try {
                Student student= this.repository.update(stud);
                if(student==null)
                    notifyObservers(new StudentChangeEvent(ChangeEventType.UPDATE,stud,oldStud));
                return student;
            } catch (ValidationException e) {
                throw new ServiceException(e.getMessage());
            }
        }
        catch (IllegalArgumentException e){
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     *
     * @param id
     * @return student with the specified id if it is found
     * @return null othervwise
     * @throws ServiceException if the given id is null
     */
    public Student deleteStudent(Integer id) throws ServiceException {
        try{
            Student student= this.repository.delete(id);
            if(student!=null)
                notifyObservers(new StudentChangeEvent(ChangeEventType.DELETE,student));
            return student;
        }
        catch (IllegalArgumentException e){
            throw new ServiceException(e.getMessage());
        }
    }


     /**
     *
     * @param id
     * @return null if the student specified has been updated
     * @return the entity if the student doesn't exist
     * @throws ServiceException if the student updated is invalid or if the given parameters are null
     */
    public Student removeStudent(Integer id) throws ServiceException {
        try {
            Student oldStud = this.repository.findOne(id);
            if(oldStud==null)
                return null;
            oldStud.setStatus(false);
            Student student= this.repository.update(oldStud);
            if(student==null)
                notifyObservers(new StudentChangeEvent(ChangeEventType.DELETE,oldStud,oldStud));
            return student;
        }
        catch (IllegalArgumentException e){
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     *
     * @param id
     * @return null if the student with the specified id is not found
     * @return the student with the specified id otherwise
     * @throws ServiceException if the given id is null
     */
    public Student getStudent(Integer id) throws ServiceException{
        try{
            return this.repository.findOne(id);
        }
        catch (IllegalArgumentException e){
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     *
     * @return all the students
     */
    public ArrayList<Student> getStudents(){
        return (ArrayList<Student>) this.repository.findAll();
    }

    /**
     * return all the active students
     */
    public List<Student> getActiveStudents(){
         return ((List<Student>)repository.findAll()).stream().filter(Student::getStatus).collect(Collectors.toList());
    }

    /**
     * return all the inactive students
     */
    public List<Student> getInactiveStudents(){
        return ((List<Student>)repository.findAll()).stream().filter(stud-> !stud.getStatus()).collect(Collectors.toList());}

    public ArrayList<Student> getGroupStudents(String group){
        return (ArrayList<Student>) getActiveStudents().stream().filter(x->x.getGroup().equals(group)).collect(Collectors.toList());
    }

    public List<Student> getFilteredByNameStudents(String name){
        return getActiveStudents().stream().filter(x->x.getName().contains(name)).collect(Collectors.toList());
    }

    public List<Map.Entry<String, List<Student>>> getStudentsPerGroups(){
        return new ArrayList<>(getActiveStudents().stream().collect(Collectors.groupingBy(Student::getGroup))
                .entrySet());
    }
    @Override
    public void addObserver(Observer<StudentChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<StudentChangeEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(StudentChangeEvent event) {
        observers.forEach(x->x.update(event));
    }
}
