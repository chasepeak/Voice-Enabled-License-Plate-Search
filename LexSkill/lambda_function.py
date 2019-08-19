from dictionaries import phonetic_alphabet, states
import traceback
import random
import json

#param: event - JSON object containing Lex interaction model details
#param: context - The context for which the event occurred
#return: JSON object detailing results
def lambda_handler(event, context):
    try:
        intent_slots = event['currentIntent']['slots']
        if event['currentIntent']['name'] == "GetLicensePlate":
            return decode_official_record(intent_slots, "license plate")
        elif event['currentIntent']['name'] == "GetDriversLicense":
            return decode_official_record(intent_slots, "drivers license")
        else:
            raise ValueError("Invalid intent.")
    except:
        return error_response("AWS Lambda isn't familiar with this intent.")

#param: intent_slots - state and record information for decoding
#param: record_type - specific type of record
def decode_official_record(intent_slots, record_type):
    try:
        phonetic_string = intent_slots['record_info']
        phonetic_words = phonetic_string.split()
        state = intent_slots['state']
        state_abbrev = states.get(state.lower(), None)
        if state_abbrev == None:
            return error_response("AWS Lambda can't recognize the state '" + state + "'.")

        record_info = ""
        for word in phonetic_words:
            digit = phonetic_alphabet.get(word.lower(), None)
            if not digit: #checks for values not appearing in the phonetic dictionary
                number_string = ""
                for number in word:
                    try:
                        eval(number) #this forces an error when an invalid value has been give
                    except:
                        return error_response("AWS Lambda couldn't find '" + word + "' in the phonetic alphabet.")
                    number_string += number
                record_info += number_string
            else:
                record_info += digit

        output_text = state_abbrev + " " + record_type + " " + record_info #necessary record information
        return build_lex_response(True, output_text)
    except:
        return error_response("AWS Lambda couldn't find an expected key/value pair from Lex.")

#param: success - boolean to convey success/failure of program
#param: response - output text
def build_lex_response(success, response):
    return {
        "sessionAttributes": {},
        "dialogAction": {
            "type": "Close",
            "fulfillmentState": "Fulfilled" if success else "Failed",
            "message": {
                "contentType": "PlainText",
                "content": response
            }
        }
    }

def error_response(input):
    traceback.print_exc()
    response = "Invalid input: " + str(input)
    return build_lex_response(False, response)
