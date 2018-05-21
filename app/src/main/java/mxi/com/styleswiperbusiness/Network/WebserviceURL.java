package mxi.com.styleswiperbusiness.Network;

/**
 * Created by parth on 8/7/16.
 */
public class WebserviceURL {

    static String common_url = "https://wazoomobile.com/styleswiper/ws/business/";

    public static String url_login = common_url + "login";
    public static String url_registration = common_url + "register";
    public static String url_logout = common_url + "logout";
    public static String url_forgotpassword = common_url + "forgotpassword";
    public static String url_changepassword = common_url + "changepassword";

    public static String url_getStylesRepository = common_url + "getStylesRepository";
    public static String url_addStyles = common_url + "AddStyles";
    public static String url_createStyles = common_url + "createNewStyle";
    public static String url_getStyleTags = common_url + "getStyleTags";
    public static String url_getMyStyles = common_url + "getMyStyles";


    static String webView_common_url = "http://styleswiper.com/webview/";

    public static String url_wv_dashbord ="http://styleswiper.com/webview?token=";
    public static String url_wv_edit_profile=webView_common_url+"editprofile?token=";
    public static String url_wv_my_location=webView_common_url+"mylocation?token=";
    public static String url_wv_style_history=webView_common_url+"stylehistory?token=";
    public static String url_wv_billing =webView_common_url+"billing?token=";

//    DashBoard - http://styleswiper.com/webview?token=
//    Edit Profile - http://styleswiper.com/editprofile?token=
//    My Location - http://styleswiper.com/mylocation?token=
//    Style History - http://styleswiper.com/stylehistory?token=
//    Billing - http://styleswiper.com/billing?token=
}
