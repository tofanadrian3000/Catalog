package Model;

import java.util.Objects;

public class Homework implements HasID<Integer> {

    private Integer id, deadline, received;
    private String description;

    @Override
    public Integer getID() {
        return id;
    }

    @Override
    public void setID(Integer id) {
        this.id = id;
    }

    public Integer getDeadline() {
        return deadline;
    }

    public void setDeadline(Integer deadline) {
        this.deadline = deadline;
    }

    public Integer getReceived() {
        return received;
    }

    public void setReceived(Integer received) {
        this.received = received;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Homework(Integer id, Integer received, Integer deadline, String description) {
        this.id = id;
        this.deadline = deadline;
        this.received = received;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Homework tema = (Homework) o;
        return Objects.equals(id, tema.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id.toString() + "," + received.toString() + "," + deadline.toString() + "," + description;
    }
}
