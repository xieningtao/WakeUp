package com.sf.banbanle.property;

import com.google.gson.Gson;
import com.sf.banbanle.SpHelper;
import com.sf.utils.baseutil.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class BeanHolder<T> {

    private T mOldData;
    private T mNewData;

    private List<BeanHolderListener<T>> mListeners = new ArrayList<>();


    public BeanHolder(T defaultValue) {
        mOldData = defaultValue;
        mNewData = defaultValue;
    }

    public void addListener(BeanHolderListener<T> listener) {
        mListeners.add(listener);
    }

    synchronized public void setValue(T newData) {
        if ((mOldData == null && newData != null) || (mOldData != null && !mOldData.equals(newData))) {
            for (BeanHolderListener listener : mListeners) {
                listener.updateValue(mOldData, newData);
            }
            mOldData = mNewData;
            mNewData = newData;
        }
    }

    synchronized public void setValue(T newData, String tag) {
        if ((mOldData == null && newData != null) || (mOldData != null && !mOldData.equals(newData))) {
            for (BeanHolderListener listener : mListeners) {
                listener.updateValue(mOldData, newData);
            }
            mOldData = mNewData;
            mNewData = newData;
            SpHelper.saveStr("new" + tag, mNewData == null ? "" : new Gson().toJson(mNewData));
            SpHelper.saveStr("old" + tag, mOldData == null ? "" : new Gson().toJson(mOldData));
        }
    }

    synchronized public T getValue() {
        return mNewData;
    }

    synchronized public T getValue(String tag, Class<T> myClass) {
        return GsonUtil.parse(SpHelper.getStr("new" + tag, ""), myClass);
    }
}
