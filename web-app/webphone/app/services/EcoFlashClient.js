/******************************************************************************************************************************
 *
 * Angular service for Flash utilities.
 *
 * Dependencies: swfobject.js
 *
 ****************************************************************************************************************************/


angular.module('webbuttonApp').factory('flashClientService', function($http) {


    var swfObjectId = "flashContent";
    var flashClientService_eventHandler ;
    var serviceInstance;
    var so;
    var swf_obj = null;
    var isMuted = false;

    var nearestServer;

    /***********************************************************************************************
     * 
     ***********************************************************************************************/
    function init () {
        console.log("EcoFlashClientService.js init() called.");
        serviceInstance=this;

        var flashvars = {}

        var params = {
            quality:'high', allowscriptaccess:'sameDomain', wmode:'transparent'
        }

        function swfLoadCallback(e) {
            console.log("EcoFlashClient.js swfLoadCallback fired." + e.success);
            if(e.success == true ){
                console.log("SWF load has succeeded");
                swf_obj = e.ref;
                console.log(swf_obj); 
            } else {
                microphoneAccessService_eventHandler.apply(this, [ "microphone_embed_error", null ] );
            }
        }

        swfobject.embedSWF("assets/flash/IFNWeb.swf"+ "?rnd=" + Math.random() * 100000, swfObjectId, "100", "100", "12.0.0","", flashvars, params, null, swfLoadCallback);
        swfobject.createCSS("#flashContent", "display:block;text-align:left;");
    }

    /***********************************************************************************************
     *
     * Set an event handler to receive events
     *
     ***********************************************************************************************/
    function setClientEventHandler( callbackfn ){
        console.log("EcoFlashClientService.js setFlashClientEventHandler() called.");
        flashClientService_eventHandler = callbackfn;
    }

    function setNearestServer( nearest ){
        console.log("EcoFlashClient.js setNearestServer() called.");
	console.log(nearest);
        /* nearest server is being passed with an http format, */
        /* need to parse the string an grab just the server URI */
	//removed because we are taking a hardcoded value from the form
        //var res = nearest.split("/");
        //console.log(res);
    	//nearestServer = res[2];
	nearestServer = nearest;
    }

    function connectNearestServer() {
	console.log("EcoFlashClientService config: " + nearestServer);

	var target = "rtmfp://" + nearestServer;
        swf_obj.connect(target);
        console.log("swf file initiate connect!");
    }

    onEcoFlashInit = function(){
        console.log("EcoFlashClientService.js onEcoFlashInit() called.");

        flashClientService_eventHandler.apply(this, ["onInit"]);
    }

    onEcoFlashIsReady = function(){
        console.log("EcoFlashClientService.js onEcoFlashIsReady() called.");

        flashClientService_eventHandler.apply(this, ["onReady"]);
    }

    onEcoFlashCallRinging = function(){
        flashClientService_eventHandler.apply(this, ["onCallRinging"]);
    }

    onEcoFlashCallConnected = function(){
        flashClientService_eventHandler.apply(this, ["onCallConnected"]);
    }

    onEcoFlashCallDisconnected = function(){
        flashClientService_eventHandler.apply(this, ["onCallDisconnected"]);
    }

    /****************************************************************************************************************************
     *
     * Shutdown all access and load in eco_rtc
     *
     *****************************************************************************************************************************/
    function onFlashClientReady() {

        /* ecortc.setSpeexQuality(10); */

        console.log("EcoFlashClientService.js onFlashClientReady() called.");
    }

    /****************************************************************************************************************************
     *
     * Execute the phone call with a specific ring to destination
     *
     * @param callDetailObj {"callerIdName":"webbutton-id-name","callerIdNumber":"+12132830912","carrierOverride":"SPRINT","ringToAddress":destination};
     *
     *****************************************************************************************************************************/

    function executeCall(callDetailObj) {
        console.log("EcoFlashClientService.js executeCall: " + callDetailObj);

        if(swf_obj == null) {
            console.log("swf_obj is null");
        } else {
            swf_obj.call(callDetailObj.ringToAddress);
        }
    }

    function disconnect() {
        console.log("EcoFlashClient.js disconnect() called.");

        if(swf_obj == null) {
            console.log("swf_obj is null");
        } else {
            swf_obj.hangup();
        }
    }

    function senddtmf( keyId ) {
        console.log("EcoFlashClient.js sendDTMF( "+keyId+") called.");

        if(swf_obj == null) {
            console.log("swf_obj is null");
        } else {
            swf_obj.sendDTMF(keyId);
        }
    }

    function playDTMFSound( keyId ) {

        var audioFile = "DTMF-" + keyId + ".mp3";

        if( keyId == "#") audioFile =  "DTMF-pound.mp3"
        if( keyId == "*") audioFile =  "DTMF-star.mp3"

        var soundAsset = "assets/audio/" + audioFile;

        /* swfobject.getObjectById(swfObjectId).playAudio(soundAsset); */

        mute(true);

        window.setTimeout( function(){
            mute(false);
        }, 3000);
    }


    function mute( setMute ) {
        console.log("EcoFlashClient.js mute() called.");

        if( setMute ) {
            console.log("service mute");
            isMuted = true;
            swf_obj.mute();
        }
        else {
            console.log("service unmute");
            isMuted = false;
            swf_obj.unmute();
        }
    }


    return {
        init:init,
        setClientEventHandler:setClientEventHandler,
	    setNearestServer:setNearestServer,
	    connectNearestServer:connectNearestServer,
        executeCall:executeCall,
        disconnect:disconnect,
        mute:mute,
        senddtmf:senddtmf
    };

});
