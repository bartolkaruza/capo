capo
====

[Dutch Open Hackathon](http://www.dutchopenhackathon.com/en) - Team Capo di tutti API

* Martin van Rhijn
* Sunil Shetty
* Florian Grashäftl
* Maxim Volgin
* Bartol Karuza

philipshue303

00:17:88:17:16:3b


PUT game/create
```
{
    "name": "game01",
    "deviceAddress" : "01-AA-01-AA"
}
```

GET game/game01
Response:
```
{
    "players":["01-AA-01-AA","03-CC-03-CC","02-BB-02-BB"],
    "targetColor":"rgb",
    "status":"started/waiting"
}
```

GET game
```
[{
    "name", "game01"
}]
```

POST/PUT game/game01/join
```
{
    "deviceAddress" : "02-BB-02-BB"
}
```

game/game01/update

Request:
```
{
    "deviceId": "01-AA-01-AA",
    "values": [
        {
            "deviceAddress" : "01-AA-01-AA",
            "rssi" : "-82"
        },
        {
            "deviceAddress" : "03-CC-03-CC",
            "rssi" : "-83"
        }
    ]
}
```
Response:

```
{
    "currentColor" : "rgb",
    "targetCOlor" : "rgb"
}
```
