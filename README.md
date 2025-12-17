BankLedger is an application that allows money transfers between accounts and is capable of processing up to 1,000 transactions per second.

To achieve this throughput, the application was tested under different database transaction strategies:

- Hibernate with isolation level READ_COMMITTED using pessimistic locking
- Hibernate with isolation level READ_COMMITTED using optimistic locking
- Hibernate with isolation level SERIALIZABLE
- Hibernate with JDBC batch processing enabled

- JDBC with isolation level READ_COMMITTED using pessimistic locking
- JDBC with isolation level READ_COMMITTED using optimistic locking
- JDBC with isolation level SERIALIZABLE

Based on load tests executed with JMeter, the configuration that consistently achieved 1,000 transactions per second was Hibernate with isolation level READ_COMMITTED, optimistic locking, and batch processing enabled.

How to run BankLedger

Open a terminal in project root directory and run
>docker compose  up -d
>mvn spring-boot:run

Now you can call this three apis:

POST localhost:8080/accounts 
POST localhost:8080/transactions
GET localhost:8080/transactions/{accountid}

These are examples of the apis call:

POST localhost:8080/accounts
body: {"alias" : "John" , "balance" : "100000000"}

POST localhost:8080/accounts
body: {"alias" : "Charles" , "balance" : "100000000"}

POST localhost:8080/transactions
body: {"fromAccount" : "1", "toAccount" : "2", "amount" : "1500"}

GET localhost:8080/transactions/1