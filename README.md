## Quotes Service
* Java 8
* Lombok
* Spring Boot 2.4.2
* Spring Websockets
* Java-WebSocket
* Maven
* Redis

### Quotes Service Overview
Quotes Service aggregates data about instruments from Partner's Service via websockets and stores the data in Redis for the subsequent usage.
It provides http endpoints to get all available instruments with price history.
Along with the http endpoints the service provides a websocket where `hot-price` events will be published as long as 
price of an instrument is changed more than 10% in price relative to the price 5 minutes ago.

### API Usage
The service provides two http endpoints and one websocket:

- `GET /api/v1/instruments` to fetch all available instruments along with the latest `close prices`

    Example response:
    ```
        {
            "PW6443U52503": 973.7215,
            "XM7418834125": 547.7451,
            "KF8868007858": 0,
            "PY0746412224": 1353.27,
            "MW63338T2530": 1292.8806,
            "WX5706242812": 284.8261,
            "KK23023DK121": 1390.8261,
            "BK30J7405250": 841.6667,
            "TY4525767181": 291.7097
        }
    ```
- `GET /api/v1/instruments/PW6443U52503` to fetch instruments history for the last 30 minutes
  
    Example response:
    ```
        [
            {
                "openTimeStamp": "2021-02-14T11:55:00",
                "closeTimeStamp": "2021-02-14T11:56:00",
                "openPrice": 952.538,
                "closePrice": 973.7215,
                "lowPrice": 948.9937,
                "highPrice": 973.7215
            },
            {
                "openTimeStamp": "2021-02-14T11:54:00",
                "closeTimeStamp": "2021-02-14T11:55:00",
                "openPrice": 952.538,
                "closePrice": 973.7215,
                "lowPrice": 948.9937,
                "highPrice": 973.7215
            },
            {
                "openTimeStamp": "2021-02-14T11:53:00",
                "closeTimeStamp": "2021-02-14T11:54:00",
                "openPrice": 952.538,
                "closePrice": 973.7215,
                "lowPrice": 948.9937,
                "highPrice": 973.7215
            },
            {
                "openTimeStamp": "2021-02-14T11:52:00",
                "closeTimeStamp": "2021-02-14T11:53:00",
                "openPrice": 952.538,
                "closePrice": 973.7215,
                "lowPrice": 948.9937,
                "highPrice": 973.7215
            }
        ]
    ```
  
- Websocket([SockJS](https://github.com/sockjs)) is available on port `8081`. The service provides one topic `/topic/hot-price`.

    Message content example:
    ```
    {"isin":"RD567G2041M8","trend":"UP","change":45.5,"isHot":true}  
    ```
    [Example of consuming the socket](src/main/resources/static/index.html)
    To test websocket open a browser and go to `localhost:8081`. Open dev tools and press `Connect` button. Once `hot-price`
    is published a message will appear in console.

### Run tests locally
`mvn clean test`

### Build service and run locally
`docker-compose up --build ` Will build docker image and run it along with redis and Partner's service image. Rest API will be available on `8081`.
Websocket will be available on the same port.

### Answers to the questions & Possible improvements
- Add Open API specification for http endpoints
- Add integration and end-to-end tests. Due to the lack of time I did not manage to add them.
- Consider calculating and inserting of missing candles rather than compute them when requested. It is arguable though
- Introduce caching for http responses as long as data is changed each minute. So we can reduce load by returning cached
  results if data was not changed
- Introduce load balancing based on `isin`. Use multiple instances of the service to handle specific 'range' of `isins`.
- Introduce external message broker(kafka, rabbitmq, etc.) for publishing events and create another service 
  which will be responsible for retranslation the events coming from message broker to websocket.
  This mechanism will allow scaling the service horizontally.
- Split service, e.g. in three independent services: the first is responsible for aggregating data,
  the second is responsible for handling http requests, and the third is responsible for calculating `hot prices` and publishing.
  Such approach will allow scaling of services independently.

### Questions
- How often should the service recalculate hot price: each 5 minutes or each minute?
