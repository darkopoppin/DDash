import socket

HOST = '' #client's IP or empty string for any IP
PORT = 65123 #the port to listen to 
#(for more info on ports and sockets look up sockets explained in the txt)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s: #(internet address family (IPv4), type of socket(default))
	s.bind((HOST,PORT)) #binds the socket to address
	s.listen()
	conn, addr = s.accept() #waits for incoming coonection and returns the connection and client's socket
	with conn:
		print('Connected by', addr)
		while True:
			data = conn.recv(1024) #returns a byte object of the data received. 1024 - bufsize
			if not data:
				break
			conn.sendall(data) #sends ALL the data to the socket