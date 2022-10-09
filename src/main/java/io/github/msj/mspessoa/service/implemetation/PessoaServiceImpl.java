package io.github.msj.mspessoa.service.implemetation;

import io.github.msj.mspessoa.dto.request.PessoaRequestDTO;
import io.github.msj.mspessoa.dto.response.PessoaResponseDTO;
import io.github.msj.mspessoa.model.Pessoa;
import io.github.msj.mspessoa.repository.PessoaRepository;
import io.github.msj.mspessoa.service.PessoaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<PessoaResponseDTO> listarTodos() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return pessoas.stream()
                .map(pessoa -> modelMapper.map(pessoa, PessoaResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PessoaResponseDTO listarPorId(Long id) {
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findById(id)).orElseThrow(() -> new RuntimeException("Teste"));
        PessoaResponseDTO pessoaResponseDTO = modelMapper.map(pessoa.get(), PessoaResponseDTO.class);
        return pessoaResponseDTO;
    }

    @Override
    public PessoaResponseDTO salvar(PessoaRequestDTO pessoaRequestDTO) {
        Pessoa pessoaRequest = modelMapper.map(pessoaRequestDTO, Pessoa.class);
        Pessoa pessoa = pessoaRepository.save(pessoaRequest);
        PessoaResponseDTO responseDTO = modelMapper.map(pessoa, PessoaResponseDTO.class);
        return responseDTO;
    }

    @Override
    public PessoaResponseDTO editar(Long idPessoa, PessoaRequestDTO pessoaRequestDTO) {
        Optional<Pessoa> pessoaAtual = Optional.of(pessoaRepository.findById(idPessoa)).orElseThrow(() -> new RuntimeException("Teste"));
        BeanUtils.copyProperties(pessoaRequestDTO, pessoaAtual.get(), "id");
        Pessoa pessoaRequest = modelMapper.map(pessoaRequestDTO, Pessoa.class);
        Pessoa pessoa = pessoaRepository.save(pessoaRequest);
        PessoaResponseDTO responseDTO = modelMapper.map(pessoa, PessoaResponseDTO.class);
        return responseDTO;
    }

    @Override
    public void deletar(Long idPessoa) {
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findById(idPessoa)).orElseThrow(() -> new RuntimeException("Teste"));
        pessoaRepository.delete(pessoa.get());
    }
}
