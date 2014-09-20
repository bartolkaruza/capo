
var io;

var games = {};
var gamesList = [];
exports.init = function(socketIo) {
    io = socketIo;
    io.sockets.on('connection', function (socket) {
        socket.on('foo', function(message) {
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

function updateMeasurement(measurement) {
    var game = games[measurement.gameId];

}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}