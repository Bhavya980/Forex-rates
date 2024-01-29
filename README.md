# Paidy Assignment

## A local proxy for Forex rates

Built a local proxy for getting Currency Exchange Rates

## Implementation details

- Since there was a limitation of the third-party provider which supports a maximum of 1000 requests per day, I have used a local storage where all the currency exchange 
  rates will be stored.
- I have used a ListBuffer as a local storage which is not an appropriate way to store data. However, to keep the 
  assignment simple, I have still used it. A better way to store the data would be a database.
- I have used an Akka actor which runs periodically. The actor is triggered every 4 minutes assuming that the 
  external API call can take upto a minute to update all the currencies stored in our system. This will make sure 
  that the rate would not be older than 5 minutes. This means that we will be making total 360 API calls to the 
  one-frame api in a day which is less than 1000 threshold.
- I have stored the secret-key and initialization-vector in `application.conf` file which is again not appropriate. These
  should be stored using a key management system. Also, the initialization-vector should be randomly generated and not
  hard coded.
- The application does not support all the currencies yet. To keep the assignment simple, I have used the existing 
  Currencies model which only has a limited number of currencies.
- The service would be able to support at least 10,000 successful requests per day as the data would be returned via 
  a database call. (assuming that application is using a database instead of ListBuffer)
- The application supports only 1 API token.
- The application returns all the appropriate errors.

## How to run the application

- Pull the docker image with `docker pull paidyinc/one-frame`
- Run the one-frame service locally on port 8080 with `docker run -p 8080:8080 paidyinc/one-frame`
- Run the application with `sbt run`
- Since the one-frame API already uses port 8080, this application uses port 8081.
- Hit the endpoint `http://localhost:8081/rates?from=<from-currency>&to=<to-currency>`
- Authorization: Header required for authentication. `2qUCoOALQSoRWgJcCDH9CY+w8/zbok+cRHLUVmZpem88SKyuxl8ausa95fiLwZ6d` 
  is the only accepted value in the current implementation.
- #### Example cURL request
```
$ curl -H "Authorization: 2qUCoOALQSoRWgJcCDH9CY+w8/zbok+cRHLUVmZpem88SKyuxl8ausa95fiLwZ6d" 'http://localhost:8081/rates?from=USD&to=JPY'
```

