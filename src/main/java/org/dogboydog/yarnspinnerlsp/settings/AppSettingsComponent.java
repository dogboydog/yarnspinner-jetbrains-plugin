package org.dogboydog.yarnspinnerlsp.settings;

// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.


import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBCheckBox debugLoggingCheckbox = new JBCheckBox("Log LSP requests to .log files in the project directory");

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(debugLoggingCheckbox, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public boolean getDebugLogging() {
        return debugLoggingCheckbox.isSelected();
    }

    public void setDebugLogging(boolean newStatus) {
        debugLoggingCheckbox.setSelected(newStatus);
    }

    public JComponent getPreferredFocusedComponent() {
        return debugLoggingCheckbox;
    }


}
