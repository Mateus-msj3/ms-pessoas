package io.github.msj.mspessoa.dto.request;

import io.github.msj.mspessoa.model.Endereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaRequestDTO {

    private Long id;

    @NotEmpty(message = "É necessário informar o nome.")
    private String nome;

    @NotEmpty(message = "Sobrenome é obrigatório")
    private String sobrenome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotEmpty(message = "É necessário informar o cpf.")
    @CPF
    private String cpf;

    @Email
    private String email;

    private Endereco endereco;

}
