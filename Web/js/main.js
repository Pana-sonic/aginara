var lat, lon, map, items, minListMode, resultMode, markers;

function callMinList(){
	var url= "http://localhost:8080/aginara/webresources/api/minList";
	var productNames = "";
	console.log(items);

	$( "#products ul li" ).each(function( index ) {
		items.push($(this).text());
	});

	$.each( items, function( key, value ) {
		console.log(value);
		productNames += value;
		productNames += ":1";
		if (key < items.length-1){
	  		productNames += ",";
	  	}
	});

	var encodedNames =  encodeURIComponent(productNames);
	var  formData = "latitude="+lat;
	formData += "&longtitude="+lon;
	formData += "&products="+encodedNames;

	return $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data : formData,
        success: function(data, textStatus, jqXHR){	
        	var totalSum=0.0;
	    	
	    	$("#product-list .separator").hide();
	    	$( ".product-input" ).hide();
	    	$("#products").html("<div id=\"res\"><span class=\"header\">Product List</span><div class=\"separator\"></div><div id=\"products\" class=\"products\"><ul></ul></div></div>");

	    	var counter = 0; 
	    	for (var i=0; i<data.length; i++){
				var newProduct = data[i].split(":");
				totalSum += parseFloat(newProduct[4]);
				if (i==0){
					var marker = new google.maps.Marker({
					    position: new google.maps.LatLng(newProduct[2], newProduct[3]),
					    map: map,
					    title: data[0],
					    icon: "img/pins/color" + counter + ".png"
					});

					markers.push(marker);
				}else if (i>0){
	    			var oldProduct = data[i-1].split(":");
	    			if(newProduct[0] !== oldProduct[0]){
	    				counter++;
	    				var marker = new google.maps.Marker({
						    position: new google.maps.LatLng(newProduct[2], newProduct[3]),
						    map: map,
						    title: data[0],
						    icon: "img/pins/color" + counter + ".png"
						});

						markers.push(marker);
	    			}
	    	  	}

				$("#products").find('ul').append("<li><span class=\"pull-left indicator color"+counter+"\"></span>"
								+"<div class=\"list-input-three-columns pull-left\">"+newProduct[1]+"</div><b>"+newProduct[4]+"</b></li>");
			}
			
			$("#products").append("<div id=\"total-sum\"><div class=\"separator\"></div><div class=\"total-sum\">Total: "+totalSum+"</div></div>");
	    },
	    error: function (jqXHR, textStatus, errorThrown) {
			alert('Please insert a valid address');
	    }
    });
}


function callMarketCarts(){
	var url= "http://localhost:8080/aginara/webresources/api/marketCarts";
	var productNames = "";
	console.log(items);

	$.each( items, function( key, value ) {
		console.log(value);
		productNames += value;
		productNames += ":1";
		if (key < items.length-1){
	  		productNames += ",";
	  	}
	});

	var encodedNames =  encodeURIComponent(productNames);
	var  formData = "latitude="+lat;
	formData += "&longtitude="+lon;
	formData += "&products="+encodedNames;

	return $.ajax({
        type: "POST",
        url: url,
        dataType: "json",
        data : formData,
        success: function(data, textStatus, jqXHR){		    	
	    	$("#products").html("<div id=\"res\"><span class=\"header\">Markets</span><div class=\"separator\"></div><div id=\"products\" class=\"products\"><ul></ul></div></div>");

	    	var counter = 0; 
	    	for (var i=0; i<data.length; i++){
				var newProduct = data[i][0].split(":");
				
				var marker = new google.maps.Marker({
				    position: new google.maps.LatLng(newProduct[5], newProduct[6]),
				    map: map,
				    title: newProduct[0],
				    icon: "img/pins/color" + counter + ".png"
				});

				$("#products").find('ul').append("<li><span class=\"pull-left indicator color"+counter+"\"></span>"
						+"<div class=\"list-input-three-columns pull-left\">"+newProduct[0]+"</div><b>"+newProduct[7]+"</b></li>");
				
				counter++;
			}
	    },
	    error: function (jqXHR, textStatus, errorThrown) {
			alert('Please insert a valid address');
	    }
    });
}

