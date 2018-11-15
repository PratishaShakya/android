package susankyatech.com.consultancymanagement.Generic;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utilities {
    public static boolean isConnectionAvailable(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager)
                activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) return true;
        else return false;
    }
}
