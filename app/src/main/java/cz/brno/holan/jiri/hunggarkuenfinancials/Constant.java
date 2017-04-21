package cz.brno.holan.jiri.hunggarkuenfinancials;


public class Constant {
    public static final String AUTHOR = "Martin Hatina";
    public static final String BUG_REPORT_URL = "https://github.com/mhatina/HungGarKuenFinances/issues/new";
    public static final String BUG_REPORT_MAIL = "hung.gar.devel@gmail.com";
    public static final String BUG_REPORT_MAIL_SUBJECT = "Bug report for HungGarKuenFinances";
    public static final String BUG_REPORT_MAIL_BODY = "Description of problem:\n\n\n" +
                                                      "Version-Release number:\n%s\n\n" +
                                                      "How reproducible:\nalways/often/sometimes/almost never\n\n" +
                                                      "Steps to Reproduce:\n1.\n2.\n3.\n\n" +
                                                      "Actual results:\n\n\n" +
                                                      "Expected results:\n\n\n" +
                                                      "Additional info:\n";

    public static final String KEY_FIREBASE_CONFIG_FORCE_UPDATE = "force_update";
    public static final String KEY_FIREBASE_CONFIG_CURRENT_VERSION = "update_current_version";
    public static final String KEY_FIREBASE_CONFIG_UPDATE_URL = "update_url";
    public static final String FIREBASE_CONFIG_UPDATE_URL = "https://github.com/mhatina/HungGarKuenFinances/releases/download/v%s/HungGarKuenFinances-%s.apk";

    public static final int SIGN_IN_CODE = 100;
    public static final int FILE_SELECT_CODE = 101;
    public static final int NEW_ENTITY_CODE = 102;
    public static final int EDIT_ENTITY_CODE = 103;

    public static final int NUMBER_OF_ENTITY_TABS = 3;
    public static final int MEMBER_LIST_INDEX = 1;
    public static final int PAYMENT_LIST_INDEX = 0;
    public static final int PRODUCT_LIST_INDEX = 2;

    public static final String NAME_REGEX = "[A-Z][a-zA-Z ]+";
    public static final String PHONE_REGEX = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
    public static final String SIMPLE_ADDRESS_REGEX = "[a-zA-Z]+[ ]+[0-9]+[a-zA-Z]*[, ]*[a-zA-Z]*[, ]*[a-zA-Z ]*";

    public static final String EDIT_ENTITY = "EDIT_ENTITY";
    public static final String PREFILLED_ENTITY = "PREFILLED_ENTITY";
    public static final String SIGNED_IN_AS = "signed_in_as";

    public static final int MEMBER_CONTEXT_GROUP_ID = 0;
    public static final int MEMBER_DETAIL_CONTEXT_GROUP_ID = 1;

    public static final int NUMBER_OF_GROUPS = 4;
    public static final int ADULT_GROUP     = 1;
    public static final int YOUNGSTER_GROUP = 1 << 1;
    public static final int JUNIOR_GROUP    = 1 << 2;
    public static final int CHILD_GROUP     = 1 << 3;
    public static final int BEGINNER_GROUP  = 1 << 4;

    public static final int NAME_SWITCH         = 1;
    public static final int SURNAME_SWITCH      = NAME_SWITCH << 1;
    public static final int BIRTH_DATE_SWITCH   = SURNAME_SWITCH << 1;
    public static final int JOINED_DATE_SWITCH  = BIRTH_DATE_SWITCH << 1;
    public static final int PAID_UNTIL_SWITCH   = JOINED_DATE_SWITCH << 1;
    public static final int NOTE_SWITCH         = PAID_UNTIL_SWITCH << 1;
    public static final int VALID_TIME_SWITCH   = NOTE_SWITCH << 1;
    public static final int VALID_GROUP_SWITCH  = VALID_TIME_SWITCH << 1;
    public static final int PRICE_SWITCH        = VALID_GROUP_SWITCH << 1;
    public static final int GROUP_SWITCH        = PRICE_SWITCH << 1;
    public static final int DETAIL_SWITCH       = GROUP_SWITCH << 1;
    public static final int MEMBER_IDS_SWITCH   = DETAIL_SWITCH << 1;
    public static final int PRODUCT_ID_SWITCH   = MEMBER_IDS_SWITCH << 1;
    public static final int PAID_SWITCH         = PRODUCT_ID_SWITCH << 1;
    public static final int DISCOUNT_SWITCH     = PAID_SWITCH << 1;
    public static final int CREATED_SWITCH      = DISCOUNT_SWITCH << 1;
    public static final int BEGINNER_SWITCH      = CREATED_SWITCH << 1;

    public static final int DAY_SELECTION = 0;
    public static final int WEEK_SELECTION = 1;
    public static final int MONTH_SELECTION = 2;
    public static final int YEAR_SELECTION = 3;
}
