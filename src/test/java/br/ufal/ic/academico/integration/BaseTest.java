package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.AcademicoApp;
import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.DepartmentDTO;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import ch.qos.logback.classic.Level;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;

@ExtendWith(DropwizardExtensionsSupport.class)
abstract class BaseTest {

    static {
        BootstrapLogging.bootstrap(Level.DEBUG);
    }

    DropwizardAppExtension<ConfigApp> RULE = new DropwizardAppExtension(AcademicoApp.class,
            ResourceHelpers.resourceFilePath("config-test.yml"));

    String api;
    String path;

    @BeforeEach
    void setup() {
        this.api = "http://localhost:" + RULE.getLocalPort() + "/api/";
    }

    DepartmentDTO createDepartment(DropwizardAppExtension<ConfigApp> RULE, String departmentName) {
        return RULE.client().target(api + "department").request()
                .post(Entity.json(new DepartmentDTO(null, departmentName, null)), DepartmentDTO.class);
    }

    DisciplineDTO createDiscipline(DropwizardAppExtension<ConfigApp> RULE, CourseDTO course, String disciplineCode, String disciplineName, Integer disciplineCredits) {
        return RULE.client().target(api + "course/" + course.getId() + "/discipline")
                .request().post(Entity.json(new DisciplineDTO(null, disciplineCode, disciplineName, disciplineCredits, null, null, null, null)), DisciplineDTO.class);
    }

    SecretaryDTO createSecretary(DropwizardAppExtension<ConfigApp> RULE, DepartmentDTO department, String type) {
        return RULE.client().target(api + "department/" + department.getId() + "/secretary")
                .request().post(Entity.json(new SecretaryDTO(null, type, null)), SecretaryDTO.class);
    }

    CourseDTO createCourse(DropwizardAppExtension<ConfigApp> RULE, SecretaryDTO secretary, String courseName) {
        return RULE.client().target(api + "secretary/" + secretary.getId() + "/course")
                .request().post(Entity.json(new CourseDTO(null, courseName, null)), CourseDTO.class);
    }

}