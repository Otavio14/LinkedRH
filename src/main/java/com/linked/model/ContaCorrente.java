package com.linked.model;

import java.math.BigDecimal;

public class ContaCorrente {
	private int id;
	private int id_correntista;
	private int id_agencia;
	private BigDecimal limite;
	private BigDecimal saldo;
	private String ativa;

	public ContaCorrente() {
		super();
	}

	public ContaCorrente(int id, int id_correntista, int id_agencia, BigDecimal limite, BigDecimal saldo,
			String ativa) {
		super();
		this.id = id;
		this.id_correntista = id_correntista;
		this.id_agencia = id_agencia;
		this.limite = limite;
		this.saldo = saldo;
		this.ativa = ativa;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_correntista() {
		return id_correntista;
	}

	public void setId_correntista(int id_correntista) {
		this.id_correntista = id_correntista;
	}

	public int getId_agencia() {
		return id_agencia;
	}

	public void setId_agencia(int id_agencia) {
		this.id_agencia = id_agencia;
	}

	public BigDecimal getLimite() {
		return limite;
	}

	public void setLimite(BigDecimal limite) {
		this.limite = limite;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public String getAtiva() {
		return ativa;
	}

	public void setAtiva(String ativa) {
		this.ativa = ativa;
	}

}
