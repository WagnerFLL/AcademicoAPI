package br.ufal.ic.academico.api.discipline;

import br.ufal.ic.academico.api.ModelDAO;
import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class DisciplineDAO extends ModelDAO<Discipline> {
    public DisciplineDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ArrayList<Discipline> getAll() {
        return (ArrayList<Discipline>) currentSession().createQuery("from Discipline").list();
    }

    public Course getCourse(Discipline discipline) {
        ArrayList<Course> courses = (ArrayList<Course>) currentSession().createQuery("from Course").list();

        for (Course c : courses) {
            assert c.getDisciplines() != null;
            for (Discipline d : c.getDisciplines())
                if (d.getId().equals(discipline.getId()))
                    return c;

        }

        return null;
    }

    public List<Discipline> getAllByStudent(Student s) {
        List<Discipline> disciplines = new ArrayList<>();
        List<Discipline> allDisciplines = this.getAll();

        for (Discipline d : allDisciplines)
            if (d.students.contains(s))
                disciplines.add(d);

        return disciplines;
    }

    public Secretary getSecretary(Discipline discipline) {
        Course course = this.getCourse(discipline);

        SecretaryDAO secretaryDAO = new SecretaryDAO(currentSession().getSessionFactory());
        Secretary secretary = null;
        List<Secretary> secretaries = secretaryDAO.getAll();

        for (Secretary s : secretaries) {
            if (s.getCourses().contains(course)) {
                secretary = s;
                break;
            }
        }

        return secretary;
    }

    public Department getDepartment(Discipline discipline) {
        Secretary secretary = this.getSecretary(discipline);

        SecretaryDAO secretaryDAO = new SecretaryDAO(currentSession().getSessionFactory());
        return secretaryDAO.getDepartment(secretary);
    }

    public void deallocateTeacherFromAllDisciplines(Professor t) {
        List<Discipline> allDisciplines = this.getAll();
        for (Discipline d : allDisciplines) {
            assert d.professor != null;
            if (d.professor.getId().equals(t.getId())) {
                d.professor = null;
                this.persist(d);
            }
        }
    }

    public void jubiler(Student student) {
        List<Discipline> allDisciplines = this.getAll();

        for (Discipline d : allDisciplines) {

            if (d.students.contains(student)) {
                d.removeStudent(student);
                this.persist(d);
            }

        }
    }

}
