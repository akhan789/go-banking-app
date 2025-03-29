// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.swingui;

import com.payter.swingui.ui.GoBankingApplicationUI;

/**
 * Main class to launch the application ui.
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
     *             the args
     */
    public static void main(String[] args) {
        GoBankingApplicationUI ui = new GoBankingApplicationUI();
        ui.setVisible(true);
    }
}