package net.mercadosocial.moneda.model;

import net.mercadosocial.moneda.util.Util;

import java.util.List;

/**
 * Created by julio on 2/02/18.
 */

public class Wallet {

    private Float balance;
    private String id;
    private String last_transaction;
    private List<TransactionLog> transaction_logs;


    public String getBalanceFormatted() {
        return Util.getDecimalFormatted(getBalance(), true);
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_transaction() {
        return last_transaction;
    }

    public void setLast_transaction(String last_transaction) {
        this.last_transaction = last_transaction;
    }

    public List<TransactionLog> getTransaction_logs() {
        return transaction_logs;
    }

    public void setTransaction_logs(List<TransactionLog> transaction_logs) {
        this.transaction_logs = transaction_logs;
    }

}