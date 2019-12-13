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

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/gradle-plugin/reference/html/)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-kafka)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

