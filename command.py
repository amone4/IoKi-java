import sys

def lock():
	###########################
	# Lock
	###########################
	fw = open('state.txt', 'w');
	fw.write('1')
	fw.close()
	########################
	return True

def unlock():
	###########################
	# Unlock
	###########################
	fw = open('state.txt', 'w');
	fw.write('0')
	fw.close()
	###########################
	return True

def getState():
	###########################
	# Get state
	###########################
	fo = open('state.txt', 'r')
	state = (fo.read()[0] == '1')
	fo.close()
	###########################
	return state

def main():
	if (len(sys.argv) == 2):
		state = getState()
		if (sys.argv[1] == '0'):
			if (state and unlock()):
				print("PASS")
			else:
				print("FAIL")
		elif (sys.argv[1] == '1'):
			if (not(state) and lock()):
				print("PASS")
			else:
				print("FAIL")
		elif (sys.argv[1] == '2'):
			if (state):
				print("L")
			else:
				print("U")
		else:
			print("FAIL")

main();