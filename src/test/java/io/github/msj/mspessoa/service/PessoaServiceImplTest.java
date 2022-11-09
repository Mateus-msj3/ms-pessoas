package io.github.msj.mspessoa.service;

import io.github.msj.mspessoa.dto.request.PessoaRequestDTO;
import io.github.msj.mspessoa.dto.response.PessoaResponseDTO;
import io.github.msj.mspessoa.exception.NegocioException;
import io.github.msj.mspessoa.model.Endereco;
import io.github.msj.mspessoa.model.Pessoa;
import io.github.msj.mspessoa.repository.PessoaRepository;
import io.github.msj.mspessoa.service.implemetation.PessoaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PessoaServiceImplTest {

    private final Long ID = 1L;

    private final PessoaResponseDTO PESSOA_RESPONSE_DTO = PessoaResponseDTO.builder().build();

    private final Pessoa PESSOA = Pessoa.builder().build();

    @InjectMocks
    PessoaServiceImpl service;

    @Mock
    ModelMapper modelMapper;

    @Mock
    PessoaRepository pessoaRepository;

    private PessoaRequestDTO informacoesPessoa() {
        return PessoaRequestDTO.builder()
                .id(null)
                .nome("pessoa")
                .sobrenome("teste")
                .cpf("00011122233")
                .dataNascimento(LocalDate.of(1995, Month.OCTOBER, 25))
                .email("pessoa@email.com")
                .endereco(Endereco.builder()
                        .cep("11222000")
                        .cidade("Testenopólis")
                        .estado("TE")
                        .rua("Rua A")
                        .numero("123")
                        .build())
                .build();
    }

    private PessoaResponseDTO informacoesRetorno() {
        return PessoaResponseDTO.builder()
                .id(null)
                .nome("Pessoa")
                .sobrenome("Teste")
                .cpf("00011122233")
                .dataNascimento(LocalDate.of(1995, Month.OCTOBER, 25))
                .email("pessoa@email.com")
                .endereco(Endereco.builder()
                        .cep("11222000")
                        .cidade("Testenopólis")
                        .estado("TE")
                        .rua("Rua A")
                        .numero("123")
                        .build())
                .build();
    }

    @Test
    @DisplayName("Deve retornar uma lista com todos os registros de pessoa")
    public void ListarTodos() {
        List<Pessoa> pessoas = Arrays.asList(Pessoa.builder().build());

        when(pessoaRepository.findAll()).thenReturn(pessoas);
        when(modelMapper.map(any(), any())).thenReturn(PESSOA_RESPONSE_DTO);

        List<PessoaResponseDTO> retorno = service.listarTodos();

        verify(pessoaRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(), any());
        assertNotNull(retorno, "Verfica se o retorno é diferente de nullo");
        assertEquals(retorno.size(), 1, "Verifica se a lista contém apenas um elemento");
    }

    @Test
    @DisplayName("Deve retornar uma pessoa pelo id informado")
    public void ListarPorId() {
        Optional<Pessoa> pessoa = Optional.of(PESSOA);

        when(pessoaRepository.findById(ID)).thenReturn(pessoa);
        when(modelMapper.map(any(), any())).thenReturn(PESSOA_RESPONSE_DTO);

        PessoaResponseDTO retorno = service.listarPorId(ID);

        verify(pessoaRepository, times(1)).findById(eq(ID));
        verify(modelMapper, times(1)).map(any(), any());
        assertNotNull(retorno, "Verfica se o retorno é diferente de nullo");
    }

    @Test
    @DisplayName("Deve retornar uma mensagem de erro quando o id é inexistente")
    public void ListarPorIdInexistente() {
        var mensagem = "Pessoa não encontrada";

        when(pessoaRepository.findById(anyLong())).thenThrow(new NegocioException(mensagem));
        assertThrows(NegocioException.class, () -> service.listarPorId(anyLong()));
    }

    @Test
    @DisplayName("Deve salvar uma pessoa")
    public void salvar() {
        PessoaRequestDTO pessoa = informacoesPessoa();

        Pessoa novaPessoa = Pessoa.builder()
                .id(null)
                .nome("pessoa")
                .sobrenome("teste")
                .cpf("00011122233")
                .dataNascimento(LocalDate.of(1995, Month.OCTOBER, 25))
                .email("pessoa@email.com")
                .endereco(Endereco.builder()
                        .cep("11222000")
                        .cidade("Testenopólis")
                        .estado("TE")
                        .rua("Rua A")
                        .numero("123")
                        .build())
                .build();

        when(pessoaRepository.existsByCpf(pessoa.getCpf())).thenReturn(false);
        when(modelMapper.map(pessoa, Pessoa.class)).thenReturn(novaPessoa);
        when(pessoaRepository.save(novaPessoa)).thenReturn(novaPessoa);
        when(modelMapper.map(novaPessoa, PessoaResponseDTO.class)).thenReturn(informacoesRetorno());

        PessoaResponseDTO retorno = service.salvar(pessoa);

        verify(modelMapper, times(1)).map(pessoa, Pessoa.class);
        verify(pessoaRepository, times(1)).save(eq(novaPessoa));
        verify(modelMapper, times(1)).map(novaPessoa, PessoaResponseDTO.class);

        assertNotNull(retorno, "Verifica se o retorno é diferente de nullo");
    }

    @Test
    @DisplayName("Deve lançar Execeção quando o CPF já estiver cadastrado")
    public void aoTentarSalvarMensagemDeErroCpfJaCadastrado() {
        PessoaRequestDTO pessoa = informacoesPessoa();

        when(pessoaRepository.existsByCpf(pessoa.getCpf())).thenReturn(true);

        assertThrows(NegocioException.class, () -> service.salvar(pessoa), "O cpf informado já está cadastrado");

        verify(pessoaRepository, times(1)).existsByCpf(eq(pessoa.getCpf()));
    }

    @Test
    @DisplayName("Deve lançar Execeção quando a data de nascimento é maior que a data atual")
    public void aoTentarSalvarlancaMensagemDeErroDataNascimentoMaiorQueDataAtual() {
        PessoaRequestDTO pessoa = PessoaRequestDTO.builder()
                .nome("Pessoa")
                .sobrenome("Teste")
                .cpf("33344455566")
                .dataNascimento(LocalDate.now().plusDays(2))
                .build();

        when(pessoaRepository.existsByCpf(pessoa.getCpf())).thenReturn(false);

        assertThrows(NegocioException.class, () -> service.salvar(pessoa), "A data de nascimento não pode maior que a data atual");

        verify(pessoaRepository, times(1)).existsByCpf(eq(pessoa.getCpf()));
    }

    @Test
    @DisplayName("Deve editar uma pessoa")
    public void editar() {
        PessoaRequestDTO pessoa = informacoesPessoa();

        Pessoa editarPessoa = Pessoa.builder()
                .id(null)
                .nome("pessoa teste")
                .sobrenome("teste teste")
                .cpf("00011122233")
                .dataNascimento(LocalDate.of(1995, Month.OCTOBER, 25))
                .email("pessoa@email.com")
                .endereco(Endereco.builder()
                        .cep("11222000")
                        .cidade("Testenopólis")
                        .estado("TE")
                        .rua("Rua A")
                        .numero("123")
                        .build())
                .build();

        when(pessoaRepository.findById(ID)).thenReturn(Optional.of(editarPessoa));
        when(pessoaRepository.save(editarPessoa)).thenReturn(editarPessoa);
        when(modelMapper.map(editarPessoa, PessoaResponseDTO.class)).thenReturn(informacoesRetorno());

        PessoaResponseDTO retorno = service.editar(ID, pessoa);

        verify(pessoaRepository, times(1)).findById(eq(ID));
        verify(pessoaRepository, times(1)).save(eq(editarPessoa));
        verify(modelMapper, times(1)).map(editarPessoa, PessoaResponseDTO.class);

        assertNotNull(retorno, "Verifica se o retorno é diferente de nullo");
        assertEquals(retorno.getNome(), "Pessoa");
    }

    @Test
    @DisplayName("Ao editar deve lançar Execeção quando o CPF já estiver cadastrado")
    public void aoTentarEditarMensagemDeErroCpfJaCadastrado() {
        PessoaRequestDTO pessoa = informacoesPessoa();

        Pessoa editarPessoa = Pessoa.builder()
                .id(null)
                .nome("pessoa teste")
                .sobrenome("teste teste")
                .cpf("00011122299")
                .dataNascimento(LocalDate.of(1995, Month.OCTOBER, 25))
                .email("pessoa@email.com")
                .endereco(Endereco.builder()
                        .cep("11222000")
                        .cidade("Testenopólis")
                        .estado("TE")
                        .rua("Rua A")
                        .numero("123")
                        .build())
                .build();

        when(pessoaRepository.findById(ID)).thenReturn(Optional.of(editarPessoa));
        when(pessoaRepository.existsByCpf(pessoa.getCpf())).thenReturn(true);

        assertThrows(NegocioException.class, () -> service.editar(ID,pessoa), "O cpf informado já está cadastrado");

        verify(pessoaRepository, times(1)).findById(eq(ID));
        verify(pessoaRepository, times(1)).existsByCpf(eq(pessoa.getCpf()));
    }

    @Test
    @DisplayName("Deve deletar uma pessoa pelo id informado")
    public void deletar() {
        Optional<Pessoa> pessoa = Optional.of(PESSOA);

        when(pessoaRepository.findById(ID)).thenReturn(pessoa);
        doNothing().when(pessoaRepository).delete(pessoa.get());

        service.deletar(ID);

        verify(pessoaRepository, times(1)).findById(eq(ID));
        verify(pessoaRepository, times(1)).delete(eq(pessoa.get()));

    }
}
