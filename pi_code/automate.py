#!/bin/env python
import subprocess
import json
import sys 
import os
from time import gmtime, strftime
from node_scan import Sniffer



def log(message):
	with open("/tmp/log.txt", 'a') as f:
		now = strftime("%Y-%m-%d %H:%M:%S", gmtime())
		f.write(now + ": " + message + '\n')

if __name__ == "__main__":
	if len(sys.argv) != 2:
		print("Usage: " + sys.argv[0] + " <node_name>")
		log("Incorrect paramters")
	else:
		log("Starting automation script...")
		node_name = sys.argv[1]
		process = subprocess.check_output(['lshw', '-json'], shell=False)
		interface_json = json.loads(process)
		all_devices = interface_json['children']
		adapter_interface_name = None
		onboard_interface_name = None
		for device in all_devices:
			if device['class'] == "network":
				if 'businfo' in device: # If this is a network adapter...
					adapter_interface_name = device['logicalname']
				if 'configuration' in device:
					if 'driver' in device['configuration']:
						if device['configuration']['driver'] ==  'brcmfmac':
							onboard_interface_name = device['logicalname']
		if adapter_interface_name is not None and onboard_interface_name is not None:
			print("On-board wifi adapter detected on " + onboard_interface_name)
			print("Wireless USB adapter detected on " + adapter_interface_name)
			print("Making sure we are using on-board wifi...")
			subprocess.check_output(['ip', 'link', 'set', onboard_interface_name, 'up'])
			print("Setting Adapter to monitor mode...")
			try:
				subprocess.check_output(['airmon-ng', 'start', adapter_interface_name])
				monitor_mode_interface = adapter_interface_name + "mon"
				print("Executing script...")
				# os.system("python scan.py " + monitor_mode_interface + " " + node_name)			
				sniffer = Sniffer(node_name)
				sniffer.start_scan(monitor_mode_interface)
                                print("ended os.system")
			except KeyboardInterrupt:
				print("Turning off monitor mode for adapter...")
				subprocess.check_output(['airmon-ng', 'stop', monitor_mode_interface])
				print("Terminated gracefully")
		elif adapter_interface_name is None:
			print("No USB Adapter Found")
		elif onboard_interface_name is None:
			print("No onboard wifi Found")
