# Chance Abemba 101011372; Danilo Vucetic 100999548; Jacob Martin 101000849
# Source: https://pymotw.com/2/socket/udp.html

import socket, sys, time

textport = sys.argv[1]
#creating a socket
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = int(textport)
server_address = ('localhost', port)
s.bind(server_address)

while True:

    print ("Waiting to receive on port %d : press Ctrl-C or Ctrl-Break to stop " % port)

    buf, address = s.recvfrom(port)
#if no data is received exit
    if not len(buf):
        break
    print ("Received %s bytes from %s %s: " % (len(buf), address, buf ))
    sendData = "ACK: " + str(buf)
#send back an acknowledgment and loop back
    s.sendto(sendData.encode('utf-8'), address)

s.shutdown(1)
