package br.ufal.ic.academico.resources;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("secretary")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class SecretaryResource {
    private final DepartmentDAO departmentDAO;
    private final SecretaryDAO secretaryDAO;
    private final CourseDAO courseDAO;

    @GET
    @UnitOfWork
    public Response getAll() {
        log.info("GETALL secretaries");
        return Response.ok(secretaryListToDTOList(secretaryDAO.getAll())).build();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET secretary: id={}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) return Response.status(404).entity("Secretaria não existe.").build();

        return Response.ok(new SecretaryDTO(s)).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE secretary: id={}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) return Response.status(404).entity("Secretaria não existe.").build();

        Department d = secretaryDAO.getDepartment(s);

        if (s.getType().equals("GRADUATION")) d.setGraduation(null);

        else d.setPostGraduation(null);

        departmentDAO.persist(d);
        secretaryDAO.delete(s);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/courses")
    @UnitOfWork
    public Response getAllCourses(@PathParam("id") Long id) {
        log.info("GETALL courses from secretary {}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) return Response.status(404).entity("Secretaria não existe.").build();

        return Response.ok(s.getCourses().stream().map(Course::getName).toArray()).build();
    }

    @POST
    @Path("/{id}/course")
    @UnitOfWork
    @Consumes("application/json")
    public Response createCourse(@PathParam("id") Long id, CourseDTO entity) {
        log.info("CREATE course on secretary {}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) return Response.status(404).entity("Secretaria não existe.").build();


        Course c = new Course(entity);
        if (s.addCourse(c)) {
            courseDAO.persist(c);
            secretaryDAO.persist(s);
            return Response.ok(new CourseDTO(c)).build();
        }

        return Response.status(400).build();
    }

    private List<SecretaryDTO> secretaryListToDTOList(List<Secretary> list) {

        List<SecretaryDTO> dtoList = new ArrayList<>();
        if (list != null) list.forEach(s -> dtoList.add(new SecretaryDTO(s)));

        return dtoList;
    }
}
