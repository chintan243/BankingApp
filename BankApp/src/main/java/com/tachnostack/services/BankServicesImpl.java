package com.tachnostack.services;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tachnostack.domain.Bank;
import com.tachnostack.repository.BankRepository;

@Service
public class BankServicesImpl implements BankServices {
	
	@Autowired
	private BankRepository bankRepository;
	
	
	@Override
	@Async
	public CompletableFuture<Bank> getBalance(Long id) throws Exception {
		Optional<Bank> bank = bankRepository.findById(id);
		if(!bank.isPresent())
			throw new RuntimeException("Get Balance is not possible");
		Thread.sleep(1000);
		return CompletableFuture.completedFuture(bank.get());
	}

	@Override
	@Transactional
	@Async
	public CompletableFuture<Bank> deposit(Long accountId, Long depositAmount) throws InterruptedException {
		//System.out.println("Executing asynchronously - "+ Thread.currentThread().getName()+"  --dp--  "+ accountId + "  -- amount --  "+ depositAmount);
		Optional<Bank> bank = bankRepository.findById(accountId);
		if(!bank.isPresent()){
			throw new RuntimeException("is not Present !");
		}
		Long curBal = bank.get().getCurrentBal();
		bank.get().setCurrentBal(curBal + depositAmount);
		try {
			Thread.sleep(5000);
			bankRepository.save(bank.get());
			return CompletableFuture.completedFuture(bank.get());
		} catch (InterruptedException e) {
			System.out.println("Problem found in deposit");
		}
		return null;
	}

	@Override
	@Transactional
	@Async
	public CompletableFuture<Bank> withdraw(Long accountId, Long withdrawAmount) throws InterruptedException {
		//System.out.println("Executing asynchronously - "+ Thread.currentThread().getName()+"  --with--  "+ accountId + "  -- amount --  "+ withdrawAmount);
		Optional<Bank> bank = bankRepository.findById(accountId);
		
		if(!bank.isPresent()){
			throw new RuntimeException();
		}
		Long curBal = bank.get().getCurrentBal();
		bank.get().setCurrentBal(curBal - withdrawAmount);
		try {
			Thread.sleep(5000);
			bankRepository.save(bank.get());
			return CompletableFuture.completedFuture(bank.get());
		} catch (InterruptedException e) {
			System.out.println("Problem found in WithDraw");
		}
		return null;
	}

	@Override
	@Transactional
	@Async
	public CompletableFuture<Bank> interFundTransaction(Long fromAccount, Long toAccount, Long transferAmount) throws InterruptedException {
		Optional<Bank> bankFrom = bankRepository.findById(fromAccount);
		Optional<Bank> bankTo = bankRepository.findById(toAccount);
		
		if(!bankFrom.isPresent() || !bankTo.isPresent())
			throw new RuntimeException("Trnasfer Fund is not possible !!!");

		withdraw(bankFrom.get().getAccountId(),transferAmount)
				.exceptionally(ex ->{
					throw new IllegalArgumentException("Withrowing money is not possible !!!");
				});
		CompletableFuture<Bank> to=deposit(bankTo.get().getAccountId(), transferAmount)
				.exceptionally(ex ->{
					throw new IllegalArgumentException("Deposit money is not possible !!!");
				});	
		return to;
	}
	
}
