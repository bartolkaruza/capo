
var io;

var games = {};

exports.init = function(socketIo) {
    io = socketIo;
    io.sockets.on('connection', function (socket) {
        socket.on('foo', function(message) {
            console.log(message);
        });
        socket.on('measurement', function (measurement) {
            socket.broadcast.emit('measurement', measurement);
        });
    });

    io.sockets.on('disconnect', function(socket) {
        console.log('disconnect: ' + socket);
    });
}

exports.handleCreateGame = function(req, resp) {
    var game = req.body;
    games[game.name] = ({name: game.name, values:[game.deviceAddress], status:'waiting'});
    resp.send(200, games[req.body.name]);
//    resp.send(200, game);
};

exports.handleGameList = function(req, resp) {
    resp.send(200, games);
};

exports.handleGetGame = function(req, resp) {
    var id = req.params['id'];
    resp.send(200, games[id]);
};

exports.handleJoin = function(req, resp) {
    var id = req.params['id'];
    var game = games[id];
    game.values.push(req.body.deviceAddress);
    if(game.values.length > 2) game.status = 'started';
    resp.send(200, game);
};