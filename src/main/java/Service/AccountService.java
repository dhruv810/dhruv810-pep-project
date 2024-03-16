package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public boolean checkAccountByAccountID(int account_id) {
        return accountDAO.checkAccountByAccountID(account_id);
    }

    public boolean checkAccountByUsername(String username) {
        return accountDAO.checkAccountByUsername(username);
    }

    public Account createAccount(Account account) {

        if (account == null || account.getUsername().trim() == "" || account.getPassword().length() < 4) {
            return null;
        }
        else if (this.checkAccountByUsername(account.getUsername())) {
            return null;
        }

        return accountDAO.registerAccount(account);
    }

    public Account checkCredentials(Account account) {
        return accountDAO.getAccountByUsernameAndPassword(account);
    }

}
