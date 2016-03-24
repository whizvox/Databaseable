## Databaseable Public Repository

Welcome one and all!

### Test Results

I use the `whizvox.databaseable.demo.StressTesting` class (in the test package) for the following tests. They are designed to test adding speed, writing speed, and reading speed.

**1,000 ROWS**

Tests Ran: 20

Total time taken: 857 ms
  Average: 42.85 ms

Adding: 139 (16.22%)
  Average: 6.95 ms

Writing: 351 (40.96%)
  Average: 17.55 ms

Reading: 367 (42.82%)
  Average: 18.35 ms
  
**10,000 ROWS**

Tests Ran: 20

Total time taken: 2503 ms
  Average: 125.15 ms

Adding: 312 (12.47%)
  Average: 15.60 ms

Writing: 608 (24.29%)
  Average: 30.40 ms

Reading: 1583 (63.24%)
  Average: 79.15 ms
  
**100,000 ROWS**

Tests Ran: 20

Total time taken: 13346 ms
  Average: 667.30 ms

Adding: 1512 (11.33%)
  Average: 75.60 ms

Writing: 3792 (28.41%)
  Average: 189.60 ms

Reading: 8042 (60.26%)
  Average: 402.10 ms
  
**1,000,000 ROWS**

Tests Ran: 20

Total time taken: 172014 ms
  Average: 8600.70 ms

Adding: 18741 (10.90%)
  Average: 937.05 ms

Writing: 58472 (33.99%)
  Average: 2923.60 ms

Reading: 94801 (55.11%)
  Average: 4740.05 ms

(interesting thing to note, everything started off very slow, so the real averages would be faster)
