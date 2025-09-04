package br.com.gabrielf.exception;

public class InvestimentNotFoundException extends RuntimeException {
    public InvestimentNotFoundException(String message) {
        super(message);
    }
}
