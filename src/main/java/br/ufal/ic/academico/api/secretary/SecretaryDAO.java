package br.ufal.ic.academico.api.secretary;

import br.ufal.ic.academico.api.ModelDAO;
import br.ufal.ic.academico.api.department.Department;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class SecretaryDAO extends ModelDAO<Secretary> {
    public SecretaryDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ArrayList<Secretary> getAll() {
        return (ArrayList<Secretary>) currentSession().createQuery("from Secretary").list();
    }

    public Department getDepartment(Secretary secretary) {
        if (secretary == null) {
            return null;
        }

        String type = secretary.getType().equals("GRADUATION") ? "graduation" : "postGraduation";
        String query = "select D from Department D where D." + type + ".id = " + secretary.getId();
        return (Department) currentSession().createQuery(query).uniqueResult();
    }
}
