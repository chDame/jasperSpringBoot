package org.example.controller;

import java.util.List;

import org.example.exception.TechnicalException;
import org.example.model.AccountManager;
import org.example.service.AccountManagerService;
import org.example.util.PersistenceUtil;
import  org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountManagerController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(AccountManagerController.class);

	@Autowired
	private AccountManagerService accountManagerService;

	@GetMapping(value = "/accountManagers")
	public List<AccountManager> list(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "10") int count, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "ASC") String order) {
		
		Specification<AccountManager> specifUser = accountManagerService.getSpecifications(q);

		return accountManagerService.list(specifUser, count, page, orderBy, order);
		
	}
	
	@PostMapping(value = "/accountManagers")
	@ResponseStatus(HttpStatus.CREATED)
	public AccountManager create(@RequestBody AccountManager accountManager) {
		return accountManagerService.create(accountManager);
	}

	@GetMapping(value = "/accountManagers/{accountManagerId}")
	public AccountManager get(@PathVariable Long accountManagerId) {
		return accountManagerService.getById(accountManagerId);
	}

	@PatchMapping(value = "/accountManagers/{accountManagerId}")
	public AccountManager patch(@PathVariable Long accountManagerId, @RequestBody AccountManager a) {
		AccountManager accountManager = accountManagerService.getById(accountManagerId);
		if (accountManager!=null) {
			PersistenceUtil.copyNonNullProperties(a, accountManager);
			return accountManagerService.update(accountManager);
		}

		throw new TechnicalException("accountManager is not persisted");
	}

	@PutMapping(value = "/accountManagers/{accountManagerId}")
	public AccountManager update(@PathVariable Long accountManagerId, @RequestBody AccountManager accountManager) {
		accountManager.setId(accountManagerId);
		AccountManager updated = accountManagerService.update(accountManager);
	
		if (updated!=null) {
			return updated;
		}

		throw new TechnicalException("accountManager is not persisted");
	}

	@DeleteMapping(value = "/accountManagers/{accountManagerId}")
	public void delete(@PathVariable Long accountManagerId) {
		accountManagerService.delete(accountManagerId);
	}



	@Override
	public Logger getLogger() {
		return logger;
	}

}
