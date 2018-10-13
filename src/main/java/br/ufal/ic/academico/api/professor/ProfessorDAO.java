package br.ufal.ic.academico.api.professor;

import br.ufal.ic.academico.api.ModelDAO;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class ProfessorDAO extends ModelDAO<Professor> {
    public ProfessorDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ArrayList<Professor> getAll() {
        return (ArrayList<Professor>) currentSession().createQuery("from Professor").list();
    }
}
