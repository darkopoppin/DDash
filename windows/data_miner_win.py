#This runs on python 3.6.x
from __future__ import print_function
import sys

import psutil

########################################################################################
#####          ###          ###   ######   #############################################
#####   ##########   #####  ###   ######   #############################################
#####   ##########   #####  ###   ######   #############################################
#####   ##########          ###   ######   #############################################
#####   ##########   ##########   ######   #############################################
#####          ###   ##########            #############################################
######################################################################################## 
"""
This returns the CPU times as a tuple
user = time spent by normal rpocesses executing in user mode
system = time spent by processess executing in kernel mode
idle = time spent doing nothing
interrupt = time sent for servicing hardware interrupts
dpc = time spent servicing deferred procedure calls (interrupts that run at a low priority standard)
"""
s = psutil.cpu_times()
print("CPU Times")
print(s)
print("\n")



"""This returns a float representing  the current system-wide CPU utilisation as a percentage"""
s = psutil.cpu_percent(interval = 0.1,percpu=True)
print("CPU usage percentage")
print(s)
print("\n")



"""Returns the number pf logical CPUs in the system"""
s = psutil.cpu_count()
print("CPU count")
print(s)
print("\n")
########################################################################################
#####             ##          ###             ##########################################
#####   ##   ##   ##   ##########   ##   ##   ##########################################
#####   ##   ##   ##   ##########   ##   ##   ##########################################
#####   ##   ##   ##          ###   ##   ##   ##########################################
#####   ##   ##   ##   ##########   ##   ##   ##########################################
#####   ##   ##   ##   ##########   ##   ##   ##########################################
#####   ##   ##   ##          ###   ##   ##   ##########################################
######################################################################################## 
"""
Returns statistics about the system memory usage as a named tuple
all in bytes btw
total = total physical memory
percent = percentage of memory being used (float)
used = memory being used (bytes)
free = memory not not being used 
available = the memory that can be given instantly to processes without the sutem going into swap.
"""
s = psutil.virtual_memory()
print("Memory")
print(s)
print("\n")



#########################################################################################
#DISK
"""Returns disk partitions as a list of name tuples including device, mountpoint, filetype"""
s = psutil.disk_partitions(all=True)
print("Disk Stuff")
print(s)
print("\n")



"""
Returns the disk usage statistics about the partiton which contains the given path as a named tuple
bytes again btw
total = total disk space
used = amount of storage being used
free = amount of disk space free
percent = the percentage of disk left unused (float)
"""
s = psutil.disk_usage('/')
print("Disk usage")
print(s)
print("\n")
   


   
"""
disk io counter
"""

s = psutil.sensors_battery()
print("BATTERY")
print(s)
