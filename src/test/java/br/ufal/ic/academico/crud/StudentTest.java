package br.ufal.ic.academico.crud;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.student.StudentDAO;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.secretary.Secretary;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class StudentTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Professor.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Discipline.class)
            .build();

    private StudentDAO dao = new StudentDAO(dbTesting.getSessionFactory());

    @Test
    void student() {
        final Student student = create("Gustavo", "Borges");
        get(student);

        for (int i = 0; i < 50; i++) {
            Integer credits = new Random().nextInt();
            student.setCredits(credits);
            update(student);
        }

        student.setLastName("Igor");
        student.setLastName("Silva");
        update(student);
        delete(student);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Student1 não foi removido da listagem de todos os Students");

        final Student s2 = create("Lucas", "Raggi");
        get(s2);
        final Student s3 = create("Gabriel", "Barbosa");
        get(s3);
    }

    private Student create(String firstName, String lastName) {
        final Student student = new Student(firstName, lastName);
        final Student student1 = dbTesting.inTransaction(() -> dao.persist(student));

        assertAll(
                () -> assertNotNull(student1, "Falhou ao salvar um novo Student"),
                () -> assertNotNull(student1.getId(), "Student não recebeu um id ao ser criado"),
                () -> assertEquals(student.getFirstName(), student1.getFirstName(), "First name do Student não corresponde com o informado"),
                () -> assertEquals(student.getLastName(), student1.getLastName(), "Last name do Student não corresponde com o informado"),
                () -> assertEquals(new Integer(0), student1.getCredits(), "Student foi cadastro com Credits diferente de 0"),
                () -> assertNull(student1.getCourse(), "Student recebeu um curso ao ser criado"),
                () -> assertEquals(new ArrayList<>(), student1.getCompletedDisciplines(), "Student recebeu uma lista de matérias concluídas ao ser criado"),
                () -> assertNull(dbTesting.inTransaction(() -> dao.getDepartment(student1)), "Student foi vinculado à um Department ao ser criado"),
                () -> assertNull(dbTesting.inTransaction(() -> dao.getSecretary(student1)), "Student foi vinculado à uma Secretary ao ser criado")
        );

        return student;
    }

    private void get(Student student) {
        Student recovered = dbTesting.inTransaction(() -> dao.get(student.getId()));

        assertAll(
                () -> assertEquals(student.getId(), recovered.getId(), "ID do Student recuperado não confere com o informado"),
                () -> assertEquals(student.getFirstName(), recovered.getFirstName(), "First Name do Student recuperado não confere com o informado"),
                () -> assertEquals(student.getLastName(), recovered.getLastName(), "Last Name do Student recuperado não confere com o informado"),
                () -> assertEquals(student.getCredits(), recovered.getCredits(), "Credits do Student recuperado não confere com o informado"),
                () -> assertEquals(student.getCompletedDisciplines().size(), recovered.getCompletedDisciplines().size(), "Quantidade de Completed Disciplines do Student recuperado não confere com a informada")
        );

    }

    private void update(Student student) {
        final Student updated = dbTesting.inTransaction(() -> dao.persist(student));

        assertAll(
                () -> assertEquals(student.getCredits(), updated.getCredits(), "Créditos não foram atualizados corretamente"),
                () -> assertEquals(student.getFirstName(), updated.getFirstName(), "First name não foi atualizado corretamente"),
                () -> assertEquals(student.getLastName(), updated.getLastName(), "Last name não foi atualizado corretamente")
        );
    }

    private void delete(Student student) {
        dbTesting.inTransaction(() -> dao.delete(student));
        assertNull(dbTesting.inTransaction(() -> dao.get(student.getId())), "Student não foi removido");
    }
}