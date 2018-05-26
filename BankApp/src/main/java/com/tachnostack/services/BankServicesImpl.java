package com.tachnostack.services;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.springframework.transaction.annotation.Propagation;
//import javax.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tachnostack.domain.Bank;
import com.tachnostack.repository.BankRepository;

import javassist.NotFoundException;

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
			throw new RuntimeException();
		
		Long curBal = bank.get().getCurrentBal();
		bank.get().setCurrentBal(curBal + depositAmount);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("Problem found in deposit");
		}
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
			throw new RuntimeException();
		
		Long curBal = bank.get().getCurrentBal();
		bank.get().setCurrentBal(curBal - withdrawAmount);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	

//	@Override
//	@Transactional(propagation = Propagation.REQUIRED, readOnly= false)
//	@Async
//	public CompletableFuture<Bank> interFundTransaction(Long fromAccount, Long toAccount, Long transferAmount) {
//
//		System.out.println("Executing interfund Transfer- " + Thread.currentThread().getName() + "  --from--  "
//				+ fromAccount + "  -- to --  " + toAccount);
//		
//		CompletableFuture<Bank> fromFuture = CompletableFuture.supplyAsync(new Supplier<Bank>() {
//				public Bank get() 
//				{
//					try {
//						return withdraw(fromAccount, transferAmount).get();
//					} catch (InterruptedException | ExecutionException e) {
//						throw new RuntimeException();
//					}
//				}
//			}).handle((bank, ex) -> {
//				if (ex != null) {
//					System.out.println("Withdraow not possibel");
//					throw new RuntimeException();
//				}
//				return CompletableFuture.completedFuture(bank);
//			}).join();
//
//		
//		CompletableFuture<Bank> toFuture = CompletableFuture.supplyAsync(new Supplier<Bank>() {
//			public Bank get() 
//			{
//				try {
//					return deposit(toAccount, transferAmount).get();
//				} catch (InterruptedException | ExecutionException e) {
//					throw new RuntimeException();
//				}
//			}
//		}).handle((bank, ex) -> {
//			if (ex != null) {
//				System.out.println("Deposit not possible");
//				throw new RuntimeException();
//			}
//			return CompletableFuture.completedFuture(bank);
//		}).join();
//		
//		while(true){
//			if(fromFuture.isDone() && toFuture.isDone() ){
//				System.out.println("Hello ---------------------");
//				break;
//			}
//		}
//		return toFuture;
//	}
	
}
