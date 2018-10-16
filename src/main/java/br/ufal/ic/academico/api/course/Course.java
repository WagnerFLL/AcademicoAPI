package br.ufal.ic.academico.api.course;

import br.ufal.ic.academico.api.discipline.Discipline;
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
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @Nullable
    List<Discipline> disciplines;

    public Course(CourseDTO entity) {
        this(entity.name);
    }

    public Course(String name) {
        this.name = name;
        this.disciplines = new ArrayList<>();
    }

    public void addDiscipline(Discipline discipline) {
        assert this.disciplines != null;
        this.disciplines.add(discipline);
    }

    public boolean deleteDiscipline(Discipline d) {
        assert this.disciplines != null;
        return this.disciplines.remove(d);
    }
}
