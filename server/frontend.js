var Google = require('google-maps');
var lawnChair = require('./lib/lawnchair-0.6.1.js');

var io = require('socket.io-browserify');
var paths = {};
var colors = {};
var map;
var measurementStore = lawnChair({name:'measurements', record:'measurement'}, function(measurementStore) {

});
//var socket = io.connect('http://bartolkaruza-measure-app.nodejitsu.com/');
var socket = io.connect('http://localhost');
socket.on('measurement', function (data) {
    var agent = data.agent;
    measurementStore.get(agent, function(measurement) {
        measurementStore.save({key:agent, lat:data.latitude, long:data.longitude})
        var agentDiv;
        if(measurement && document.getElementById(agent)) {
            agentDiv = document.getElementById(agent);
        } else {
            agentDiv = document.createElement('div');
            colors.agent = randomColor();
            agentDiv.setAttribute('id', agent);
            agentDiv.style.color = colors.agent;
            agentDiv.style.borderStyle = 'solid';
            agentDiv.style.borderWidth = '1px';
            agentDiv.style.borderColor = colors.agent;
            agentDiv.style.float = 'left';
            var parent = document.getElementById('measurements');
            parent.insertBefore(agentDiv, parent.firstChild);

            // Resize all collumns
            var children = parent.children;
            if(children) {
                for(var i = 0; i < children.length; i++) {
                    children[i].style.width = ((100 / parent.children.length) - 4) + '%';
                }
            }
            paths[agent] = makePath(colors.agent);
        }
        var currentElement = document.createTextNode('agent: ' +  agent + ' lat:' +  data.latitude + ' long: ' + data.longitude + ' alt: ' + data.altitude);
        agentDiv.insertBefore(currentElement, agentDiv.firstChild);
        agentDiv.insertBefore(document.createElement('br'), agentDiv.firstChild);

        var path = paths[agent].getPath();
        if(path) {
            var latLng = new Google.google.maps.LatLng(data.latitude, data.longitude);
            path.push(latLng);
            map.setCenter(latLng);
        }
    });
});

Google.load(function() {
    initialize();
});

function initialize() {
    var mapOptions = {
        zoom: 18,
        center: new Google.google.maps.LatLng(52.374033, 4.880707)
    };
    map = new Google.google.maps.Map(document.getElementById('map-div'), mapOptions);
}

function makePath(color) {
    var flightPath = new google.maps.Polyline({
        path: [],
        geodesic: true,
        strokeColor: color,
        strokeOpacity: 1.0,
        strokeWeight: 2
    });

    flightPath.setMap(map);
    return flightPath;
}



function randomColor() {
    return '#'+Math.floor(Math.random()*16777215).toString(16);
}