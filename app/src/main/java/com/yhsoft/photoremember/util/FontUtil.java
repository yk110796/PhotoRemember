package com.yhsoft.photoremember.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Locale;

import static android.graphics.Typeface.createFromAsset;
import static java.util.Locale.getDefault;

public class FontUtil {

    /**
     * Korean font set - Noto Sans font
     */
    private static Typeface notoSansBold = null;
    private static Typeface notoSansMedium = null;
    private static Typeface notoSansRegular = null;

    /**
     * English font set - Roboto font
     */

    private static Typeface robotoBold = null;
    private static Typeface robotoMedium = null;
    private static Typeface robotoRegular = null;

    private static boolean checkKoreanLocale() {
        String language = getDefault().getLanguage();
        if (language.equals("ko")) {
            return true;
        } else {
            return false;
        }
    }

    public static Typeface getBoldTypeFace(final Context context) {
        if (checkKoreanLocale()) {
            return getNotoSansBoldTypeface(context);
        } else {
            return getRobotoBoldTypeface(context);
        }
    }

    public static Typeface getMediumTypeFace(final Context context) {
        if (checkKoreanLocale()) {
            return getNotoSansMediumTypeface(context);
        } else {
            return getRobotoMediumTypeface(context);
        }
    }

    public static Typeface getRegularTypeFace(final Context context) {
        if (checkKoreanLocale()) {
            return getNotoSansRegularTypeface(context);
        } else {
            return getRobotoRegularTypeface(context);
        }
    }

    private static Typeface getNotoSansBoldTypeface(final Context context) {
        if (notoSansBold == null) {
            notoSansBold = createFromAsset(context.getAssets(), "NotoSansKR-Bold.ttf");
        }
        return notoSansBold;
    }

    private static Typeface getNotoSansMediumTypeface(final Context context) {
        if (notoSansMedium == null) {
            notoSansMedium = createFromAsset(context.getAssets(), "NotoSansKR-Medium.ttf");
        }
        return notoSansMedium;
    }

    private static Typeface getNotoSansRegularTypeface(final Context context) {
        if (notoSansRegular == null) {
            notoSansRegular = createFromAsset(context.getAssets(), "NotoSansKR-Regular.ttf");
        }
        return notoSansRegular;
    }

    private static Typeface getRobotoBoldTypeface(final Context context) {
        if (robotoBold == null) {
            robotoBold = createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
        }
        return robotoBold;
    }

    private static Typeface getRobotoMediumTypeface(final Context context) {
        if (robotoMedium == null) {
            robotoMedium = createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        }

        return robotoMedium;
    }

    private static Typeface getRobotoRegularTypeface(final Context context) {
        if (robotoRegular == null) {
            robotoRegular = createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        }
        return robotoRegular;
    }

}
