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
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findById(id).orElseThrow(() -> new NegocioException("Pessoa não encontrada")));
        PessoaResponseDTO pessoaResponseDTO = modelMapper.map(pessoa.get(), PessoaResponseDTO.class);
        return pessoaResponseDTO;
    }

    @Override
    public PessoaResponseDTO buscarPorCpf(String cpf) {
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findByCpf(cpf)).orElse(null);
        if (pessoa.isPresent()) {
            PessoaResponseDTO pessoaResponseDTO = modelMapper.map(pessoa.get(), PessoaResponseDTO.class);
            return pessoaResponseDTO;
        }
        return null;
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
        Optional<Pessoa> pessoaAtual = Optional.of(pessoaRepository.findById(idPessoa).orElseThrow(() -> new NegocioException("Pessoa não encontrada")));
        this.antesDeEditarVerficaCpf(pessoaRequestDTO, pessoaAtual);
        this.antesDeEditarValidaPessoa(pessoaRequestDTO);
        BeanUtils.copyProperties(pessoaRequestDTO, pessoaAtual.get(), "id");
        Pessoa pessoa = pessoaRepository.save(pessoaAtual.get());
        PessoaResponseDTO responseDTO = modelMapper.map(pessoa, PessoaResponseDTO.class);
        return responseDTO;
    }

    @Override
    public void deletar(Long idPessoa) {
        Optional<Pessoa> pessoa = Optional.of(pessoaRepository.findById(idPessoa).orElseThrow(() -> new NegocioException("Pessoa não encontrada")));
        pessoaRepository.delete(pessoa.get());
    }

    public void validarNome(PessoaRequestDTO pessoaRequestDTO) {
        if (!Character.isUpperCase(pessoaRequestDTO.getNome().codePointAt(0))) {
            var nome = pessoaRequestDTO.getNome().toLowerCase();
            nome = WordUtils.capitalize(nome);
            pessoaRequestDTO.setNome(nome);
        }
    }

    public void validarSobrenome(PessoaRequestDTO pessoaRequestDTO) {
        if (!Character.isUpperCase(pessoaRequestDTO.getSobrenome().codePointAt(0))) {
            var sobrenome = pessoaRequestDTO.getSobrenome().toLowerCase();
            sobrenome = WordUtils.capitalize(sobrenome);
            pessoaRequestDTO.setSobrenome(sobrenome);
        }
    }

    private void validarCpf(PessoaRequestDTO pessoaRequestDTO) {
        String cpfFormatado = pessoaRequestDTO.getCpf().replaceAll("[^\\d ]", "");
        pessoaRequestDTO.setCpf(cpfFormatado);
    }

    private void validarCpfExistente(PessoaRequestDTO pessoaRequestDTO) {
        validarCpf(pessoaRequestDTO);
        if (pessoaRepository.existsByCpf(pessoaRequestDTO.getCpf())) {
            throw new NegocioException("O cpf informado já está cadastrado");
        }
    }

    private void validarDataNascimento(LocalDate data) {
        LocalDate dataAtual = LocalDate.now();
        if (data != null && data.isAfter(dataAtual)) {
            throw new NegocioException("A data de nascimento não pode maior que a data atual");
        }

    }

    private void antesDeSalvarValidaPessoa(PessoaRequestDTO pessoaRequestDTO) {
        this.validarCpfExistente(pessoaRequestDTO);
        this.validarNome(pessoaRequestDTO);
        this.validarSobrenome(pessoaRequestDTO);
        this.validarDataNascimento(pessoaRequestDTO.getDataNascimento());
    }

    private void antesDeEditarVerficaCpf(PessoaRequestDTO pessoaRequestDTO, Optional<Pessoa> pessoa) {
            validarCpf(pessoaRequestDTO);
        if (!pessoaRequestDTO.getCpf().equals(pessoa.get().getCpf())) {
            validarCpfExistente(pessoaRequestDTO);
        }
    }

    private void antesDeEditarValidaPessoa(PessoaRequestDTO pessoaRequestDTO) {
        this.validarNome(pessoaRequestDTO);
        this.validarSobrenome(pessoaRequestDTO);
        this.validarDataNascimento(pessoaRequestDTO.getDataNascimento());
    }
}
