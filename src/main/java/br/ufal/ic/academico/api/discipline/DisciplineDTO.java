package br.ufal.ic.academico.api.discipline;

import br.ufal.ic.academico.api.student.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class DisciplineDTO {
    public Long id;
    public String code;
    public String name;
    public Integer credits = 0;
    public Integer requiredCredits = 0;
    public List<String> requiredDisciplines;
    public String professor;
    public List<StudentDTO> students;

    public DisciplineDTO(Discipline entity) {

        this.id = entity.getId();
        this.code = entity.code;
        this.name = entity.name;
        this.credits = entity.credits;
        this.requiredCredits = entity.requiredCredits;
        this.requiredDisciplines = entity.requiredDisciplines;

        if (entity.professor != null){
            if (entity.professor.getLastName() == null) this.professor = entity.professor.getFirstName();
            else this.professor = entity.professor.getFirstName() + " " + entity.professor.getLastName();
        }

        ArrayList<StudentDTO> dtoList = new ArrayList<>();
        if (entity.students != null) entity.students.forEach(s -> dtoList.add(new StudentDTO(s)));

        this.students = dtoList;
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class StudentDTO {
        public Long id;
        public String name;

        StudentDTO(Student entity) {
            this.id = entity.getId();

            if (entity.getLastName() == null) this.name = entity.getFirstName();
            else this.name = entity.getFirstName() + " " + entity.getLastName();
        }
    }
}
