/*
 * Copyright (c) 2015-2020 Kairos, Inc.
 *
 * Kairos is a registered trademark of Kairos Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.kairos.photoremember.view;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;

/**
 * Created by James on 2/27/15.
 */
public class CommonDialogWindow extends ListPopupWindow {
    public CommonDialogWindow(Context context) {
        super(context);
    }

    public CommonDialogWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonDialogWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CommonDialogWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
