#  Chance Abemba 101011372; Danilo Vucetic 100999548; Jacob Martin 101000849
# Source: https://pymotw.com/2/socket/udp.html

import socket, sys, time

host = sys.argv[1]
textport = sys.argv[2]
textnumber = sys.argv[3]

#create a socket
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = int(textport)
number = int(textnumber)
server_address = (host, port)
i = 0
while i < number:
    data = "Message " + str(i)
#if there is no data exit
    if not len(data):
        break
#prepare encode the data and send it 
    s.sendto(data.encode(), server_address)
    buf, address = s.recvfrom(port)
    print(str(buf))
    i+=1
s.close()
#s.shutdown(1)

