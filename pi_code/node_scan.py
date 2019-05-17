from connect_to_IOT import publish_to_iot
from scapy.all import sniff, Dot11
from pprint import pprint
from time import gmtime, strftime
import sys

macs = ["98:ca:33:05:58:24", "3c:28:6d:1e:07:fa"]

class Sniffer:
	def __init__(self, node_name):
		self.node_name = node_name

	def sig_level(self, p):
		return p.dBm_AntSignal
	
	def log(self, message):
		with open("/tmp/log.txt", 'a') as f:
			now = strftime("%Y-%m-%d %H:%M:%S", gmtime())
			f.write(now + ": " + message + '\n')

	def handler(self, p):
		if p.haslayer(Dot11):
			source_address = p.addr2
			if source_address in macs:
				rssi = self.sig_level(p)
				now = strftime("%Y-%m-%d %H:%M:%S", gmtime())
				data = {
					"node":self.node_name,
					"rssi":rssi,
					"mac":source_address,
					"timestamp":now
				}
				self.log("Detected device")
				print(data)
				try:
					publish_to_iot(data)
					self.log("Published succesfully")
				except Exception as e:
					print("Failed to publish data: " + str(e))
					self.log("Failed to publish: " + e.message)
	def start_scan(self, interface):
		print("starting...")
		self.log("Starting scan...")
		while True:
			sniff(iface=interface, prn=self.handler)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: " + sys.argv[0] + " <interface> <node_name>")
    else:
        interface = sys.argv[1]
        node_name = sys.argv[2]
        sniffer = Sniffer(node_name)
        print("Starting scan for " + node_name)
        sniffer.start_scan(interface)

