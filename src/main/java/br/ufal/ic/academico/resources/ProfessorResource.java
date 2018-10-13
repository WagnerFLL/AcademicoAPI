package br.ufal.ic.academico.resources;

import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDAO;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.professor.ProfessorDAO;
import br.ufal.ic.academico.api.professor.ProfessorDTO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("professor")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

    private final ProfessorDAO professorDAO;
    private final DisciplineDAO disciplineDAO;

    @GET
    @UnitOfWork
    public Response getAll() {
        log.info("GETALL professors");
        return Response.ok(professorListToDTOList(professorDAO.getAll())).build();
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    public Response create(ProfessorDTO entity) {
        log.info("CREATE professor: {}", entity);

        if (entity.firstName == null) return Response.status(400).entity("Você precisa informar os dados.").build();
        Professor t = new Professor(entity);
        return Response.ok(new ProfessorDTO(professorDAO.persist(t))).build();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET professor: id={}", id);

        Professor t = professorDAO.get(id);
        if (t != null) return Response.ok(new ProfessorDTO(t)).build();
        return Response.status(404).entity("Este professor não está registrado.").build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, ProfessorDTO entity) {
        log.info("UPDATE professor: id={}", id);

        Professor t = professorDAO.get(id);
        if (t == null) return Response.status(404).entity("Este professor não está registrado.").build();
        t.update(entity);
        return Response.ok(new ProfessorDTO(professorDAO.persist(t))).build();
    }

    @POST
    @Path("/{idP}/discipline/{idD}")
    @UnitOfWork
    public Response allocate(@PathParam("idP") Long idP, @PathParam("idD") Long idD) {
        log.info("ALLOCATE professor {} in discipline {}", idP, idD);

        Professor t = professorDAO.get(idP);
        if (t == null) return Response.status(404).entity("Este professor não está registrado.").build();

        Discipline d = disciplineDAO.get(idD);
        if (d == null) return Response.status(404).entity("Esta disciplina não está registrado.").build();

        d.setProfessor(t);
        return Response.ok(new DisciplineDTO(disciplineDAO.persist(d))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE professor: id={}", id);

        Professor t = professorDAO.get(id);
        if (t == null) return Response.status(404).entity("Este professor não está registrado.").build();

        disciplineDAO.deallocateTeacherFromAllDisciplines(t);
        professorDAO.delete(t);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private List<ProfessorDTO> professorListToDTOList(List<Professor> list) {

        List<ProfessorDTO> dtoList = new ArrayList<>();
        list.forEach(s -> dtoList.add(new ProfessorDTO(s)));

        return dtoList;
    }

}
