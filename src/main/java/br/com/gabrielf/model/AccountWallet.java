package br.com.gabrielf.model;

import lombok.Getter;

import java.util.List;

import static br.com.gabrielf.model.BanckService.*;
@Getter
public class AccountWallet extends  Wallet{
    private final List<String> pix;

    public AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
        addMoney(amount , "valor de criacao de conta");
    }


    public void addMoney(long amount,  String description) {
       List<Money> money = generateMoney(amount, description);
       this.money.addAll(money);
    }
}
