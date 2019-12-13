# Getting Started on using the sample ProducerApp with Kafka SSL 

## Pre-requisites 

Assuming you have completed the steps for generation of server and client certfificates
as listed 
[Kafka](https://github.com/SyedAhmed2019/kafka)


## Step 1 

Update the application.yml with your <clientssl> directory location

## Step 2 
cd consumer dir and gradle clean build 
```
cd consumer
gradle clean build 
```

## Step 3
To run consumer (default is tracks 1000 messages)
```
cd build/libs/
java -jar consumer.jar 
```



