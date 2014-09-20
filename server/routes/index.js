var gameServer = require('../module/game-server.js');

/*
 * GET home page.
 */
function index(req, res){
  res.render('index', { title: 'measure-app' });
}

/**
 * Set up routes
 */

module.exports = function(app, options) {

  app.get('/', index);

  app.put('/game', gameServer.handleCreateGame);

  app.post('/game/:id/join', gameServer.handleJoin);

  app.get('/game/:id', gameServer.handleGetGame);

  app.get('/game', gameServer.handleGameList);

  app.post('/testMeasurements', gameServer.testMeasurements);

};
