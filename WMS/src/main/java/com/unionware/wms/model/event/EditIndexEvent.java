package com.unionware.wms.model.event;

import android.widget.EditText;

import java.io.Serializable;

public class EditIndexEvent implements Serializable {
    private int index;
    private int pos;
    private EditText editText;

    public EditIndexEvent(int index, int pos) {
        this.index = index;
        this.pos = pos;
    }

    public EditIndexEvent(int index, int pos, EditText editText) {
        this.index = index;
        this.pos = pos;
        this.editText = editText;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
