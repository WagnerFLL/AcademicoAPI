package br.ufal.ic.academico;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDAO;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.student.StudentDAO;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.professor.ProfessorDAO;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import br.ufal.ic.academico.resources.*;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcademicoApp extends Application<ConfigApp> {

    public static void main(String[] args) throws Exception {
        new AcademicoApp().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<ConfigApp> bootstrap) {
        log.info("initialize");
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(ConfigApp config, Environment environment) {

        final StudentDAO studentDAO = new StudentDAO(hibernate.getSessionFactory());
        final ProfessorDAO professorDAO = new ProfessorDAO(hibernate.getSessionFactory());
        final DepartmentDAO departmentDAO = new DepartmentDAO(hibernate.getSessionFactory());
        final SecretaryDAO secretaryDAO = new SecretaryDAO(hibernate.getSessionFactory());
        final CourseDAO courseDAO = new CourseDAO(hibernate.getSessionFactory());
        final DisciplineDAO disciplineDAO = new DisciplineDAO(hibernate.getSessionFactory());

        final StudentResource studentResource = new StudentResource(courseDAO, studentDAO, disciplineDAO);
        final DepartmentResource departmentResource = new DepartmentResource(departmentDAO, secretaryDAO);
        final SecretaryResource secretaryResource = new SecretaryResource(departmentDAO, secretaryDAO, courseDAO);
        final CourseResource courseResource = new CourseResource(secretaryDAO, courseDAO, disciplineDAO);
        final DisciplineResource disciplineResource = new DisciplineResource(courseDAO, disciplineDAO);
        final ProfessorResource professorResource = new ProfessorResource(professorDAO,disciplineDAO);

        environment.jersey().register(studentResource);
        environment.jersey().register(departmentResource);
        environment.jersey().register(secretaryResource);
        environment.jersey().register(courseResource);
        environment.jersey().register(disciplineResource);
        environment.jersey().register(professorResource);
    }

    private final HibernateBundle<ConfigApp> hibernate
            = new HibernateBundle<ConfigApp>(Student.class, Professor.class, Department.class, Secretary.class, Course.class, Discipline.class) {

        @Override
        public DataSourceFactory getDataSourceFactory(ConfigApp configuration) {
            return configuration.getDatabase();
        }
    };
}
