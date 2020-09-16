
package org.example.repository;

import org.example.model.AccountManager;

public interface AccountManagerRepository extends AbstractRepository<AccountManager> {

	AccountManager findByEmail(String email);

}
