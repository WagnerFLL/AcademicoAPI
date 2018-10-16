package br.ufal.ic.academico.crud;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.secretary.Secretary;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class CourseTest {

    private DAOTestExtension daoTestExtension = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Professor.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Discipline.class)
            .build();

    private CourseDAO dao = new CourseDAO(daoTestExtension.getSessionFactory());

    @Test
    void course() {

        final Course c1 = create("Ciência da Computação");
        get(c1);
        c1.setName(" ");
        c1.addDiscipline(new Discipline());
        update(c1);
        delete(c1);

        assertEquals(0, daoTestExtension.inTransaction(dao::getAll).size(),
                "Course1 não foi removido da listagem total de Courses");
    }


    private Course create(String name) {

        final Course course = new Course(name);
        final Course other = daoTestExtension.inTransaction(() -> dao.persist(course));

        assertAll(
                () -> assertNotNull(other, "Falhou ao salvar um novo Course"),
                () -> assertNotNull(other.getId(), "Course não recebeu um id ao ser criado"),
                () -> assertEquals(name, other.getName(), "Name do Course não corresponde com o informado"),
                () -> assertNotNull(other.getDisciplines(), "Course não recebeu uma lista vazia de Disciplines"),
                () -> assertEquals(0, other.getDisciplines().size(), "Course foi criado com DisciplineTest(s) associada(s)"),
                () -> assertNull(daoTestExtension.inTransaction(() -> dao.getSecretary(other)), "Course foi associado à uma Secretary")
        );

        return course;
    }

    private void get(Course course) {
        Course recovered = daoTestExtension.inTransaction(() -> dao.get(course.getId()));

        assertAll(
                () -> assertEquals(course.getId(), recovered.getId(), "ID do Course recuperado não confere com o informado"),
                () -> assertEquals(course.getDisciplines().size(), recovered.getDisciplines().size(), "Quantidade de Disciplines do Course recuperado não confere com a informada"),
                () -> assertEquals(course.getName(), recovered.getName(), "Name do Course recuperado não confere com o informado")
        );

    }

    private void update(Course course) {
        final Course newCourse = daoTestExtension.inTransaction(() -> dao.persist(course));

        assertAll(
                () -> assertEquals(course.getId(), newCourse.getId(), "Ao ser atualizado, Course teve seu ID alterado"),
                () -> assertEquals(course.getName(), newCourse.getName(), "Name do Course não foi atualizado corretamente"),
                () -> assertNotNull(newCourse.getDisciplines(), "Ao ser atualizado, Course teve sua lista de Disciplines deletada"),
                () -> assertEquals(course.getDisciplines().size(), newCourse.getDisciplines().size(), "Discpline não foi associada corretamente")
        );
    }

    private void delete(Course course) {
        daoTestExtension.inTransaction(() -> dao.delete(course));
        assertNull(daoTestExtension.inTransaction(() -> dao.get(course.getId())), "Course não foi removido");
    }
}