// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui.view;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * 
 * @author AbidKhan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
abstract class AbstractView extends JPanel {

    private static final long serialVersionUID = -7589858606373973971L;

    AbstractView() {
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }
}