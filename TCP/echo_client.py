import socket

HOST = '64.43.36.172' #the server's ip address
PORT = 80 #the port user by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
	s.connect((HOST,PORT))
	i = 1
	while i < 2:
		s.sendall(b'darko') #read encoding strings in python in the txt
		data = s.recv(1024)
		i += 1

print('Received', repr(data)) #read str vs repr in the txt