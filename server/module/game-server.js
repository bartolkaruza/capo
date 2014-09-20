
var io;

var games = {};
var gamesList = [];
exports.init = function(socketIo) {
    io = socketIo;
    io.sockets.on('connection', function (socket) {
        socket.on('foo', function(message) {
            socket.emit('bar', message);
            console.log(message);
        });
        socket.on('measurement', function (measurement) {
            var game = games[measurement.gameId];
            socket.broadcast.emit('update', {currentColor:"afrdbn", targetColor:"afrdbn"});
        });
    });

    io.sockets.on('disconnect', function(socket) {
        console.log('disconnect: ' + socket);
    });
}

exports.handleCreateGame = function(req, resp) {
    var game = req.body;
    games[game.name] = ({name: game.name, values:[{address:game.deviceAddress, playerNumber:1}], status:'waiting', targetColor:getRandomColor()});
    gamesList.push(games[game.name]);
    resp.send(200, games[req.body.name]);
};

exports.handleGameList = function(req, resp) {
    resp.send(200, gamesList);
};

exports.handleGetGame = function(req, resp) {
    var id = req.params['id'];
    resp.send(200, games[id]);
};

exports.handleJoin = function(req, resp) {
    var id = req.params['id'];
    var game = games[id];
    game.values.push({address:req.body.deviceAddress, playerNumber:(game.values.length + 1)});
    if(game.values.length > 2) game.status = 'started';
    resp.send(200, game);
};

var currentColor = {
    red:0,
    green:0,
    blue:0
};

var mapping = {
    player12:"red",
    player13:"blue",
    player23:"green"
}

function updateMeasurement(measurement) {
    var game = games[measurement.gameId];
    var sourceNumber;
    var targetNumber;
    for(value in game.values) {
        if(value.address === measurement.deviceId) {
             sourceNumber = value.playerNumber;
        } else {
            for(measureValue in measurement.values) {
                if(measureValue.deviceAddress === value.address) {
                    targetNumber = value.playerNumber;
                }
            }
        }
    }
    if(sourceNumber === 1) {
        if(targetNumber === 2) {
            game.currentColor = updateColor(mapping.player12, measurement.rssi, game.measurements[player12]);
        } else if(targetNumber === 3) {
            game.currentColor = updateColor(mapping.player13, measurement.rssi, game.measurements[player13]);
        }
    } else if(sourceNumber === 2) {
        if(targetNumber === 1) {
            game.currentColor = updateColor(mapping.player12, measurement.rssi, game.measurements[player12]);
        } else if(targetNumber === 3) {
            game.currentColor = updateColor(mapping.player23, measurement.rssi, game.measurements[player23]);
        }
    } else if(sourceNumber === 3) {
        if(targetNumber === 1) {
            game.currentColor = updateColor(mapping.player13, measurement.rssi, game.measurements[player13]);
        } else if(targetNumber === 2) {
            game.currentColor = updateColor(mapping.player23, measurement.rssi, game.measurements[player23]);
        }
    }
}

function updateColor(pair, rssi, measurements) {
    return calculateColorValue(rssi, currentColor[mapping[pair]], measurements);
}

function calculateColorValue(rssi, currentValue, measurements) {
    var oldRssi = measurements.last();
    measurements.shift();
    measurements.push(rssi);
    var newRssi = (oldRssi + rssi);
    currentValue = newRssi
    return 1;
}

function map() {

}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}