<div id="demo" class="carousel slide" data-ride="carousel">
	<ul class="carousel-indicators">
		<li data-target="#demo" data-slide-to="0" class="active"></li>
		<li data-target="#demo" data-slide-to="1"></li>
		<li data-target="#demo" data-slide-to="2"></li>
	</ul>

	<div class="carousel-inner" role="listbox">
		<div class="item active">
			<img src="/img/home/1.jpg" width="1200" style="height: 400px;">
			<div class="carousel-caption">
				<div class="row">
					<div class="col-md-12 justify-content-center">
						<button class="btn btn-primary" name="fullDownload">Download Prism (Autox Ide)</button>
					</div>
				</div>
			</div>
		</div>
		<div class="item">
			<img src="/img/home/2.jpg" width="1200" style="height: 400px;">
			<div class="carousel-caption">
				<div class="row">
					<div class="col-md-12 justify-content-center">
						<button class="btn btn-primary" name="fullDownload">Download Prism (Autox Ide)</button>
					</div>
				</div>
			</div>
		</div>
		<div class="item">
			<img src="/img/home/3.jpg" width="1200" style="height: 400px;">
			<div class="carousel-caption">
				<div class="row">
					<div class="col-md-12 justify-content-center">
						<button class="btn btn-primary" name="fullDownload">Download Prism (Autox Ide)</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<a class="left carousel-control" href="#demo" role="button" data-slide="prev">
		<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
		<span class="sr-only">Previous</span>
	</a>
	<a class="right carousel-control" href="#demo" role="button" data-slide="next">
		<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
		<span class="sr-only">Next</span>
	</a>
</div>


<script>
	var links=[{path:"/downloads/yukthi-prism-1.0.0.zip",file:"yukthi-prism-1.0.0.zip"}];

	$(document).ready(function()
	{
		$("button[name='fullDownload']").click(function(e)
		{
			var attribute=document.createElement('a');
			document.body.appendChild(attribute);

			for(var i=0;i<links.length;i++)
			{
				var data=links[i];
				attribute.setAttribute('href',data.path);
				attribute.setAttribute('download',data.file);
				attribute.click();
			}
		})
	});
</script>

