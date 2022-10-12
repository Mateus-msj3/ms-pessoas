package io.github.msj.mspessoa.service.implemetation;

import io.github.msj.mspessoa.exception.NegocioException;
import io.github.msj.mspessoa.repository.PessoaRepository;
import io.github.msj.mspessoa.service.ValidacaoPessoaService;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class ValidacaoPessoaServiceImpl implements ValidacaoPessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    public String validarNome(String nome) {
        if (!Character.isUpperCase(nome.codePointAt(0))) {
            nome = nome.toLowerCase();
            nome = WordUtils.capitalize(nome);
            return nome;
        }
        return nome;
    }

    @Override
    public String validarSobrenome(String sobrenome) {
        if (!Character.isUpperCase(sobrenome.codePointAt(0))) {
            sobrenome = sobrenome.toLowerCase();
            sobrenome = WordUtils.capitalize(sobrenome);
            return sobrenome;
        }
        return sobrenome;
    }

    @Override
    public void validarDataNascimento(LocalDate data) {
        LocalDate dataAtual = LocalDate.now();
        if (data != null && data.isAfter(dataAtual)) {
            throw new NegocioException("A data de nascimento não pode maior que a data atual");
        }

    }

    @Override
    public void validarCpfExistente(String cpf) {
        if (pessoaRepository.existsByCpf(cpf)) {
            throw new NegocioException("O cpf informado já está cadastrado");
        }
    }
}
