import select
import sys
import time

while True:
    # stdin must be submissive and readable
    if sys.stdin in select.select([sys.stdin], [], [], 0)[0]:
        line = sys.stdin.readline()
    else:
        time.sleep(1)
        continue
    print(f'echo: {line.strip()}', flush=True)
