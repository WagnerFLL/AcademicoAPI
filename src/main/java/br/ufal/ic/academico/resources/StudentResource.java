package br.ufal.ic.academico.resources;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDAO;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.student.StudentDAO;
import br.ufal.ic.academico.api.student.StudentDTO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("student")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    private final DisciplineDAO disciplineDAO;

    @GET
    @UnitOfWork
    public Response getAllStudents() {
        log.info("GETALL students");

        return Response.ok(studentListToDTOList(studentDAO.getAll())).build();
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    public Response create(StudentDTO entity) {
        log.info("CREATE student: {}", entity);

        if (entity.firstName == null) return Response.status(400).entity("Você precisa informar os dados.").build();
        Student s = new Student(entity);
        return Response.ok(new StudentDTO(studentDAO.persist(s))).build();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET student: id={}", id);

        Student s = studentDAO.get(id);
        if (s != null) return Response.ok(new StudentDTO(s)).build();
        return Response.status(404).entity("Este estudante não está matriculado.").build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, StudentDTO entity) {
        log.info("UPDATE student: id={}", id);

        Student s = studentDAO.get(id);
        if (s == null) return Response.status(404).entity("Este estudante não está matriculado.").build();
        s.update(entity);

        return Response.ok(new StudentDTO(studentDAO.persist(s))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE student: id={}", id);

        Student s = studentDAO.get(id);
        if (s == null) return Response.status(404).entity("Este estudante não está matriculado").build();

        disciplineDAO.jubiler(s);
        s.setCourse(null);
        studentDAO.persist(s);
        studentDAO.delete(s);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{idS}/course/{idC}")
    @UnitOfWork
    public Response matriculateOnCourse(@PathParam("idS") Long idS, @PathParam("idC") Long idC) {
        log.info("MATRICULATE student {} in course {}", idS, idC);

        Course c = courseDAO.get(idC);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();


        Student s = studentDAO.get(idS);
        if (s == null) return Response.status(404).entity("Este estudante não está matriculado.").build();

        if (s.getCourse() != null) return Response.status(400).entity("Este estudante não está matriculado.").build();

        s.setCourse(c);
        return Response.ok(new StudentDTO(studentDAO.persist(s))).build();
    }

    @GET
    @Path("/{sId}/discipline")
    @UnitOfWork
    public Response getAllDisciplines(@PathParam("sId") Long sId) {
        log.info("GETALL disciplines from student {} department", sId);

        Student student = studentDAO.get(sId);
        if (student == null) return Response.status(404).entity("Este estudante não está matriculado.").build();

        Department department = studentDAO.getDepartment(student);
        if (department == null) return Response.status(400).entity("Este estudante não pertence a nenhum departamento ainda.").build();


        List<DisciplineDTO> dtoList = new ArrayList<>();
        if (department.getGraduation() != null) {
            for (Course c : department.getGraduation().getCourses()) {
                List<Discipline> res = c.getDisciplines();
                if (res != null) dtoList.addAll(res.stream().map(DisciplineDTO::new).collect(Collectors.toList()));
            }
        }
        return Response.ok(dtoList).build();
    }

    @POST
    @Path("/{sId}/discipline/{id}")
    @UnitOfWork
    public Response matriculateInDiscipline(@PathParam("sId") Long sId, @PathParam("id") Long id) {
        log.info("MATICULATE student {} in discipline {}", sId, id);

        Student s = studentDAO.get(sId);
        if (s == null) return Response.status(404).entity("Este estudante não está matriculado.").build();

        Discipline d = disciplineDAO.get(id);
        if (d == null) return Response.status(404).entity("Esta disciplina não existe.").build();

        String res = d.matriculate(s, studentDAO.getDepartment(s), disciplineDAO.getDepartment(d),
                studentDAO.getSecretary(s), disciplineDAO.getSecretary(d));

        if (res != null) return Response.status(400).entity(res).build();

        return Response.ok(new DisciplineDTO(disciplineDAO.persist(d))).build();
    }

    @POST
    @Path("/{idS}/complete/{idD}")
    @UnitOfWork
    public Response completeDiscipline(@PathParam("idS") Long idS, @PathParam("idD") Long idD) {
        log.info("complete discipline {}, student {}", idD, idS);

        Student s = studentDAO.get(idS);
        if (s == null) return Response.status(404).entity("Este estudante não está matriculado.").build();

        Discipline d = disciplineDAO.get(idD);
        if (d == null) return Response.status(404).entity("Esta disciplina não existe.").build();

        if (!d.removeStudent(s)) return Response.status(400).entity("Este estudante não esteve matriculado nesta disciplina.").build();

        s.completeDiscipline(d);
        disciplineDAO.persist(d);
        return Response.ok(new StudentDTO(studentDAO.persist(s))).build();
    }

    private List<StudentDTO> studentListToDTOList(List<Student> list) {

        List<StudentDTO> dtoList = new ArrayList<>();
        list.forEach(s -> dtoList.add(new StudentDTO(s)));

        return dtoList;
    }
}
