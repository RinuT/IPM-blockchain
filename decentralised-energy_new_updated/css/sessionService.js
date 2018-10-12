// sessionService.js
function sessionService() {
	var model = {};
	var session = {};
	model.query = function(dataWhichNeeded) {
		return session[dataWhichNeeded] || null;
	}
	model.put = function(identifier, data) {
		session[identifier] = data;
	}
  
}
sessionService.$inject = [];
app.service("sessionService", sessionService);

