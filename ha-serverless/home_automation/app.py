import os
import json
import logging
import boto3
from botocore.exceptions import ClientError

def send_sqs_message(sqs_queue_url, msg_body):
    """
    :param sqs_queue_url: String URL of existing SQS queue
    :param msg_body: String message body
    :return: Dictionary containing information about the sent message. If
        error, returns None.
    """

    # Send the SQS message
    sqs_client = boto3.client('sqs')
    try:
        msg = sqs_client.send_message(QueueUrl=sqs_queue_url,
                                      MessageBody=msg_body)
    except ClientError as e:
        logging.error(e)
        return None
    return msg


def lambda_handler(event, context):
    result = send_sqs_message(os.environ.get('SQS_QUEUE_URL'), f'Test message')
    
    if result is None:
        response = { 'statusCode': 500, 'body': json.dumps({ 'status': 'ERROR' }) }
    else:
        response = { 'statusCode': 200, 'body': json.dumps({ 'status': 'SUCCESS' }) }
    
    return response
