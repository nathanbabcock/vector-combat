package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class encapsulates the NRC chat components. It writes out commands to the server in response to user input
 *
 * @author Gabriel Kishi
 * @author Nathan
 */
public class ChatPanel extends JPanel {
    private static final long serialVersionUID = 7686336736079994065L;

    private JTextArea textArea; // chat log displayed here
    private JTextField textField; // field where user enters text

    private class EnterListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            String message = textField.getText();
            if (!message.equals(""))
                System.out.println("sendin mesgage");
            textField.setText("");
        }
    }

    /**
     * Constructs a new ChatPanel for given username, using the given OutputStream
     */
    public ChatPanel() {
        textArea = new JTextArea();
        textArea.setEditable(false);

		/* Setup the GUI */
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(800, 150));

        // create gui components
        textField = new JTextField();
        JButton enterButton = new JButton("Send");

        textField.setPreferredSize(new Dimension(600, 40));
        enterButton.setPreferredSize(new Dimension(100, 40));

        // add button and field to a lower panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(textField);
        bottomPanel.add(enterButton);

        // add text area and lower panel
        this.add(new JScrollPane(textArea), BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // create a listener for writing messages to server
        ActionListener listener = new EnterListener();

        // attach listener to field & button
        textField.addActionListener(listener);
        enterButton.addActionListener(listener);
    }
}


/*package view;

import javax.swing.*;

public class ChatPanel extends JPanel {
    public JTextArea text;
    public JTextField field;

    public ChatPanel(){
//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        text = new JTextArea();
        text.setText("Hello world");

        field = new JTextField();
        add(text);
        add(field);

        setVisible();
    }
}*/