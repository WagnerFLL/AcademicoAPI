package br.ufal.ic.academico.api.professor;

import br.ufal.ic.academico.api.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;

@Entity
@Getter
@RequiredArgsConstructor
public class Professor extends Person {
    public Professor(ProfessorDTO entity) {
        this(entity.firstName, entity.lastName);
    }

    public Professor(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public void update(ProfessorDTO entity) {
        super.update(entity.firstName, entity.lastName);
    }
}
