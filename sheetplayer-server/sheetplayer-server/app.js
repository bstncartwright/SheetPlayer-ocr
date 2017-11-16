//Based on tutorial from https://www.learn2crack.com/2014/08/android-upload-image-node-js-server.html

/**

 * Module dependencies.

 */

var express  = require('express');

var connect = require('connect');

var cookieParser = require('cookie-parser');

var logger = require('morgan');

var bodyParser = require('body-parser');

var app      = express();

var port     = process.env.PORT || 8080;


// Configuration

app.use(express.static(__dirname + '/public'));

app.use(cookieParser());

app.use(logger('dev'));

app.use(bodyParser());


app.use(bodyParser.json());

app.use(bodyParser.urlencoded());


// Routes


require('./routes.js')(app);


app.listen(port);

console.log('The App runs on port ' + port);

