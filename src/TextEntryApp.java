import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextEntryApp extends JFrame implements NativeKeyListener  {

    private JTextArea textArea;

    private AtomicBoolean continueInput = new AtomicBoolean(true);

    public TextEntryApp() {
        initUI();
        initGlobalKeyboardHook();
    }

    private void initUI() {
        setTitle("Text Entry App");
        setSize( 640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton pasteButton = new JButton("Paste from Clipboard");
        pasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteFromClipboard();
            }
        });

        JButton entryButton = new JButton("Enter Text");
        entryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterText();
            }
        });

        JButton clearButton = new JButton("Clear Text");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pasteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(entryButton);


        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the window
    }

    private void pasteFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(this);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                textArea.append(clipboardText);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
    }

     private void enterText() {
        String textToEnter = textArea.getText();

        // Simulate entering text after a delay
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get the currently focused window
        Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();

        // Use Robot class to simulate key presses
        try {
            Robot robot = new Robot();
            continueInput.set(true);
            char[] charArray = textToEnter.toCharArray();
            for (int i = 0; i < charArray.length && continueInput.get(); i++ ) {
                char c = charArray[i];
                boolean shift = Character.isUpperCase(c) || isSpecialCharacter(c);
                if (shift) {
                    // For uppercase and special characters, simulate pressing and releasing the Shift key
                    robot.keyPress(KeyEvent.VK_SHIFT);
                }

                int keyCode = getKeyCode(c);
                if (keyCode != KeyEvent.VK_UNDEFINED) {
                    try {
                        robot.keyPress(keyCode);
                        robot.keyRelease(keyCode);
                    } catch (java.lang.IllegalArgumentException e) {
                        // System.out.println("Failed on character: " + c);
                        // System.out.println("Code was " + keyCode);
                        throw e;
                    }
                }

                if (shift) {
                    // For uppercase and special characters, simulate releasing the Shift key
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Restore focus to the original window
        focusedWindow.toFront();
    }

    private boolean isSpecialCharacter(char c) {
        // Add more special characters as needed
        return "{}`~!@#$%^&*()_+:\"|<>?".indexOf(c) != -1;
    }

    private int getKeyCode(char c) {
        // Custom mapping for special characters
        switch (c) {
            case '{':
                return KeyEvent.VK_OPEN_BRACKET;
            case '}':
                return KeyEvent.VK_CLOSE_BRACKET;
            case '~': case '`':
                return KeyEvent.VK_BACK_QUOTE;
            case ':':
                return KeyEvent.VK_SEMICOLON;
            case '"':
                return KeyEvent.VK_QUOTE;
            case '|':
                return KeyEvent.VK_BACK_SLASH;
            case '<':
                return KeyEvent.VK_COMMA;
            case '>':
                return KeyEvent.VK_PERIOD;
            case '?':
                return KeyEvent.VK_SLASH;
            case '!':
                return KeyEvent.VK_1;
            case '@':
                return KeyEvent.VK_2;
            case '#':
                return KeyEvent.VK_3;
            case '$':
                return KeyEvent.VK_4;
            case '%':
                return KeyEvent.VK_5;
            case '^':
                return KeyEvent.VK_6;
            case '&':
                return KeyEvent.VK_7;
            case '*':
                return KeyEvent.VK_8;
            case '(':
                return KeyEvent.VK_9;
            case ')':
                return KeyEvent.VK_0;
            case '_':
                return KeyEvent.VK_MINUS;
            case '+':
                return KeyEvent.VK_EQUALS;
            default:
                return KeyEvent.getExtendedKeyCodeForChar(c);
        }
    }

    private void initGlobalKeyboardHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            Logger.getLogger(TextEntryApp.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        // Add the nativeKeyListener
        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        // Unused
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F12) {
            // System.out.println("Stop entering key pressed!");
            continueInput.set(false);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        // Unused
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TextEntryApp app = new TextEntryApp();
            app.setVisible(true);
        });
    }
}