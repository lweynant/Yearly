package com.lweynant.yearly;

import android.support.annotation.StringRes;

public interface IStringResources {
    public String getString(@StringRes int id);

    public String getString(@StringRes int resId, Object... formatArgs);

    String[] getStringArray(int id);
}
