package com.jmeterastra.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jmeter.gui.plugin.MenuCreator;
import com.jmeterastra.agent.dev.AddElementDevMenuItem;
import com.jmeterastra.agent.dev.DeleteElementDevMenuItem;
import com.jmeterastra.agent.dev.MoveElementDevMenuItem;
import com.jmeterastra.agent.dev.ToggleElementDevMenuItem;
import com.jmeterastra.agent.dev.UpdateElementPropertyDevMenuItem;
import com.jmeterastra.claudecode.ClaudeCodeMenuItem;
import com.jmeterastra.correlation.CorrelationMenuItem;
import com.jmeterastra.pet.PetBootstrap;
import com.jmeterastra.utils.AiConfig;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AiMenuCreator implements MenuCreator {
    private static final Logger log = LoggerFactory.getLogger(AiMenuCreator.class);

    /** Property that shows the AI Dev tool-exercise menu items under Run. Default false. */
    public static final String DEV_MENU_PROPERTY = "jmeter.ai.dev.menu";

    public AiMenuCreator() {
        try {
            PetBootstrap.initialize();
        } catch (Throwable e) {
            log.warn("Failed to initialize the JMeter pet", e);
        }
    }

    /**
     * True when the AI Dev menu items should appear under Run.
     * Default is off so the Run menu stays clean for most users.
     */
    static boolean isDevMenuEnabled() {
        return Boolean.parseBoolean(AiConfig.getProperty(DEV_MENU_PROPERTY, "false"));
    }

    @Override
    public JMenuItem[] getMenuItemsAtLocation(MENU_LOCATION location) {
        if (location == MENU_LOCATION.RUN) {
            try {
                // Create a temporary parent component to pass to AiMenuItem
                JMenu parentMenu = new JMenu("AI");
                List<JMenuItem> items = new ArrayList<>();
                items.add(new AiMenuItem(parentMenu));
                items.add(new CorrelationMenuItem());
                items.add(new ClaudeCodeMenuItem(parentMenu));
                if (isDevMenuEnabled()) {
                    items.add(new AddElementDevMenuItem());
                    items.add(new UpdateElementPropertyDevMenuItem());
                    items.add(new DeleteElementDevMenuItem());
                    items.add(new ToggleElementDevMenuItem());
                    items.add(new MoveElementDevMenuItem());
                }
                return items.toArray(new JMenuItem[0]);
            } catch (Throwable e) {
                log.error("Failed to build AI Run-menu items", e);
                return new JMenuItem[0];
            }

        } else {
            return new JMenuItem[0];
        }
    }

    @Override
    public JMenu[] getTopLevelMenus() {
        return new JMenu[0];
    }

    @Override
    public boolean localeChanged(MenuElement menu) {
        return false;
    }

    @Override
    public void localeChanged() {
    }
}
