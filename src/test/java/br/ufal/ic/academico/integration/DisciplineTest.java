package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.DepartmentDTO;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class DisciplineTest extends BaseTest {

    @Test
    void disciplineResources() {
        assertEquals(0, RULE.client().target(api + "discipline").request().get(new GenericType<List<DisciplineDTO>>(){}).size());

        DepartmentDTO department = createDepartment(RULE, "IC");
        SecretaryDTO secretary = createSecretary(RULE, department, "GRADUATION");
        CourseDTO course = createCourse(RULE, secretary, "Ciência da Computação");
        DisciplineDTO discipline = createDiscipline(RULE, course, "P5", "Teste de Software", 80);
        course.getDisciplines().add(discipline);
        getByID(discipline);

        List<String> requiredDisciplines = new ArrayList<>();
        requiredDisciplines.add("A93");
        discipline = update(discipline);
        deleteDiscipline(discipline, course);
    }

    private void getByID(DisciplineDTO discipline) {
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "discipline/0").request().get(DisciplineDTO.class));

        DisciplineDTO response = RULE.client().target(api + "discipline/" + discipline.getId()).request().get(DisciplineDTO.class);

        assertAll(
                () -> assertEquals(discipline.id, response.id),
                () -> assertEquals(discipline.code, response.code),
                () -> assertEquals(discipline.professor, response.professor),
                () -> assertEquals(discipline.credits, response.credits),
                () -> assertEquals(discipline.requiredCredits, response.requiredCredits),
                () -> assertEquals(discipline.requiredDisciplines.size(), response.requiredDisciplines.size())
        );
    }

    private DisciplineDTO update(DisciplineDTO discipline) {

        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "discipline/0").request().put(Entity.json(new DisciplineDTO()), DisciplineDTO.class));

        DisciplineDTO entity = new DisciplineDTO();
        DisciplineDTO response = RULE.client().target(api + "discipline/" + discipline.getId()).request().put(Entity.json(entity), DisciplineDTO.class);

        assertAll(
                () -> assertEquals(discipline.id, response.id),
                () -> assertEquals(discipline.code, response.code),
                () -> assertEquals(discipline.name, response.name),
                () -> assertEquals((int) discipline.credits, 80),
                () -> assertEquals(discipline.requiredCredits, response.requiredCredits),
                () -> assertEquals(discipline.professor, response.professor),
                () -> assertEquals(discipline.students.size(), response.students.size()),
                () -> assertEquals(discipline.requiredDisciplines.size(), response.requiredDisciplines.size())
        );

        return response;
    }

    private void deleteDiscipline(DisciplineDTO discipline, CourseDTO course) {


        RULE.client().target(api + "discipline/" + discipline.getId()).request().delete(Discipline.class);
        CourseDTO response = RULE.client().target(api + "course/" + course.getId()).request().get(CourseDTO.class);

        assertEquals(course.id, response.id);
        assertEquals(course.name, response.name);

        course.getDisciplines().remove(discipline);
    }
}
