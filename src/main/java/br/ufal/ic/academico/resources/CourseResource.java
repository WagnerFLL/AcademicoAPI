package br.ufal.ic.academico.resources;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDAO;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("course")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private final SecretaryDAO secretaryDAO;
    private final CourseDAO courseDAO;
    private final DisciplineDAO disciplineDAO;

    @GET
    @UnitOfWork
    public Response getAll() {
        log.info("GETALL courses");
        return Response.ok(courseListToDTOList(courseDAO.getAll())).build();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET course: id={}", id);

        Course c = courseDAO.get(id);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();

        return Response.ok(new CourseDTO(c)).build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, CourseDTO entity) {
        log.info("UPDATE course {} to {}", id, entity);

        Course c = courseDAO.get(id);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();

        c.update(entity);
        return Response.ok(new CourseDTO(courseDAO.persist(c))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE course {}", id);

        Course c = courseDAO.get(id);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();


        Secretary s = courseDAO.getSecretary(c);
        s.deleteCourse(c);

        secretaryDAO.persist(s);
        courseDAO.delete(c);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/disciplines")
    @UnitOfWork
    public Response getAllDisciplines(@PathParam("id") Long id) {
        log.info("GETALLL disciplines from course {}", id);

        Course c = courseDAO.get(id);
        if (c == null) {
            return Response.status(404).entity("Este curso não existe.").build();
        }
        
        assert c.getDisciplines() != null;
        return Response.ok(c.getDisciplines().stream().map(DisciplineDTO::new).toArray()).build();
    }

    @POST
    @Path("/{id}/discipline")
    @UnitOfWork
    @Consumes("application/json")
    public Response create(@PathParam("id") Long id, DisciplineDTO entity) {
        log.info("CREATE discipline in course {}", id);

        if (entity.getCode() == null) return Response.status(400).entity("Precisa fornecer o código da disciplina.").build();

        Course c = courseDAO.get(id);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();


        Discipline d = new Discipline(entity);
        disciplineDAO.persist(d);

        c.addDiscipline(d);
        courseDAO.persist(c);

        return Response.ok(new DisciplineDTO(d)).build();
    }

    private List<CourseDTO> courseListToDTOList(List<Course> list) {

        List<CourseDTO> dtoList = new ArrayList<>();
        if (list != null) list.forEach(c -> dtoList.add(new CourseDTO(c)));

        return dtoList;
    }
}
