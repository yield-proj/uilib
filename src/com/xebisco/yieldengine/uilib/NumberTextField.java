package com.xebisco.yieldengine.uilib;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberTextField<T extends Number> extends JFormattedTextField {
    private final Class<? extends Number> numberClass;

    public NumberTextField(Class<T> numberClass, boolean allowNegatives) {
        super(getNumberFormatter(numberClass, allowNegatives));
        this.numberClass = numberClass;
        setText("0");

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals("0")) {
                    setText("");
                } else {
                    int caret = getCaretPosition();
                    SwingUtilities.invokeLater(() -> {
                        setCaretPosition(caret);
                    });
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText("0");
                }
            }
        });
    }

    @Override
    public NumberFormatter getFormatter() {
        return (NumberFormatter) super.getFormatter();
    }

    private static NumberFormatter getNumberFormatter(Class<? extends Number> numberClass, boolean allowNegatives) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance()) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.isEmpty()) {
                    return null;
                }
                return super.stringToValue(text);
            }
        };
        formatter.setValueClass(numberClass);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        if (!allowNegatives) {
            formatter.setMinimum(0);
        }

        return formatter;
    }

    @Override
    public T getValue() {
        //noinspection unchecked
        return (T) super.getValue();
    }

    public T getNumberValue() {
        if (getValue() == null && numberClass != null) {
            try {
                //noinspection unchecked
                return (T) numberClass.getDeclaredMethod("valueOf", String.class).invoke(null, "0");
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return getValue();
    }

    public Class<? extends Number> getNumberClass() {
        return numberClass;
    }
}