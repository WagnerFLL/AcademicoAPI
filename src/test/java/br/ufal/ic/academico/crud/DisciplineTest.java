package br.ufal.ic.academico.crud;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDAO;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.secretary.Secretary;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class DisciplineTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Professor.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Discipline.class)
            .build();

    private DisciplineDAO dao = new DisciplineDAO(dbTesting.getSessionFactory());

    @Test
    void discipline() {

        final Discipline d1 = create("Programação 1", "CC001", 80, 0, new ArrayList<>());
        get(d1);
        d1.setProfessor(new Professor("Bado", "Ino"));
        d1.setCredits(80);
        d1.setRequiredCredits(0);

        List<String> preRequisites = new ArrayList<>();
        preRequisites.add("CC002");
        preRequisites.add("CC003");
        d1.setRequiredDisciplines(preRequisites);

        update(d1);
        delete(d1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(), "Discipline1 não foi removido da listagem total de Disciplines");

        final Discipline d2 = create("Programação 2", "CC002", 0, 0, new ArrayList<>());
        get(d2);
        final Discipline d3 = create("Teste de Software", "CC003", 0, 0, new ArrayList<>());
        get(d3);

    }

    private Discipline create(String name, String code, Integer credits, Integer requiredCredits, List<String> requiredDisciplines) {

        final Discipline discipline = new Discipline(name, code, credits, requiredCredits, requiredDisciplines);
        final Discipline discipline2 = dbTesting.inTransaction(() -> dao.persist(discipline));

        assertAll(
                () -> assertNull(dbTesting.inTransaction(() -> dao.getCourse(discipline)), "Discipline foi associada a um Course ao ser criada"),
                () -> assertNull(dbTesting.inTransaction(() -> dao.getSecretary(discipline)), "Discipline foi associada a uma Secretary ao ser criada"),
                () -> assertNotNull(discipline2.getId(), "Discipline não recebeu um id ao ser criada"),
                () -> assertEquals(code, discipline2.getCode(), "Code da Discipline não corresponde com o informado"),
                () -> assertEquals(name, discipline2.getName(), "Name da Discipline não corresponde com o informado"),
                () -> assertEquals(credits, discipline2.getCredits(), "Credits não corresponde com o informado"),
                () -> assertEquals(requiredCredits, discipline2.getRequiredCredits(), "Required Credits não corresponde com o informado"),
                () -> assertEquals(requiredDisciplines.size(), discipline2.getRequiredDisciplines().size(), "Pré-requisitos foram associados incorretamente"),
                () -> assertNull(discipline2.getProfessor(), "Um professor foi associado à nova Discipline"),
                () -> assertEquals(new ArrayList<>(), discipline2.getStudents(), "Aluno(s) foi(ram) associado(s) à nova Discipline"),
                () -> assertNotNull(discipline2, "Falhou ao salvar uma nova Discipline")
        );

        return discipline;
    }

    private void get(Discipline discipline) {

        Discipline recovered = dbTesting.inTransaction(() -> dao.get(discipline.getId()));

        assertAll(
                () -> assertEquals(discipline.getId(), recovered.getId(), "ID da Discipline recuperada não confere com o informado"),
                () -> assertEquals(discipline.getName(), recovered.getName(), "Name da Discipline recuperada não confere com o informada"),
                () -> assertEquals(discipline.getCode(), recovered.getCode(), "Code da Discipline recuperada não confere com o informado"),
                () -> assertEquals(discipline.getCredits(), recovered.getCredits(), "Credits da Discipline recuperada não confere com o informado"),
                () -> assertEquals(discipline.getRequiredCredits(), recovered.getRequiredCredits(), "Required Credits da Discipline recuperada não confere com o informado"),
                () -> assertEquals(discipline.getRequiredDisciplines().size(), recovered.getRequiredDisciplines().size(), "Quantidade de Required Disciplines da Discipline recuperada não confere com a informada")
        );

    }

    private void update(Discipline discipline) {
        final Discipline updated = dbTesting.inTransaction(() -> dao.persist(discipline));

        assertAll(
                () -> assertEquals(discipline.getId(), updated.getId(), "Ao ser atualizada, Discipline teve seu ID alterado"),
                () -> assertEquals(discipline.getName(), updated.getName(), "Name da Discipline não foi alterado corretamente"),
                () -> assertEquals(discipline.getCode(), updated.getCode(), "Code da Discipline não foi alterado corretamente"),
                () -> assertEquals(discipline.getStudents().size(), updated.getStudents().size(), "Lista de Students foi alterada incorretamente"),
                () -> assertEquals(discipline.getCredits(), updated.getCredits(), "O valor de credits da Discipline não foi atualizado corretamente"),
                () -> assertEquals(discipline.getRequiredCredits(), updated.getRequiredCredits(), "Required credits não foi atualizado corretamente"),
                () -> assertEquals(discipline.getRequiredDisciplines().size(), updated.getRequiredDisciplines().size(), "Pré-requisitos não foram atualizados corretamente"),
                () -> {
                    if (discipline.getProfessor() != null) {
                        assertNotNull(updated.getProfessor(), "Nenhum Teacher foi associado à Discipline");
                        assertEquals(discipline.getProfessor().getId(), updated.getProfessor().getId(), "Teacher correto não foi associado à Discipline");
                    } else {
                        assertNull(updated.getProfessor(), "Teacher foi associado à Discipline ao atualizá-la");
                    }
                }
        );
    }

    private void delete(Discipline discipline) {
        dbTesting.inTransaction(() -> dao.delete(discipline));
        assertNull(dbTesting.inTransaction(() -> dao.get(discipline.getId())), "Discipline não foi removida");
    }
}