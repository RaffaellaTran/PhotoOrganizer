//(function(){
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
     getGroupName(firebaseUser);
	 } else{
		 console.log('not logged in');
	 }
 })

  //});
	const btnOut= document.getElementById('btnSignOut');
	btnOut.addEventListener('click', e=> {

		firebase.auth().signOut();
	});

  function getGroupName(firebaseUser) {
    //Get elements
  	const preObject=document.getElementById('object');
  	const ulList= document.getElementById('list');

    // Get group name
    var userId = firebase.auth().currentUser.uid;
    return firebase.database().ref('/users/' + userId).once('value').then(function(snapshot) {
      groupName = snapshot.val().group
      console.log(groupName)
      startListeningToImages(firebaseUser, groupName)
    });
  }



  function startListeningToImages(firebaseUser, groupName) {
    //Create reference
  	const dbRefObject= firebase.database().ref('/pictures/' + groupName).once('value').then(function(snapshot) {
      console.log(snapshot)
      images = snapshot.val()
      for (id in images) {
        image = images[id]
        console.log(image)
        var storage = firebase.storage();
        var storageRef = storage.ref();
        var imgRef = storageRef.child(image.bucket_identifier);

        imgRef.getDownloadURL().then(function(url)
        {
            showImage(image, url)
        })
      }
    });
  }

  function showImage(image, downloadURL) {
    console.log(downloadURL)
  }
	//Sync object change
  /*
	dbimg.on('value', snap=>{

		preObject.innerText= JSON.stringify(snap.val(), null,3);
		img.innerHTML= attrValue;
	//	document.getElementById("mess").innerHTML = JSON.stringify(snap.val(), null,3);
	//	document.getElementById("mess").innerHTML =pathReference;
	//	if (key='bucket_identifier'){document.getElementById("mess").innerHTML =snap.val();}
	});
  */




//}());
