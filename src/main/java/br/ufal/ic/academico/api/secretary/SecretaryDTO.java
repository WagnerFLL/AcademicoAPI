package br.ufal.ic.academico.api.secretary;

import br.ufal.ic.academico.api.discipline.Discipline;
import br.ufal.ic.academico.api.discipline.DisciplineDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class SecretaryDTO {

    public Long id;
    public String type;
    public List<DisciplineDTO> disciplines = new LinkedList<>();

    public SecretaryDTO(Secretary entity) {
        this.id = entity.getId();
        this.type = entity.type;

        if (entity.courses != null) {
            ArrayList<DisciplineDTO> dtoList = new ArrayList<>();

            entity.courses.forEach(c -> {
                assert c.getDisciplines() != null;
                dtoList.addAll(c.getDisciplines().stream().map(DisciplineDTO::new).collect(Collectors.toList()));
            });

            this.disciplines = dtoList;
        }
    }
}
