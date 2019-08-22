package com.amazonaws.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * This class captures user audio into a .wav file.
 * 
 * @author chasepeak
 * @version 2.4
 *@since 08-21-2019
 */

public class WavRecorder {

	/**
	 * Object to determine potential audio streaming line.
	 */
	private TargetDataLine line;
	
	/**
	 * A stream to transfer bits from a .wav file.
	 */
	private FileInputStream fileStream;
	
	/**
	 * The input line for audio from the user.
	 */
	private AudioInputStream inputStream;
	
	/**
	 * Storage for user audio. The file-path can be changed within this declaration, but initially it will show up in the home directory.
	 */
	private File wavFile;
	
	/**
	 * This method records the users input through a data line into a .wav file.
	 */
	public void recordAudio() {
		try {
			wavFile = new File(Main.directoryPath + "/audioRecording" + Integer.toString(Main.counter) + ".wav");
			AudioFormat format = setAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
			Thread captureThread = new Thread(new Runnable() {
				@Override
				public void run() {
					inputStream = new AudioInputStream(line);
					try {
						AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, wavFile);
					} catch (IOException e) {
						System.out.println("Audio System could not write audio input stream to wav file");
						e.printStackTrace();
					}	
				}
			});
			captureThread.start();
		}
		catch(LineUnavailableException e) {
			System.out.println("Line from Audio System unavailable\n" + e);
			System.exit(1);
		}
		catch (Exception e) {
			System.out.println("unhandled exception determined:\n" + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is invoked to end the data line capture and prepare the audio recording for export.
	 */
	public void finishRecording() {
		line.stop();
		line.close();
		try {
			fileStream = new FileInputStream(wavFile);
		} catch (FileNotFoundException e1) {
			System.out.println("File stream could not be created from .wav file.");
			System.exit(1);
		}
	}
	
	/**
	 * Getter method for the recorder's file input stream.
	 * @return this class' file input stream.
	 */
	public FileInputStream getFileStream() {
		return fileStream;
	}
	 
	/**
	 * Setter method for the format of the data line.
	 * @return initialized AudioFormat instance.
	 */
	private AudioFormat setAudioFormat() {
		int sampleRate = 8000;
		int bitSampleSize = 16;
		int channels = 1;
		boolean signed = true;
		boolean smallEndian = true;
		return new AudioFormat(sampleRate, bitSampleSize, channels, signed, smallEndian);
	}
}
