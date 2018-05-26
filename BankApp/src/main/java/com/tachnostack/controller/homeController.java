package com.tachnostack.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tachnostack.domain.Bank;
import com.tachnostack.domain.InterFundTransfer;
import com.tachnostack.services.BankServices;

@RestController
public class homeController {

	private BankServices bankServices;
	private ConcurrentMap<Long, Long> map;
	
	public homeController(BankServices bankServices,ConcurrentMap<Long, Long> map) {
		this.bankServices = bankServices;
		this.map = map;
	}

	@GetMapping("/get/{id}")
	public Bank getBal(@PathVariable("id") Long id) throws Exception {
		CompletableFuture<Bank> bank = bankServices.getBalance(id);
		while (true) {
			if (bank.isDone()) {
				break;
			}
		}
		return bank.get();
	}

	@PostMapping("/deposit")
	public Bank deposit(@RequestBody Bank bank) throws Exception {
		setLock(bank.getAccountId());
		CompletableFuture<Bank> depositFuture = bankServices.deposit(bank.getAccountId(), bank.getCurrentBal());
		while (true) {
			if (depositFuture.isDone()) {
				break;
			}
		}
		freeAccount(bank.getAccountId());
		return depositFuture.get();
	}

	@PostMapping("/withdraw")
	public Bank WithDraw(@RequestBody Bank bank) throws Exception {
		setLock(bank.getAccountId());
		CompletableFuture<Bank> withdrawFuture = bankServices.withdraw(bank.getAccountId(), bank.getCurrentBal());
		while (true) {
			if (withdrawFuture.isDone()) {
				break;
			}
		}
		freeAccount(bank.getAccountId());
		return withdrawFuture.get();
	}

	@PostMapping("/fundtransfer/inter")
	public Bank interFundTransferOps(@RequestBody InterFundTransfer interFundTransfer) throws Exception {
		setLock(interFundTransfer.getFromAccount(),interFundTransfer.getToAccount());
		CompletableFuture<Bank> interFundTransferFuture = bankServices.interFundTransaction(interFundTransfer.getFromAccount(),
				interFundTransfer.getToAccount(), interFundTransfer.getTransferAmount());
		while (true) {
			if (interFundTransferFuture.isDone()) {
				break;
			}
		}
		freeAccount(interFundTransfer.getFromAccount());
		freeAccount(interFundTransfer.getToAccount());
		return interFundTransferFuture.get();
	}	
	
	private synchronized void setLock(Long fromAccount, Long toAccount) throws InterruptedException {
		while (this.map.containsKey(toAccount) || this.map.containsKey(fromAccount)) {
			wait();
		}
		this.map.putIfAbsent(fromAccount, (long) 1);
		this.map.putIfAbsent(toAccount, (long) 1);
	}

	private synchronized void setLock(Long accId) throws InterruptedException {
		while (this.map.containsKey(accId)) {
			wait();
		}
		this.map.putIfAbsent(accId, (long) 1);
	}
	
	private synchronized void freeAccount(Long accId) {
		this.map.remove(accId);
		notify();
	}
}
