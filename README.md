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
This application produces a graphical user interface that gives the user instructions, a recording indicator, and a display for their command response. By activating the program with a press-and-hold of the [enter] button, the user's audio is included in an Amazon Lex PostContent call, along with other details specifying the Lex Bot. Lex performs the audio interpretation and fitting to the two intents for license plates and driver's licenses. Lex is set up to trigger AWS Lambda for intent fullfilment, which takes the interpreted utterance of the user and executes the same logic on it performed within the Alexa implementation. Then, the original PostContent request returns the formatted string of state, record and record type information. ...
\
In order to effectively use this program in its current state, it's necessary to have available Java SE 11 or higher on the local machine. Also, a key is required for access to the Lex bot and other Amazon services used in this program.

##### Files:
- lambda\_function
Contains the business logic required to interpret and return JSON objects specific to Amazon Lex. It operates in a nearly identical way to the lambda function used in the Alexa implementation of RecordSearch.

- dictionaries
Used by lambda\_function to verify the code words are within the US Law Enforcement phonetic alphabet, or that the command contained a valid input for the state information.

- **Record\_Search**
This directory contains the source code to the RecordSearch Java application.



For any further questions, contact Chase Peak at **cpeak@calpoly.edu**.
