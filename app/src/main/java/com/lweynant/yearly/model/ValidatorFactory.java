package com.lweynant.yearly.model;

public class ValidatorFactory {
    public IValidator create() {
        return new Validator();
    }
}
