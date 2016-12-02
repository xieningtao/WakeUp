package com.sf.banbanle.property;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public interface BeanHolderListener<T> {

    void updateValue(T oldValue,T newValue);
}
