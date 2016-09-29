package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.enums;

/**
 * Created by mhatina on 26/08/16.
 */
public enum MemberStatus {
    DUE_WITH_PAYMENT(0),
    PAYMENT_IMMINENT(1),
    PAYMENT_OK(2),
    INACTIVE(3);

    MemberStatus(int value) {
        this.value = value;
    }

    int value;

    public int getValue() {
        return value;
    }
}
