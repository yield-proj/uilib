package com.xebisco.yieldengine.uilib;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class IconTextField extends JTextField {
    private IconTextComponentHelper mHelper;

    class IconTextComponentHelper {
        private static final int ICON_SPACING = 4;

        private Border mBorder;
        private Icon mIcon;
        private Border mOrigBorder;

        IconTextComponentHelper() {
            mOrigBorder = getBorder();
            mBorder = mOrigBorder;
        }

        Border getBorder() {
            return mBorder;
        }

        void onPaintComponent(Graphics g) {
            if (mIcon != null) {
                Insets iconInsets = mOrigBorder.getBorderInsets(IconTextField.this);
                mIcon.paintIcon(IconTextField.this, g, iconInsets.left, iconInsets.top);
            }
        }

        void onSetBorder(Border border) {
            mOrigBorder = border;

            if (mIcon == null) {
                mBorder = border;
            } else {
                Border margin = BorderFactory.createEmptyBorder(0, mIcon.getIconWidth() + ICON_SPACING, 0, 0);
                mBorder = BorderFactory.createCompoundBorder(border, margin);
            }
        }

        void onSetIcon(Icon icon) {
            mIcon = icon;
            resetBorder();
        }

        private void resetBorder() {
            setBorder(mOrigBorder);
        }

        public Border getmBorder() {
            return mBorder;
        }

        public IconTextComponentHelper setmBorder(Border mBorder) {
            this.mBorder = mBorder;
            return this;
        }

        public Icon getmIcon() {
            return mIcon;
        }

        public IconTextComponentHelper setmIcon(Icon mIcon) {
            this.mIcon = mIcon;
            return this;
        }

        public Border getmOrigBorder() {
            return mOrigBorder;
        }

        public IconTextComponentHelper setmOrigBorder(Border mOrigBorder) {
            this.mOrigBorder = mOrigBorder;
            return this;
        }
    }

    public IconTextComponentHelper getmHelper() {
        return mHelper;
    }

    public IconTextField setmHelper(IconTextComponentHelper mHelper) {
        this.mHelper = mHelper;
        return this;
    }

    public IconTextField() {
        super();
    }

    public IconTextField(int cols) {
        super(cols);
    }

    private IconTextComponentHelper getHelper() {
        if (mHelper == null)
            mHelper = new IconTextComponentHelper();

        return mHelper;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        getHelper().onPaintComponent(graphics);
    }

    public void setIcon(Icon icon) {
        getHelper().onSetIcon(icon);
    }

    @Override
    public void setBorder(Border border) {
        getHelper().onSetBorder(border);
        super.setBorder(getHelper().getBorder());
    }
}