package com.redhat.training.bank.message;

public class NewBankAccountBack {

    public  Long id;
    public Long balance;
    public NewBankAccountBack(Long id, Long balance) {
        this.id = id;
        this.balance = balance;
    }

    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
    }
    
    
}
