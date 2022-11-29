package com.jakitrans.mc.utils;

import com.jakitrans.mc.models.payment.PaymentMethod;

public interface OnItemPaymentSelected {
    void onItemSelected(PaymentMethod method);
}
