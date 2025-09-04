package br.com.gabrielf.repository;

import br.com.gabrielf.exception.AccountNotFoundException;
import br.com.gabrielf.exception.PixInUseException;
import br.com.gabrielf.model.AccountWallet;
import br.com.gabrielf.model.MoneyAudit;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.gabrielf.repository.CommomsRepository.checkFundsForTransaction;
import static java.time.temporal.ChronoUnit.*;

public class AccountRepository {
    private List<AccountWallet> account = new ArrayList<>();


    public AccountWallet create(final List<String> pix, final long initialFunds){
        if (!account.isEmpty()){
            List<String> pixList = account.stream().flatMap(a -> a.getPix().stream()).toList();
            for (String p : pix) {
                if (pixList.contains(p)) {
                    throw new PixInUseException("O Pix " + p + " já esta em uso");
                }
            }
        }
        AccountWallet newAccount = new AccountWallet(initialFunds,pix);
        account.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount){
        AccountWallet  target=  findByPix(pix);
        target.addMoney(fundsAmount,"deposito");
    }
    public long withdraw(final String pix, final long amount){
        AccountWallet  source=  findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String pix, final String targetPix, final long amount){
        AccountWallet  source=  findByPix(pix);
        checkFundsForTransaction(source, amount);
        AccountWallet  target=  findByPix(targetPix);
        String message = "pix enviado de '"+ source + "' para '"+ target +"'";
        target.addMoney(source.reduceMoney(amount), source.getServiceType(), message);
    }

    public AccountWallet findByPix(final String pix){
        return account.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("A conta com a chave pix "+pix+ " não existe ou esta encerrada" ));
    }
    public List<AccountWallet> list(){
        return this.account;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix){
        var wallet = findByPix(pix);
        var audit = wallet.getFinancialTrasactions();
        return audit.stream()
                .collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(SECONDS)));
    }
}
