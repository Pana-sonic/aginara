<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>

<title>Product!</title>
<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/themes/base/minified/jquery-ui.min.css" type="text/css" /> 
<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.10.1/jquery-ui.min.js"></script>

</head>
<body>

	<form action="" method="post">
		<p><label> Προιόν </label><input type="text" placeholder="Όνομα Προιόντος" class="auto"></p>
	</form>
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="http://code.jquery.com/ui/1.10.1/jquery-ui.min.js"></script>	
	<script type="text/javascript">
	$(function() {
		//autocomplete
		$(".auto").autocomplete({
			source: "search.php",
			minLength: 1
		});				
	 
	});
	</script>
</body>


