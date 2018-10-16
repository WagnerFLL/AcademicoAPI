package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.DepartmentDTO;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(DropwizardExtensionsSupport.class)
class CourseTest extends BaseTest {

    @Test
    void courseResources() {

        assertEquals(0, RULE.client().target(api + "course").request().get(new GenericType<List<CourseDTO>>(){}).size());

        DepartmentDTO department = createDepartment(RULE, "IC");
        SecretaryDTO secretary = createSecretary(RULE, department, "GRADUATION");
        department.secretaries.add(secretary);
        CourseDTO course = createCourse(RULE, secretary, "Computação");
        getCourse(course);

        course = createCourse(RULE, secretary, "Sistemas de Informação");
        getAllDisciplinesFromCourse(course, 0);
        createDiscipline(course, "CC001", "Programação 1", 25, 36, new ArrayList<>());

    }

    private void getCourse(CourseDTO course) {
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "course/0").request().get(CourseDTO.class));

        CourseDTO response = RULE.client().target(api + "course/" + course.getId()).request().get(CourseDTO.class);

        assertAll(
                () -> assertEquals(course.id, response.id),
                () -> assertEquals(course.name, response.name),
                () -> assertEquals(course.getDisciplines().size(), response.getDisciplines().size())
        );

    }

    private void getAllDisciplinesFromCourse(CourseDTO course, int expectedDisciplinesQuantity) {
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "course/0/disciplines")
                        .request().get(new GenericType<List<DisciplineDTO>>() {}));

        List<DisciplineDTO> response = RULE.client().target(api + "course/" + course.getId() + "/disciplines")
                .request().get(new GenericType<List<DisciplineDTO>>() {});

        assertEquals(expectedDisciplinesQuantity, response.size());
    }

    private DisciplineDTO createDiscipline(CourseDTO course, String code, String name, int credits,
                                           int requiredCredits, List<String> requiredDisciplines) {

        DisciplineDTO entity = new DisciplineDTO();

        assertAll(
                () -> {
                    entity.code = code;
                    entity.name = name;
                    path = api + "course/0/discipline";
                    assertThrows(NotFoundException.class, () -> RULE.client().target(path).request().
                            post(Entity.json(entity), DisciplineDTO.class));
                },

                () -> {
                    path = api + "course/" + course.getId() + "/discipline";
                    assertThrows(BadRequestException.class, () -> RULE.client().target(path).request()
                            .post(Entity.json(new DisciplineDTO()), DisciplineDTO.class));
                },

                () -> {
                    entity.code = code;
                    entity.name = null;
                    path = api + "course/" + course.getId() + "/discipline";
                    assertThrows(BadRequestException.class, () -> RULE.client().target(path).request()
                                    .post(Entity.json(entity), DisciplineDTO.class));
                },

                () -> {
                    entity.name = "";
                    path = api + "course/" + course.getId() + "/discipline";
                    assertThrows(BadRequestException.class, () -> RULE.client().target(path).request()
                                    .post(Entity.json(entity), DisciplineDTO.class));
                }


        );

        entity.name = name;
        entity.credits = credits;
        entity.requiredCredits = requiredCredits;
        entity.requiredDisciplines = requiredDisciplines;
        path  = api + "course/" + course.getId() + "/discipline";
        DisciplineDTO response = RULE.client().target(path)
                .request().post(Entity.json(entity), DisciplineDTO.class);

        assertAll(
                () -> assertNotNull(response.id),
                () -> assertEquals(code, response.code),
                () -> assertEquals(name, response.name),
                () -> assertEquals(credits, (int) response.credits),
                () -> assertEquals(requiredCredits, (int) response.requiredCredits),
                () -> assertEquals(0, response.students.size()),
                () -> assertNull(response.professor),
                () -> assertEquals(requiredDisciplines.size(), response.requiredDisciplines.size())
        );

        return response;
    }
}
