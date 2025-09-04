package br.com.gabrielf.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.gabrielf.model.BanckService.INVESTIMENT;

@ToString
@Getter
public class InvestimentWallet extends Wallet {

    private final Investiment investiment;
    private final AccountWallet account;

    public InvestimentWallet(Investiment investiment, AccountWallet account, final long amount) {
        super(INVESTIMENT);
        this.investiment = investiment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getServiceType(), "investimento");
    }

    public  void  updateAmount(final long percent){
        long amount = getFounds() * percent / 100;
        MoneyAudit history = new MoneyAudit(UUID.randomUUID(), getServiceType(), "rendimentos", OffsetDateTime.now());
        List<Money> money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);
    }
}
