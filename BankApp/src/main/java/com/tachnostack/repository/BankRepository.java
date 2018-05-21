package com.tachnostack.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tachnostack.domain.Bank;

@Repository
public interface BankRepository extends CrudRepository<Bank, Long> {

}
