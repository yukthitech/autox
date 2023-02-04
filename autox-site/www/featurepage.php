<?php
 $features=array(
   array(
     "image"=>"/img/featurepage/image.png",
     "Title"=>"Template Like Language",
     "Description"=>"Template like xml language makes autox powerful yet simple for everyone"),
   array(
     "image"=>"/img/featurepage/image1.png",
     "Title"=>"Embedded Templates",
     "Description"=>"In all attributes and text contents (including cdata sections) freemarker template support is inbuilt.
      Making autox making more dynamic,
      this feature is mostly used in defining dynamic queries or payloads.
      In standard programming languages this takes up lot of boiler plate coding or extra load of external resource management"),
   array(
     "image"=>"/img/featurepage/image2.png",
     "Title"=>" Question Expressions",
     "Description"=>"DML query steps support question (?) expressions which works similar to free marker expressions,
      but instead of replacing the value directly, the value will be passed as prepare statement parameter"),
   array(
     "image"=>"/img/featurepage/image3.png",
     "Title"=>"Expression Parsers & Pipes",
     "Description"=>"In attributes and text contents,
      expression prefixes can be used to parse/process followed content in dynamic ways like parsing,
      loading files/resources, parsing json, xpath search, property search etc.
      And pipes (like unix syntax) can be used to pass the output of one parser to next parser."),
   array(
     "image"=>"/img/featurepage/image4.png",
     "Title"=>"Log Reports",
     "Description"=>"High informative log reports will be generated.
      Each step in the test case will result in detailed log information.These logs are searchable"),
   array(
     "image"=>"/img/featurepage/image5.png",
     "Title"=>"Log Monitors",
     "Description"=>"Log monitors can be used to fetch logs from local machine or remote machines for the duration of each test case individually.
      This helps in ease of debugging when testing distributed architecture."),
   array(
     "image"=>"/img/featurepage/image6.png",
     "Title"=>"Selenium Based UI Automation",
     "Description"=>"Inbuilt support for ui automation is provided using selenium.
      And elements can be located using different locators like id based, css based, name based, xpath based etc all with simple expression syntax string.
      And also screen shots of the current browser state can be taken,
      which would be part of the log as shown below"),
   array(
     "image"=>"/img/featurepage/image7.png",
     "Title"=>"Pluggable Custom steps/validations",
     "Description"=>"custom steps or validations can be created in Java by implementing IStep/IValidation interfaces directly or indirectly and adding simple annotations for meta information."),
   array(
     "image"=>"/img/featurepage/image8.png",
     "Title"=>"Pluggable Expression Parsers",
     "Description"=>"New expressions parses can be added in java by a simple public method with @ExpressionParser annotation providing meta information")
 );
?>
<!DOCTYPE html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="utf-8">
		<title>Autox Features</title>
		
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
			$color='green';
			include('common/topnavbar.php');
			include('common/scrollingimage.php');
		 ?>

		<div class="container" style="margin-top: 3em;">
			<div class="row ">
				<?php
					foreach($features as $feature)
					{
				?>
					<div class="col-sm-4">
						<div class="card-list">
							<div class="card-body" style="margin: 1em; text-align: center; width: 300px;">
								<div id="image" >
									<a  href="<?=$feature['image']?>">
										<img class="card-img-top" src="<?=$feature['image']?>" style="height: auto; width: 100%;">
									</a>
								</div>
								
								<a class="card-list-title" style="color:blue"  data-content="<?= $feature['Description'] ?>"data-placement="bottom" data-trigger="hover"><b><?=$feature['Title']?></b></a><br/>
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

