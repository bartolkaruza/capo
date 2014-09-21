
var io;

var games = {};
var gamesList = [];
var currentSocket;
exports.init = function(socketIo) {
    io = socketIo;
    io.sockets.on('connection', function (socket) {
        currentSocket = socket;
        socket.on('foo', function(message) {
            socket.emit('bar', message);
            console.log(message);
        });
        socket.on('measurement', function (measurement) {
            measurement = JSON.parse(measurement);
            var game = games[measurement.gameId];
            console.log(measurement);
			if (game) {
				for (id in game.values) {
					if (measurement.valuePair.deviceAddress === game.values[id].address) { //player is known
						var color = updateMeasurement(measurement);
						socket.broadcast.emit('update', {currentColor:color, targetColor:game.targetColor});	
					}
				}
			}
        });
    });

    io.sockets.on('disconnect', function(socket) {
        console.log('disconnect: ' + socket);
    });
}

exports.handleCreateGame = function(req, resp) {
    var game = req.body;
    games[game.name] = ({name: game.name, values:[{address:game.deviceAddress, playerNumber:1}], status:'waiting', targetColor:getRandomColor(), currentColor:{red:125, green:125, blue:125}, measurements:{red:[80], green:[80], blue:[80]}});
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
    if(game) {
        game.values.push({address:req.body.deviceAddress, playerNumber:(game.values.length + 1)});
        if(game.values.length > 2) game.status = 'started';
        resp.send(200, game);
        currentSocket.emit("join", game);
    } else {
        resp.send(400, "game not found");
    }


};

var mapping = {
    player12:"red",
    player13:"blue",
    player23:"green"
}

exports.testMeasurements = function(req, resp) {
    resp.send(200, updateMeasurement(req.body));
};

function updateMeasurement(measurement) {

    var game = games[measurement.gameId];
    var sourceNumber;
    for(x in game.values) {
        var value = game.values[x];
        if(value.address === measurement.deviceId) {
             sourceNumber = value.playerNumber;
        } else {
            for(y in measurement.values) {
                var measurementValue = measurement.values[y];
                if(measurementValue.deviceAddress === value.address) {
                    game.currentColor = mapMeasurement(sourceNumber, value.playerNumber, game, mapping, measurementValue);
                }
            }
        }
    }

    return game.currentColor;
}

function mapMeasurement(sourceNumber, targetNumber, game, mapping, measurement) {
    if (sourceNumber === 1) {
        if (targetNumber === 2) {
            return updateColor(game.currentColor, mapping.player12, measurement.rssi, game.measurements[mapping.player12]);
        } else if (targetNumber === 3) {
            return updateColor(game.currentColor, mapping.player13, measurement.rssi, game.measurements[mapping.player13]);
        }
    } else if (sourceNumber === 2) {
        if (targetNumber === 1) {
            return updateColor(game.currentColor, mapping.player12, measurement.rssi, game.measurements[mapping.player12]);
        } else if (targetNumber === 3) {
            return updateColor(game.currentColor, mapping.player23, measurement.rssi, game.measurements[mapping.player23]);
        }
    } else if (sourceNumber === 3) {
        if (targetNumber === 1) {
            return updateColor(game.currentColor, mapping.player13, measurement.rssi, game.measurements[mapping.player13]);
        } else if (targetNumber === 2) {
            return updateColor(game.currentColor, mapping.player23, measurement.rssi, game.measurements[mapping.player23]);
        }
    }
}

function updateColor(currentColor, color, rssi, measurements) {
    currentColor[color] = calculateColorValue(rssi, currentColor[color], measurements);
    return currentColor;
}

function calculateColorValue(rssi, currentValue, measurements) {
    var oldRssi = measurements.pop();
    if(!oldRssi) {
        oldRssi = rssi;
    }
    measurements.push(rssi);
    var newRssi = (Math.abs(oldRssi) + Math.abs(rssi)) / 2;
    var newValue = map(newRssi, 70, 90, 0, 255);
    return (newValue + currentValue) / 2;
}

function map(value, oldMin, oldMax, newMin, newMax) {
    return newMin + (newMax - newMin) * (value - oldMin) / (oldMax - oldMin);
}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}