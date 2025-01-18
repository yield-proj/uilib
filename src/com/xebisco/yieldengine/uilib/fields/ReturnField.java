package com.xebisco.yieldengine.uilib.fields;

import java.io.Serializable;
import java.lang.reflect.Field;

public interface ReturnField {
    EditableField getEditableField(String name, Serializable value, boolean editable, Field field);
}
