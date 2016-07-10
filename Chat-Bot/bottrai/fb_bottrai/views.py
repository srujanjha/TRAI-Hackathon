from django.views import generic
from django.http.response import HttpResponse
from django.shortcuts import render
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
import json
from pprint import pprint
import requests
import thread
# from wit import Wit
from hello import *

usercontext = {}

timestamp1 = {}

class BotTraiView(generic.View):
	def get(self, request, *args, **kwargs):
		if self.request.GET['hub.verify_token'] == 'secret':
			return HttpResponse(self.request.GET['hub.challenge'])
		else:
			return HttpResponse('Error, invalid token')

	@method_decorator(csrf_exempt)
	def dispatch(self, request, *args, **kwargs):
		return generic.View.dispatch(self, request, *args, **kwargs)

	# Post function to handle Facebook messages
	def post(self, request, *args, **kwargs):
		#global usercontext
		print("909090900909909090909000000000009090909099999090909")
		incoming_message = json.loads(self.request.body.decode('utf-8'))
		print("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
		print(request)
		print("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
		print(incoming_message)
		print("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
		# try:
		# 	event = incoming_message['entry'][0]['messaging']
		# 	for x in event:
		# 		print "TIME STAMP ", x['timestamp']
		# 	#---------------------------------------------------------------------------
		# 	for x in event:
		# 		if (x.get('message') and x['message'].get('text')):
		# 			message = x['message']['text']
		# 			recipient_id = x['sender']['id']
		# 			timenow = message
		# 			time1 = ""
		# 			if timestamp1.get(recipient_id):
		# 				time1 = timestamp1[recipient_id]
		# 			print "Current time ", timenow, " OLD TIME " ,time1
		# 			if timenow == time1:
		# 				print "SAME THING "
		# 			timestamp1[recipient_id] = timenow

		# 			print "BEFORE MSG SENT ", recipient_id, usercontext, message, " TIMe STAMP ", timestamp1
		# 			if message == "reset" or message == "Reset":
		# 				usercontext.pop(recipient_id, None)
		# 				print "After reset ", usercontext
		# 			else:
		# 				print "STARTING NEW THREAD"
		# 				print("####################################")
		# 				print(message)
		# 				print("####################################")
		# 				thread.start_new_thread(post_wit_msg, (recipient_id, message))
		# 			print "After MSG SENT, ", recipient_id ,usercontext
		# 		# elif x.get('postback') and x['postback'].get('payload'):
		# 		# 	print "PAYLOAD IS ", x['postback']['payload']
		# 		# 	print x
		# 		# 	y = eval(x['postback']['payload'])
		# 		# 	send_with_filters(y["mySearch"], y["filter"], x['sender']['id'])
		# 		else:
		# 			print x
		# 			print "Nothing"
		# 			pass
		# 	return "Success"
		# except Exception, e:
		# 	print e
		# 	return "Failed"
		# 	if 'message' in message:
		# 		pprint(message)
		# 		post_wit_msg(message['sender']['id'], message['message']['text'])
		# return HttpResponse()
		for entry in incoming_message['entry']:
			for message in entry['messaging']:
				if 'message' in message:
					pprint(message)
					post_wit_msg(message['sender']['id'], message['message']['text'])
					# post_facebook_message(message['sender']['id'], message['message']['text'])
		return HttpResponse()

# def post_facebook_message(fbid, response_msg):
# 	post_message_url = 'https://graph.facebook.com/v2.6/me/messages?access_token=EAAYkRy7WYPYBAOAqlE1OHZCEymNIYlKcXcR10tqbWzHElUXY64uc8v9KmpZCBN3ft8R5sZAUNgDjs0VkYwM3oNpGseirXvtE0tgdmWYjvZAs1QcuCRdsQY5J6OX9XgiMTvaIszdBbgZACrZB703PQRzPOaisQIBRp3d6OfZCstqdwZDZD'
# 	response_msg = json.dumps({"recipient":{"id":fbid}, "message":{"text":response_msg}})
# 	status = requests.post(post_message_url, headers={"Content-Type": "application/json"},data=response_msg)
# 	pprint(status.json())

def post_wit_msg(fbid, recevied_message):
	global usercontext
	context = usercontext.get(fbid, {})
	context = client.run_actions(fbid, recevied_message, context = context)
	usercontext[fbid] = context
	print "Returning from SEND TEXT ", fbid, context, " USER CONTEXT ", usercontext
	# client = Wit(access_token="B2WWFR5TPTQ4RTLC7PK5KZDPTSWCQDJT", actions=None)
	# mg = client.converse(str(fbid), recevied_message, {})
	# if 'msg' in mg:
	# 	post_facebook_message(fbid, mg['msg'])
	# else:
	# 	post_facebook_message(fbid, 'I do not know!!')