package io.github.msj.mspessoa.service;

import java.time.LocalDate;

public interface ValidacaoPessoaService {

    String validarNome(String nome);

    String validarSobrenome(String sobrenome);

    void validarDataNascimento(LocalDate data);

    void validarCpfExistente(String cpf);
}
