package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * This class encapsulates the NRC chat components. It writes out commands to the server in response to user input
 *
 * @author Gabriel Kishi
 * @author Nathan
 */
public class ChatPanel extends JPanel {
    public JTextArea textArea; // chat log displayed here
    public JTextField textField; // field where user enters text

    public ChatPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Container
        JPanel chatContainer = new JPanel();
        chatContainer.setLayout(new BorderLayout());
        chatContainer.setOpaque(false);
        chatContainer.setBackground(Color.BLUE);

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setOpaque(false);
        chatPanel.setBackground(Color.RED);
        chatPanel.setSize(new Dimension(getWidth() / 2, getHeight() / 2));
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Text Area
        textArea = new JTextArea(/*"excalo: GLHF NOOBS\nDankJr: Lel\n"*/);
        textArea.setColumns(25);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setFont(GUI.FONT_TEXT.deriveFont(14f));
        textArea.setEditable(false);
        textArea.setFocusable(false);
        chatPanel.add(textArea);

        // Text Field
        textField = new JTextField();
        textField.setColumns(25);
        textField.setOpaque(false);
        textField.setFont(GUI.FONT_TEXT.deriveFont(14f));
        textField.setBorder(null);
        textField.setVisible(false);
        textField.setFocusTraversalKeysEnabled(false);
        chatPanel.add(textField);

        chatContainer.add(chatPanel, BorderLayout.WEST);
        add(chatContainer, BorderLayout.SOUTH);
    }
}