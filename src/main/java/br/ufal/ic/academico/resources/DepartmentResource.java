package br.ufal.ic.academico.resources;

import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
import br.ufal.ic.academico.api.department.DepartmentDTO;
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

@Path("department")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentResource {

    private final DepartmentDAO departmentDAO;
    private final SecretaryDAO secretaryDAO;

    @GET
    @UnitOfWork
    public Response getAll() {
        log.info("getAll departments");

        return Response.ok(departmentListToDTOList(departmentDAO.getAll())).build();
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    public Response create(DepartmentDTO entity) {
        log.info("CREATE department: {}", entity);

        Department d = new Department(entity);
        return Response.ok(new DepartmentDTO(departmentDAO.persist(d))).build();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET department: id={}", id);

        Department d = departmentDAO.get(id);
        if (d == null) return Response.status(404).entity("Departmento não existe.").build();

        return Response.ok(new DepartmentDTO(d)).build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, DepartmentDTO entity) {
        log.info("UPDATE department: id={}", id);

        Department d = departmentDAO.get(id);
        if (d == null) return Response.status(404).entity("Departmento não existe.").build();

        d.update(entity);
        return Response.ok(new DepartmentDTO(departmentDAO.persist(d))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE department: id={}", id);

        Department d = departmentDAO.get(id);
        if (d == null) return Response.status(404).entity("Departmento não existe.").build();

        departmentDAO.delete(d);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{id}/secretaries")
    @UnitOfWork
    public Response getAllSecretariesFromDepartment(@PathParam("id") Long id) {
        log.info("GETALL secretaries from department {}", id);

        Department d = departmentDAO.get(id);
        if (d == null) return Response.status(404).entity("Departmento não existe.").build();


        ArrayList<SecretaryDTO> secretaries = new ArrayList<>();
        if (d.getGraduation() != null) secretaries.add(new SecretaryDTO(d.getGraduation()));

        if (d.getPostGraduation() != null) secretaries.add(new SecretaryDTO(d.getPostGraduation()));

        return Response.ok(secretaries).build();
    }

    @POST
    @Path("/{id}/secretary")
    @UnitOfWork
    @Consumes("application/json")
    public Response create(@PathParam("id") Long id, SecretaryDTO entity) {
        log.info("CREATE secretary on department {}", id);

        Department d = departmentDAO.get(id);
        if (d == null) return Response.status(404).entity("Departmento não existe.").build();


        if (!entity.type.toUpperCase().equals("GRADUATION")
                && !entity.type.toUpperCase().equals("POST-GRADUATION"))
            return Response.status(400).entity("Invalid secretary type.").build();


        Secretary s = new Secretary(entity);
        secretaryDAO.persist(s);

        if (s.getType().equals("GRADUATION")) {
            if (d.getGraduation() != null)
                return Response.status(400).entity("Esta secretaria já existe!.").build();
            d.setGraduation(s);
        } else {
            if (d.getPostGraduation() != null)
                return Response.status(400).entity("Esta secretaria já existe!.").build();
            d.setPostGraduation(s);
        }

        departmentDAO.persist(d);
        return Response.ok(new SecretaryDTO(s)).build();
    }

    private List<DepartmentDTO> departmentListToDTOList(List<Department> list) {

        List<DepartmentDTO> dtoList = new ArrayList<>();
        if (list != null) list.forEach(d -> dtoList.add(new DepartmentDTO(d)));

        return dtoList;
    }
}
