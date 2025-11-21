package backend.payments;

public class CreditCardPayment implements PaymentMethod {

    private String cardNumber;
    private String cardHolder;
    private String expiry;

    public CreditCardPayment() {}

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }

    @Override
    public void pay(float amount) {}
}
