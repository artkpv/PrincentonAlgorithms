import sys

line = sys.stdin.readline()
sys.stdout.write(line)

for i in (sys.stdin, sys.stdout, sys.stderr):
	print(i)
