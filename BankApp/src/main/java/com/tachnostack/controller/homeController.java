package com.tachnostack.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private BankServices bankServices;

	@Autowired
	private ConcurrentMap<Long, Long> map;

	private boolean isLocked(Long accId) {
		return this.map.containsKey(accId);
	}

	private synchronized void setLock(Long fromAccount, Long toAccount) throws InterruptedException {
		while (this.map.containsKey(toAccount) || this.map.containsKey(fromAccount)) {
			wait();
			System.out.println("Wait temp release Lock - " + Thread.currentThread().getName());
		}
		
		System.out.println("set Lock - " + Thread.currentThread().getName() + "  " + fromAccount);
		System.out.println("set Lock - " + Thread.currentThread().getName() + "  " + toAccount);
		this.map.putIfAbsent(fromAccount, (long) 1);
		this.map.putIfAbsent(toAccount, (long) 1);
	}

	private synchronized void setLock(Long accId) throws InterruptedException {
		while (this.map.containsKey(accId)) {
			wait();
			System.out.println("Wait temp release Lock - " + Thread.currentThread().getName() + "  " + accId);
		}
		System.out.println("set Lock - " + Thread.currentThread().getName() + "  " + accId);
		this.map.putIfAbsent(accId, (long) 1);
	}

	private synchronized void freeAccount(Long accId) {
		System.out.println("Released Lock - " + Thread.currentThread().getName() + "  " + accId);
		this.map.remove(accId);
		notify();
	}

	@GetMapping("/get/{id}")
	public Bank getBal(@PathVariable("id") Long id) throws Exception {
		CompletableFuture<Bank> bank = bankServices.getBalance(id);
		while (true) {
			if (bank.isDone()) {
				System.out.println("Result from asynchronous process  " + id + " - " + bank.get().getCurrentBal());
				break;
			}
		}
		return bank.get();
	}

	@PostMapping("/deposit")
	public Bank deposit(@RequestBody Bank bank) throws Exception {
		while (isLocked(bank.getAccountId())) {
			continue;
		}
		setLock(bank.getAccountId());
		CompletableFuture<Bank> depositFuture = bankServices.deposit(bank.getAccountId(), bank.getCurrentBal());
		while (true) {
			if (depositFuture.isDone()) {
				System.out.println("Result from asynchronous process  " + depositFuture.get().getAccountId() + " - "
						+ depositFuture.get().getCurrentBal());
				break;
			}
		}
		freeAccount(bank.getAccountId());
		return depositFuture.get();
	}

	@PostMapping("/withdraw")
	public Bank WithDraw(@RequestBody Bank bank) throws Exception {
		while (isLocked(bank.getAccountId())) {
			continue;
		}
		setLock(bank.getAccountId());
		CompletableFuture<Bank> withdrawFuture = bankServices.withdraw(bank.getAccountId(), bank.getCurrentBal());
		while (true) {
			if (withdrawFuture.isDone()) {
				System.out.println("Result from asynchronous process  " + withdrawFuture.get().getAccountId() + " - "
						+ withdrawFuture.get().getCurrentBal());
				break;
			}
		}
		freeAccount(bank.getAccountId());
		return withdrawFuture.get();
	}

	@PostMapping("/funtransfer/inter")
	public Bank interFundTransferOps(@RequestBody InterFundTransfer interFundTransfer) throws Exception {
		while (isLocked(interFundTransfer.getFromAccount()))
			continue;
		setLock(interFundTransfer.getFromAccount(),interFundTransfer.getToAccount());

		CompletableFuture<Bank> interFundTransferFuture = bankServices.interFundTransaction(interFundTransfer.getFromAccount(),
				interFundTransfer.getToAccount(), interFundTransfer.getTransferAmount())
				.handle((s,t)->{
					if(s != null)
						return s;
					else
						System.out.println("Not possible Home Controller: --- "+ t.getMessage());
					return null;
				});
		while (true) {
			if (interFundTransferFuture.isDone()) {
				break;
			}
		}
		freeAccount(interFundTransfer.getFromAccount());
		freeAccount(interFundTransfer.getToAccount());
		return interFundTransferFuture.get();
	}
}
