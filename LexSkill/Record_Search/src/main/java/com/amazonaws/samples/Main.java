package com.amazonaws.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Scanner;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostContentRequest;
import com.amazonaws.services.lexruntime.model.PostContentResult;

/**
 * This is the main class for the AWS Lex official record search program. It creates a GUI and executes the AWS lex-runtime command.
 * The process to return a string from Lex takes 2-3 seconds
 * 
 * @author chasepeak
 * @version 2.3
 *@since 08-19-2019
 */

public class Main {
	
	/**
	 * The user interface to be interacted with.
	 */
	private static myGUI window = new myGUI();
	
	/**
	 * The log file to store Lex details from each run of the program.
	 */
	private static File logFile;
	
	/**
	 * The path to the project's audio recording and log file directory.
	 */
	public static String directoryPath = "./RecordSearchDebug";
	
	/**
	 * The path to the log file.
	 */
	private static String filePath = directoryPath + "/RecordSearchLog.out";
	
	/**
	 * The unique user_id that indicates to Lex when a single user is active for multiple sessions (or calls to Lex).
	 */
	private static String user_id;
	
	/**
	 * The incrementing value for tracking the calls and audio recordings from Lex.
	 */
	public static int counter;
	
	public static File count = new File(directoryPath + "/count.out");

	public static void main(String[] args) {
		if(!Files.exists(Paths.get(directoryPath))) {
			new File(directoryPath).mkdirs();
			logFile = new File(filePath);
			counter = 0;
			try {
				logFile.createNewFile();
				count.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not generate log and count files: ");
				e.printStackTrace();
				System.exit(1);
			}
		}
		else {
			try {
				Scanner scanner = new Scanner(count);
				counter = Integer.parseInt(scanner.next());
				scanner.close();
			} catch (FileNotFoundException e) {
				System.out.println("Error retrieving counter information. Counter reset to 0.");
				e.printStackTrace();
				counter = 0;
			}
		}
		
		user_id = "lrs-" + LocalDateTime.now();
		window.render();
	}

	/**
	 * Sends the audio and related configurations to Lex.
	 * @param fileStream - the user's audio recording.
	 * @return - the string representation of the response returned from Lex, or an error message equipped with transcript and/or information about unfulfilled slots.
	 */
	public static String executeAWS(FileInputStream fileStream) {
		AmazonLexRuntime client = AmazonLexRuntimeClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
		PostContentResult contentResult = client.postContent(setContentRequest(fileStream));
		String inputText = contentResult.getInputTranscript(); //Lex's translated text
		String response = contentResult.getMessage();
		
		try {
			Files.write(Paths.get(filePath), formatLogInfo(contentResult).getBytes(),StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("Log information could not be written to file.");
			e.printStackTrace();
		}
		counter++;
		
		try {
			count.createNewFile();
			Files.write(Paths.get(count.getPath()), Integer.toString(counter).getBytes(), StandardOpenOption.WRITE);
		} catch (IOException e) {
			System.out.println("Count file could not be created.");
			e.printStackTrace();
		}
		
		switch(contentResult.getDialogState()) {
		case "Fulfilled":
			return "\n" + response;
		case "ElicitSlot":
			return formatResponse("information about the " + contentResult.getSlotToElicit()+ " could not be recognized. Please try again.", inputText);
		case "ElicitIntent":
			return formatResponse("Lex couldn't determine the intent of your command (search a license plate/drivers license/id)",inputText);
		default:
			return formatResponse(response, inputText);
		}
	}
	
	/**
	 * Creates the full post request with the specified parameters.
	 * @param fileStream - the user's audio recording.
	 * @return - post content request object from specified parameters.
	 */
	private static PostContentRequest setContentRequest(FileInputStream fileStream) {
		//create unique user-id for each usage of the program  
		String botName = "RecordSearchBot";
		String botAlias = "$LATEST";
		String contextType = "audio/lpcm; sample-rate=8000; sample-size-bits=16; channel-count=1; is-big-endian=false";
		String acceptType = "text/plain; charset=utf-8";
		
		PostContentRequest contentRequest = new PostContentRequest();
		contentRequest.setBotName(botName);
		contentRequest.setBotAlias(botAlias);
		contentRequest.setUserId(user_id);
		contentRequest.setContentType(contextType);
		contentRequest.setAccept(acceptType);
		contentRequest.setInputStream(fileStream);
		return contentRequest;
	}
	
	/**
	 * formats the response message for handling errors.
	 * @param errorMessage - the message specific to either an unfulfilled slot, or unsuccessful interpretation of user audio.
	 * @param lexInterpretation - the result of Lex converting the user's audio into text.
	 * @return - response text to be displayed on the GUI.
	 */
	private static String formatResponse(String errorMessage, String lexInterpretation) {
		return ("<html><br>" + errorMessage + "<br>Lex's interpretation: " + lexInterpretation + "</html>");
	}
	
	private static String formatLogInfo(PostContentResult result) {
		String response = result.getMessage();
		String dialogState = result.getDialogState();
		String intentName = result.getIntentName();
		String slots = result.getSlots();
		String inputText = result.getInputTranscript(); //Lex's translated text
		return String.format("Execution #%d at %s\nIntent: %s\nSlots: %s\nDialog state: %s\nLex's interpretation: %s\nResponse: %s\n\n", counter, LocalDateTime.now(), intentName, slots, dialogState, inputText, response);
	}
}