// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui;

import javax.swing.SwingUtilities;

/**
 * Main class to launch the application UI.
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class Main {

    /**
     * Main entry point.
     * 
     * @param args
     *             the command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GoBankingApplicationUI ui = new GoBankingApplicationUI();
            ui.setVisible(true);
        });
    }
}