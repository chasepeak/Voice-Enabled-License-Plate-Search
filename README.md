```                                                                                
Chase Peak                                                                      
Cal Poly Digital Transformation Hub                                             
```                                                                             

# Voice-Enabled-License-Plate-Search                                               
### General Overview                                                             

The goal of the voice-enabled license plate search program is to make the experience of a police officer operating a vehicle or performing a traffic stop safer. With this program, an officer can record audio from their local computer's microphone with the press and hold of a button, and then send that information to AWS Lex for interpretation and AWS Lambda for translation. As a result, the officer receives back their information in the required syntax for use by their dispatch software. This program was implemented in a Java application to be installed on a local computer with internet access. It takes commands of the form "run {state} {license plate/drivers license} {record information}" from the user, and returns the interpreted information formatted to the user's requirements.
 
This project can be broken into two stages, displaying the growth of the concept:

### Alexa Skill

First, the Alexa skills proves the concept that a voice-enabled application to interact with the officer is possible, and can manipulate and format the data in any way required. This process is carried out through a custom Alexa skill and a lambda function, where Alexa interacts with the user through an Echo device, and the lambda function executes the business logic required to interpret the user's input. Together, this implementation can take record information encoded by US Law Enforcement phonetics, state, and record-type details to return a specific format for a query or log.

##### Files:

- lambda\_function:                                                                 
Contains the business logic for parsing the user-inputted JSON object. It expects a JSON object specific to the structure provided by Alexa, and focuses on the state and record\_info slots expected from Alexa (i.e. state, record\_info) as well as the ParseLicensePlate or ParseDriversLicense intents. 
\
In returning speech responses, the SSML output speech type is used in order to return text in the required form, but respond vocally in a different way. For example, when the user inputs information regarding a state, the program returns the abbreviated version in text (ex. California -> CA) but will give the vocal response of "California" when explaining the result. This allows for a total explanation of what the user is receiving in return, all while giving the necessary format to the response text.

- dictionaries:                                                                     
Used by lambda\_function to verify the code words are within the US Law Enforcement phonetic alphabet, or that the command contained a valid input for the state information.

- voice\_enabled\_license\_plate\_search:                                           
The JSON code for setting up the Alexa skill. This contains the intents for gather license plate/drivers license information, their related sample utterances, and the slots used to pick up information on state and record information.

This stage of the project acted as a proof-of-concept to the technological need for a voice-enabled license plate search application. For purposes of providing a program to an entire police department, it would be impractical to require Echo devices to be present wherever the application was needed. So to meet the need of accessibility and convenience, a stand-alone application would be necessary.

### Lex Skill
talk about what goes into this program on the technical side

##### Files:
- lambda\_function
equipped to handle and return lex JSON objects.

- dictionaries
same as the other one

- **Record\_Search**
This directory contains the source code. give directory path to source code and explain each here  
  - Main.java
explain this
  - myGUI.java
explain this
  - WavRecorder.java
explain this

explain Lex utterances and stuff (since you can't extract a JSON)



For this program, a user may request "run California license plate adam boy charles 1234". Lex identifies that string from the submitted audio, and then AWS Lambda translates this string into the required syntax. This translated and formatted text is then returned to the user.
\                                                                                  
Unlike the Alexa implementation, this is a stand-alone program that only requires local storage and an internet connection. By running the application, a GUI appears with context to the workings of the program, and an identification label to show when the program is recording the user's audio. By pressing and holding the *ENTER* button, the user can record a command and have that audio transported to AWS Lex and AWS Lambda for interpretation. Lex in this case can be configured to determine the types of utterances that are accepted by the application, and the associated Lambda function determines how the information translated by Lex is handled in its conversion. 
\ 
-lambda\_function:                                                                 
Contains the business logic for parsing the user inputted JSON object. The JSON object is specifically tailored to be interpreted by Lex for responding to the user's Lex runtime command.

For any further questions, contact Chase Peak at **cpeak@calpoly.edu**.
