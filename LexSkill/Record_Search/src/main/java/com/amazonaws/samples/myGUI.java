package com.amazonaws.samples;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This program supports a graphical user interface to facilitate audio recording.
 * 
 * @author chasepeak
 * @version 2.3
 *@since 08-19-2019
 */

@SuppressWarnings("serial")
public class myGUI extends JFrame {

	/**
	 * Controls the press-and-hold process to limit keyPressed(KeyEvent) to 1 audio recording per process
	 */
	private boolean isPressed;
	
	/**
	 * The window frame initiated by this GUI.
	 */
	private static JFrame frame;
	
	/**
	 * The returned value from Lex presented to the user.
	 */
	private String outputMessage;
	
	/**
	 * The window label displaying the output message.
	 */
	private static JLabel outputLabel = new JLabel();
 
	/**
	 * The audio-recording object used to capture user input.
	 */
	private WavRecorder recorder = new WavRecorder();
	
	/**
	 * The dimensions of the GUI.
	 */
	private final Dimension windowSize = new Dimension(400, 200);
	
	/**
	 * An object to set guidelines within the GUI window.
	 */
	private final GridBagConstraints constraints = new GridBagConstraints();
	
	/**
	 * Generic constructor to set up myGUI.
	 */
	public myGUI() {
		super("Voice-Enabled Record Search");
		setup();
	}
	
	/**
	 * Sets up the window with constraints, text, and a key press-and-hold listener.
	 */
	private void setup() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(windowSize);
		setLayout(new GridBagLayout());
		
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		String recordingMessage = "recording status: ";
		
		JLabel header = new JLabel("Press + hold [enter] to capture audio");
		JLabel context0 = new JLabel("Utterance format:");
		JLabel context1 = new JLabel("\"run {state} {license plate/drivers license} {record information}.\"");
		JLabel recordingStatus = new JLabel(recordingMessage + "off");
		
		add(recordingStatus, constraints);
		add(header, constraints);
		add(context0, constraints);
		add(context1, constraints);
		add(outputLabel, constraints);
	
		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				//to modify which key to press + hold, change KeyEvent KeyCode
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !isPressed) {
					isPressed = !isPressed;
					recordingStatus.setText(recordingMessage + "on");
					recorder.recordAudio();
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					isPressed = !isPressed;
					recorder.finishRecording();
					
					recordingStatus.setText(recordingMessage + "off");
					outputMessage = Main.executeAWS(recorder.getFileStream());
					outputLabel.setText(outputMessage);
				}
			}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
	}
	
	/**
	 * Instantiates a window on the user's screen, and can receive a message to output to the user.
	 * @param message - the string to be written to the GUI.
	 */
	public void render() {
		frame = new myGUI();
		frame.pack();
		frame.setVisible(true);
	}
}