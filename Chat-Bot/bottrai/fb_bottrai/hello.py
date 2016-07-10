from random import shuffle
import sys
from wit import Wit
import datetime
from ebaysdk.exception import ConnectionError
from ebaysdk.finding import Connection
import requests
import json
import string
from pprint import pprint

access_token = "B2WWFR5TPTQ4RTLC7PK5KZDPTSWCQDJT"
url = "https://trai.azure-mobile.net/tables/WrongCharged"
application_key = "sKVAklhSawgSCrrQHfOfSDnYqVUiOR89"
runtime_name = "Python"
runtime_version = string.split(sys.version, ' ')[0]

def first_entity_value(entities, entity):
    if entity not in entities:
        return None
    val = entities[entity][0]['value']
    if not val:
        return None
    return val['value'] if isinstance(val, dict) else val

def merge(request):
    context = request['context']
    # print("------------------------------------")
    # print(context)
    # print("------------------------------------")
    entities = request['entities']
    print "Entities ", entities
    item = first_entity_value(entities, 'chargedfor')
    if item:
        context['chargedfor'] = str(item)
    provider = first_entity_value(entities, 'provider')
    if provider:
        context['provider'] = str(provider)
    area = first_entity_value(entities, 'service_area')
    if area:
        context['service_area'] = str(area)
    # print("====================================")
    # print(context)
    # print("====================================")
    return context

def wrongCharged(request):
	post_data = request['context']
	post_headers = {'X-ZUMO-APPLICATION' : application_key}
	response = requests.post(url, data=post_data, headers=post_headers)
	print "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
	print(response)
	print "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"

# def say(session_id, context, msg):
#     print "BEFORE SAY, ", session_id, context

#     payload = {
#             'recipient': {
#                 'id': session_id
#             },
#             'message': {
#                 'text':msg,
#             }
#         }
#     print "DOING JSON"
#     send_to_fb(session_id, payload)
#     # if 'itemslist' in context:
#     #     del context['itemslist']
#     # print "After Say ", session_id, context
#     return context

def send(request, response):
    print("********************************************")
    print(request)
    print("********************************************")
    print(response)
    print("********************************************")
    #payload = get_payload(response['text'], request['session_id'])
    #result = send_to_fb(request['session_id'], payload)
    post_message_url = 'https://graph.facebook.com/v2.6/me/messages?access_token=EAAMg128tOi4BANZACTSsPAg8T3m6LBkjkPzeIuQ83uMfu16ufGVbomWnm6bcBRLaZCZAMtLvauNjlxYNAHHcDe3GZAo98Ss20KpbrY2v4TOjZCbJwpemCl6MbI0oT0rnPZBTNIvlZAI1RWZBsJOSZBQpzvSFO91fno3UJDysFQWUpGAZDZD'
    response_msg = json.dumps({"recipient":{"id":request['session_id']}, "message":{"text":response['text']}})
    status = requests.post(post_message_url, headers={"Content-Type": "application/json"},data=response_msg)
    pprint(status.json())
    print('Sending to user...', response['text'])
    print('&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&')

def send_to_fb(session_id, payload):
    access_token = "EAAMg128tOi4BANZACTSsPAg8T3m6LBkjkPzeIuQ83uMfu16ufGVbomWnm6bcBRLaZCZAMtLvauNjlxYNAHHcDe3GZAo98Ss20KpbrY2v4TOjZCbJwpemCl6MbI0oT0rnPZBTNIvlZAI1RWZBsJOSZBQpzvSFO91fno3UJDysFQWUpGAZDZD"
    base_url = ("https://graph.facebook.com/v2.6/me/messages?access_token={0}").format(access_token)
    result = requests.post(base_url, json=payload).json()
    return result

def get_payload(content, session_id):
    payload = {
            'recipient': {
                'id': session_id
            },
            'message': {
                "attachment": {
                    "type": "template",
                    "payload": {
                        "template_type": "generic",
                        "elements": content
                    }
                }
            }
        }
    return payload

actions = {
	'send':send,
	'merge':merge,
	'wrongCharged':wrongCharged,
}

client = Wit(access_token, actions)
#client.interactive()