package com.app.yourvideoschannelapps.config;

import com.app.yourvideoschannelapps.utils.Constant;

public class AppConfig {

    //your Server Key obtained from the admin panel
    public static final String SERVER_KEY = "WVVoU01HTkViM1pNTWtacllsZHNkVXh1VW5sWlYxSnNaRWhXTUdJelNqSmhWMUpzWW5rMWRWcFlVWFpZTWtaM1kwZDRjRmt5UmpCaFZ6bDFVMWRTWmxreU9YUk1iWGh4Wkcxc2ExcFhPSFZaTW1ob1ltMDFiR0pCUFQwPQ==";

    //your Rest API Key obtained from the admin panel
    public static final String REST_API_KEY = "cda11QbXIO9Z4ly0a2khTFDPA3x6UgSVtCiYRjBqpsfL7w5neN";

    //Constant.VIDEO_LIST_DEFAULT or Constant.VIDEO_LIST_COMPACT
    public static final int DEFAULT_VIDEO_VIEW_TYPE = Constant.VIDEO_LIST_DEFAULT;

    //Enable it with true value if want to the app will force to display open ads first before start the main menu
    //Longer duration to start the app may occur depending on internet connection or open ad response time itself
    public static final boolean FORCE_TO_SHOW_APP_OPEN_AD_ON_START = false;

    //press back twice to exit from player screen
    public static final boolean PRESS_BACK_TWICE_TO_CLOSE_PLAYER = false;

    //layout customization
    public static final boolean SET_CATEGORY_AS_MAIN_MENU = false;
    public static final boolean FORCE_PLAYER_TO_LANDSCAPE = false;
    public static final boolean ENABLE_VIEW_COUNT = true;
    public static final boolean ENABLE_DATE_DISPLAY = true;
    public static final boolean DISPLAY_DATE_AS_TIME_AGO = true;
    public static final boolean ENABLE_VIDEO_COUNT_ON_CATEGORY = true;

    //highly recommended to keep with false value to prevent the app accessed using VPN for security reason
    public static final boolean ALLOW_APP_ACCESSED_USING_VPN = true;

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = false;

    //display dialog for exit confirmation
    public static final boolean ENABLE_EXIT_DIALOG = true;

    public static final boolean ENABLE_GDPR_UMP_SDK = true;

    //load more for next list videos
    public static final int LOAD_MORE = 10;

    //delay splash screen
    public static final int DELAY_SPLASH = 100;

}