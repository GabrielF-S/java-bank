package br.com.gabrielf.repository;

import br.com.gabrielf.exception.AccountWithInvestimentException;
import br.com.gabrielf.exception.WalletNotFoundException;
import br.com.gabrielf.model.AccountWallet;
import br.com.gabrielf.model.Investiment;
import br.com.gabrielf.model.InvestimentWallet;

import java.util.ArrayList;
import java.util.List;

import static br.com.gabrielf.repository.CommomsRepository.checkFundsForTransaction;

public class InvestimentRepository {
    private long neextId =0;

    private final List<Investiment> investiments = new ArrayList<>();
    private final  List<InvestimentWallet> wallets = new ArrayList<>();


    public Investiment create(final long tax,  final long initialFunds){

        this.neextId++;
        Investiment investiment = new Investiment(this.neextId,tax,initialFunds);
        investiments.add(investiment);
        return investiment;

    }
    public InvestimentWallet initInvestment(final AccountWallet account, final long id){
        if (!wallets.isEmpty()){
        var accountInUse = wallets.stream().map(a -> a.getAccount()).toList();

            if (accountInUse.contains(account)) {
                throw new AccountWithInvestimentException("A conta "+ account +" já esta em uso");
            }
        }

        Investiment investiment = findById(id);
        checkFundsForTransaction(account, investiment.initialFunds());
        InvestimentWallet wallet = new InvestimentWallet(investiment, account, investiment.initialFunds());
        wallets.add(wallet);
        return wallet;

    }

    public InvestimentWallet deposit(final String pix, final long funds){
        InvestimentWallet wallat = findWallatByAccount(pix);
        wallat.addMoney(wallat.getAccount().reduceMoney(funds), wallat.getServiceType(),"Investimento");
        return  wallat;
    }

    public InvestimentWallet withdraw( final String pix, final long funds){
        InvestimentWallet wallet = findWallatByAccount(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount()
                .addMoney(wallet.reduceMoney(funds),wallet.getServiceType(), "saque de investimentos");
        if (wallet.getFounds()==0){
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount(){
        wallets.forEach(w -> w.updateAmount(w.getInvestiment().tax()));
    }

    public Investiment findById(final long id){
            return  investiments.stream()
                    .filter(a -> a.id() == id)
                    .findFirst()
                    .orElseThrow(
                            () -> new WalletNotFoundException("O investimenrto '"+ id +"' não foi encontrado")
                    );
    }

    public InvestimentWallet findWallatByAccount(final String pix){
        return  wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("A carteira não foi encontrada"));
    }
    public List<InvestimentWallet> listWallets() {
        return this.wallets;
    }

    public List<Investiment> list(){
        return  this.investiments;
    }

}
