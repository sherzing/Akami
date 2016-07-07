package com.jiahaoliuliu.akami.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model created to contain Withdraws. This is an example:
 * - AED 4200.00 withdrawn from acc. XXX132001 on Feb  1 2015  6:32PM at ATM-CBD ATM MARINA BR 2501.
 *   Avail.Bal.AED12283.31.Exercise caution with large amount of cash.Visit www.adcb.com/mobileapp to
 *   download the ADCB Mobile Banking App.
 * Created by jiahaoliuliu on 7/7/16.
 */
public class Withdraw implements ITransactions {

    private static final String TAG = "Withdraw";

    private static final String DATE_FORMAT = "MMM dd yyyy HH:mmaa";

    // Static variables for parsing. Since the parsing is done in each expense, it does make sense to have them as static
    private static final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private Date date;

    private float quantity;

    private String account;

    // When the app is able to parse more than one bank, add the field bank
    private String branch;

    // The constructor which creates expense from the sms
    public Withdraw(Sms sms) {
        switch (sms.getType()) {
            case WITHDRAW:
                parseSms(sms);
                break;
            case UNKNOWN:
            default:
                throw new IllegalArgumentException("The sms type is unknown " + sms);
        }
    }

    // Other methods
    private void parseSms(Sms sms) {
        Pattern pattern = Pattern.compile(sms.getType().getRegExpression());
        Matcher matcher = pattern.matcher(sms.getBody());
        if (!matcher.find()) {
            throw new IllegalArgumentException("The type of the sms suppose to be expense 1 but it does not matches");
        }

        // Get the first expense
        try {
            this.quantity = Float.parseFloat(matcher.group(1));
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("Error parsing the quantity " + matcher.group(1));
        }

        this.account = matcher.group(2);
        try {
            this.date = simpleDateFormatter.parse(matcher.group(3));
        } catch (ParseException parseException) {
            // Use the date of the message instead
            Log.w(TAG, "Error parsing the date \"" + matcher.group(3) + "\"");
            this.date = sms.getDate();
        }
        this.branch = matcher.group(4);
    }

    @Override
    public Type getType() {
        return Type.WITHDRAW;
    }

    @Override
    public float getQuantity() {
        return quantity;
    }

    @Override
    public String getSource() {
        return account;
    }

    @Override
    public String getDestination() {
        return branch;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Withdraw withdraw = (Withdraw) o;

        if (Float.compare(withdraw.getQuantity(), getQuantity()) != 0) return false;
        if (getDate() != null ? !getDate().equals(withdraw.getDate()) : withdraw.getDate() != null)
            return false;
        if (account != null ? !account.equals(withdraw.account) : withdraw.account != null)
            return false;
        return branch != null ? branch.equals(withdraw.branch) : withdraw.branch == null;

    }

    @Override
    public int hashCode() {
        int result = getDate() != null ? getDate().hashCode() : 0;
        result = 31 * result + (getQuantity() != +0.0f ? Float.floatToIntBits(getQuantity()) : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (branch != null ? branch.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Withdraw{" +
                "date=" + date +
                ", quantity=" + quantity +
                ", account='" + account + '\'' +
                ", branch='" + branch + '\'' +
                '}';
    }
}
