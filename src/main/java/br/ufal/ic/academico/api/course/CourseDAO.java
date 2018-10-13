package br.ufal.ic.academico.api.course;

import br.ufal.ic.academico.api.ModelDAO;
import br.ufal.ic.academico.api.secretary.Secretary;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class CourseDAO extends ModelDAO<Course> {
    public CourseDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ArrayList<Course> getAll() {
        return (ArrayList<Course>) currentSession().createQuery("from Course").list();
    }

    public Secretary getSecretary(Course course) {
        ArrayList<Secretary> secretaries = (ArrayList<Secretary>) currentSession().createQuery("from Secretary").list();
        for (Secretary s : secretaries) {
            for (Course c : s.getCourses()) {
                if (c.getId().equals(course.getId())) {
                    return s;
                }
            }
        }
        return null;
    }
}
