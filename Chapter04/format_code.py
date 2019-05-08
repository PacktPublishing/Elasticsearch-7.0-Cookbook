import sys

filename=sys.argv[1]
data=open(filename, "r").read().split("\n")
for line in data:
    if line:
        spaces=len(line)-len(line.lstrip())
        print("<kbd>"+"&nbsp;"*spaces+line.lstrip()+"</kbd>")
        print("<br>")