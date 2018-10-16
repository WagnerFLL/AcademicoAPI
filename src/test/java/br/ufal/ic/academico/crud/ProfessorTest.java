package br.ufal.ic.academico.crud;

import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.professor.ProfessorDAO;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class ProfessorTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Professor.class)
            .build();

    private ProfessorDAO dao = new ProfessorDAO(dbTesting.getSessionFactory());

    @Test
    void professor() {

        final Professor t1 = create("Willy", "Carvalho Tiengo");
        get(t1);
        update(t1, "Will", "Tiengo");
        delete(t1);
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(), "Teacher1 não foi removido da listagem de todos os Teachers");

        final Professor t2 = create("Rodrigo", "Paes");
        get(t2);
        final Professor t3 = create("Márcio", "Ribeiro");
        get(t3);
    }

    private Professor create(String firstName, String lastName) {
        final Professor professor = new Professor(firstName, lastName);
        final Professor professor2 = dbTesting.inTransaction(() -> dao.persist(professor));

        assertAll(
                () -> assertNotNull(professor2, "Falhou ao salvar um novo Teacher"),
                () -> assertNotNull(professor2.getId(), "Teacher não recebeu um id ao ser criado"),
                () -> assertEquals(professor.getFirstName(), professor2.getFirstName(), "First name do Teacher não corresponde com o informado"),
                () -> assertEquals(professor.getLastName(), professor2.getLastName(), "Last name do Teacher não corresponde com o informado")
        );

        return professor;
    }

    private void get(Professor profes) {
        Professor professorNew = dbTesting.inTransaction(() -> dao.get(profes.getId()));
        assertAll(
                () -> assertEquals(profes.getId(), professorNew.getId(), "ID do Teacher recuperado não confere com o informado"),
                () -> assertEquals(profes.getFirstName(), professorNew.getFirstName(), "First Name do Teacher recuperado não confere com o informado"),
                () -> assertEquals(profes.getLastName(), professorNew.getLastName(), "Last Name do Teacher recuperado não confere com o informado")
        );
    }

    private void update(Professor pr, String newFirstName, String newLastName) {

        pr.setFirstName("Will");
        pr.setLastName("Tiengo");
        final Professor updated = dbTesting.inTransaction(() -> dao.persist(pr));
        assertEquals(pr.getFirstName(), updated.getFirstName(), "First name não foi atualizado corretamente");
        assertEquals(pr.getLastName(), updated.getLastName(), "Last name não foi atualizado corretamente");
    }

    private void delete(Professor teacher) {
        dbTesting.inTransaction(() -> dao.delete(teacher));
        assertNull(dbTesting.inTransaction(() -> dao.get(teacher.getId())), "Teacher não foi removido");
    }
}