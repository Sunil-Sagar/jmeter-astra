package com.jmeterastra.gui;

import org.junit.jupiter.api.Test;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class JmeterAstraToolbarTest {

    @Test
    void toggleTooltip_containsBrandName() {
        // OS variance: the shortcut suffix branches on isMac() (⌘⇧A on macOS,
        // Ctrl+Shift+A elsewhere); only the platform-independent prefix is asserted.
        String tip = JmeterAstraToolbar.toggleTooltip();
        assertTrue(tip.startsWith("JmeterAstra"));
    }

    @Test
    void isMac_matchesOsNameProperty() {
        boolean expected = System.getProperty("os.name", "").toLowerCase().contains("mac");
        assertEquals(expected, JmeterAstraToolbar.isMac());
    }

    @Test
    void installToggleBinding_registersStrokeAndInvokesToggle() throws Exception {
        AtomicBoolean toggled = new AtomicBoolean(false);
        SwingUtilities.invokeAndWait(() -> {
            JRootPane root = new JRootPane();
            JmeterAstraToolbar.installToggleBinding(root, () -> toggled.set(true));

            Object key = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .get(JmeterAstraToolbar.toggleKeyStroke());
            assertNotNull(key, "toggle keystroke must be bound on the root pane");

            Action action = root.getActionMap().get(key);
            assertNotNull(action, "bound key must map to an action");
            action.actionPerformed(new ActionEvent(root, ActionEvent.ACTION_PERFORMED, "toggle"));
        });
        assertTrue(toggled.get(), "invoking the bound action must run the toggle");
    }

    @Test
    void installToggleBinding_nullSafe() {
        assertDoesNotThrow(() -> JmeterAstraToolbar.installToggleBinding(null, () -> { }));
        assertDoesNotThrow(() -> JmeterAstraToolbar.installToggleBinding(new JRootPane(), null));
    }

    @Test
    void toggleKeyStroke_isShiftAWithMenuShortcut() {
        KeyStroke ks = JmeterAstraToolbar.toggleKeyStroke();
        assertEquals(KeyEvent.VK_A, ks.getKeyCode());
        assertTrue((ks.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0);
    }

    @Test
    void isJmeterAstraButton_matchesActionCommand() {
        JButton button = new JButton();
        button.setActionCommand(JmeterAstraToolbar.TOGGLE_ACTION);
        assertTrue(JmeterAstraToolbar.isJmeterAstraButton(button));
    }

    @Test
    void isJmeterAstraButton_matchesNewAndLegacyTooltip() {
        JButton modern = new JButton();
        modern.setToolTipText("JmeterAstra (Ctrl+Shift+A)");
        assertTrue(JmeterAstraToolbar.isJmeterAstraButton(modern));

        JButton legacy = new JButton();
        legacy.setToolTipText("Toggle FeatherWand Panel");
        assertTrue(JmeterAstraToolbar.isJmeterAstraButton(legacy));
    }

    @Test
    void isJmeterAstraButton_rejectsUnrelated() {
        JButton other = new JButton();
        other.setToolTipText("Toggle AI CLI Terminal");
        other.setActionCommand("toggle_claude_code_panel");
        assertFalse(JmeterAstraToolbar.isJmeterAstraButton(other));
        assertFalse(JmeterAstraToolbar.isJmeterAstraButton(null));
    }

    @Test
    void cliTerminalLogoIsAvailableAtUiSizes() {
        for (int size : new int[] {16, 22, 32}) {
            String path = "/com/jmeterastra/jmeterastra-terminal-" + size + "x" + size + ".png";
            assertNotNull(com.jmeterastra.claudecode.ClaudeCodeMenuItem.class.getResource(path));
            javax.swing.ImageIcon icon =
                    com.jmeterastra.claudecode.ClaudeCodeMenuItem.getClaudeCodeIcon(size);
            assertEquals(size, icon.getIconWidth());
            assertEquals(size, icon.getIconHeight());
        }
    }
}
