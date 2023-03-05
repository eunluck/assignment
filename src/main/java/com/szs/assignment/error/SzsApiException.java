package com.szs.assignment.error;

import com.szs.assignment.util.MessageUtils;

public class SzsApiException extends ServiceRuntimeException {

    public static final String MESSAGE_KEY = "error.szs";

    public static final String MESSAGE_DETAIL = "error.szs.details";

    public SzsApiException(String message) {
        super(MESSAGE_KEY, MESSAGE_DETAIL, new Object[]{message});
    }

    @Override
    public String getMessage() {
        return MessageUtils.getMessage(getDetailKey(), getParams());
    }

    @Override
    public String toString() {
        return MessageUtils.getMessage(getMessageKey());
    }

}
