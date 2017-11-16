//Based on tutorial from https://www.learn2crack.com/2014/08/android-upload-image-node-js-server.html

var fs = require('fs');

const util = require('util');

var dirname = "/home/kd0euh/audiveris/sheetplayer-server";

var multipart = require('connect-multiparty');

const exec = util.promisify(require('child_process').exec);

var pythonShell = require('python-shell');

module.exports = function(app) {



app.get('/',function(req,res){

	res.end("Node-File-Upload");


});

app.post('/upload', multipart(), function(req, res) {

	console.log(req.files.image.originalFilename);

	console.log(req.files.image.path);

		fs.readFile(req.files.image.path, function (err, data){

		var newPath = dirname + "/uploads/" + req.files.image.originalFilename;

		fs.writeFile(newPath, data, function (err) {

		if(err){

		res.json({'response':"Error"});

		}else {

		res.json({'response':"Saved"});

		sendTransfer(req.files.image.originalFilename);

}

});

});

});



app.get('/uploads/:file', function (req, res){

		file = req.params.file;

		var img = fs.readFileSync(dirname + "/uploads/" + file);

		res.writeHead(200, {'Content-Type': 'image/*' });

		res.end(img, 'binary');


});


app.get('/outs/:file', function (req, res){

		file = req.params.file;

		var img = fs.readFileSync(dirname + "/outs/" + file);

		res.writeHead(200, {'Content-Type': 'audio/midi' });

		res.end(img, 'binary');


});

app.get('/outs', function (req, res){

	fs.readdir(dirname + "/outs/", function(err, items) {
		console.log("CALLED FOR OUTS LIST, SENDING");
			res.send(items);
	});
});

};


async function sendTransfer(file) {

console.log("CALLING PYTHON FUNCTION" + file);
console.log("ARUGMENTS " + dirname + "/uploads/" + file + " --- " + dirname + "/outs/" + file);

var options = {
	mode: 'text',
	scriptPath: '/home/kd0euh/audiveris/audiveris/',
	pythonPath: '/usr/bin/python3',
	args: [dirname + "/uploads/" + file, dirname + "/outs/" + file]};

var pyShell = new pythonShell('test04.py', options);
pyShell.on('message', function (message) {
console.log(message);
});

}
