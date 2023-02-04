<?php
 $features=array(
   array(
     "image"=>"/img/idefeature/image.png",
     "Title"=>" Basic Ide with multi file editing",
     "Description"=>"Provides common ide featueres like project explorer, multi file editing,
      multi poject management etc."),
   array(
     "image"=>"/img/idefeature/image1.png",
     "Title"=>"Syntax highlighting with errors and warnings ",
     "Description"=>"Provides basic xml syntax highlighting.
      And also provides autox based errors and warnings for effective development."),
   array(
     "image"=>"/img/idefeature/image2.png",
     "Title"=>"Auto completion support",
     "Description"=>"All the available steps, validations,
      functions etc are provided as auto-complete options along with documentation."),
   array(
     "image"=>"/img/idefeature/image3.png",
     "Title"=>"Test suite/test cases execution",
     "Description"=>"With simple short keys ide provides means of executing test suites or test cases in place.
      And the console output, report ouput, context attributes created as part of the execution can be viewed within ide.
      Full report can be displayed in browser with a click of button"),
   array(
     "image"=>"/img/idefeature/image4.png",
     "Title"=>"Interactive Development",
     "Description"=>"Development of test cases can be done interactively.
      Developer can code a step, execute it analyze the result,
      if require make modifications or proceed with next step coding.
      All this without saving the file. And the resultant console logs,
      attributes and report entries can be viewed within the ide."),
   array(
     "image"=>"/img/idefeature/image5.png",
     "Title"=>" Docs and Help",
     "Description"=>"Documentation of all available steps,
      validations, functions etc are provided within the ide.
       And for ease of usage, lucene based search is provided"),

 );
?>
<!DOCTYPE html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="utf-8">
		<title>Autox Ide Features</title>
		
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
			
			#image
			{
				border:1px solid black;
				padding: 5px;
				margin-bottom: 5px;
			}

			.active
			{
			  color:lightblue;
			  padding-bottom:1px;
			  border-bottom:1px solid white;
			}
		</style>
	</head>
	<body>
		<?php
			$color="orange";
			include('common/topnavbar.php');
			include('common/scrollingimage.php');
		 ?>

		<div  class="container" style="margin-top: 3em;">
			<div class="row ">
				<?php
					foreach($features as $feature)
					{
				?>
					<div class="col-sm-4">
						<div class="card-list">
							<div class="card-body" style="margin: 1em; text-align: center; width: 300px;">
								<div id="image"  >
									<a href="<?=$feature['image']?>"><img class="card-img-top" src="<?=$feature['image']?>" style="height: auto; width: 100%;"></a>
								</div>
					
								<a class="card-list-title"  style="color:blue" data-content="<?= $feature['Description'] ?>" data-placement="bottom" data-trigger="hover"><b><?=$feature['Title']?></b></a><br/>
							</div>
						</div>
					</div>
				<?php
					}
				?>
			</div>
		</div>
		<script>
			$(function () {
				$('[data-toggle="popover"]').popover()
			});
			
			$(function(){
				$('[data-trigger="hover"]').popover()
			});
		</script>
	</body>
</html>


