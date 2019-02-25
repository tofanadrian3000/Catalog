package DTO;

import java.util.Objects;

public class GroupTeacherDTO  {
    String group, teacher;

    public GroupTeacherDTO(String group, String teacher) {
        this.group = group;
        this.teacher = teacher;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTeacherDTO that = (GroupTeacherDTO) o;
        return Objects.equals(group, that.group) &&
                Objects.equals(teacher, that.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, teacher);
    }

    @Override
    public String toString() {
        return "GroupTeacherDTO{" +
                "group='" + group + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
