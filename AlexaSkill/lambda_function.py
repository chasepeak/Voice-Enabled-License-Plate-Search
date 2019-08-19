from dictionaries import phonetic_alphabet, states
import traceback
import random
import json


#event: The event object, describing the type and attributes of the event
#context: The context for which the event occurred
#return: speech and card info to Alexa Skill
def lambda_handler(event, context):
    try:
        if event['request']['type'] == "IntentRequest":
            return handle_intent(event['request'])
        elif event['request']['type'] == "LaunchRequest":
            return welcome_response()
        elif event['request']['type'] == "SessionEndRequest":
            return handle_session_end(event['request'], event['session'])
    except:
        return error_response(True)


#end_request: request identification value
#session: the session associated with the end request
def handle_session_end(end_request, session):
    card_title = "Session Ended"
    speech_output = "License plate search program closing."
    return build_response({}, build_speech_response(card_title, speech_output, True))


def welcome_response():
    card_title = "Welcome"
    speech_output = "<speak>Hello. What should I run?</speak>"
    return build_response({}, build_speech_response(card_title, speech_output, False))


#intent_request: data specific to the invoked intent
#return: call to the intent-specific function
def handle_intent(intent_request):
    try:
        intent_name = intent_request['intent']['name']
        intent_slots = intent_request['intent']['slots']
        if intent_name == "ParseLicensePlate":
            return decode_official_record(intent_slots, "license plate")
        elif intent_name == "ParseId":
            return decode_official_record(intent_slots, "ID")
        elif intent_name == "ParseDriversLicense":
            return decode_official_record(intent_slots, "drivers license")
        else:
            raise ValueError("Invalid intent.")
    except:
        return error_response()


def decode_official_record(intent_slots, record_type):
    try:
        phonetic_string = intent_slots['record_info']['value']
        phonetic_words = phonetic_string.split()

        initial_state = intent_slots['state']['value']
        state_string = initial_state.split()
        state = state_string[0] if len(state_string) != 1 else initial_state
        state_abbrev = states.get(state.lower())

        record_info = ""
        for word in phonetic_words:
            digit = phonetic_alphabet.get(word.lower(), None)
            if not digit: #checks for values not appearing in the phonetic dictionary
                number_string = ""
                for number in word:
                    eval(number) #this forces an error when an invalid value has been give
                    number_string += number
                record_info += number_string
            else:
                record_info += digit

        output_text = state_abbrev + " " + record_type + " " + record_info #necessary record information
        output_speech = '<speak><sub alias="' + state + '\">' + state_abbrev + '</sub> ' + record_type + ' <say-as interpret-as="spell-out">' + record_info + '</say-as></speak>'
        return build_answer(output_speech, output_text)
    except:
        return error_response(False, phonetic_string, initial_state, record_type)


def error_response(should_end_session = False, phonetic_string = None, state = None, record_type = None):
    traceback.print_exc()
    if phonetic_string:
        output_speech = ("<speak>There was in a error in processing " + str(phonetic_string) +
        " as a " + state + " " + record_type + "</speak>")
    else:
        output_speech = "<speak>Sorry, but I was unable to process your intended request</speak>"
    return build_response({}, build_speech_response("Error in processing your request.", 
        output_speech, should_end_session))


def build_answer(speech_output, output_text):
    return build_response({}, build_speech_response("Record information: ",
        speech_output, False, output_text))


def build_speech_response(title, output, should_end_session, output_text = None):
    return {
        "outputSpeech": {"type": "SSML", "ssml": output},
        "card": {"type": "Standard", "title": title, "context": "", "text": output_text},
        "reprompt": {"outputSpeech": {"type": "SSML", "ssml": output}},
        "shouldEndSession": should_end_session
    }


def build_response(session_attributes, speech_reponse):
    return {"version": "1.0", "sessionAttributes": session_attributes,
        "response": speech_reponse
    }
