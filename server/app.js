require('strong-agent').profile();

/**
 * If configured as a cluster master, just start controller.
 */
var control = require('strong-cluster-control');
var options = control.loadOptions();

if(options.clustered && options.isMaster) {
  return control.start(options);
}

/**
 * Main application
 */
var express = require('express');
var routes = require('./routes');
var http = require('http');
var path = require('path');

var app = express(),
    server = http.createServer(app),
    io = require('socket.io').listen(server);

var port = 3000;

app.configure(function(){
  server.listen(process.env.PORT || port);
  app.set('views', __dirname + '/views');
  app.set('view engine', 'ejs');
  app.use(express.favicon());
  app.use(express.logger('dev'));
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(path.join(__dirname, 'public')));
});

app.configure('development', function(){
  app.use(express.errorHandler());
});

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

var options = {

};

routes(app, options);

http.createServer(app).listen(app.get('port'), function(){
  console.log('measure-app listening on port: ' + port);
});