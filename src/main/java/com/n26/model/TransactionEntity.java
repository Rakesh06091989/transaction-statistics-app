package com.n26.model;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class TransactionEntity {

    private BigDecimal amount;
    private XMLGregorianCalendar timeStamp;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public XMLGregorianCalendar getTimeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(XMLGregorianCalendar timestamp){
        this.timeStamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(amount, that.amount) &&
                Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(amount, timeStamp);
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "amount=" + amount +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
