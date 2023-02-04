<div id="height">
	<nav class="navbar navbar-inverse navbar-fixed-top" style="background-color: black;">
		<div class="container-fluid">
			<div class="navbar-header" style="margin-right: 3em;">
				<a class="navbar-brand">
					<span style="font-size:2em;color:white">Auto</span>
					<span class="text-left" style="font-size:2.5em;color:red;">X</span>
				</a>
			</div>
			
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li class="<?php if($color=='red'){echo 'active';}?>">
						<a class="nav-link" href="/index.php" style="color:white;"><b>Home</b></a>
					</li>
					<li class="<?php if($color=='green'){echo 'active';}?>">
						<a class="nav-link" href="/featurepage.php" style="color:white"><b>Feature</b></a>
					</li>
					<li class="<?php if($color=='orange'){echo 'active';}?>">
						<a class="nav-link" href="/idefeature.php" style="color:white"><b>Ide Feature</b></a>
					</li>
					<li class="<?php if($color=='blue'){echo 'active';}?>">
						<a class="nav-link" href="/doc.php" style="color:white"><b>Documentation</b></a>
					</li>
				</ul>

				<div class="nav navbar-nav navbar-right">
					<a><img src="/img/content/logo.png" style="width:200px"></a>
				</div>
			</div>

		</div>
	</nav>
</div>
