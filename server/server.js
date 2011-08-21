var http = require('http');
var dbconn = require('mongoskin').db('localhost:27017/finbert?auto_reconnect');
var argv = require('optimist').argv;
var mac = "Wanha, eka, toka, HUUTO, kiroilu, v*ttuilu ja muup*rseily kielletty Seuraus: IP-esto";
var mode = argv.mode;

http.createServer(function (req, res) {
  var parsedUrl = require('url').parse(req.url, true);
  var valid = false;
  console.log(parsedUrl);
  if(mode != 'debug') {
      if(messageIsValid(parsedUrl.query)) { 
          valid = true;
      }
  } else {
      valid = true;
  }

  if(valid) {
      res.writeHead(200, {'Content-Type': 'text/plain'});
  } else {
      res.writeHead(404, {'Content-Type': 'text/html'});
      res.end();
      return;
  }

  if(handler[parsedUrl.pathname]) {
     handler[parsedUrl.pathname](res, parsedUrl.query);
  } else {
      res.end();
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
         res.write(JSON.stringify({success : true }));
         res.end();
     })
  },
 
  '/ratings/set': function(res, query) {
     console.log(query);
     console.log('rating/set:');
     dbconn.collection('ratings').insert({ date : query.date, rating : query.rating }, function(){
         res.write(JSON.stringify({success : true }));
         res.end();
     })
  },
 
  '/ratings/get': function(res, query) {
     console.log(query);
     console.log('ratings/get:');
     dbconn.collection('ratings').find({ date : query.date }).toArray(function(err, items){
         console.log('Calculating rating for');
         console.log(items);
         var sum = 0;
         var isSuccess = false; 
         items.forEach(function(item) {
            sum += item.rating;
            isSuccess = true;
         });
         res.write(JSON.stringify({success : isSuccess,  rating: sum / items.length }));
         res.end();
     })
  } 
}

function messageIsValid (query) {
   console.log('Validating query');
   console.log(query);
   var valid = false;
   var checkSumGenerator = require('crypto').createHash('md5');
   if(query.date && query.checksum) {
      console.log('calculating checksum from: "' + query.date + mac + '"');
       var checkSum = checkSumGenerator.update(query.date + mac).digest('hex');
       if(checkSum === query.checksum) {
          console.log('query is valid');
          valid = true;
       } else {
          console.log('Invalid checksum, ' + checkSum + ' != ' + query.checksum);
       }
   } else {
      console.log('Not enough parameters for query');
   } 
   return valid;
}
console.log('Server running at http://127.0.0.1:1337/');
