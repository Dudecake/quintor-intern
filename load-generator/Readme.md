Load-generator

Application to generate a load on a rest-call using SpringBoot

Usage <code>curl &lt;host&gt;:8080/load[?[threads=3]&[load=100]&[duration=30000]]</code>

Where threads is the amount of threads, defaults to 3  
load is the intended load per thread in percentage, defaults to 100%  
duration is the duration of the load in milliseconds, defaults to a random number between 30000 and 60000  

Or <code>curl &lt;host&gt;:8080/intload[?[threads=3]&[load=100]&[duration=30000]&[count=1]&[sleep=300]]</code>

Where threads is the amount of threads, defaults to 3  
load is the intended load per thread in percentage with disputable accuracy, defaults to 100%  
duration is the duration of the load in milliseconds, defaults to a random number between 30000 and 60000  
count is the amount of loops to be run, defaults to 1  
sleep is the amount of milliseconds to sleep between runs, defaults to 300  