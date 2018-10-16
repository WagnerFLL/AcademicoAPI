package br.ufal.ic.academico.crud;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
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
class DepartmentTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Professor.class)
            .addEntityClass(Department.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Discipline.class)
            .build();

    private DepartmentDAO dao = new DepartmentDAO(dbTesting.getSessionFactory());

    @Test
    void department() {

        final Department d1 = create("IC");
        get(d1);
        d1.setName("FDA");
        d1.setGraduation(new Secretary());
        d1.setPostGraduation(new Secretary());
        update(d1);
        delete(d1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(), "Department não foi removido da listagem total de Department");

    }

    private void get(Department department) {
        Department department1 = dbTesting.inTransaction(() -> dao.get(department.getId()));

        assertAll(
                () -> assertEquals(department.getId(), department1.getId(), "ID do Department recuperado não confere com o informado"),
                () -> assertEquals(department.getName(), department1.getName(), "Name do Department recuperado não confere com o informado"),
                () -> {
                    if (department.getGraduation() == null) {
                        assertNull(department1.getGraduation(), "Graduation Secretary do Department recuperado não confere com o informado");
                    } else {
                        assertEquals(department.getGraduation().getId(), department1.getGraduation().getId(),
                                "Graduation Secretary do Department recuperado não confere com o informado");
                    }
                },
                () -> {
                    if (department.getPostGraduation() != null) {
                        assertEquals(department.getPostGraduation().getId(), department1.getPostGraduation().getId(),
                                "Post Graduation Secretary do Department recuperado não confere com o informado");
                    } else {
                        assertNull(department1.getPostGraduation(), "Post Graduation Secretary do Department recuperado não confere com o informado");
                    }
                }
        );
    }

    private void update(Department department) {

        final Department updated = dbTesting.inTransaction(() -> dao.persist(department));

        assertAll(
                () -> assertEquals(department.getId(), updated.getId(), "Ao ser atualizado, Department teve seu ID alterado"),
                () -> assertEquals(department.getName(), updated.getName(), "Name do Department não foi atualizado corretamente"),
                () -> assertEquals(department.getGraduation().getId(), updated.getGraduation().getId(), "Secretaria de graduação associada incorretamente"),
                () -> assertEquals(department.getPostGraduation().getId(), updated.getPostGraduation().getId(), "Secretaria de pós graduação associada incorretamente")
        );
    }

    private Department create(String name) {

        final Department department = new Department(name);
        final Department saved = dbTesting.inTransaction(() -> dao.persist(department));

        assertAll(
                () -> assertNotNull(saved, "Falhou ao salvar um novo Department"),
                () -> assertNotNull(saved.getId(), "Department não recebeu um id ao ser criado"),
                () -> assertEquals(department.getName(), saved.getName(), "Name do Department criado não corresponde com o informado"),
                () -> assertNull(saved.getGraduation(), "Department recebeu uma secretaria de graduação ao ser criado"),
                () -> assertNull(saved.getPostGraduation(), "Department recebeu uma secretaria de pós graduação ao ser criado")
        );

        return department;
    }

    private void delete(Department department) {
        dbTesting.inTransaction(() -> dao.delete(department));
        assertNull(dbTesting.inTransaction(() -> dao.get(department.getId())), "Department não foi removido");
    }
}
