package io.github.msj.mspessoa.service.implemetation;

import io.github.msj.mspessoa.dto.request.PessoaRequestDTO;
import io.github.msj.mspessoa.dto.response.PessoaResponseDTO;
import io.github.msj.mspessoa.exception.NegocioException;
import io.github.msj.mspessoa.model.Pessoa;
import io.github.msj.mspessoa.repository.PessoaRepository;
import io.github.msj.mspessoa.service.PessoaService;
import org.apache.commons.lang.WordUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findById(id)).orElseThrow(() -> new NegocioException("Pessoa não encontrada"));
        PessoaResponseDTO pessoaResponseDTO = modelMapper.map(pessoa.get(), PessoaResponseDTO.class);
        return pessoaResponseDTO;
    }

    @Override
    public PessoaResponseDTO salvar(PessoaRequestDTO pessoaRequestDTO) {
        this.antesDeSalvarValidaPessoa(pessoaRequestDTO);
        Pessoa pessoaRequest = modelMapper.map(pessoaRequestDTO, Pessoa.class);
        Pessoa pessoa = pessoaRepository.save(pessoaRequest);
        PessoaResponseDTO responseDTO = modelMapper.map(pessoa, PessoaResponseDTO.class);
        return responseDTO;
    }

    @Override
    public PessoaResponseDTO editar(Long idPessoa, PessoaRequestDTO pessoaRequestDTO) {
        Optional<Pessoa> pessoaAtual = Optional.of(pessoaRepository.findById(idPessoa)).orElseThrow(() -> new NegocioException("Pessoa não encontrada"));
        this.antesDeEditarVerficaCpf(pessoaRequestDTO, pessoaAtual);
        this.antesDeEditarValidaPessoa(pessoaRequestDTO);
        BeanUtils.copyProperties(pessoaRequestDTO, pessoaAtual.get(), "id");
        Pessoa pessoa = pessoaRepository.save(pessoaAtual.get());
        PessoaResponseDTO responseDTO = modelMapper.map(pessoa, PessoaResponseDTO.class);
        return responseDTO;
    }

    @Override
    public void deletar(Long idPessoa) {
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findById(idPessoa)).orElseThrow(() -> new NegocioException("Pessoa não encontrada"));
        pessoaRepository.delete(pessoa.get());
    }

    public String validarNome(String nome) {
        if (!Character.isUpperCase(nome.codePointAt(0))) {
            nome = nome.toLowerCase();
            nome = WordUtils.capitalize(nome);
            return nome;
        }
        return nome;
    }

    public String validarSobrenome(String sobrenome) {
        if (!Character.isUpperCase(sobrenome.codePointAt(0))) {
            sobrenome = sobrenome.toLowerCase();
            sobrenome = WordUtils.capitalize(sobrenome);
            return sobrenome;
        }
        return sobrenome;
    }

    public void validarCpfExistente(String cpf) {
        cpf = cpf.replaceAll("[^\\d ]", "");
        if (pessoaRepository.existsByCpf(cpf)) {
            throw new NegocioException("O cpf informado já está cadastrado");
        }
    }

    public void validarDataNascimento(LocalDate data) {
        LocalDate dataAtual = LocalDate.now();
        if (data != null && data.isAfter(dataAtual)) {
            throw new NegocioException("A data de nascimento não pode maior que a data atual");
        }

    }

    private void antesDeSalvarValidaPessoa(PessoaRequestDTO pessoaRequestDTO) {
        this.validarNome(pessoaRequestDTO.getNome());
        this.validarSobrenome(pessoaRequestDTO.getSobrenome());
        this.validarCpfExistente(pessoaRequestDTO.getCpf());
        this.validarDataNascimento(pessoaRequestDTO.getDataNascimento());
    }

    private void antesDeEditarVerficaCpf(PessoaRequestDTO pessoaRequestDTO, Optional<Pessoa> pessoa) {
        if (!pessoaRequestDTO.getCpf().equals(pessoa.get().getCpf())) {
            validarCpfExistente(pessoaRequestDTO.getCpf());
        }
    }
    private void antesDeEditarValidaPessoa(PessoaRequestDTO pessoaRequestDTO) {
        this.validarNome(pessoaRequestDTO.getNome());
        this.validarSobrenome(pessoaRequestDTO.getSobrenome());
        this.validarDataNascimento(pessoaRequestDTO.getDataNascimento());
    }
}
