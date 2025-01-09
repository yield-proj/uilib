package com.xebisco.yieldengine.uilib.fields;

import java.io.Serializable;
import java.lang.reflect.Field;

public interface ReturnField {
    EditableField getEditableField(Serializable value, Field field);
}
