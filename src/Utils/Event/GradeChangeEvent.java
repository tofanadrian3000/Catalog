package Utils.Event;

import Model.Grade;

public class GradeChangeEvent implements Event{
    private ChangeEventType type;
    private Grade data, oldData;

    public GradeChangeEvent(ChangeEventType type, Grade data) {
        this.type = type;
        this.data = data;
    }
    public ChangeEventType getType() {
        return type;
    }

    public Grade getData() {
        return data;
    }
}
