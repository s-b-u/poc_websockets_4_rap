(function($) {
	if (rwt.remote._wsinitialized || false)
		return;
	rwt.remote._wsinitialized = true;
	let proto = rwt.remote.Request.prototype;
	let createXHR=rwt.remote.Request.createXHR;
	rwt.remote.Request = function(url, method, responseType) {
		this._url = url;
		this._method = method;
		this._async = true;
		this._success = null;
		this._error = null;
		this._data = null;
		this._responseType = responseType;
		this._request = (url||"").startsWith("rwt-resources") ? rwt.remote.Request.createXHR() : rwt.remote.Request.createWSRequest();
	}
	rwt.remote.Request.prototype = proto;
	rwt.remote.Request.createXHR = createXHR;
	rwt.remote.Request.createWSRequest = function() {
		return {
			data : null,
			async : true,
			method : "POST",
			url : null,

			setRequestHeader : function() {
			},

			open : function(method, url, async) {
				this.method = method;
				this.url = url;
				this.async = async;
			},

			send : function(data) {
				let request = this;
				let conn = rwt.remote.Connection.getInstance();
				let requestId = (new Date).getTime();
				let urlparams={};
				let parts=this.url.split('?');
				if(parts.length>1){
					urlparams=parts[1].split('&').map(q=>q.split("=")).reduce((acc,cur)=>{acc[cur[0]]=cur[1]; return acc},{})
				}
				this._getWebSocket(conn).then(
						function(ws) {
							let message = {
								requestId : requestId,
								parameters : {
									cid : conn.getConnectionId()
								},
								message : (data || false) ? JSON
										.parse(data) : null
							}
							Object.assign(message.parameters,urlparams)
							if (conn._cookies || false)
								message.cookies = conn._cookies;
							(conn._requests = conn._requests || {})[requestId] = request
							ws.send(JSON.stringify(message))
						});
			},

			abort : function() {				
			},

			getAllResponseHeaders : function() {
				return Object.entries(this.responseHeaders || {}).map(function(e){return e.join(':');}).join("\r\n");
			},

			_getWebSocket : function(connection) {
				return connection._websocket = connection._websocket
						|| (function(url) {
							return new Promise(
									function(resolve) {
										let ws = new WebSocket(url);
										ws.onerror = function(e) {
											console.log(e)
										}
										ws.onmessage = function(m) {
											let response = JSON.parse(m.data);
											let request = connection._requests[response.requestId];
											delete connection._requests[response.requestId];
											if ((request || false)
													&& (request.onreadystatechange || false)) {
												request.status = 200;
												request.readyState = 4;
												request.responseText = JSON
														.stringify(response.message);
												request.responseHeaders = response.headers;
												request.onreadystatechange();
											}
										}
										ws.onclose = function(m) {
											delete connection._websocket;
										}

										ws.onopen = function() {
											resolve(ws)
										}
									})
						})(window.location.href.replace(/^http/, "ws"))
			},

			onreadystatechange : function() {
			}

		}
	}
})(rwt.remote.Request)