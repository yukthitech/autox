<html lang="en" dir="ltr">
	<head>
		<meta charset="utf-8">
		<title>AutoX Home page</title>
		
		<link rel="icon" type="image/png" href="autox-logo.png" />

		<script type="text/javascript" src="jquery/jquery-3.4.1.min.js"></script>
		<script type="text/javascript" src="bootstrap-3.4.1/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="/popper/popper.min.js"></script>

		<link rel="stylesheet" href="bootstrap-3.4.1/css/bootstrap.min.css">
	    <link rel="stylesheet" href="site/homepagecontent.css">

		<style>
			#height
			{
			  line-height:1;
			}
			#header
			{
			  word-spacing:-6px;
			}

			img
			{
				 width:100%;
			}
			.active
			{
			  color:lightblue;
			  padding-bottom:1px;
			  border-bottom:1px solid white;
			}
		</style>
	</head>
	<body data-spy="scroll" data-target="#myScrollspy" data-offset="40">
		<?php
			  $color='red';
			  include('common/topnavbar.php');
			  include('common/scrollingimage.php');
			  include('homepagecontent.php');
		 ?>
	</body>
</html>