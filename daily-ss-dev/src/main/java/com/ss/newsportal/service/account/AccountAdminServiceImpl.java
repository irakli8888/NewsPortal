package com.ss.newsportal.service.account;

import com.ss.newsportal.dto.account.AccountsStatAdminResponse;
import com.ss.newsportal.entity.view.AccountAdmin;
import com.ss.newsportal.mapper.account.AccountAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountAdminServiceImpl implements AccountAdminService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AccountAdminMapper accountMapper;

    @Autowired
    public AccountAdminServiceImpl(AccountAdminMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountsStatAdminResponse getAccountsAdmin(final String search) {

        List<Predicate> predicates = new ArrayList<>();

        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountAdmin> cQuery = cBuilder.createQuery(AccountAdmin.class);
        Root<AccountAdmin> account = cQuery.from(AccountAdmin.class);

        if (!search.isBlank()) {
            predicates.add(cBuilder.like(cBuilder.lower(
                    cBuilder.function("concat_ws", String.class,
                            cBuilder.literal(' '), account.get("profile"), account.get("username"))),
                    "%" + search.toLowerCase() + "%"
            ));
        }

        if (!predicates.isEmpty()) {
            cQuery.where(predicates.toArray(new Predicate[0]));
        }

        List<AccountAdmin> accounts = entityManager.createQuery(cQuery).getResultList();

        AccountsStatAdminResponse accountsStatAdminResponse = new AccountsStatAdminResponse();

        accountsStatAdminResponse.setAccounts(accountMapper.toDtoList(accounts));

        long countTotal = accounts.size();

        long countRegister = accounts.stream().filter(accountAdmin ->
                Optional.ofNullable(accountAdmin.getUsername()).isPresent()).count();

        accountsStatAdminResponse.setCountTotal(countTotal);
        accountsStatAdminResponse.setCountRegister(countRegister);
        accountsStatAdminResponse.setCountAnonymous(countTotal - countRegister);

        return accountsStatAdminResponse;
    }
}
