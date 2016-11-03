package cz.brno.holan.jiri.hunggarkuenfinancials;


public class Constant {
    public static final int SIGN_IN_CODE = 100;
    public static final int FILE_SELECT_CODE = 101;
    public static final int NEW_ENTITY_CODE = 102;

    public static final int NUMBER_OF_TABS = 3;
    public static final int MEMBER_LIST_INDEX = 1;
    public static final int PAYMENT_LIST_INDEX = 2;
    public static final int PRODUCT_LIST_INDEX = 0;

    public static final String NAME_REGEX = "[A-Z][a-zA-Z ]+";
    public static final String PHONE_REGEX = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
    public static final String SIMPLE_ADDRESS_REGEX = "[a-zA-Z]+[ ]+[0-9]+[a-zA-Z]*[, ]*[a-zA-Z]*[, ]*[a-zA-Z ]*";

    public static final String EDIT_ENTITY = "EDIT_ENTITY";
}
