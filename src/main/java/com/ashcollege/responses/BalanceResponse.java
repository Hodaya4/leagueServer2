package com.ashcollege.responses;

public class BalanceResponse {
    private boolean success;
    private Float balance;
    private String errorMessage;

    public BalanceResponse(boolean success, Float balance, String errorMessage) {
        this.success = success;
        this.balance = balance;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

