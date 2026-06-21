package com.casaflow.subscription;

public enum PlanCode {
    STARTER(2),
    PRO(10),
    BUSINESS(50);

    private final int userLimit;

    PlanCode(int userLimit) {
        this.userLimit = userLimit;
    }

    public int getUserLimit() {
        return userLimit;
    }
}
