package com.tachnostack.services;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tachnostack.domain.Bank;
import com.tachnostack.exception.NotFoundException;
import com.tachnostack.repository.BankRepository;


@Service
public class BankServicesImpl implements BankServices {
	
	@Autowired
	private BankRepository bankRepository;
	
	
	@Override
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
	public CompletableFuture<Bank> deposit(Long accountId, Long depositAmount){
		System.out.println("Executing asynchronously - "+ Thread.currentThread().getName()+"  --dp--  "+ accountId + "  -- amount --  "+ depositAmount);
		
		Optional<Bank> bank = bankRepository.findById(accountId);
		if(!bank.isPresent())
			throw new NotFoundException("Resource Not found");
		
		Long curBal = bank.get().getCurrentBal();
		bank.get().setCurrentBal(curBal + depositAmount);
		bankRepository.save(bank.get());
		return CompletableFuture.completedFuture(bank.get());
	}

	@Override
	@Transactional
	@Async
	public CompletableFuture<Bank> withdraw(Long accountId, Long withdrawAmount) {
		System.out.println("Executing asynchronously - " + Thread.currentThread().getName() + "  --with--  " + accountId+ "  -- amount --  " + withdrawAmount);
		
		Optional<Bank> bank = bankRepository.findById(accountId);
		if (!bank.isPresent()) 
			throw new NotFoundException("Resource Not found");
		
		Long curBal = bank.get().getCurrentBal();
		bank.get().setCurrentBal(curBal - withdrawAmount);
		bankRepository.save(bank.get());
		return CompletableFuture.completedFuture(bank.get());
	}
	
	@Override
	@Transactional
	@Async
	public CompletableFuture<Bank> interFundTransaction(Long fromAccount, Long toAccount, Long transferAmount) {
		withdraw(fromAccount,transferAmount);
		CompletableFuture<Bank> toFuture=deposit(toAccount, transferAmount);
		return toFuture;
	}
}
