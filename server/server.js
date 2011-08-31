var logger = require('logging').from(__filename);
var http = require('http');
var dbconn = require('mongoskin').db('localhost:27017/finbert?auto_reconnect');
var argv = require('optimist').argv;
var Status = { "SUCCESS" : "SUCCESS", "FAILURE_INVALID_MESSAGE": "FAILURE_INVALID_MESSAGE", "FAILURE_OTHER": "FAILURE_OTHER" };
var mac = "Wanha, eka, toka, HUUTO, kiroilu, v*ttuilu ja muu p*rseily kielletty Seuraus: IP-esto";
var mode = argv.mode;

http.createServer(function (req, res) {
  req.startTime = Date.now();
  req.rid = getRequestID();
  req.addListener('end', requestEndCallback);
  var parsedUrl = require('url').parse(req.url, true);
  logger("RID [" + req.rid + "] :: New request");
  logger("RID [" + req.rid + "] :: ", parsedUrl);

  var valid = false;
  if(mode != 'debug') {
      if(messageIsValid(req)) { 
          valid = true;
      }
  } else {
      valid = true;
  }

  if(valid) {
      res.writeHead(200, {'Content-Type': 'text/plain'});
  } else {
      res.writeHead(200, {'Content-Type': 'text/plain'});
      var result = [];
      logger("RID [" + req.rid + "] :: ", Status.FAILURE_INVALID_MESSAGE );
      res.write(JSON.stringify(result));
      res.end();
      return;
  }

  if(allowedActions[parsedUrl.pathname]) {
     allowedActions[parsedUrl.pathname](res, req);
  } else {
      res.end();
  }
}).listen(4325);

function comments_get (res, req) {
     var query = require('url').parse(req.url, true).query;
     logger("RID [" + req.rid + "] :: ", query);
     dbconn.collection('comments').find({ date : query.date }, { _id: 0, created_on: 0 }).sort( { created_on: -1 }).toArray(function comments_get_callback (err, items){
         logger("RID [" + req.rid + "] :: ", items);
	 res.write(JSON.stringify(items));
	 res.end();
     })
}
 
function comments_insert (res, req) {
     var query = require('url').parse(req.url, true).query;
     logger("RID [" + req.rid + "] :: ", query);
     dbconn.collection('comments').insert({ date : query.date, name: query.name, comment : query.comment, created_on: new Date() }, function comments_insert_callback (){
         var result = 'true';
         logger("RID [" + req.rid + "] :: ", result);
         res.write(JSON.stringify(result));
         res.end();
     })
}

function comments_count (res, req) {
     var query = require('url').parse(req.url, true).query;
     logger("RID [" + req.rid + "] :: ", query);
     dbconn.collection('comments').count({ date : query.date }, function comments_count_callback (err, result) {
         logger("RID [" + req.rid + "] :: ", result);
         res.write(JSON.stringify(result));
         res.end();
     })
}

var allowedActions = {
  '/comments/get'   : function(res,req) {  comments_get (res, req)    },
  '/comments/insert': function(res,req) {  comments_insert (res, req) },
  '/comments/count' : function(res,req) {  comments_count (res, req)  },
}

function messageIsValid (req) {
   var query = require('url').parse(req.url, true).query;
   logger("RID [" + req.rid + "] :: Validating query", query);
   var valid = false;
   var checkSumGenerator = require('crypto').createHash('md5');
   if(query.date && query.checksum) {
       var checkSum = checkSumGenerator.update(query.date + mac).digest('hex');
       if(checkSum === query.checksum) {
          valid = true;
       } else {
          logger("RID [" + req.rid + "] :: Invalid checksum", checkSum, query.checksum);
       }
   } else {
      logger("RID [" + req.rid + "] :: Not enough parameters for query"); 
   } 
   return valid;
}

function requestEndCallback() {
   logger("RID [" + this.rid + "] :: request done " + (Date.now() - this.startTime) / 1000 + " s" );
}
// between 1000 - 9999
function getRequestID() {
   return Math.floor(Math.random() * 9000) + 1000;
}

logger('Server running at http://192.168.1.100:4325/');
