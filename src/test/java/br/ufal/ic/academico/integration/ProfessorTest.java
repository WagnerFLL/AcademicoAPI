package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.api.student.StudentDTO;
import br.ufal.ic.academico.api.professor.ProfessorDTO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class ProfessorTest extends BaseTest {
    private List<ProfessorDTO> professors = new ArrayList<>();

    @Test
    void runProfessorTest() {
        createProfessorList();
        getProfessorByID();
    }

    private void createProfessorList() {

        path = api + "professor";
        for (int i = 1; i <= 5; i++) {
            ProfessorDTO entity = new ProfessorDTO();
            entity.firstName = "Fabinho";
            ProfessorDTO response = RULE.client().target(path).request().post(Entity.json(entity), ProfessorDTO.class);

            professors.add(response);
            assertNotNull(response.id);
            assertNull(response.lastName);
            assertEquals(entity.firstName, response.firstName);
        }

        assertThrows(BadRequestException.class, () -> RULE.client().target(path)
                .request().post(Entity.json(new ProfessorDTO()), ProfessorDTO.class));
        assertEquals(professors.size(), RULE.client().target(api + "professor").request()
                .get(new GenericType<ArrayList<StudentDTO>>(){}).size());
    }

    private void getProfessorByID() {

        ProfessorDTO response = RULE.client().target(api + "professor/" + professors.get(0).id).request().get(ProfessorDTO.class);
        assertEquals(professors.get(0).id, response.id);
        response = RULE.client().target(api + "professor/" + professors.get(1).id).request().get(ProfessorDTO.class);
        assertEquals(professors.get(1).id, response.id);
        response = RULE.client().target(api + "professor/" + professors.get(2).id).request().get(ProfessorDTO.class);
        assertEquals(professors.get(2).id, response.id);
        response = RULE.client().target(api + "professor/" + professors.get(3).id).request().get(ProfessorDTO.class);
        assertEquals(professors.get(3).id, response.id);
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "professor/0").request().get(StudentDTO.class));
    }

}
