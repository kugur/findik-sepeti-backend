package com.kolip.findiksepeti.payment;

public class Payment {
    private String creditCardNumber;

    public Payment() {
    }

    public Payment(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    @Override
    public String toString() {
        return "Payment{" + "creditCardNumber='" + creditCardNumber + '\'' + '}';
    }
}
