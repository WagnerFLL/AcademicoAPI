package br.ufal.ic.academico.api.professor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ProfessorDTO {
    public Long id;
    public String firstName, lastName;

    public ProfessorDTO(Professor entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
    }
}
