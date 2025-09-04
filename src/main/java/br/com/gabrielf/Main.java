package br.com.gabrielf;

import br.com.gabrielf.exception.AccountNotFoundException;
import br.com.gabrielf.exception.NoFundsEnoughException;
import br.com.gabrielf.model.AccountWallet;
import br.com.gabrielf.repository.AccountRepository;
import br.com.gabrielf.repository.InvestimentRepository;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class Main {
    private final static AccountRepository ACCOUNT_REPOSITORY = new AccountRepository();
    private final static InvestimentRepository INVESTIMENT_REPOSITORY = new InvestimentRepository();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá seja bem vindo ao Banco");

        while (true) {
            System.out.println("Digite a operacaod desejada");
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar uminvestimento");
            System.out.println("3 - Criar uma carteira de investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar da conta");
            System.out.println("6 - Transferencia da conta");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar Investimento");
            System.out.println("9 - Listar Contas");
            System.out.println("10 - Listar Investimento");
            System.out.println("11 - Listar carteiras de investimentos");
            System.out.println("12 - Atualizar Investimento");
            System.out.println("13 - Historico de conta");
            System.out.println("14 - Sair");
            var option = scanner.nextInt();
            switch (option) {
                case 1 -> createAccount();
                case 2 -> createInvestement();
                case 3 -> createWalletInvestment();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferToAccount();
                case 7 -> incInvestment();
                case 8 -> rescueInvestment();
                case 9 -> ACCOUNT_REPOSITORY.list().forEach(System.out::println);
                case 10 -> INVESTIMENT_REPOSITORY.list().forEach(System.out::println);
                case 11 -> INVESTIMENT_REPOSITORY.listWallets().forEach(System.out::println);
                case 12 -> {
                    INVESTIMENT_REPOSITORY.updateAmount();
                    System.out.println("Investimentos reajustados");
                }
                case 13 -> checkHistory();
                case 14 -> System.exit(0);
                default -> System.out.println("Opção invalida");

            }

        }
    }

    private static void incInvestment() {
        System.out.println("Informe a chave pix para investimento");
        var pix = scanner.next();
        System.out.println("Informe o valor que sera investido: ");
        var amount = scanner.nextLong();
        try {
            INVESTIMENT_REPOSITORY.deposit(pix, amount);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void createAccount() {
        System.out.println("Informe as chaves pix (separadas por ';'");
        var pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("valor inciial de deposito");
        var amount = scanner.nextLong();

        var wallet = ACCOUNT_REPOSITORY.create(pix, amount);
        System.out.println("Conta criada: " + wallet);
    }


    private static void createInvestement() {
        System.out.println("Informe ataxa do investimento: ");
        var tax = scanner.nextInt();
        System.out.println("valor inciial de deposito: ");
        var initialFunds = scanner.nextLong();
        var investiment = INVESTIMENT_REPOSITORY.create(tax, initialFunds);
        System.out.println("Investimento criado: " + investiment);
    }

    private static void withdraw() {
        System.out.println("Informe a chave pix para saque");
        var pix = scanner.next();
        System.out.println("Informe o valor que sera sacado: ");
        var amount = scanner.nextLong();
        try {

            ACCOUNT_REPOSITORY.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void deposit() {
        System.out.println("Informe a chave pix para deposito");
        var pix = scanner.next();
        System.out.println("Informe o valor que sera depositado: ");
        var amount = scanner.nextLong();
        try {
            ACCOUNT_REPOSITORY.deposit(pix, amount);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void transferToAccount() {
        System.out.println("Informe a chave pix de origem");
        var source = scanner.next();
        System.out.println("Informe a chave pix de destino");
        var target = scanner.next();
        System.out.println("Informe o valor que sera depositado: ");
        var amount = scanner.nextLong();
        try {
            ACCOUNT_REPOSITORY.transferMoney(source, target, amount);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createWalletInvestment() {
        System.out.println("Informe a chave pix da conta");
        var pix = scanner.next();
        var account = ACCOUNT_REPOSITORY.findByPix(pix);
        System.out.println("Informe o identificador do investimento");
        var investmentId = scanner.nextInt();
        var investmentWallet = INVESTIMENT_REPOSITORY.initInvestment(account, investmentId);
        System.out.println("Conta de investimento criada: " + investmentWallet);
    }

    private static void rescueInvestment() {
        System.out.println("Informe a chave pix para resgate de investimento");
        var pix = scanner.next();
        System.out.println("Informe o valor que sera sacado: ");
        var amount = scanner.nextLong();
        try {

            INVESTIMENT_REPOSITORY.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void checkHistory() {
        System.out.println("Informe a chave pix da conta para verificar extrato: ");
        var pix = scanner.next();
        try {
            var sortedHistory = ACCOUNT_REPOSITORY.getHistory(pix);
            sortedHistory.forEach((k, v)-> {
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println(v.getFirst().trasactionId());
                System.out.println(v.getFirst().description());
                System.out.println("R$ " + (v.size()/100)+(v.size() %100));
            });


        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }


    }

}