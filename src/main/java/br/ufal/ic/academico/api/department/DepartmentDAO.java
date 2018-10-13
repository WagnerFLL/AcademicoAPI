package br.ufal.ic.academico.api.department;

import br.ufal.ic.academico.api.ModelDAO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

@Slf4j
public class DepartmentDAO extends ModelDAO<Department> {
    public DepartmentDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ArrayList<Department> getAll() {
        return (ArrayList<Department>) currentSession().createQuery("from Department").list();
    }
}
