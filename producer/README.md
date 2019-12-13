# Getting Started on using the sample ProducerApp with Kafka SSL 

## Pre-requisites 

Assuming you have completed the steps for generation of server and client certfificates
as listed 
[Kafka](https://github.com/SyedAhmed2019/kafka)


## Step 1 

Update the application.yml with your <clientssl> directory location

## Step 2 
Go to producer directory and build it 
```
cd producer
gradle clean build 
```

## Step 3 
Run producer 
```
cd build/libs/
java -jar producer-0.0.1-SNAPSHOT.jar
```
