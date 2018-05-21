package com.tachnostack.domain;

import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class InterFundTransfer {
	private Long fromAccount;
	private Long toAccount;
	private Long transferAmount;
	
	public InterFundTransfer(){}
	public InterFundTransfer(Long fromAccount, Long toAccount, Long transferAmount) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.transferAmount = transferAmount;
	}
	public Long getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(Long fromAccount) {
		this.fromAccount = fromAccount;
	}
	public Long getToAccount() {
		return toAccount;
	}
	public void setToAccount(Long toAccount) {
		this.toAccount = toAccount;
	}
	public Long getTransferAmount() {
		return transferAmount;
	}
	public void setTransferAmount(Long transferAmount) {
		this.transferAmount = transferAmount;
	}
	
	
}
