/******************************************************************************************************************************
 *
 * Angular service for WebRTC utilities.
 *
 * Dependencies: jssip.js
 *
 ****************************************************************************************************************************/


angular.module('webbuttonApp').factory('sipRTCClientService', function($http) {


    var sipRTCClientService_eventHandler ;
    var serviceInstance;
    var controllerQueryNearestCallback;

    var rtcSession;
    var selfView;
    var remoteView;

    /****************************************************************************************************************************
     * 
     *****************************************************************************************************************************/
    function init () {
        console.log("EcoRTCSIPClientService.js init() called.");
        serviceInstance=this;
    }

    /****************************************************************************************************************************
     *
     * Set an event handler to receive events
     *
     *****************************************************************************************************************************/
    function setClientEventHandler( callbackfn ){
        console.log("EcoRTCSIPClientService.js setRTCSIPClientEventHandler() called.");
        sipRTCClientService_eventHandler = callbackfn;
    }

    setNearestServer = function( nearest ) {
        serviceInstance.connectNearestServer(nearest);
    }

    connectNearestServer = function( nearest ) {
        console.log("EcoRTCSIPClientService config: " + nearest);

	//var res = nearest.split("/");

        //console.log("EcoRTCSIPClientService config server: " + res[2]);

        CustomJsSIPSettings.ws_servers=  "ws://" + nearest  //  From webbutton:  "ws://" + res[2] + ":5066";
        ButtonPhone = new JsSIP.UA(CustomJsSIPSettings);

        ButtonPhone.on('connected', onEcoRTCIsReady);
        ButtonPhone.start();
    }

    onEcoRTCIsReady = function(){
        console.log("EcoRTCSIPClientService.js onEcoRTCIsReady() called.");

        $('#remoteView').attr('hidden', true);
        $('#selfView').attr('hidden', true);
	selfView = document.getElementById('selfView');
	remoteView = document.getElementById('remoteView');

        sipRTCClientService_eventHandler.apply(this, ["onReady"]);
    }

    onEcoRTCCallRinging = function(){
        sipRTCClientService_eventHandler.apply(this, ["onCallRinging"]);
    }

    onEcoRTCCallDisconnected = function(){
        sipRTCClientService_eventHandler.apply(this, ["onCallDisconnected"]);
    }

    onEcoRTCCallConnected = function(){
        sipRTCClientService_eventHandler.apply(this, ["onCallConnected"]);
    }

    /****************************************************************************************************************************
     *
     * Shutdown all access and load in eco_rtc
     *
     *****************************************************************************************************************************/
    function onRTCSIPClientReady() {
        console.log("EcoRTCSIPClientService.js onRTCSIPClientReady() called.");
    }

    /****************************************************************************************************************************
     *
     * Execute the phone call with a specific ring to destination
     *
     * @param callDetailObj {"callerIdName":"webbutton-id-name","callerIdNumber":"+12132830912","carrierOverride":"SPRINT","ringToAddress":destination};
     *
     *****************************************************************************************************************************/

    function executeCall(callDetailObj) {
        console.log("EcoRTCSIPClientService.js executeCall: " + callDetailObj);

	internal_executeCall(callDetailObj);
    }

    function internal_executeCall(callDetailObj) {
        console.log("EcoRTCSIPClientService.js ... placing call.");

        var eventHandlers = {
            'progress':   function(e){ /* Your code here */ },
            'failed':     function(e){ /* Your code here */ },
            'started':    function(e){
                 rtcSession = e.sender;
                 sipRTCClientService_eventHandler.apply(this, ["onCallConnected"]);

                 // Attach local stream to selfView
                 if (rtcSession.getLocalStreams().length > 0) {
                     selfView.src = window.URL.createObjectURL(rtcSession.getLocalStreams()[0]);
                     console.log("LOCAL: " + selfView.src);
                 } else {
                     console.log("NO LOCAL Stream!!");
                 }

                 // Attach remote stream to remoteView
                 if (rtcSession.getRemoteStreams().length > 0) {
                     remoteView.src = window.URL.createObjectURL(rtcSession.getRemoteStreams()[0]);
                     console.log("REMOTE: " + remoteView.src);
                 } else {
                     console.log("NO REMOTE Stream!!");
                 }
             },
             'ended':      function(e){ console.log("ended"); }
        };

        var options = {
            'eventHandlers': eventHandlers,
            'mediaConstraints': {'audio': true, 'video': false},
            'RTCConstraints': {"optional": [{'DtlsSrtpKeyAgreement': 'true'}]}
        };

        console.log(callDetailObj);
	target = callDetailObj.ringToAddress;
	ButtonPhone.call(target, options);
    }

    function disconnect() {
        console.log("EcoRTCSIPClientService.js disconnect() called.");

	ButtonPhone.stop();
    }

    function senddtmf( keyId ) {
        console.log("EcoRTCSIPClientService.js sendDTMF( "+keyId+") called.");

        rtcSession.sendDTMF(keyId);
        // playDTMFSound(keyId);
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


    function mute( isMuted ) {
        console.log("EcoRTCSIPClientService.js mute() called.");

        if( isMuted ) {
            // ecortmfp.setMicVolume(0);
        }
        else {
            // ecortmfp.setMicVolume(100);
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
