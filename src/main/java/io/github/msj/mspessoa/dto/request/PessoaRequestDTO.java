package io.github.msj.mspessoa.dto.request;

import io.github.msj.mspessoa.model.Endereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaRequestDTO {

    private Long id;

    @NotEmpty
    private String nome;

    @NotEmpty
    private String sobrenome;

    @NotEmpty
    private LocalDate dataNascimento;

    @NotEmpty
    private String cpf;

    @Email
    private String email;

    private Endereco endereco;

}
