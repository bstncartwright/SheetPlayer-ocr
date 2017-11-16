var fs = require('fs');

const util = require('util');

const exec = util.promisify(require('child_process').exec);

var dirname = "C:/Users/Boston/Documents/sheetplayer-server/";

async function sendTransfer(file) {


  const { stdout, stderr } = await exec("/home/kd0euh/audiveris/audiveris/test04.py" + " " + file + " " + dirname + "/outs/");


  console.log('stdout:', stdout);


  console.log('stderr:', stderr);


}
