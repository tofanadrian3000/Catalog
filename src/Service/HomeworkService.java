package Service;

import Model.Grade;
import Model.Homework;
import Notification.NewHomeworkNotification;
import Notification.UpdateHomeworkNotification;
import Repository.CrudRepository;
import Repository.HomeworkDBRepository;
import Utils.Event.ChangeEventType;
import Utils.Event.HomeworkChangeEvent;
import Utils.Observable;
import Utils.Observer;
import Validation.HomeworkValidator;
import Validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeworkService implements Observable<HomeworkChangeEvent> {
    private CrudRepository<Integer, Homework> repository;
    private Integer currentWeek;
    private ArrayList<Observer<HomeworkChangeEvent>> observers;

    public HomeworkService(Integer currentWeek) {
        this.repository = new HomeworkDBRepository(new HomeworkValidator());
        this.currentWeek = currentWeek;
        observers = new ArrayList<>();
    }

    /**
     * @return homework with the specified attributes if the homework already exists or null if the homework has been added
     * @throws ServiceException if the homework is not valid!
     */
    public Homework addHomework(Integer id, Integer received, Integer deadline, String description) throws ServiceException {
        try{
            Homework homework = new Homework(id,received,deadline,description);
            if(this.repository.save(homework)==null) {
                notifyObservers(new HomeworkChangeEvent(ChangeEventType.ADD, new Homework(id, received, deadline, description)));
                return null;
            }
            return homework;
        }
        catch(ValidationException e){
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     *
     * @return null if the homework specified has been updated or the homework doesn't exist
     * @throws ServiceException if the deadline entered is smaller then the previous one
     * @throws ServiceException if the homework updated is invalid
     */
    public Homework updateHomework(Integer id, Integer deadline) throws ServiceException {
        try {
            Homework oldHomework = this.repository.findOne(id);
            if(oldHomework==null)
                return new Homework(id,1,deadline,"default");
            if(deadline>oldHomework.getDeadline()){
                if(currentWeek<=deadline) {
                    Homework homework = new Homework(id,oldHomework.getReceived(),deadline,oldHomework.getDescription());
                    try {
                        if(this.repository.update(homework)==null) {
                            notifyObservers(new HomeworkChangeEvent(ChangeEventType.UPDATE, homework, oldHomework));
                            return null;
                        }
                        return homework;
                    } catch (ValidationException e) {
                        throw new ServiceException(e.getMessage());
                    }
                }
                else throw new ServiceException("The new deadline is expired!\n");
                }
            else throw new ServiceException("Deadline is wrong!\n");
        }
        catch (IllegalArgumentException | ServiceException e){
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @return the Homework that has the specified id
     * @throws ServiceException if id entered is null
     */
    public Homework getHomework(Integer id) throws ServiceException{
        try{
            return this.repository.findOne(id);
        }
        catch (IllegalArgumentException e){
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     *
     * @return all the homeworks
     */
    public ArrayList<Homework> getHomeworks(){
        return (ArrayList<Homework>) this.repository.findAll();
    }

    double getMediumForHomeworks(List<Grade> grades){
        AtomicInteger sum= new AtomicInteger();
        grades.forEach(x-> sum.addAndGet( Grade.getRoundedGrade(x.getGrade())));
        if(grades.size()!=0)
            return sum.get()/grades.size();
        else return sum.get();
    }

    @Override
    public void addObserver(Observer<HomeworkChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<HomeworkChangeEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(HomeworkChangeEvent event) {
        observers.forEach(x->x.update(event));
    }

}
