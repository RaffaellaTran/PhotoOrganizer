//https://medium.com/google-cloud/google-firebase-can-host-your-website-and-single-page-application-4e9c9e0c6a95
(function (){
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
  
  const txtemail= document.getElementById('email');
  const txtname= document.getElementById('name');
  const txtpassword= document.getElementById('password');
  const btnSignUp= document.getElementById('btnSignUp');
  const txtmessage= document.getElementById('message');
  
  btnSignUp.addEventListener( 'click', e=>{
	 const email= txtemail.value;
	  const pass= txtpassword.value;
	  
	  const mess= txtmessage.value;
	  const name= txtname.value;
	  const auth= firebase.auth();
	  // Sign in
	  const promise= auth.createUserWithEmailAndPassword(email, pass);
	  promise
	  .catch (e=>{
		  console.log(e.message);
		  document.getElementById("message").innerHTML = e.message;
  }
		);
		
    
	  
  }
  );
  
   //Add a realtime listener
  firebase.auth().onAuthStateChanged(firebaseUser=>{
	 if (firebaseUser){
		 console.log(firebaseUser);
		 window.location.href="./gallery.html";
	 } else{
		 console.log('not logged in');
	 }
	  
  });
  
  
 
}());

function login(){
	
	window.location.href="./login.html";
}

