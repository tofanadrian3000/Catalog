package Repository;

import Model.HasID;
import Validation.ValidationException;
import Validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Repository<ID, E extends HasID<ID>> implements CrudRepository<ID,E> {

    private Map<ID, E> elems;
    private Validator<E> validator;

    public Repository(Validator<E> validator) {
        this.elems = new HashMap<>();
        this.validator = validator;
    }

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Id-ul este null!.");
        if (elems.containsKey(id))
            return elems.get(id);
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        ArrayList<E> allElems = new ArrayList<>();
        elems.forEach((id, e) -> allElems.add(e));
        return allElems;
    }

    @Override
    public E save(E elem) throws ValidationException {
        if (elem == null)
            throw new IllegalArgumentException("Id-ul este null!.");

        validator.validate(elem);

        if (findOne(elem.getID()) == null) {
            this.elems.put(elem.getID(), elem);
            return null;
        }
        return elem;
    }

    @Override
    public E delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Id-ul este null!.");

        E elem = this.findOne(id);
        if (elem != null) {
            this.elems.remove(elem.getID());
            return elem;
        }
        return null;
    }

    @Override
    public E update(E elem) {
        if (elem == null)
            throw new IllegalArgumentException("Id-ul este null!.");

        validator.validate(elem);
        if (this.elems.containsKey(elem.getID()))
            this.elems.put(elem.getID(), elem);
        return null;
    }
}
