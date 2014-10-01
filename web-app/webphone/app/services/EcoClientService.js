/******************************************************************************************************************************
 *
 * Angular service for client service utilities.
 *
 * Dependencies: <script src="webrtcsupport.bundle.js"> </script>
 *
 ****************************************************************************************************************************/


angular.module('webbuttonApp').factory('ecoClientService', function($http) {


    var ecoClientService_supportcb;
    var serviceInstance;
    var controllerQueryNearestCallback;

    /****************************************************************************************************************************
     * 
     *****************************************************************************************************************************/
    function init () {
        console.log("EcoClientService.js init() called.");
        serviceInstance=this;
    }

    /****************************************************************************************************************************
     *
     * Set a callback to report webrtc/flash support
     *
     *****************************************************************************************************************************/
    function checkClientSupport( callbackfn ){
        console.log("EcoClientService.js checkClientSupport() called.");

        var webrtc = webrtcsupport.support;
        var prefix = webrtcsupport.prefix;

        console.log("WebRTC support: " + webrtc);
        console.log("WebRTC prefix: " + prefix);
	callbackfn.apply(this, [webrtc, prefix]);
    }

    function queryNearestServer( callbackfcn ){
        console.log("EcoClientService.js queryNearestServer() called.");
    	controllerQueryNearestCallback = callbackfcn;
 
	var nearestNeighborList = new Array();
	var nearest = true;
  
	$http.get('serverList.js').success(
 		function(data) {
 			var serverList = data;
			console.log(data);
			angular.forEach(data.serverList, 
 				function (value,key) {
					var img = new Image();
					img.startTs = (+new Date());
					img.serverName = value.name;
					img.onload=function() {
						if (nearest == true) {
							console.log("If this is the first time you pass through this method, you have your winner");
							console.log("return the server info, otherwise just return");
							nearest = false;
						} else {
							return;
						}
						img.roundTripTime = (+new Date() - img.startTs);
						console.log(img.serverName + " in " + img.roundTripTime  + ", from start time: " + img.startTs);
						nearestNeighborList.push( {"serverName" : img.serverName , "roundTripTime" :img.roundTripTime} );
						//refresh the databound view, just for UI sake..
						// $apply();
        					// this.onEcoRTCConfig();
						console.log("Reading config from custom.js: " + window.CustomJsSIPSettings.ws_servers);
						controllerQueryNearestCallback.apply(this, [value.testURL]);
						return;
					}
					//ready,set,go
					img.src = value.testURL	+ '?' + Math.random() + '=' + new Date;
  				}
  			);
		});
    }


    return {

        init:init,
        checkClientSupport:checkClientSupport,
	    queryNearestServer:queryNearestServer

    };

});
