package com.linked.controller;

import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.linked.model.RequestWrapper;
import com.linked.services.Bd;

@RestController
public class Contas {
	private Bd bd;

	public Contas() {
		bd = new Bd();
	}

	@PostMapping("/contas")
	public ResponseEntity<String> criarConta(@RequestBody RequestWrapper requestWrapper) {
		bd.connect();
		try {
			bd.preparedStatement = bd.connection.prepareStatement("SELECT * FROM agencia WHERE id=?");
			bd.preparedStatement.setInt(1, requestWrapper.getContaCorrente().getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				bd.preparedStatement = bd.connection.prepareStatement(
						"INSERT INTO correntista(nome,cpf,nascimento) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
				bd.preparedStatement.setString(1, requestWrapper.getCorrentista().getNome());
				bd.preparedStatement.setString(2, requestWrapper.getCorrentista().getCpf());
				bd.preparedStatement.setString(3, requestWrapper.getCorrentista().getNascimento());
				bd.preparedStatement.execute();
				bd.resultSet = bd.preparedStatement.getGeneratedKeys();
				if (bd.resultSet.next()) {
					bd.preparedStatement = bd.connection.prepareStatement(
							"INSERT INTO contacorrente(id_correntista,id_agencia,limite,saldo,ativa) VALUES(?,?,?,?,?)");
					bd.preparedStatement.setInt(1, bd.resultSet.getInt(1));
					bd.preparedStatement.setInt(2, requestWrapper.getContaCorrente().getId_agencia());
					bd.preparedStatement.setBigDecimal(3, requestWrapper.getContaCorrente().getLimite());
					bd.preparedStatement.setBigDecimal(4, requestWrapper.getContaCorrente().getSaldo());
					bd.preparedStatement.setString(5, requestWrapper.getContaCorrente().getAtiva());
					bd.preparedStatement.execute();
				}
			} else {
				return new ResponseEntity<>("Agência inexistente!", HttpStatus.NOT_FOUND);
			}
			bd.close();
			return new ResponseEntity<>("Conta criada com sucesso!", HttpStatus.CREATED);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Erro!", HttpStatus.BAD_REQUEST);
		}
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

	@PatchMapping("/saques1")
	public String transferencia() {
		return "TESTADO!";
	}

	@PatchMapping("/saques2")
	public String desativarConta() {
		return "TESTADO!";
	}
}
