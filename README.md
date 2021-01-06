# netty-proxy

Mini test 

`ab -n 2000 -c 200 "http://localhost:8090/"`

Server Software:        
Server Hostname:        localhost
Server Port:            8090

Document Path:          /
Document Length:        0 bytes

Concurrency Level:      200
Time taken for tests:   55.069 seconds
Complete requests:      2000
Failed requests:        0
Total transferred:      38000 bytes
HTML transferred:       0 bytes
Requests per second:    36.32 [#/sec] (mean)
Time per request:       5506.858 [ms] (mean)
Time per request:       27.534 [ms] (mean, across all concurrent requests)
Transfer rate:          0.67 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    3   1.9      2       8
Processing:  5000 5003   2.8   5002    5013
Waiting:     5000 5003   2.5   5002    5011
Total:       5000 5006   4.1   5005    5015