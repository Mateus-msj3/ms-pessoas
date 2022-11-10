package io.github.msj.mspessoa.service;

import io.github.msj.mspessoa.dto.request.PessoaRequestDTO;
import io.github.msj.mspessoa.dto.response.PessoaResponseDTO;

import java.util.List;

public interface PessoaService {

    List<PessoaResponseDTO> listarTodos();

    PessoaResponseDTO listarPorId(Long id);

    PessoaResponseDTO buscarPorCpf(String cpf);

    PessoaResponseDTO salvar(PessoaRequestDTO pessoaRequestDTO);

    PessoaResponseDTO editar(Long idPessoa, PessoaRequestDTO pessoaRequestDTO);

    void deletar(Long idPessoa);

}
