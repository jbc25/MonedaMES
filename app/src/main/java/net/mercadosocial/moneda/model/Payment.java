package net.mercadosocial.moneda.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by julio on 1/02/18.
 */

public class Payment {

    //when receiving pending
    private String id;
    private String sender;
    private String status;
    private String processed;
    private String timestamp;

    // when sending new
    private String receiver;
    private String pin;

    // common
    private Float total_amount;
    private Float currency_amount;




    public String getBoniatosAmountFormatted() {
        NumberFormat numberFormat = new DecimalFormat("0.##");
        String amountFormatted = numberFormat.format(getCurrency_amount());
        return amountFormatted;
    }

    public String getEurosAmountFormatted() {
        NumberFormat numberFormat = new DecimalFormat("0.##");
        String amountFormatted = numberFormat.format(getTotal_amount() - getCurrency_amount());
        return amountFormatted;
    }

    public String getTotalAmountFormatted() {
        NumberFormat numberFormat = new DecimalFormat("0.##");
        String amountFormatted = numberFormat.format(getTotal_amount());
        return amountFormatted;
    }


    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Float total_amount) {
        this.total_amount = total_amount;
    }

    public Float getCurrency_amount() {
        return currency_amount;
    }

    public void setCurrency_amount(Float currency_amount) {
        this.currency_amount = currency_amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
