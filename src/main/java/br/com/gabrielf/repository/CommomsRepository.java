package br.com.gabrielf.repository;

import br.com.gabrielf.exception.NoFundsEnoughException;
import br.com.gabrielf.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.gabrielf.model.BanckService.*;
import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class CommomsRepository {

    public static void checkFundsForTransaction(final Wallet wallet, final long amount){
        if (wallet.getFounds()<amount){
            throw  new NoFundsEnoughException("Sua conta nao tem dinehiro o suficiente para realizar essa transação");
        }
    }

    public static List<Money> generateMoney(final UUID uuid, final long funds, final String description){
        MoneyAudit history = new MoneyAudit(uuid, ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history)).limit(funds).toList();
    }
}
