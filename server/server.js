var mongo = require('mongoskin');
var dbconn = mongo.db('localhost:27017/finbert?auto_reconnect');
var http = require('http');

http.createServer(function (req, res) {
  res.writeHead(200, {'Content-Type': 'text/plain'});
  var parsedUrl = require('url').parse(req.url, true); 
  console.log(parsedUrl);
  if(handler[parsedUrl.pathname]) {
     handler[parsedUrl.pathname](res, parsedUrl.query);
  }
}).listen(1337, "127.0.0.1");

var handler = {
  '/comments/get': function(res, query) {
     console.log(query);
     console.log('comments/get:');
     dbconn.collection('comments').find({ date : query.date }).toArray(function(err, items){
         res.write(JSON.stringify(items));
         res.end();
     })
  }, 
  '/comments/insert': function(res, query) {
     console.log(query);
     console.log('comments/insert:');
     dbconn.collection('comments').insert({ date : query.date, comment : query.comment }, function(){
         res.write(JSON.stringify({status : "OK" }));
         res.end();
     })
  } 
}
console.log('Server running at http://127.0.0.1:1337/');
