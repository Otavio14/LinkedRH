package com.linked.model;

import java.util.List;

public class ContasCorrente_Correntista {
	List<ContaCorrente> contaCorrente;
	Correntista correntista;

	public List<ContaCorrente> getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(List<ContaCorrente> contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public Correntista getCorrentista() {
		return correntista;
	}

	public void setCorrentista(Correntista correntista) {
		this.correntista = correntista;
	}
}