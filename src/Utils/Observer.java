package Utils;


import Utils.Event.Event;

public interface Observer<E extends Event> {
    void update(E e);
}