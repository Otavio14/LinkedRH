package com.linked.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.linked.model.ContaCorrente;
import com.linked.model.ContaCorrente_Correntista;
import com.linked.model.ContasCorrente_Correntista;
import com.linked.model.Correntista;
import com.linked.repository.Repository;

@RestController
public class Controller {
	@Autowired
	Repository repository;

	@PostMapping("/contas")
	public ResponseEntity<String> criarConta(@RequestBody ContaCorrente_Correntista contaCorrente_Correntista) {
		if (contaCorrente_Correntista.getContaCorrente().getSaldo().compareTo(BigDecimal.ZERO) <= 0)
			return new ResponseEntity<String>("O saldo deve ser maior do que zero!", HttpStatus.BAD_REQUEST);

		return repository.criarConta(contaCorrente_Correntista);
	}

	@GetMapping("/saldos")
	public ResponseEntity<String> consultarSaldo(@RequestBody ContaCorrente contaCorrente) {
		return repository.consultarSaldo(contaCorrente);
	}

	@PatchMapping("/depositos")
	public ResponseEntity<String> depositar(@RequestBody ContaCorrente contaCorrente) {
		if (!(contaCorrente.getSaldo().compareTo(BigDecimal.ZERO) > 0))
			return new ResponseEntity<String>("O valor a ser depositado deve ser maior do que zero!",
					HttpStatus.FORBIDDEN);

		return repository.depositar(contaCorrente);
	}

	@PatchMapping("/saques")
	public ResponseEntity<String> sacar(@RequestBody ContaCorrente contaCorrente) {
		if (!(contaCorrente.getSaldo().compareTo(BigDecimal.ZERO) > 0))
			return new ResponseEntity<String>("O valor a ser sacado deve ser maior do que zero!", HttpStatus.FORBIDDEN);

		return repository.sacar(contaCorrente);
	}

	@PatchMapping("/transferencias")
	public ResponseEntity<String> transferir(@RequestBody List<ContaCorrente> contaCorrente) {
		if (!(contaCorrente.get(1).getSaldo().compareTo(BigDecimal.ZERO) > 0))
			return new ResponseEntity<String>("O valor a ser transferido deve ser maior do que zero!",
					HttpStatus.FORBIDDEN);

		return repository.transferir(contaCorrente);
	}

	@GetMapping("/correntistas")
	public ResponseEntity<ContasCorrente_Correntista> consultarCorrentista(@RequestBody Correntista correntista) {
		return repository.consultarCorrentista(correntista);
	}
	
	@PatchMapping("/contas")
	public ResponseEntity<ContaCorrente> desativarConta(@RequestBody ContaCorrente contaCorrente) {
		return repository.desativarConta(contaCorrente);
	}
}
