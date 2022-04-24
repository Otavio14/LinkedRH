package com.linked.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.linked.model.ContaCorrente;
import com.linked.model.ContaCorrente_Correntista;
import com.linked.model.ContasCorrente_Correntista;
import com.linked.model.Correntista;
import com.linked.services.Bd;

@Service
public class Repository {
	private Bd bd;

	public Repository() {
		bd = new Bd();
	}

	public ResponseEntity<String> criarConta(ContaCorrente_Correntista contaCorrente_Correntista) {
		bd.connect();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date parsed = formatter.parse(contaCorrente_Correntista.getCorrentista().getNascimento());
			java.sql.Date data = new java.sql.Date(parsed.getTime());
			bd.preparedStatement = bd.connection.prepareStatement("SELECT * FROM agencia WHERE id=?");
			bd.preparedStatement.setInt(1, contaCorrente_Correntista.getContaCorrente().getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				bd.preparedStatement = bd.connection.prepareStatement(
						"INSERT INTO correntista(nome,cpf,nascimento) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
				bd.preparedStatement.setString(1, contaCorrente_Correntista.getCorrentista().getNome());
				bd.preparedStatement.setString(2, contaCorrente_Correntista.getCorrentista().getCpf());
				bd.preparedStatement.setDate(3, data);
				bd.preparedStatement.execute();
				bd.resultSet = bd.preparedStatement.getGeneratedKeys();
				if (bd.resultSet.next()) {
					bd.preparedStatement = bd.connection.prepareStatement(
							"INSERT INTO contacorrente(id_correntista,id_agencia,limite,saldo,ativa) VALUES(?,?,?,?,'T')");
					bd.preparedStatement.setInt(1, bd.resultSet.getInt(1));
					bd.preparedStatement.setInt(2, contaCorrente_Correntista.getContaCorrente().getId_agencia());
					bd.preparedStatement.setBigDecimal(3, contaCorrente_Correntista.getContaCorrente().getLimite());
					bd.preparedStatement.setBigDecimal(4, contaCorrente_Correntista.getContaCorrente().getSaldo());
					bd.preparedStatement.execute();
				}
			} else {
				return new ResponseEntity<String>("Agência inexistente!", HttpStatus.NOT_FOUND);
			}
			bd.close();
			return new ResponseEntity<String>("Conta criada com sucesso!", HttpStatus.CREATED);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Erro!", HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<String> consultarSaldo(ContaCorrente contaCorrente) {
		bd.connect();
		try {
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT saldo,ativa FROM contacorrente WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				if (bd.resultSet.getString("ativa").equals("F"))
					return new ResponseEntity<String>("Conta inativa!", HttpStatus.BAD_REQUEST);
				return new ResponseEntity<String>("Saldo: R$ " + bd.resultSet.getBigDecimal("saldo"), HttpStatus.OK);
			}
			bd.close();
			return new ResponseEntity<String>("Conta inexistente!", HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Erro!", HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<String> depositar(ContaCorrente contaCorrente) {
		bd.connect();
		try {
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT ativa FROM contacorrente WHERE id=? AND id_agencia=? AND ativa='F'");
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				return new ResponseEntity<String>("Conta inativa!", HttpStatus.BAD_REQUEST);
			}
			bd.preparedStatement = bd.connection
					.prepareStatement("UPDATE contacorrente SET saldo=saldo+? WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setBigDecimal(1, contaCorrente.getSaldo());
			bd.preparedStatement.setInt(2, contaCorrente.getId());
			bd.preparedStatement.setInt(3, contaCorrente.getId_agencia());
			bd.preparedStatement.executeUpdate();
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT saldo FROM contacorrente WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				return new ResponseEntity<String>("Saldo: R$ " + bd.resultSet.getBigDecimal("saldo"), HttpStatus.OK);
			}
			bd.close();
			return new ResponseEntity<String>("Conta inexistente!", HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Erro!", HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<String> sacar(ContaCorrente contaCorrente) {
		bd.connect();
		try {
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT ativa,limite,saldo FROM contacorrente WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				if (bd.resultSet.getString("ativa").equals("F"))
					return new ResponseEntity<String>("Conta inativa!", HttpStatus.BAD_REQUEST);
				if (bd.resultSet.getBigDecimal("saldo").subtract(contaCorrente.getSaldo())
						.compareTo(bd.resultSet.getBigDecimal("limite").negate()) == -1)
					return new ResponseEntity<String>("Limite excedido!", HttpStatus.FORBIDDEN);
			}
			bd.preparedStatement = bd.connection
					.prepareStatement("UPDATE contacorrente SET saldo=saldo-? WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setBigDecimal(1, contaCorrente.getSaldo());
			bd.preparedStatement.setInt(2, contaCorrente.getId());
			bd.preparedStatement.setInt(3, contaCorrente.getId_agencia());
			bd.preparedStatement.executeUpdate();
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT saldo FROM contacorrente WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				return new ResponseEntity<String>("Saldo: R$ " + bd.resultSet.getBigDecimal("saldo"), HttpStatus.OK);
			}
			bd.close();
			return new ResponseEntity<String>("Conta inexistente!", HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Erro!", HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<String> transferir(List<ContaCorrente> contaCorrente) {
		bd.connect();
		try {
			bd.preparedStatement = bd.connection.prepareStatement(
					"SELECT id,id_agencia,ativa,limite,saldo FROM contacorrente WHERE (id=? AND id_agencia=?) OR (id=? AND id_agencia=?)");
			bd.preparedStatement.setInt(1, contaCorrente.get(0).getId());
			bd.preparedStatement.setInt(2, contaCorrente.get(0).getId_agencia());
			bd.preparedStatement.setInt(3, contaCorrente.get(1).getId());
			bd.preparedStatement.setInt(4, contaCorrente.get(1).getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			int count = 0;
			while (bd.resultSet.next()) {
				count++;
				if (bd.resultSet.getString("ativa").equals("F"))
					return new ResponseEntity<String>("Conta inativa!", HttpStatus.BAD_REQUEST);
				if (bd.resultSet.getInt("id") == contaCorrente.get(0).getId()
						&& bd.resultSet.getInt("id_agencia") == contaCorrente.get(0).getId_agencia()
						&& bd.resultSet.getBigDecimal("saldo").subtract(contaCorrente.get(1).getSaldo())
								.compareTo(bd.resultSet.getBigDecimal("limite").negate()) == -1)
					return new ResponseEntity<String>("Limite excedido!", HttpStatus.FORBIDDEN);
			}
			if (count < 2)
				return new ResponseEntity<String>("Conta inexistente!", HttpStatus.NOT_FOUND);
			bd.preparedStatement = bd.connection
					.prepareStatement("UPDATE contacorrente SET saldo=saldo-? WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setBigDecimal(1, contaCorrente.get(1).getSaldo());
			bd.preparedStatement.setInt(2, contaCorrente.get(0).getId());
			bd.preparedStatement.setInt(3, contaCorrente.get(0).getId_agencia());
			bd.preparedStatement.executeUpdate();
			bd.preparedStatement = bd.connection
					.prepareStatement("UPDATE contacorrente SET saldo=saldo+? WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setBigDecimal(1, contaCorrente.get(1).getSaldo());
			bd.preparedStatement.setInt(2, contaCorrente.get(1).getId());
			bd.preparedStatement.setInt(3, contaCorrente.get(1).getId_agencia());
			bd.preparedStatement.executeUpdate();
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT saldo FROM contacorrente WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setInt(1, contaCorrente.get(0).getId());
			bd.preparedStatement.setInt(2, contaCorrente.get(0).getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				return new ResponseEntity<String>("Saldo: R$ " + bd.resultSet.getBigDecimal("saldo"), HttpStatus.OK);
			}
			bd.close();
			return new ResponseEntity<String>("Conta inexistente!", HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Erro!", HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<ContasCorrente_Correntista> consultarCorrentista(Correntista correntista) {
		ContasCorrente_Correntista contasCorrente_Correntista = new ContasCorrente_Correntista();
		Correntista correntistaDados = new Correntista();
		ContaCorrente contaCorrente = new ContaCorrente();
		List<ContaCorrente> contasCorrente = new ArrayList<ContaCorrente>();

		bd.connect();
		try {
			bd.preparedStatement = bd.connection.prepareStatement("SELECT * FROM correntista WHERE cpf=?");
			bd.preparedStatement.setString(1, correntista.getCpf());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				correntistaDados.setId(bd.resultSet.getInt("id"));
				correntistaDados.setNome(bd.resultSet.getString("nome"));
				correntistaDados.setCpf(bd.resultSet.getString("cpf"));
				correntistaDados.setNascimento(bd.resultSet.getString("nascimento"));
				contasCorrente_Correntista.setCorrentista(correntistaDados);
				bd.preparedStatement = bd.connection.prepareStatement(
						"SELECT id,id_agencia,if(limite=0, 'Comum', 'Especial') as tipo FROM contacorrente WHERE id_correntista=?");
				bd.preparedStatement.setInt(1, correntistaDados.getId());
				bd.resultSet = bd.preparedStatement.executeQuery();
				while (bd.resultSet.next()) {
					contaCorrente.setId(bd.resultSet.getInt("id"));
					contaCorrente.setId_agencia(bd.resultSet.getInt("id_agencia"));
					contaCorrente.setTipo(bd.resultSet.getString("tipo"));
					contasCorrente.add(contaCorrente);
				}
				contasCorrente_Correntista.setContaCorrente(contasCorrente);
				return new ResponseEntity<ContasCorrente_Correntista>(contasCorrente_Correntista, HttpStatus.OK);
			}
			bd.close();
			return new ResponseEntity<ContasCorrente_Correntista>(contasCorrente_Correntista, HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<ContasCorrente_Correntista>(contasCorrente_Correntista, HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<ContaCorrente> desativarConta(ContaCorrente contaCorrente) {
		ContaCorrente contaCorrenteDados = new ContaCorrente();

		bd.connect();
		try {
			bd.preparedStatement = bd.connection.prepareStatement(
					"UPDATE contacorrente SET ativa='F' WHERE id=? AND id_agencia=?", Statement.RETURN_GENERATED_KEYS);
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			int count = bd.preparedStatement.executeUpdate();
			bd.preparedStatement = bd.connection
					.prepareStatement("SELECT * FROM contacorrente WHERE id=? AND id_agencia=?");
			bd.preparedStatement.setInt(1, contaCorrente.getId());
			bd.preparedStatement.setInt(2, contaCorrente.getId_agencia());
			bd.resultSet = bd.preparedStatement.executeQuery();
			if (bd.resultSet.next()) {
				contaCorrenteDados.setId(bd.resultSet.getInt("id"));
				contaCorrenteDados.setId_correntista(bd.resultSet.getInt("id_correntista"));
				contaCorrenteDados.setId_agencia(bd.resultSet.getInt("id_agencia"));
				contaCorrenteDados.setLimite(bd.resultSet.getBigDecimal("limite"));
				contaCorrenteDados.setSaldo(bd.resultSet.getBigDecimal("saldo"));
				contaCorrenteDados.setAtiva(bd.resultSet.getString("ativa"));
				if (count == 0)
					return new ResponseEntity<ContaCorrente>(contaCorrenteDados, HttpStatus.NOT_MODIFIED);
				return new ResponseEntity<ContaCorrente>(contaCorrenteDados, HttpStatus.OK);
			}
			bd.close();
			return new ResponseEntity<ContaCorrente>(contaCorrenteDados, HttpStatus.FORBIDDEN);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<ContaCorrente>(contaCorrenteDados, HttpStatus.BAD_REQUEST);
		}
	}
}
