package com.unionware.wms.utlis.adapter;

import android.widget.EditText;

import unionware.base.model.bean.EntityBean;

public interface OnEditorActionChangeListener {
    void onEditorActionListener(EditText EditText, EntityBean bean, int pos);
}
