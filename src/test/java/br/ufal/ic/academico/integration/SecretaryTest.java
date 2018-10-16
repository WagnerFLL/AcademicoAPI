package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.DepartmentDTO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class SecretaryTest extends BaseTest {

    @Test
    void secretaryResources() {
        assertEquals(0, RULE.client().target(api + "secretary").request().get(new GenericType<List<SecretaryDTO>>(){}).size());

        DepartmentDTO department = createDepartment(RULE, "IC");
        SecretaryDTO secretary = createSecretary(RULE, department, "GRADUATION");
        department.secretaries.add(secretary);
        getSecretaryByID(secretary);

        secretary = createSecretary(RULE, department, "POST-GRADUATION");
        department.secretaries.add(secretary);
        getSecretaryByID(secretary);

        getCoursesFromSecretary(secretary, 0);
        createCourse(secretary, "Computação");
        getCoursesFromSecretary(secretary, 1);
    }

    private void getSecretaryByID(SecretaryDTO secretary) {
        path = api + "secretary/" + secretary.getId();
        SecretaryDTO response = RULE.client().target(path).request().get(SecretaryDTO.class);
        assertEquals(secretary.id, response.id);
        assertEquals(secretary.type, response.type);
        assertEquals(secretary.disciplines, response.disciplines);
    }

    private void getCoursesFromSecretary(SecretaryDTO secretary, int expectedCoursesQuantity) {
        path = api + "secretary/" + secretary.getId() + "/courses";
        List<String> response = RULE.client().target(path).request()
                .get(new GenericType<List<String>>(){});
        assertEquals(expectedCoursesQuantity, response.size());
    }

    private void createCourse(SecretaryDTO secretary, String courseName) {

        CourseDTO entity = new CourseDTO();
        entity.name = courseName;

        path = api + "secretary/" + secretary.getId() + "/course";
        CourseDTO response = RULE.client().target(path).request().post(Entity.json(entity), CourseDTO.class);
        assertNotNull(response.getId());
        assertEquals(courseName, response.name);
        assertEquals(0, response.getDisciplines().size());
    }
}