function initialize() {
	var mapOptions = {
	  	center: new google.maps.LatLng(37.975087,23.735694),
	  	zoom: 7,
	  	disableDefaultUI: true
	  	/*rotateControl: false, 
	  	streetViewControl: false*/
	};
	
	map = new google.maps.Map(document.getElementById("map-canvas"),
	    mapOptions);

	var marker = new google.maps.Marker({
	   // animation: google.maps.Animation.DROP
	});
	locationCircle = new google.maps.Circle();

	var input = (document.getElementById('location-input'));
	var options = {
	   componentRestrictions: {country: 'gr'}
	};
	var autocomplete = new google.maps.places.Autocomplete(input, options);
  	//autocomplete.bindTo('bounds', map);

  	google.maps.event.addListener(autocomplete, 'place_changed', function() {
	    var place = autocomplete.getPlace();
  		if (!place.geometry) { return; }

  		lat = place.geometry.location.lat();
  		lon = place.geometry.location.lng()

  		if (map.getZoom()!=13)
	      	map.setZoom(13);

	    if (place.geometry.viewport) {	//console.log('1');
	      map.fitBounds(place.geometry.viewport);
	    } else {
	      map.setCenter(place.geometry.location);    	
	    }

	    marker.setPosition(place.geometry.location);
	    marker.setMap(map);

	    var locationCircleOptions = {
		    strokeColor: '#ccc',
		    strokeOpacity: 1,
		    strokeWeight: 2,
		    fillColor: '#ccc',
		    fillOpacity: 0.35,
		    map: map,
		    center: place.geometry.location,
		    radius: 4000
		};

		locationCircle.setOptions(locationCircleOptions);
  	});
}


    
$(document).ready(function(){
	initialize();
	minListMode = true;
	resultMode = false;
	markers = [];

	var colors = ["#8F187C", "#512480",  "#1C3687", "#00599D", "#009598", "#009353", "#62A73B", "#E3D200", "#D99116","#D4711A", "#CF3834"];
	$(".product-input").autocomplete({
		source: listOfProducts,
		minLength: 2
	});				

    $('#products').on( 'click', '.delete-btn', function() {
		var parentElement = this.parentElement;
		parentElement.remove();
		$('#products ul').trigger('contentChanged');
	});

    $('#minListMode').on( 'click', function() {
		if(minListMode) return false;

	});

	$('#marketCartsMode').on( 'click', function() {
		if(!minListMode) return false;
		

		for(var i=0; i<markers.length; i++){
			map.removeOverlay(markers[i]);
			markers = [];
		}

		callMarketCarts().done(function(result) {
		   resultMode = true;
		   results = result;
		   $("#search-btn").html('New Search');
		   console.log(results);
		}).fail(function() {
		    alert('An error occured. Please refresh and try again');
		});
	});

	$('#products ul').on('contentChanged',function(){
	 	var listElement = $('#products ul li');
	 	var searchButton = $('#search-btn')
	 	if(listElement.length==0 && searchButton.css('display')==="inline-block"){
	 		searchButton.css('display','none');
	 	} else if ( listElement.length>0 && searchButton.css('display')==="none"){
	 		searchButton.css('display','inline-block');
	 	}
	});

	$('#search-btn').on( 'click', function() {
		if (resultMode){
			$("#search-btn").html('Search');
			$( ".product-input" ).show();
	    	$("#product-list .separator").show();
	    	$("#total-sum").remove();
			$("#res").html("<div id=\"products\" class=\"products\"><ul></ul></div>");
			resultMode = false;
			return false;
		}
		var results;
		items = [];

		if (typeof(lat)==='undefined' || typeof(lon)==='undefined'){
			alert('Please insert a valid address');
		}

		callMinList().done(function(result) {
		   resultMode = true;
		   results = result;
		   $("#search-btn").html('New Search');
		   console.log(results);
		}).fail(function() {
		    alert('An error occured. Please refresh and try again');
		});

		$('#sidebar').on( 'click', '.products ul li', function() {
		  	for (var j=0; j<results.length; j++){
			  	var productDetails = results[j].split(":");
			  	if($(this).text().trim() === (productDetails[1].trim()+productDetails[4])){
			  		map.setCenter(new google.maps.LatLng(productDetails[2], productDetails[3]));
			  	}
		  	}
		});
	});


	$( ".product-input" ).autocomplete({
	  select: function( event, ui ) {
	  	setTimeout(function() {
	  		var productName = $('#product-autocomplete').val().trim();
		  	$("#products ul").append("<li><div class=\"pull-left list-input-two-columns\">"+productName+"</div><img src=\"img/remove.png\" class=\"delete-btn\"></li>");
	    	$('#products ul').trigger('contentChanged');
		  	$( ".product-input" ).val('');
		  	event.preventDefault();
		}, 0);
	  }
	});

});
