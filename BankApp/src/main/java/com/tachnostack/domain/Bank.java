package com.tachnostack.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Bank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;
	private Long  currentBal;
	
	public Bank(){}
	
	public Bank(Long accountId, Long currentBal) {
		this.accountId = accountId;
		this.currentBal = currentBal;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getCurrentBal() {
		return currentBal;
	}

	public void setCurrentBal(Long currentBal) {
		this.currentBal = currentBal;
	}
	
}
