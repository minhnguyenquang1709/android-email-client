package vn.edu.usth.email.Helper;

import android.content.Context;
import android.content.Intent;

public class NavigationHelper {

    public static void navigateToActivity(Context context, Class<?> targetActivity, String userId, String accessToken) {
        Intent intent = new Intent(context, targetActivity);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        context.startActivity(intent);
    }
}
