# HACS498 - AWS/ACES
The goal of this project is create a system that identifies and tracks MAC addresses. 3 Raspberry pis are being used as sensors for wireless trilateration. 

Labmda function had a Rule that gets triggered every time a message gets published to one of the node topics. 

--Disclaimer the following info does not include the data collection stuff, just IoT connection setup--

## Setup Pi and SSH into it
Not sure if there's a different way to do this but here's what I did: 
1. Flash the SD card with the OS, put it into the Pi, and boot it up (I added more detailed instructions to the shared google drive)
2. Connected it to a monitor+mouse+keyboard. 
3. Open settings->preferences->interfaces, and make sure SSH is enabled
4. Run `hostname -I` to get the ip address
5. From laptop, run `ssh pi@<ip address>`. The (default) password is "raspberry" (unless you changed it when the Pi prompted you after booting it up)

### Setup and run script
1. Devices need certs from AWS Things console
2. (SSH into the pi) `cd` into the directory containing the `connect_to_IOT` script 
3. run `python connect_to_IOT.py` to connect to AWS IoT

Currently, the code will publish the same message over and over to the topic specified. The topic is formatted as follows: `$aws/things/node_<node number>/shadow/update/#`

## View published data in AWS Console
1. Navigate to the AWS console. Open the IOT Core service. Click "Test" in the left sidebar menu. 
2. In the "subscribe to a topic" view, paste the topic into the "Subscription topic" field and click Subscribe (topic name is commented in the script. You can also see the different topics for a Thing in the IOT Core console). 
3. After subscribing to the topic, run the script on the pi, and you should see the messages coming in. 

