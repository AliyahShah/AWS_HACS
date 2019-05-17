import json
import boto3
import datetime
import math
import argparse
import scipy
from scipy import optimize
import logging
from decimal import Decimal



logger = logging.getLogger()
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    # recieves the event from MQTT 
    distance=rssi_to_m(event['state']['reported']['rssi'])

    new_data = {
        #TODO: rssi should be int not string
        "rssi": event['state']['reported']['rssi'],
        "time": event['state']['reported']['timestamp'],
        "distance":str(distance)
    }
    
    mac_address = event['state']['reported']['mac']
    node = event['state']['reported']['node']
    
    print("data recieved on %s", node)
        
     # make the connection to dynamodb
    dynamodb = boto3.resource('dynamodb')

    # select the table
    table = dynamodb.Table("new_table_with_distance")

    try:
        print("updating table")
        table.update_item( Key={'macaddress':mac_address}, AttributeUpdates={node:{"Action":"PUT","Value":new_data}}) 
        print(new_data)
    except Exception as e:
        print("dynamo update error")
        print(e)
    
    try:
        address_data = table.get_item(Key={'macaddress':mac_address})["Item"]
        if (len(address_data) >= 4): #if node data for at least 3 nodes
            print("found 3 nodes")
            logger.info('found 3 points')
            if fresh_data(address_data) == True: #if the timestamps for all three nodes are fresh 
                print("data is fresh, performing trialt")
                logger.info('data is fresh, performing trialt')
                location = perform_trilat(address_data['node_1']['rssi'], address_data['node_2']['rssi'], address_data['node_3']['rssi'])
                print(location)
                try:
                    #put location back into the dynamo record
                    x=Decimal(location[0])
                    y=Decimal(location[1])
                    x=x*1 #This is necessary because dynamo typing is a thing
                    y=y*1 # so location stored is now in milimeters instead of meters
                  
                
                    table.update_item( Key={'macaddress':mac_address}, AttributeUpdates={'locationX':{"Action":"PUT","Value":x}})
                    table.update_item( Key={'macaddress':mac_address}, AttributeUpdates={'locationY':{"Action":"PUT","Value":y}}) 
                    
                                        
                except Exception as e:
                    print('ERROR')
                    print(e)

        
    except Exception as e:
        print(e)
        logger.info(e)
            
    return {
        'statusCode': 200,
        'body': "Finished running"
    }
    
    
 
    
def fresh_data (data):
    #TODO - check if all the times from each node are within 5 seconds
    return True
    
#In the algorithm d is the distance in metres,n is thef path-loss exponent 
# and Ptx is the transmitter power in dBm.
#Choosing value for n is not exact science and it depends on the enviroment.
#  Typical values are: 2 for free space, 2.7 to 3.5 for urban areas,
#  3.0 to 5.0 in suburban areas and 1.6 to 1.8 for indoors when there is line of 
# sight to the router.
#Default transmit power Ptx for DD-WRT based routers is 70mW or 18.5dBm.
#  I use this value for calculations. Transmit power varies a lot between manufacturers.

def rssi_to_m(rssi):
    #path loss constant
    pt_x = -45
    print(type(rssi))
 
    rssi=float(rssi)
    n = 1.7
    return(10**((pt_x-rssi)/(10*n)))
    
def perform_trilat(rssi1, rssi2, rssi3):
    #https://www.alanzucconi.com/2017/03/13/positioning-and-trilateration/
    #https://appelsiini.net/2017/trilateration-with-n-points/
    
    d1=rssi_to_m(rssi1)
    d2=rssi_to_m(rssi2)
    d3=rssi_to_m(rssi3)
    locations = [(3,6), (0,0), (0,10)] 
    distances=[d1, d2, d3]
    initial_location = (0,0)
    #path loss constant
    n = 3.5
    
    #distance formula
    def great_circle_distance(x1, y1, x2, y2):
        return(((x2-x1)**2 + (y2-y1)**2)**.5)
    
    # Mean Square Error
    # locations: [ (lat1, long1), ... ]
    # distances: [ distance1, ... ]
    def mse(x, locations, distances):
    	mse = 0.0
    	for location, distance in zip(locations, distances):
    		distance_calculated = great_circle_distance(x[0], x[1], location[0], location[1])
    		mse += math.pow(distance_calculated - distance, 2.0)
    	return mse
    
    # initial_location: (lat, long)
    # locations: [ (lat1, long1), ... ]
    # distances: [ distance1,     ... ] 
    result = scipy.optimize.minimize(
    	mse,                         # The error function
    	initial_location,            # The initial guess
    	args=(locations, distances), # Additional parameters for mse
    	method='L-BFGS-B',           # The optimisation algorithm
    	options={
    		'ftol':1e-5,         # Tolerance
    		'maxiter': 1e+7      # Maximum iterations
    	})
    
    
    

    location = result.x
    
    #example use of rssi to meters
    #print(rssi_to_m(-70, 18.5))
    
    return location