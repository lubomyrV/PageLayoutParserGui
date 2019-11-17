import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextPanel extends JPanel {
	private JTextField field;
	private JTextArea textArea;
	
	public TextPanel() {
		textArea = new JTextArea();
		field = new JTextField();
		setLayout(new BorderLayout());
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		//add(field, BorderLayout.NORTH);
	}
	
	public void appendText(String text) {
		textArea.append(text);
	}
	
}
