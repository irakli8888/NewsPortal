package com.ss.newsportal.repository;

import com.ss.newsportal.entity.Account;
import com.ss.newsportal.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByEmail(String email);

    Person findByAccount(Account account);

    @Query("select p from Person p where p.account.username=:username")
    Person findByAccountUsername(String username);
}