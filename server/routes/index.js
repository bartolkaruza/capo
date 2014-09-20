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
    app.put('/game/create', function(req, resp) {
        resp.send(200, {
            "name": "game01",
            "deviceAddress": "01-AA-01-AA"
        });
    });

    app.get('/game/:id', function(req, resp) {
        resp.send(200, {
            players:["01-AA-01-AA","03-CC-03-CC","02-BB-02-BB"],
            targetColor:"rgb",
            status:"started/waiting"
        });
    });

    app.get('/game', function(req, resp) {
        resp.send(200, {
            games:[
                {
                    name:"game01"
                }
            ]
        });
    });

    app.put('/game/:id/join', function(req, resp) {
        resp.send(200, {
            players:["01-AA-01-AA","03-CC-03-CC","02-BB-02-BB"],
            targetColor:"rgb",
            status:"started/waiting"
        });
    });

    app.put('/game/:id/update', function(req, resp) {
        resp.send(200, {
            currentColor : "rgb",
            targetCOlor : "rgb"
        });
    });
//  app.put('/measurements', options.measurement);

};
