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
    """
    :param sqs_queue_url: String URL of existing SQS queue
    :param msg_body: String message body
    :return: Dictionary containing information about the sent message. If
        error, returns None.
    """

    # Send the SQS message
    try:
        msg = sqs_client.send_message(QueueUrl=sqs_queue_url,
                                      MessageBody=msg_body)
    except (ClientError, ParamValidationError) as e:
        logging.error(e)
        return None
        
    return msg

def handle_power_control(event, context):
    request_method = event['directive']['header']['name']
    
    response_header = event['directive']['header']
    response_header['namespace'] = "Alexa"
    response_header['name'] = "Response"
    response_header['messageId'] = response_header["messageId"] + "-R"
    
    request_token = event['directive']['endpoint']['scope']['token']
    
    if request_method == "TurnOn":
        power_result = "ON"
    elif request_method == "TurnOff":
        power_result = "OFF"
    else:
        response_header['name'] = "ErrorResponse"
        error_response = {
            "event": {
                "header": response_header,
                "endpoint":{
                    "endpointId": "demo_id"
                },
                "payload": {
                    "type": "Request method error",
                    "message": "Unknown request method: {}".format(request_method)
                }
            }
        }
        return error_response
    
    sqs_message = json.dumps({ 'service': 'easybulb', 'action': request_method })
    sqs_result = send_sqs_message(sqs_message)
    
    if sqs_result is None:
        response_header['name'] = "ErrorResponse"
        error_response = {
            "event": {
                "header": response_header,
                "endpoint":{
                    "endpointId": "demo_id"
                },
                "payload": {
                    "type": "SQS Error",
                    "message": "An error ocurred adding the message to the SQS queue"
                }
            }
        }
        
        return error_response

    
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
    
    response = {
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
    
    logging.debug("PowerController response is: {}".format(json.dumps(response)))
    
    return response
    

def handle_discovery(event, context):
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
                            "supported": [{ "name": "powerState" }],
                            "retrievable": False,
                            "proactivelyReported": False
                        }
                    }
                ]
            },
        ]
    }
    header = event['directive']['header']
    header['name'] = "Discover.Response"
    return { "event": { "header": header, "payload": payload } }


def lambda_handler(event, context):
    if event['directive']['header']['namespace'] == 'Alexa.Discovery' and event['directive']['header']['name'] == 'Discover':
        logger.debug('Discover request: {}'.format(json.dumps(event)))
        return handle_discovery(event, context)
    elif event['directive']['header']['namespace'] == 'Alexa.PowerController':
        logger.debug('PowerController request: {}'.format(json.dumps(event)))
        return handle_power_control(event, context)
        
    logger.error('Unknown request received: {}'.format(json.dumps(event)))

