package br.ufal.ic.academico.integration;

import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.DepartmentDTO;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import br.ufal.ic.academico.api.professor.ProfessorDTO;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import br.ufal.ic.academico.api.student.StudentDTO;
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
class MatriculateTest extends BaseTest {

    private List<StudentDTO> students = new ArrayList<>();
    private StudentDTO studentIC, studentFDA;
    private CourseDTO course, otherCourse;
    private DisciplineDTO discipline;
    private DepartmentDTO department;
    private SecretaryDTO secretary;
    private StudentDTO student, foreigner;
    private DisciplineDTO responseD;
    private StudentDTO responseS;
    private ProfessorDTO responseP;

    @Test
    void initializeTests() {
        registerProfessor();
        deallocateProfessor();
        createStudents();
        testStudent();
        createResources();
        matriculate();
    }

    private void registerProfessor() {

        department = createDepartment(RULE, "IC");
        secretary = createSecretary(RULE, department, "GRADUATION");
        course = createCourse(RULE, secretary, "Ciência da Computação");
        discipline = createDiscipline(RULE, course, "3842", "P1", null);

        ProfessorDTO professorDTO = new ProfessorDTO();
        professorDTO.firstName = "Geraldo";

        path = api + "professor";
        responseP = RULE.client().target(path).request().post(Entity.json(professorDTO), ProfessorDTO.class);

        path = api + "professor/" + responseP.id + "/discipline/" + discipline.getId();
        responseD = RULE.client().target(path).request().post(null, DisciplineDTO.class);

        assertAll(
                () -> assertNotNull(responseD.getProfessor()),
                () -> assertEquals(discipline.getCode(), responseD.getCode()),
                () -> assertEquals(discipline.getId(), responseD.getId()),
                () -> assertEquals(discipline.getName(), responseD.getName()),
                () -> assertEquals(discipline.getCredits(), responseD.getCredits()),
                () -> assertEquals(discipline.getRequiredCredits(), responseD.getRequiredCredits()),
                () -> assertEquals(discipline.getRequiredDisciplines(), responseD.getRequiredDisciplines())
        );
    }

    private void deallocateProfessor() {

        path = api + "professor/" + responseP.getId();
        RULE.client().target(path).request().delete();

        path = api + "discipline/" + responseD.getId();
        discipline = RULE.client().target(path).request().get(DisciplineDTO.class);

        assertEquals(responseD.getId(), discipline.getId());
        assertNull(discipline.getProfessor());

    }

    private void testStudent() {

        student = students.get(0);
        foreigner = students.get(1);
        getStudentByID(student);
        students.set(0, updateStudent(student));
        students.set(1, updateStudent(foreigner));

        assertEquals(students.size(),
                RULE.client().target(api + "student").request().get(new GenericType<ArrayList<StudentDTO>>(){}).size());

        deleteStudent(students.get(0));
        assertEquals(students.size(),
                RULE.client().target(api + "student").request().get(new GenericType<ArrayList<StudentDTO>>() {}).size());

        studentIC = students.get(0);
        studentFDA = students.get(1);

    }

    private void createResources() {

        department = createDepartment(RULE, "CV");
        DepartmentDTO otherDepartment = createDepartment(RULE, "PCC");

        secretary = createSecretary(RULE, department, "GRADUATION");
        SecretaryDTO otherSecretary = createSecretary(RULE, otherDepartment, "POST-GRADUATION");

        course = createCourse(RULE, secretary, "Ciência");
        otherCourse = createCourse(RULE, otherSecretary, "Direito");

        discipline = createDiscipline(RULE, course, "8473", "Programação 1", 50);
    }

    private void matriculate(){

        studentIC = registerStudentInCourse(studentIC, course);
        studentFDA = registerStudentInCourse(studentFDA, otherCourse);
        getDisciplines(students.get(2));
        discipline = matriculateStudentInDiscipline(studentFDA);
        finalizeDiscipline(studentFDA);
        discipline = createDiscipline(RULE, course, "CC002", "Programação 2", 50);
        discipline = matriculateStudentInDiscipline(studentFDA);

    }

    private void createStudents() {

        for (int i = 1; i <= 5; i++) {

            StudentDTO entity = new StudentDTO();
            entity.firstName = "Luis";
            responseS = RULE.client().target(api + "student").request().post(Entity.json(entity), StudentDTO.class);

            assertAll(
                    () -> assertNotNull(responseS.id),
                    () -> assertNull(responseS.course),
                    () -> assertNull(responseS.lastName),
                    () -> assertEquals(0, (int) responseS.credits),
                    () -> assertEquals(entity.firstName, responseS.firstName),
                    () -> assertEquals(new ArrayList<>(), responseS.completedDisciplines)
            );

            students.add(responseS);
        }
    }

