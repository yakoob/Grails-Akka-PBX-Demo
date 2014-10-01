

var SCREEN_MIC_CONTENT = "MicConfigContent";
var SCREEN_OUTBOUND_CONTENT = "OutboundCallContent";

(function() {

var app = angular.module('webbuttonApp', ["pascalprecht.translate",
        "OutboundController.module",
        "ngSanitize",
        "ngResource" ])

    .config(function($locationProvider,$routeProvider,$translateProvider)
    {

        console.log("WebButtonApp.module setting up routes.");

        $routeProvider.
        when('/microphoneSettings', {title:"Microphone Settings", templateUrl: 'views/microphoneSettings.html',   controller: 'MicrophoneController' }).
        when('/phones/:phoneId', {templateUrl: 'partials/phone-detail.html', controller: 'PhoneDetailCtrl'}).
        otherwise({redirectTo: '/phones'});

        console.log("provider: " + $translateProvider.useStaticFilesLoader)
        $translateProvider.useStaticFilesLoader({
            prefix: 'app/locale/locale.',
            suffix: '.properties'
        });

       $translateProvider.preferredLanguage('en_US');

    });




var LOGS = 'logs';

/**
 * The controller for the personal log app.
 */
app.controller('WebAppController', ['$scope', function LogCtrl($scope) {

  var logs = $scope.logs ;

     $scope.activeContent = "test";
     $scope.micScreen = SCREEN_MIC_CONTENT;
     $scope.outboundScreen = SCREEN_OUTBOUND_CONTENT;

        /****************************************************************************************
         *
         * Core method which broadcasts events for which screen to show
         * @param screenId
         *
         ****************************************************************************************/

          $scope.showScreen = function(screenId) {
              console.log("showScreen called " + screenId);

              $scope.activeContent = screenId;

              $scope.$broadcast("event:changeScreen", {screen:screenId} );

          }



}]).directive('viewstack', function() {


        var link;
            link = function($scope,element,attrs){



                /****************************************************************************************
                 *
                 * IE 7 does not work with emitted events, so we set a root level function to be called
                 *
                 ****************************************************************************************/
                onIEAngularJSEvent = function(event,args){
                    console.log("WebButtonApp.js onIEAngularJSEvent : " +event );
                    $scope.$broadcast(event, args );
                }
                console.log("WebButtonApp showing content: " + attrs.activeContent );

                /****************************************************************************************
                 *
                 * IE 7 hack as jquery cannot read child elements with children() and also
                 * ng-show does not work in IE 7 as expected so we manually change the visibility
                 *
                 ****************************************************************************************/
                if( navigator.userAgent.indexOf("MSIE 7") >= 0 || navigator.userAgent.indexOf("MSIE 8") >= 0  )
				{

					var myscreenElements = new Array();
					myscreenElements.push( $("#" + SCREEN_MIC_CONTENT).get(0) );
					myscreenElements.push( $("#" + SCREEN_OUTBOUND_CONTENT).get(0) );
					
					showScreen(myscreenElements, attrs.activeContent);

					$scope.$on('event:changeScreen', function(event, args){ showScreen.apply(null,[myscreenElements,args.screen]) });


					return;
				}

				showScreen(element.children(), attrs.activeContent);

                /****************************************************************************************
                 *
                 * Create event for listening for changescreen events.
                 *
                 ****************************************************************************************/
                $scope.$on('event:changeScreen', function(event, args){ showScreen.apply(null,[element.children(),args.screen]) });

            };

            function showScreen(childElements, screenIdToShow){

                console.log("WebButtonApp showScreen: " + screenIdToShow );
                console.log("WebButtonApp childElements: " + childElements );
                /******************************************************************************
                 * Iterate through the viewstack and decide which div should be visible
                 *******************************************************************************/
                for( var i=0; i< childElements.length; i++ ){
                    var childElem = childElements[i];
                    var divContentId = childElem.id;
					
                    if( divContentId != screenIdToShow ) {
					
                        $(childElem).addClass("webbuttonapp_hidden");
                    }
                    else {
						
                        $(childElem).removeClass("webbuttonapp_hidden");
                        $(childElem).addClass("webbuttonapp_visible");
                    }

                }
            }


            return {
               restrict:"E",
               link:link,
               scope: {
                   activeContent : "@"
               }
            }

    });


})();



function dumpObject( obj ){
    for( var prop in obj ){
        console.log("Prop " + prop + " = " + obj[prop]);
    }
}



