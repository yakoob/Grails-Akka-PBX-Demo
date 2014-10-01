

var webbutton_callState = {
   "onInit" : "onInit",
   "onReady" : "onReady",
   "onCallConnected" : "onCallConnected",
   "onCallRinging" : "onCallRinging",
   "onCallDisconnected" : "onCallDisconnected",
   "onConnectError" : "onConnectError"
}

var ref = angular.module( 'OutboundController.module', [], function(){})
    .controller( 'OutboundController', function OutboundCallController( $scope, $log ) {

        console.log("OutboundController loaded " + $scope.showScreen);

        /*********************************************************************************************
         *
         * Create directive which handles the logic behind the microphone settings and SWF object manipulation
         *
         *********************************************************************************************/
    }).directive('outboundDirective', function($compile, $translate) {

        var scope ;
        var sipClient;
        var flashClient;
        var ecoClient;
        var myController;
        var clientService;
        var isMuted = false;
	    var element;

        var ringto;
        var protocol;
        var server;
        var port;

        function Controller($scope, $element, $transclude, ecoClientService, sipRTCClientService, flashClientService ) {
             console.log("outboundController.js contructor called!");

             ringto = this.getParam("ringToAddress").toString();
             protocol = this.getParam("protocol").toString();
             server = this.getParam("server").toString();
             port = this.getParam("port").toString();

             console.log("RINGTO FROM MENU: " + ringto);
             console.log("PROTOCOL FROM MENU: " + protocol);
             console.log("SERVER FROM MENU: " + server);
             console.log("PORT FROM MENU: " + port);

             scope = $scope;
             scope.callLength = 0;
             scope.callLengthInterval;
             scope.imageClass ="";
             element = $element;
             myController = this;
             sipClient=sipRTCClientService;
             flashClient=flashClientService;
             clientService=ecoClientService;
             clientService.checkClientSupport(this.onClientSupportCallback);

             scope.translationData = {
                 businessName: ringto
             };

            scope.youAreTalkingTo = "SCREEN_OUTBOUND_CONNECTING_TO_OPERATOR";

            this.startCallLengthTimer();
            this.setUpUnloadListener();

            this.showConnectedState();
            window.setTimeout(function(){
                $scope.$emit("event:changeScreen", {screen:SCREEN_OUTBOUND_CONTENT} );
            },2000);

        }
        /*********************************************************************************************
         *
         * Primary events received from the SIP Client service
         *
         *********************************************************************************************/
        Controller.prototype.onClientSupportCallback = function( webrtc, webrtc_pre ){
            console.log("EcoOutboundController.js client support: " + webrtc + "->" + webrtc_pre);

            /* if WebRTC is available, default to that service */
		    /* TEST FLASH ON CHROME */
                /* webrtc = false; */
		    /* TEST FLASH ON CHROME */
            if(webrtc && protocol == "webrtc") {
                console.log("WebRTC has been selected and is available, setting service!");
                ecoClient=sipClient;
            } else { /* otherwise default to flash */
                console.log("Setting Flash service!");
                ecoClient=flashClient;
            }
	        console.log("Using ecoClient:");
	        console.log(ecoClient);
            /* Creating/setting this variable allows it to be passed to the key pad press handler */
            scope.ecoClient = ecoClient;

	        //Ignore the nearest server, use the form overrides for the server settings
            //clientService.queryNearestServer(myController.onNearestCallback);
       
	        ecoClient.init();
		    console.log(this);
            ecoClient.setClientEventHandler(myController.onSIPState);
            ecoClient.setNearestServer(server + ":" + port);

        }
     /*
	This method not needed because we are hardcoding the server and port from the form 
        Controller.prototype.onNearestCallback = function( nearest ){
            console.log("EcoOutboundController.js callback nearest server: " + nearest);

            // retrieve config for that server and connect to server 
             ecoClient.init();
             ecoClient.setClientEventHandler(myController.onSIPState);
             // ecoClient.setNearestServer(nearest); 
             ecoClient.setNearestServer(this.server);
        }
     */

        Controller.prototype.onSIPState = function( state ){

            console.log("OutboundController.js onSIPState: " + state);

            if( state == webbutton_callState.onInit){
                console.log("OutboundController.js onInit - module initiated");
                ecoClient.connectNearestServer();
            }

            if( state == webbutton_callState.onReady){
                console.log("OutboundController.js onReady - executeCall");
                myController.executeCall();
                console.log("[Executed]OutboundController.js onReady - executeCall");
            }

            if( state == webbutton_callState.onCallConnected || state == webbutton_callState.onCallDisconnected || state == webbutton_callState.onCallRinging){
                //myController.showCallState(state);
            	if( state == webbutton_callState.onCallConnected){
            		console.log("OutboundController.js showConnectedState. " + state);

            		$(".btn").remove();
            		$(".phoneCallIcon").remove();
            		$(".keyPadStyles").remove();

            		myController.showConnectedState();

            		scope.$apply();
		        } else {
                    myController.showCallState(state);
                }
            }

            if( state == webbutton_callState.onConnectError ){
                myController.showError();
            }

        }

        Controller.prototype.setUpUnloadListener = function(){
            var needToConfirm = true;
            window.onbeforeunload = askConfirm;
            window.onunload = unloadPage;
            var isDelete = true;

            function unloadPage()
            {

                ecoClient.disconnect();
                console.log("Page unloaded...");

            }

            function askConfirm()
            {

                if (needToConfirm)
                {
                    // Message to be displayed in Warning.
                    return $translate('SCREEN_OUTBOUND_ARE_YOU_SURE_CANCEL');
                }
                else
                {
                    ecoClient.disconnect();
                    isDelete = false;
                }
            }

        }

        /*********************************************************************************************
         *
         * Make the call
         *
         *********************************************************************************************/
        Controller.prototype.executeCall = function(){

            scope.youAreTalkingTo = "SCREEN_OUTBOUND_CONNECTING_TO_OPERATOR";
            scope.callState ="";
            scope.contentArea= '';
            scope.imageClass ="";


            var callDetail = {"callerIdName":
                 "webbutton-id-name",
                 "callerIdNumber":"+12132830912",
                 "carrierOverride":"SPRINT",
                 "ringToAddress":this.getParam("ringToAddress").toString()};

            ecoClient.executeCall(callDetail);

        }

        Controller.prototype.getParam = function( myVar )
        {
            myVar = myVar.replace(/\?/ig, "").replace(/=/ig, ""); // Globally replace illegal chars.

            var url = window.location.href;                                   // Get the URL.
            var parameters = url.substring(url.indexOf("?") + 1).split("&");  // Split by "param=value".
            var params = [];                                                  // Array to store individual values.

            for(var i = 0; i < parameters.length; i++)
                if(parameters[i].search(myVar + "=") != -1)
                    return parameters[i].substring(parameters[i].indexOf("=") + 1).split("+");

            return "Parameter not found";
        }

        /*********************************************************************************************
         *
         * Make the call
         *
         *********************************************************************************************/
        Controller.prototype.showCallState = function(lstate){

            console.log("OutboundController.js showCallState. " + lstate);

            this.resetDOM();

            if( state == webbutton_callState.onCallConnected ) this.showConnectedState();

            if( state == webbutton_callState.onCallDisconnected)  this.showDisconnectedState();

            if( state == webbutton_callState.onCallRinging)  this.showRingingState();

            scope.$apply();
        }

        Controller.prototype.resetDOM = function(){
            $(".btn").remove();
            $(".phoneCallIcon").remove();
            $(".keyPadStyles").remove();
        }


        /*********************************************************************************************
         *
         * Show error
         *
         *********************************************************************************************/
        Controller.prototype.showError = function(){
            scope.contentArea= "SCREEN_OUTBOUND_CALL_ERROR_CONNECTING";

            this.stopCallLengthTimer();
        }

        /*****************************************************************************************
         *
         *  When a call is ringing we just show a cancel button and timer
         *
         *****************************************************************************************/
        Controller.prototype.showRingingState = function(){

            // Add in a button for returning to the prior screen.
            var cancelButton = $compile( "<button id='returnBtn' class='btn outboundController_controlButton' style='margin-top: 20px;'><li class='icon-ban-circle '></li>{{ 'SCREEN_OUTBOUND_CANCEL' | translate }}</button>" )( scope );
            element.append( cancelButton );
            cancelButton.bind("click", function(){
                ecoClient.disconnect();
            }) ;

            this.setUpUnloadListener();

        }

        /*****************************************************************************************
         * Once the user is disconnected we then show the end call information
         *
         *****************************************************************************************/
        Controller.prototype.showDisconnectedState = function(){
            scope.youAreTalkingTo = "SCREEN_OUTBOUND_CALL_END_TITLE";
            scope.callState ="SCREEN_OUTBOUND_STATE_DISCONNECTED";
            scope.contentArea= 'This is customer content that is custom put here';

            var reconnectButton = $compile( "<button id='returnBtn' class='btn' style='margin-top: 20px;'>{{ 'SCREEN_OUTBOUND_CALL_END_RECONNECT_BUTTON' | translate }}</button>" )( scope );
            element.append( reconnectButton );
            reconnectButton.bind("click", function(){

                myController.startCallLengthTimer();

                myController.executeCall();
            }) ;


            var phoneImage = $compile( "<div class='phoneCallIcon'></div>" )( scope );
            element.prepend( phoneImage );

            window.onbeforeunload = null;
            window.onunload = null;

            this.stopCallLengthTimer();
        }

        Controller.prototype.stopCallLengthTimer = function()
        {
            clearInterval(scope.callLengthInterval);
        }

        /***
         * Once the call is connected we need to display the following:
         *
         * 1. Mute button
         * 2. End button
         * 3. Keypad with DTMF
         */

        Controller.prototype.showConnectedState = function()
        {
            scope.youAreTalkingTo = "SCREEN_OUTBOUND_YOU_ARE_TALKING_TO";
            scope.callState ="SCREEN_OUTBOUND_STATE_CONNECTED";
            this.startCallLengthTimer();

            // Add in a button for muting
            var muteButton = $compile( "<button id='returnBtn' class='btn outboundController_controlButton' style='margin-top: 20px;'><li class='icon-volume-up'></li>{{ 'SCREEN_OUTBOUND_MUTE' | translate }}</button>" )( scope );
            element.append( muteButton );
            muteButton.bind("click", function(){

                if( myController.isMuted ) {
                    console.log("Controller mute set to false");
                    ecoClient.mute(false);
                    myController.isMuted=false;
                } else {
                    console.log("Controller mute set to true");
                    ecoClient.mute(true);
                    myController.isMuted=true;
                }

            })

            // Add in a button for ending call
            var endButton = $compile( "<button id='returnBtn' class='btn btn-danger outboundController_controlButton' style='margin-top: 20px; '><li class='icon-ban-circle' ></li>{{ 'SCREEN_OUTBOUND_END' | translate }}</button>" )( scope );
            element.append( endButton );
            endButton.bind("click", function(){
                   ecoClient.disconnect();
            })

            // Add in a keypad
            var keypad = $compile( "<div key-pad></div>" )( scope );
            element.append( keypad );

        }

        /*********************************************************************************************
         *
         * Creates a call timer and displays this during the call.
         *
         *********************************************************************************************/
        Controller.prototype.startCallLengthTimer = function()
        {
            scope.totalTime = new Date(0,0,0,0,0,0,0);
            scope.callLength = 0;

            clearInterval(scope.callLengthInterval);

            scope.callLengthInterval = window.setInterval( function(){

                scope.callLength  += 1;
                scope.totalTime = new Date(0,0,0,0,0,scope.callLength,0);
                scope.$apply();

            },1000);

        }

        /*********************************************************************************************
         *
         * DOM manipulation in the link function which has a reference to the controller
         *
         *********************************************************************************************/
        var link = function($scope,$element,attrs, controller){

            element = $element;
            console.log("OutboundController.js micDirective created. " + controller);
        };

        return {
            restrict:"EAC",
            template:
                '<div ng-classname="outboundControllerContent" style="text-align: center !important; margin-left: auto; margin-right: auto; width: 300px;"> ' +
                '<div class=\'outboundController_title\'>{{ youAreTalkingTo | translate:translationData }}</div> ' +
                '<div id=\'outboundContentArea\' ng-bind-html-unsafe=" contentArea | translate "></div> ' +
                '<div class=\'horizontalRule\' > </div><div ng-bind-html-unsafe=" callState | translate "></div> ' +
                '<div> {{totalTime | date:"HH:mm:ss"}} </div>  ' +
                '</div>',
            transclude:true,
            replace:true,
            link:link,
            controller:Controller,
            scope: {
                headline : "@headline"
            }

        }
        /*********************************************************************************************
         *
         * Create key pad component which interacts with the sip client service
         *
         *********************************************************************************************/
    }).directive('keyPad', function ( ) {
        return {
            restrict: 'A',
            template: '<div class=\'keyPadStyles\'>' +
                '<button class="btn btn-primary btn-small keyPadButton"  ng-click="sendKeyPress(key.index)"  ng-repeat="key in keysrow1">' +
                '{{ key.index }}<br>{{ key.letters }}' +
                '</button>' +
                '</div>' +
                 '<div class=\'keyPadStyles\'>' +
                '<button class="btn btn-primary btn-small keyPadButton" ng-click="sendKeyPress(key.index)" ng-repeat="key in keysrow2">' +
                '{{ key.index }}<br>{{ key.letters }}' +
                '</button>' +
                '</div>' +
                '<div class=\'keyPadStyles\'>' +
                '<button class="btn btn-primary btn-small keyPadButton"  ng-click="sendKeyPress(key.index)" ng-repeat="key in keysrow3">' +
                '{{ key.index }}<br>{{ key.letters }}' +
                '</button>' +
                '</div>' +
                '<div class=\'keyPadStyles\'>' +
                '<button class="btn btn-primary btn-small keyPadButton"  ng-click="sendKeyPress(key.index)" ng-repeat="key in keysrow4">' +
                '{{ key.index }}<br>{{ key.letters }}' +
                '</button>' +
                '</div>',
            link: function (scope, elem, attrs) {
                console.log("keypad loaded");
                scope.keysrow1 = [];
                scope.keysrow2 = [];
                scope.keysrow3 = [];
                scope.keysrow4 = [];

                scope.keysrow1.push({ index: "1", letters:"" });
                scope.keysrow1.push({ index: "2", letters:"ABC" });
                scope.keysrow1.push({ index: "3", letters:"DEF" });

                scope.keysrow2.push({ index: "4", letters:"GHI" });
                scope.keysrow2.push({ index: "5", letters:"JKL" });
                scope.keysrow2.push({ index: "6", letters:"MNO" });

                scope.keysrow3.push({ index: "7", letters:"PQRS" });
                scope.keysrow3.push({ index: "8", letters:"TUV" });
                scope.keysrow3.push({ index: "9", letters:"WXYZ" });

                scope.keysrow4.push({ index: "*", letters:"" });
                scope.keysrow4.push({ index: "0", letters:"+" });
                scope.keysrow4.push({ index: "#", letters:"" });

                scope.sendKeyPress = function( keyId ){
                    // sipRTCClientService.senddtmf(keyId);
                    console.log(scope);
                    scope.ecoClient.senddtmf(keyId);
                }
            }
        }
    });

var OutboundController = ref.controller();


