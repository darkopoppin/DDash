import socket

HOST = '127.0.0.1' #the server's ip address
PORT = 65123 #the port user by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
	s.connect((HOST,PORT))
	s.sendall(b'Hello World') #read encoding strings in python in the txt
	data = s.recv(1024)

print('Received', repr(data)) #read str vs repr in the txt
