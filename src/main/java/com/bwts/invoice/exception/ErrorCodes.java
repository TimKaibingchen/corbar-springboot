package com.bwts.invoice.exception;

import com.bwts.common.exception.ErrorCode;

public class ErrorCodes {
    public static ErrorCode WRONG_TOKEN_FORMAT = new ErrorCode(4031,"WRONG_TOKEN_FORMAT","token format is wrong");
    public static ErrorCode THIRD_PARTY_CODE_NOT_EXIST = new ErrorCode(4033,"THIRD_PARTY_CODE_NOT_EXIST","third party code not exist");
    public static ErrorCode EXPIRED_TOKEN = new ErrorCode(4034,"EXPIRED_TOKEN","expired token");
    public static ErrorCode THIRD_PARTY_SECRET_KEY_NULL = new ErrorCode(4035,"THIRD_PARTY_SECRET_KEY_NULL","third party secret key is null");
    public static ErrorCode TOKEN_SIGNATURE_NOT_MATCHED = new ErrorCode(4036,"TOKEN_SIGNATURE_NOT_MATCHED","token signature is not matched");
    public static ErrorCode NO_TOKEN = new ErrorCode(4037,"NO_TOKEN","no token in header");
    public static ErrorCode INVALID_TOKEN = new ErrorCode(4038,"INVALID_TOKEN","invalid token");
}
