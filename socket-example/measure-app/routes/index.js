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
//  app.put('/measurements', options.measurement);
  
};