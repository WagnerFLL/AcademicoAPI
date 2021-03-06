package br.ufal.ic.academico.api.department;

import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class DepartmentDTO {

    public Long id;
    public String name;
    public List<SecretaryDTO> secretaries = new ArrayList<>();

    public DepartmentDTO(Department entity) {
        this.id = entity.getId();
        this.name = entity.name;

        if (entity.graduation != null) this.secretaries.add(new SecretaryDTO(entity.graduation));

        if (entity.postGraduation != null) this.secretaries.add(new SecretaryDTO(entity.postGraduation));

    }
}
