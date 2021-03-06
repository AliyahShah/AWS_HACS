
from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTShadowClient
import time
import json

# A random programmatic shadow client ID.
SHADOW_CLIENT = "node_1"

# The unique hostname that AWS IoT generated for 
# this device. (IOT Core->Manage->Things->|ThingName|->Interact)
HOST_NAME = "a1qhjls5ihpm8a-ats.iot.us-east-1.amazonaws.com"

# The relative path to the correct root CA file for AWS IoT, 
# that you have already saved onto this device.
ROOT_CA = "rootca.pem"

# The relative path to your private key file that 
# AWS IoT generated for this device, that you 
# have already saved onto this device.
PRIVATE_KEY = "b0e3b3f742-private.pem.key"

# The relative path to your certificate file that 
# AWS IoT generated for this device, that you 
# have already saved onto this device.
CERT_FILE = "b0e3b3f742-certificate.pem.crt"

# A programmatic shadow handler name prefix.
SHADOW_HANDLER = "node_1"

# Automatically called whenever the shadow is updated.
def myShadowUpdateCallback(payload, responseStatus, token):
  print()
  print('UPDATE: $aws/things/' + SHADOW_HANDLER +
    '/shadow/update/#')
  print("payload = " + payload)
  print("responseStatus = " + responseStatus)
  print("token = " + token)

# Create, configure, and connect a shadow client.
myShadowClient = AWSIoTMQTTShadowClient(SHADOW_CLIENT)
myShadowClient.configureEndpoint(HOST_NAME, 8883)
myShadowClient.configureCredentials(ROOT_CA, PRIVATE_KEY,
  CERT_FILE)
myShadowClient.configureConnectDisconnectTimeout(10)
myShadowClient.configureMQTTOperationTimeout(5)
myShadowClient.connect()
myDeviceShadow = myShadowClient.createShadowHandlerWithName(
  SHADOW_HANDLER, True)


 """Publish method to AWS IOT topic: $aws/things/node_1/shadow/update/#
    The "state" and "reported" fields are required by IOT
    INPUT: data: a json object
"""
def publish_to_iot(data):
  myDeviceShadow.shadowUpdate(
    '{"state":{"reported":' + json.dumps(data) + '}}',
  myShadowUpdateCallback, 5)