    private void getStudentByID(StudentDTO student) {
        path = api + "student/" + student.id;
        responseS = RULE.client().target(path).request().get(StudentDTO.class);
        assertEquals(student.id, responseS.id);
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "student/0").request().get(StudentDTO.class));
    }

    private StudentDTO updateStudent(StudentDTO real) {


        real.lastName = "Silva";
        responseS = RULE.client().target(api + "student/" + real.id).request().put(Entity.json(real), StudentDTO.class);

        assertAll(
                () -> assertEquals(real.id, responseS.id),
                () -> assertEquals(real.lastName, responseS.lastName),
                () -> assertEquals(real.firstName, responseS.firstName),
                () -> assertEquals(real.credits, responseS.credits),
                () -> assertEquals(real.course, responseS.course),
                () -> assertEquals(real.completedDisciplines, responseS.completedDisciplines),
                () -> assertThrows(NotFoundException.class,
                        () -> RULE.client().target(api + "student/0")
                                .request().put(Entity.json(new StudentDTO()), StudentDTO.class))
        );

        return responseS;
    }

    private void deleteStudent(StudentDTO original) {

        students.remove(original);

        path = api + "student/" + original.id;
        assertDoesNotThrow(() -> RULE.client().target(path).request().delete());
        path = api + "student/";
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "student/" + original.id).request().get(StudentDTO.class));
        path += '0';
        assertThrows(NotFoundException.class, () -> RULE.client().target(api + "student/0").request().delete(StudentDTO.class));
    }

    private StudentDTO registerStudentInCourse(StudentDTO student, CourseDTO course) {

        path = api + "student/0/course/" + course.getId();
        assertThrows(NotFoundException.class, () -> RULE.client().target(path).request().post(null, StudentDTO.class));

        path = api + "student/" + student.getId() + "/course/0";
        assertThrows(NotFoundException.class, () -> RULE.client().target(path).request().post(null, StudentDTO.class));

        path = api + "student/" + student.getId() + "/course/" + course.getId();
        responseS = RULE.client().target(path).request().post(null, StudentDTO.class);

        assertAll(
                () -> assertNotNull(responseS.course),
                () -> assertEquals(student.getId(), responseS.id),
                () -> assertEquals(student.credits, responseS.credits),
                () -> assertEquals(student.lastName, responseS.lastName),
                () -> assertEquals(student.firstName, responseS.firstName),
                () -> assertEquals(student.completedDisciplines, responseS.completedDisciplines)
        );

        return responseS;
    }

    private void getDisciplines(StudentDTO unregistered) {

        path = api + "student/0/discipline";
        assertThrows(NotFoundException.class, () -> RULE.client().target(path).request().get(new GenericType<List<DisciplineDTO>>(){}));

        path = api + "student/" + studentIC.getId() + "/discipline";
        List<DisciplineDTO> disciplines = RULE.client().target(path).request().get(new GenericType<List<DisciplineDTO>>(){});
        assertEquals(1, disciplines.size());

        assertNull(unregistered.course);
        path = api + "student/" + unregistered.getId() + "/discipline";
        assertThrows(BadRequestException.class, () -> RULE.client().target(path).request().get(new GenericType<List<DisciplineDTO>>(){}));

        path = api + "student/" + studentFDA.getId() + "/discipline";
        assertEquals(0, RULE.client().target(path).request().get(new GenericType<List<DisciplineDTO>>(){}).size());
    }

    private DisciplineDTO matriculateStudentInDiscipline(StudentDTO visitant) {

        path = api + "student/" + visitant.getId() + "/discipline/" + discipline.getId();
        assertThrows(BadRequestException.class, () -> RULE.client().target(path).request().post(null, DisciplineDTO.class));

        path = api + "student/" + studentIC.getId() + "/discipline/" + discipline.getId();
        DisciplineDTO response = RULE.client().target(path).request().post(null, DisciplineDTO.class);
        assertEquals(discipline.getStudents().size() + 1, response.getStudents().size());

        return response;
    }

    private void finalizeDiscipline(StudentDTO anotherStudent) {

        path = api + "student/" + anotherStudent.getId() + "/complete/" + discipline.getId();
        assertThrows(BadRequestException.class, () -> RULE.client().target(path).request().post(null, StudentDTO.class));

        path = api + "student/" + studentIC.getId() + "/complete/" + discipline.getId();
        StudentDTO response = RULE.client().target(path).request().post(null, StudentDTO.class);

        assertEquals(studentIC.credits + discipline.getCredits(), 50);
        assertFalse(response.completedDisciplines.contains(discipline.getCode()));
    }
}
