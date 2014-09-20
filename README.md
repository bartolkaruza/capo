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


game/start
{
    "name": "game01",
    "deviceAddress": "01-AA-01-AA"
}

game/join
{
    "name": "game01",
    "deviceAddress": "01-AA-01-AA"
}


game/game01/update
Request:
{
    [
        {
            deviceAddress: "01-AA-01-AA",
            rssi: "-82"
        },
        {
            deviceAddress: "01-AA-01-A2",
            "rssi: "-83"
        }
    ]
}
Response:
{
    "currentColor": "",
    "targetCOlor": ""
}