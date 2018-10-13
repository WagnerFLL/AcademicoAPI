package br.ufal.ic.academico.api.discipline;

import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.professor.Professor;
import br.ufal.ic.academico.api.secretary.Secretary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    String name;
    String code;

    @Setter
    Integer credits, requiredCredits;

    @Setter
    @ElementCollection
    List<String> requiredDisciplines;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @Nullable
    Professor professor;

    @ManyToMany(cascade = CascadeType.ALL)
    List<Student> students;

    public Discipline(String name, String code, Integer credits,
                      Integer requiredCredits, List<String> requiredDisciplines) {

        this.name = name;
        this.code = code;
        this.students = new ArrayList<>();

        if (credits == null) this.credits = 0;
        else this.credits = credits;

        if (requiredCredits == null) this.requiredCredits = 0;
        else this.requiredCredits = requiredCredits;

        if (requiredDisciplines == null) this.requiredDisciplines = new ArrayList<>();
        else this.requiredDisciplines = requiredDisciplines;
    }

    public Discipline(DisciplineDTO entity) {
        this(entity.name, entity.code, entity.credits, entity.requiredCredits, entity.requiredDisciplines);
    }

    public boolean removeStudent(Student student) {
        return this.students.remove(student);
    }

    public void update(DisciplineDTO entity) {
        if (entity.name != null) name = entity.name;

        if (entity.credits != null) credits = entity.credits;

        if (entity.requiredCredits != null) requiredCredits = entity.requiredCredits;

        if (entity.requiredDisciplines != null) requiredDisciplines = entity.requiredDisciplines;
    }

    public String matriculate(Student student, Department studentDepartment,
                              Department disciplineDepartment, Secretary studentSecretary,
                              Secretary disciplineSecretary) {

        if (student.getCompletedDisciplines().contains(this.code))
            return "O estudante já finalizou esta disciplina.";

        if (student.getCourse() == null) return "O estudante não está cursando neenhum curso.";

        if (!studentDepartment.getId().equals(disciplineDepartment.getId()))
            return "A disciplina não é ofertada para este estudante.";

        if (!studentSecretary.isGraduation() && disciplineSecretary.isGraduation())
            return "Esta disciplina é ofertada apenas para alunos da graduação.";


        if (student.getCredits() < requiredCredits)
            return "O estudante tem " + student.getCredits() +
                    " créditos. E são necessários " + requiredCredits ;

        for (String code : requiredDisciplines)
            if (!student.getCompletedDisciplines().contains(code))
                return "O estudante precisa completar o pré-requisito: " + code + ".";

        if (studentSecretary.isGraduation() && !disciplineSecretary.isGraduation() && student.getCredits() < 170)
            return "O estudante não tem créditos suficiente para disciplinas de pós-graduação. ";

        if (this.students.contains(student)) return "O estudante já está matriculado nesta disciplina.";

        this.students.add(student);
        return null;
    }

}
