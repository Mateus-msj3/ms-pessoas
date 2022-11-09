package io.github.msj.mspessoa.controller;

import io.github.msj.mspessoa.dto.request.PessoaRequestDTO;
import io.github.msj.mspessoa.dto.response.PessoaResponseDTO;
import io.github.msj.mspessoa.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> listarTodos() {
        List<PessoaResponseDTO> list = pessoaService.listarTodos();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> listarPorId(@PathVariable Long id) {
        PessoaResponseDTO pessoaResponseDTO = pessoaService.listarPorId(id);
        return ResponseEntity.ok().body(pessoaResponseDTO);
    }

    @PostMapping("/salvar")
    public ResponseEntity<PessoaResponseDTO> salvar(@RequestBody @Valid PessoaRequestDTO pessoaRequestDTO) {
        PessoaResponseDTO pessoaResponseDTO = pessoaService.salvar(pessoaRequestDTO);
        return ResponseEntity.ok().body(pessoaResponseDTO);
    }

    @PutMapping("/editar/{idPessoa}")
    public ResponseEntity<PessoaResponseDTO> editar(@PathVariable Long idPessoa, @RequestBody @Valid PessoaRequestDTO pessoaRequestDTO) {
        pessoaRequestDTO.setId(idPessoa);
        PessoaResponseDTO pessoaResponseDTO = pessoaService.editar(idPessoa, pessoaRequestDTO);
        return ResponseEntity.ok().body(pessoaResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long idPessoa) {
        pessoaService.deletar(idPessoa);
        return ResponseEntity.noContent().build();
    }
}
