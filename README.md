# Mining-Data-Streams

Please implement a way of getting the number of ones from binary data
stream of zeros and ones. You should implement it in either Java or
C/C++.

Your program should use the DGIM algorithm, and run two threads: one
thread takes an input stream of random sequence of zeros and ones from
a host:port pair read from stdin first, and then another thread accepts
queries from stdin and answers to stdout. The queries have the format of
“What is the number of ones for last <k> data?”. The answer can be exact
or estimated depending on k (k ≤ N where N is the input size) is inside a
DGIM bucket or not. The output should show the following:
a) input immediately,
b) the queries, and
c) the number of ones of “exact” or “estimated” as the answers of the
queries in the format of “The number of ones of last <k> data is
[exact|estimated] <num>.”
