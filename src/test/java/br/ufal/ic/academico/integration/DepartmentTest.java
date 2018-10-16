package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.api.department.DepartmentDTO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class DepartmentTest extends BaseTest {

    @Test
    void departmentResources() {
        assertEquals(0, RULE.client().target(api + "department").request().get(new GenericType<List<DepartmentDTO>>(){}).size());

        DepartmentDTO department = createDepartment();
        getDepartmentByID(department);
        department = createDepartment();
        createSecretary(department, "POST-GRADUATION");
        createSecretary(department, "GRADUATION");
        getDepartmentSecretaries(department);

    }

    private DepartmentDTO createDepartment() {

        assertThrows(BadRequestException.class, () ->   RULE.client().target(api + "department").request()
                .post(Entity.json(new DepartmentDTO()), DepartmentDTO.class));

        DepartmentDTO entity = new DepartmentDTO();
        entity.name = "IC";
        DepartmentDTO response = RULE.client().target(api + "department").request().post(Entity.json(entity), DepartmentDTO.class);

        assertNotNull(response.id);
        assertEquals(entity.name, response.name);
        assertEquals(0, response.secretaries.size());

        return response;
    }

    private void getDepartmentByID(DepartmentDTO department) {

        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "department/0")
                .request().get(DepartmentDTO.class));
        path = api + "department/" + department.getId();
        DepartmentDTO response =  RULE.client().target(path).request().get(DepartmentDTO.class);

        assertAll(
                () -> assertEquals(department.id, response.id),
                () -> assertEquals(department.name, response.name),
                () -> assertEquals(department.secretaries.size(), response.secretaries.size())
        );

    }

    private void createSecretary(DepartmentDTO department, String type) {

        path = api + "department/0/secretary";
        assertThrows(NotFoundException.class, () -> RULE.client().target(path)
                .request().post(Entity.json(new SecretaryDTO()), SecretaryDTO.class));


        SecretaryDTO entity = new SecretaryDTO();
        entity.type = type;
        path = api + "department/" + department.getId() + "/secretary";
        SecretaryDTO response = RULE.client().target(path).request().post(Entity.json(entity), SecretaryDTO.class);

        assertNotNull(response.id);
        path = api + "department/" + department.getId() + "/secretary";
        assertThrows(BadRequestException.class, () -> RULE.client().target(path).request().post(Entity.json(entity), DepartmentDTO.class));

        department.secretaries.add(response);
    }

    private void getDepartmentSecretaries(DepartmentDTO department) {

        path = api + "department/0/secretaries";
        assertThrows(NotFoundException.class, () -> RULE.client().target(path).request().get(new GenericType<List<SecretaryDTO>>(){}));

        path = api + "department/" + department.getId() + "/secretaries";
        List<SecretaryDTO> response = RULE.client().target(path).request().get(new GenericType<List<SecretaryDTO>>(){});

        assertEquals(department.secretaries.size(), response.size());
    }
}
