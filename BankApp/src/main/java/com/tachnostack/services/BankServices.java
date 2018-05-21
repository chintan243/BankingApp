package com.tachnostack.services;

import java.util.concurrent.CompletableFuture;

import com.tachnostack.domain.Bank;

public interface BankServices {

	CompletableFuture<Bank> getBalance(Long id) throws Exception;

	CompletableFuture<Bank> deposit(Long accountId, Long depositAmount)throws Exception;

	CompletableFuture<Bank> withdraw(Long accountId, Long withdrawAmount) throws InterruptedException;

	CompletableFuture<Bank> interFundTransaction(Long fromAccount, Long toAccount, Long transferAmount) throws InterruptedException;

}
