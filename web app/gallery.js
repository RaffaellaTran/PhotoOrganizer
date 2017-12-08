(function(){
	// Initialize Firebase
 var config = {
    apiKey: "AIzaSyA-RE77NPqD1m0zsw5jprChVAckh8nq-4M",
    authDomain: "mcc-fall-2017-g08.firebaseapp.com",
    databaseURL: "https://mcc-fall-2017-g08.firebaseio.com",
    projectId: "mcc-fall-2017-g08",
    storageBucket: "mcc-fall-2017-g08.appspot.com",
    messagingSenderId: "672954146858"
  };
  firebase.initializeApp(config);
  
  var imgName = 'hi.jpeg';
  var img= '<div class="row" id="img"><div class="col-md-2"><img class="img"  src="'+ imgName +'" ></div></div>';
  
  //Add a realtime listener
  firebase.auth().onAuthStateChanged(firebaseUser=>{
	 if (firebaseUser){
		 console.log(firebaseUser);
	 } else{
		 console.log('not logged in');
	 }
	  
  //});
	const btnOut= document.getElementById('btnSignOut');
	btnOut.addEventListener('click', e=> {
		
		firebase.auth().signOut();
	});
	
	//Get elements
	const preObject=document.getElementById('object');
	const ulList= document.getElementById('list');
	
	//Create reference
	const dbRefObject= firebase.database().ref().child('pictures');
	const dbRefList= dbRefObject.child('TestGroup2');
	const dbimg= dbRefList.child('-L-XgfdJxKPfdTBGJOw8');
	const imagess= dbRefList.child('bucket_identifier');
	
	//download
	//var fileName = window.AppInventor.getWebViewString();
	var storage    = firebase.storage();
	var storageRef = storage.ref();
	//var pathReference = storage.ref(fileName);
//	document.getElementById("mess").innerHTML =pathReference;
            /*      pathReference.getDownloadURL().then(function(url) {
                  window.AppInventor.setWebViewString(url);
 });*/
	for (var i = 0; i < dbRefList.length; i++){
    var obj = dbRefList[i];
    for (var key in obj){
        var attrName = key;
        var attrValue = obj[key];
		
    }
}
	//Sync object change
	dbimg.on('value', snap=>{
		
		preObject.innerText= JSON.stringify(snap.val(), null,3);
		img.innerHTML= attrValue;
	//	document.getElementById("mess").innerHTML = JSON.stringify(snap.val(), null,3);
	//	document.getElementById("mess").innerHTML =pathReference;
	//	if (key='bucket_identifier'){document.getElementById("mess").innerHTML =snap.val();}
	});
	
	
}());






























