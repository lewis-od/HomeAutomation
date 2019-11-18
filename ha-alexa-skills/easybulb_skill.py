import os
import json
import time
import logging
import boto3
from botocore.exceptions import ClientError, ParamValidationError

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

sqs_client = boto3.client('sqs')
sqs_queue_url = os.environ.get('SQS_QUEUE_URL')


def send_sqs_message(msg_body):
    try:
        msg = sqs_client.send_message(QueueUrl=sqs_queue_url,
                                      MessageBody=msg_body)
    except (ClientError, ParamValidationError) as e:
        logging.error(e)
        return None

    logging.info("Sent SQS message: " + msg_body)

    return msg


def construct_response_header(event):
    response_header = event['directive']['header']
    response_header['namespace'] = "Alexa"
    response_header['name'] = "Response"
    response_header['messageId'] = response_header["messageId"] + "-R"

    return response_header


def construct_sqs_error_response(response_header):
    response_header['name'] = "ErrorResponse"
    return {
        "event": {
            "header": response_header,
            "endpoint": {
                "endpointId": "demo_id"
            },
            "payload": {
                "type": "SQS Error",
                "message": "An error ocurred adding the message to the SQS queue"
            }
        }
    }


def construct_method_error_response(response_header, request_method):
    response_header['name'] = "ErrorResponse"
    error_response = {
        "event": {
            "header": response_header,
            "endpoint": {
                "endpointId": "demo_id"
            },
            "payload": {
                "type": "Request method error",
                "message": "Unknown request method: {}".format(request_method)
            }
        }
    }
    return error_response


def construct_success_response(context_result, response_header, request_token):
    return {
        "context": context_result,
        "event": {
            "header": response_header,
            "endpoint": {
                "scope": {
                    "type": "BearerToken",
                    "token": request_token
                },
                "endpointId": "demo_id"
            },
            "payload": {}
        }
    }


def handle_power_control(event, context):
    request_method = event['directive']['header']['name']
    request_token = event['directive']['endpoint']['scope']['token']

    response_header = construct_response_header(event)

    if request_method == "TurnOn":
        power_result = "ON"
    elif request_method == "TurnOff":
        power_result = "OFF"
    else:
        return construct_method_error_response(response_header, request_method)

    sqs_message = json.dumps({'service': 'easybulb', 'action': request_method})
    sqs_result = send_sqs_message(sqs_message)

    if sqs_result is None:
        return construct_sqs_error_response(response_header)

    context_result = {
        "properties": [{
            "namespace": "Alexa.PowerController",
            "name": "powerState",
            "value": power_result,
            "timeOfSample": time.strftime("%Y-%m-%dT%H:%M:%S.00Z", time.gmtime()),
            # "timeOfSample": datetime.datetime.now().replace(microsecond=0).isoformat() + ".52Z",
            "uncertaintyInMilliseconds": 500
        }]
    }

    return construct_success_response(context_result, response_header, request_token)


def handle_colour_control(event, context):
    request_method = event['directive']['header']['name']
    request_token = event['directive']['endpoint']['scope']['token']

    response_header = construct_response_header(event)

    if request_method != 'SetColor':
        return construct_method_error_response(response_header, request_method)

    colour = event['directive']['payload']['color']
    hsv = [str(colour[k]) for k in ['hue', 'saturation', 'brightness']]
    colour_str = ", ".join(hsv)

    sqs_message = json.dumps({
        "service": "easybulb",
        "action": request_method,
        "value": colour_str
    })
    sqs_result = send_sqs_message(sqs_message)

    if sqs_result is None:
        return construct_sqs_error_response(response_header)

    context_result = {
        "properties": [{
            "namespace": "Alexa.ColorController",
            "name": "color",
            "value": colour,
            "timeOfSample": time.strftime("%Y-%m-%dT%H:%M:%S.00Z", time.gmtime()),
            "uncertaintyInMilliseconds": 500
        }]
    }

    return construct_success_response(context_result, response_header, request_token)

def handle_brightness_control(event, context):
    request_method = event['directive']['header']['name']
    request_token = event['directive']['endpoint']['scope']['token']

    response_header = construct_response_header(event)

    if request_method != 'SetBrightness':
        return construct_method_error_response(response_header, request_method)

    brightness = event['directive']['payload']['brightness']
    brightness_str = str(brightness)

    sqs_message = json.dumps({
        "service": "easybulb",
        "action": request_method,
        "value": brightness
    })
    sqs_result = send_sqs_message(sqs_message)

    if sqs_result is None:
        return construct_sqs_error_response(response_header)

    context_result = {
        "properties": [{
            "namespace": "Alexa.ColorController",
            "name": "brightness",
            "value": brightness,
            "timeOfSample": time.strftime("%Y-%m-%dT%H:%M:%S.00Z", time.gmtime()),
            "uncertaintyInMilliseconds": 500
        }]
    }

    return construct_success_response(context_result, response_header, request_token)


def handleDiscovery(event, context):
    payload = {
        'endpoints': [
            {
                "endpointId": "demo_id",
                "manufacturerName": "Lewis O'Driscoll",
                "friendlyName": "Easybulb light",
                "description": "Easybulb lightbulb control",
                "displayCategories": ["LIGHT"],
                "capabilities": [
                    {
                        "type": "AlexaInterface",
                        "interface": "Alexa",
                        "version": "3"
                    },
                    {
                        "interface": "Alexa.PowerController",
                        "version": "3",
                        "type": "AlexaInterface",
                        "properties": {
                            "supported": [{"name": "powerState"}],
                            "retrievable": False,
                            "proactivelyReported": False
                        }
                    },
                    {
                        "type": "AlexaInterface",
                        "version": "3",
                        "interface": "Alexa.ColorController",
                        "properties": {
                            "supported": [{"name": "color"}],
                            "proactivelyReported": False,
                            "retrievable": False
                        }
                    },
                    {
                        "type": "AlexaInterface",
                        "interface": "Alexa.BrightnessController",
                        "version": "3",
                        "properties": {
                            "supported": [{"name": "brightness"}],
                            "proactivelyReported": False,
                            "retrievable": False 
                        }
                    },
                ]
            },
        ]
    }
    header = event['directive']['header']
    header['name'] = "Discover.Response"
    return {"event": {"header": header, "payload": payload}}


def lambda_handler(event, context):
    if event['directive']['header']['namespace'] == 'Alexa.Discovery' and event['directive']['header']['name'] == 'Discover':
        logger.debug('Discover request: {}'.format(json.dumps(event)))
        return handleDiscovery(event, context)
    elif event['directive']['header']['namespace'] == 'Alexa.PowerController':
        logger.debug('PowerController request: {}'.format(json.dumps(event)))
        return handle_power_control(event, context)
    elif event['directive']['header']['namespace'] == 'Alexa.ColorController':
        logger.debug('ColorController request: {}'.format(json.dumps(event)))
        return handle_colour_control(event, context)
    elif event['directive']['header']['namespace'] == 'Alexa.BrightnessController':
        logger.debug('BrightnessController request: {}'.format(json.dumps(event)))
        return handle_brightness_control(event, context)

    logger.error('Unknown request received: {}'.format(json.dumps(event)))
