# Kafka SSL - Kafka Server Side Configuration 

## Create CA cert

```
mkdir cacerts && cd cacerts
openssl req -new -newkey rsa:4096 -days 365 -x509 -subj "/CN=CA" -keyout ca_key -out ca_cert -nodes

keytool -printcert -v -file ca_cert
``` 

## Create Kafka Server Certificate

A. Create server certificate (prompts for keystore password) 
```
keytool -genkey -keystore kafka.server.keystore.jks -validity 365 -dname "CN=localhost.com" -storetype pkcs12 -keyalg RSA
```

B. Create server certificate with keystore password 

```
keytool -genkey -keystore kafka.server.keystore.jks -validity 365 -storepass <password> -keypass <password>  -dname "CN=localhost.com" -storetype pkcs12 -keyalg RSA
e.g. keytool -genkey -keystore kafka.server.keystore.jks -validity 365 -storepass changeit -keypass changeit  -dname "CN=localhost.com" -storetype pkcs12 -keyalg RSA
```

C.  List the key in keystore 

```
 keytool -list -v -keystore kafka.server.keystore.jks
```

## Create a certificate signing request file, this will be signed using CA

```
keytool -keystore kafka.server.keystore.jks -certreq -file csr_file -storepass <password> -keypass <password>
e.g. keytool -keystore kafka.server.keystore.jks -certreq -file csr_file -storepass changeit -keypass changeit
```

## Using the CSR file generated above sign the server cert

```
openssl x509 -req -CA ca_cert -CAkey ca_key -in csr_file -out server_cert_signed -days 365 -CAcreateserial -passin pass:<password>
e.g. openssl x509 -req -CA ca_cert -CAkey ca_key -in csr_file -out server_cert_signed -days 365 -CAcreateserial -passin pass:changeit
keytool -printcert -v -file server_cert_signed
keytool -list -v -keystore kafka.server.keystore.jks
```

# Create a truststore to trust the CA and import the ca_cert to truststore
```
keytool -keystore kafka.server.truststore.jks -alias CARoot -import -file ca_cert -storepass <password> -keypass <password> -noprompt
e.g. keytool -keystore kafka.server.truststore.jks -alias CARoot -import -file ca_cert -storepass changeit -keypass changeit -noprompt

```
# Import CA and signed server certificate into the keystore
```
keytool -keystore kafka.server.keystore.jks -alias CARoot -import -file ca_cert -storepass <password> -keypass <password> -noprompt
e.g.  keytool -keystore kafka.server.keystore.jks -alias CARoot -import -file ca_cert -storepass changeit -keypass changeit -noprompt

keytool -keystore kafka.server.keystore.jks -import -file server_cert_signed -storepass <password> -keypass <password> -noprompt
```

# Copy the server_kafka1.1.1.properties as your Kafka config/server.properties 
```
Diff server_kafka1.1.1.properties with original config/server.properties to see that listeners, advertised_listeners 
got updated 
Note: server_kafka1.1.1.properties is tried on localhost -- you need to update the domain of your Kafka hosted server 
in the properties
Restart Kafka and grep for EndPoint in server.log it should show 

[2019-12-11 11:13:18,073] INFO Registered broker 0 at path /brokers/ids/0 with addresses: ArrayBuffer(EndPoint(localhost,9092,ListenerName(PLAINTEXT),PLAINTEXT), EndPoint(localhost,9093,ListenerName(SSL),SSL)) (kafka.zk.KafkaZkClient)

```


# Verify SSL config from any other host than where Kafka server is running
```
openssl s_client -connect <<DNS of Kafka server host>>:9093
e.g. 
openssl s_client -connect localhost:9093

```

# Kafka SSL - Kafka Client Side Configuration for Mutual Authentication 
## Make a Client SSL directory to create truststore 
```
mkdir clientssl &&  cd clientssl
```

## Copy the Kafka Server CA Cert and import into created client truststore 
```
e.g.
scp root@<ip>:/root/kafka/kafka_2.12-1.1.1/ca_certs/ca_cert . 

keytool -keystore kafka.client.truststore.jks -alias CARoot -import -file ca_cert  -storepass <password> -keypass <password> -noprompt
e.g. 
keytool -keystore kafka.client.truststore.jks -alias CARoot -import -file ca_cert  -storepass changeit -keypass changeit -noprompt

keytool -list -v -keystore kafka.client.truststore.jks

```
## Create a client.properties file containing the following properties
```
security.protocol=SSL
ssl.truststore.location=<directory>/kafka.client.truststore.jks
ssl.truststore.password=changeit

```

## Use sample kafka producer passing the client.properties 

```
bin/kafka-console-producer.sh --broker-list <<Kafka-Server-IPorDNS>>:9093 --topic test --producer.config <dir>/client.properties

e.g. 


Send some messages 

```

## Use sample Kafka consumer passing the client.properties 

```
bin/kafka-console-consumer.sh --bootstrap-server <<Kafka-Server-IPorDNS>>:9093 --topic test --from-beginning --consumer.config <dir>/client.properties
```

## Generation of client side certificate keystore 

```
keytool -genkey -keystore kafka.client.keystore.jks -validity 365 -storepass <password> -keypass <password> -dname "CN=client" -alias "CDG" -storetype pkcs12 -keyalg RSA
e.g.
 keytool -genkey -keystore kafka.client.keystore.jks -validity 365 -storepass changeit -keypass changeit -dname "CN=client" -alias "CDG" -storetype pkcs12 -keyalg RSA
```

## Generation of client certificate signing request (client CSR) 

```
keytool -keystore kafka.client.keystore.jks -certreq -file client_csr_file -storepass <password> -keypass <password> -alias "CDG"

```

## Generate signed client certificate using CA 
Our CA is on Kafka Server, copy the client CSR (client_csr_file) to Kafka Server and create a signed certificate 
```
scp client_csr_file root@<ip>:/tmp/

Use server ca_certs folder: 
 openssl x509 -req -CA ca_cert -CAkey ca_key -in /tmp/client_csr_file -out client_cert_signed -days 365 -CAcreateserial -passin pass:<serverstorepassword>


```

## Download and import CA root, signed client certificate signed by CA to keystore
```
scp root@<ip>:/tmp/client_csr_file  <dir>/clientssl/

keytool -keystore kafka.client.keystore.jks -alias CARoot -import -file ca_cert -storepass <password> -keypass <password> -noprompt
keytool -keystore kafka.client.keystore.jks -alias CDG -import -file client_cert_signed -storepass changeit  -keypass changeit -noprompt
```
 
## Make sure that Kafka server properties has SSL auth required 
```
ssl.client.auth=required
```
