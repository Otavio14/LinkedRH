package com.linked.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Contas {
	@PostMapping("/contas")
	public String criarConta() {
		return "TESTADO!";
	}

	@GetMapping("/saldos")
	public String consultaSaldo() {
		return "TESTADO!";
	}

	@PatchMapping("/depositos")
	public String deposito() {
		return "TESTADO!";
	}

	@PatchMapping("/saques")
	public String saque() {
		return "TESTADO!";
	}

	@PatchMapping("/saques")
	public String transferencia() {
		return "TESTADO!";
	}

	@PatchMapping("/saques")
	public String desativarConta() {
		return "TESTADO!";
	}
}
