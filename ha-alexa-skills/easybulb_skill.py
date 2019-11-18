import os
import json
import time
import logging
import boto3
from functools import partial
from botocore.exceptions import ClientError, ParamValidationError


class InvalidMethodException(Exception):
    pass


logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

sqs_client = boto3.client('sqs')
sqs_queue_url = os.environ.get('SQS_QUEUE_URL')


def send_sqs_message(msg_body):
    msg = sqs_client.send_message(QueueUrl=sqs_queue_url, MessageBody=msg_body)
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


def event_handler_wrapper(handler, event):
    request_namespace = event['directive']['header']['namespace']
    request_method = event['directive']['header']['name']
    request_token = event['directive']['endpoint']['scope']['token']
    request_payload = event['directive'].get('payload', None)

    response_header = construct_response_header(event)

    try:
        result = handler(request_method, request_payload)
    except (ClientError, ParamValidationError) as e:
        logging.error(e)
        return construct_sqs_error_response(response_header)
    except InvalidMethodException as e:
        logging.error(e)
        return construct_method_error_response(response_header, request_method)

    context_result = {
        "properties": [{
            "namespace": request_namespace,
            "name": result['name'],
            "value": result['value'],
            "timeOfSample": time.strftime("%Y-%m-%dT%H:%M:%S.00Z", time.gmtime()),
            "uncertaintyInMilliseconds": 500
        }]
    }

    return construct_success_response(context_result, response_header, request_token)


def power_handler(method, payload):
    if method == "TurnOn":
        power_result = "ON"
    elif method == "TurnOff":
        power_result = "OFF"
    else:
        raise InvalidMethodException("Invalid PowerControl method: " + method)

    sqs_message = json.dumps({'service': 'easybulb', 'action': method})
    send_sqs_message(sqs_message)

    return {"name": "powerState", "value": power_result}


def colour_handler(method, payload):
    if method != 'SetColor':
        raise InvalidMethodException(
            "Invalid PowerControl exception: " + method)

    colour = payload['color']
    hsv = [str(colour[k]) for k in ['hue', 'saturation', 'brightness']]
    colour_str = ", ".join(hsv)

    sqs_message = json.dumps({
        "service": "easybulb",
        "action": method,
        "value": colour_str
    })
    sqs_result = send_sqs_message(sqs_message)

    return {"name": "color", "value": colour}


def brightness_handler(method, payload):
    if method != 'SetBrightness':
        raise InvalidMethodException(
            "Invalid BrightnessControl method: " + method)

    brightness = payload['brightness']
    brightness_str = str(brightness)

    sqs_message = json.dumps({
        "service": "easybulb",
        "action": method,
        "value": brightness
    })
    sqs_result = send_sqs_message(sqs_message)

    return {"name": "brightness", "value": brightness}


def discovery_handler(event, context):
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
    event_handlers = {
        "Alexa.Discovery": discovery_handler,
        "Alexa.PowerController": partial(event_handler_wrapper, power_handler),
        "Alexa.ColorController": partial(event_handler_wrapper, colour_handler),
        "Alexa.BrightnessController": partial(event_handler_wrapper, brightness_handler)
    }
    supported_namespaces = event_handlers.keys()

    namespace = event['directive']['header']['namespace']
    if namespace in supported_namespaces:
        logger.debug('{} request: {}'.format(namespace, json.dumps(event)))
        return event_handlers[namespace](event)

    logger.error('Unknown request received: {}'.format(json.dumps(event)))
