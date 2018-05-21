package mxi.com.styleswiperbusiness.Network;

/**
 * Created by parth on 19/12/16.
 */
public class Constants {

    public static class SplashScreen {
        public static int countdownTime = 3000; // 3 seconds
        public static int countdownIntervalTime = 1500; // 1.5 seconds
    }

    public static class SelectedGender {
        public static String isManSelected = "SELECTED_GENDER";
        public static boolean isGenderChanged = false;
    }

    public static class Login {
        public static String deviceID = "device_id";
    }

    public static class SignUp {
        public static String key_name = "KEY_NAME";
        public static String key_email = "KEY_EMAIL";
        public static String key_phone = "KEY_PHONE";
        public static String key_pass = "KEY_CONFIRM";
        public static String key_confirm_pass = "KEY_CONFIRM_PASS";
    }

    public static class AddStyle {
        public static String popupGender = "GENDER";
        public static String popupColors = "COLORS";
        public static String popupStyle = "STYLE";
        public static String popupLength = "LENGTH";
    }

    public static class StyleDetails {
        public static String StyleID = "StyleID";
        public static String Color = "Color";
        public static String Style = "Style";
        public static String StyleImage = "StyleImage";
        public static String Length = "Length";
        public static String Cost = "Cost";
    }
    public static boolean isChangePass = false;
}
