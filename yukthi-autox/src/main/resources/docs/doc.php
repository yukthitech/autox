<!DOCTYPE html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="utf-8">
		<title>AutoX API Documentation</title>
		
		<link rel="icon" type="image/png" href="autox-logo.png" />

		<script type="text/javascript" src="jquery/jquery-3.4.1.min.js"></script>
		<script type="text/javascript" src="bootstrap-3.4.1/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="popper/popper.min.js"></script>
		<script type="text/javascript" src="doc/docs.js"></script>

		<link rel="stylesheet" href="bootstrap-3.4.1/css/bootstrap.min.css">
	    <link rel="stylesheet" href="site/homepagecontent.css">
		<link rel="stylesheet" href="doc/docs.css">

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
	<body data-spy="scroll" data-target="#myScrollspy" data-offset="80">
		<?php
			  $color='blue';
			  include('common/topnavbar.php');
		 ?>

		<div class="container" style="margin-top: 70px; padding: 0px; width: 100%;">
			<div class="row" style="margin: 0px;">
				<div class="col-md-3 col-lg-2">
					<div id="myScrollspy" class="bs-docs-sidebar" style="position: fixed; overflow: auto;">
						<ul class="nav nav-stacked" data-offset-top="10" style="position: initial;">
							<li>
								<a href="#steps">Steps and Assertions</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
									<#list activeGroups as group>
										<li>
											<a href="#grp-${group}">${group}</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
												<#list getStepsWithGroup(group) as step>
													<li>
														<a href="#step-${group}-${step.name}">${step.name}</a>
													</li>
												</#list>
											</ul>
										</li>
									</#list>
								</ul>
							</li>
							
							<li>
								<a href="#fmarkerMethods">Free Marker Methods</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
									<#list freeMarkerMethods as fmarker>
										<li>
											<a href="#fmarker-${fmarker.name}">${fmarker.name}</a>
										</li>
									</#list>
								</ul>
							</li>
							
							<li>
								<a href="#parsers">Expression Types</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
									<#list parsers as parser>
										<li>
											<a href="#parser-${parser.name}">${parser.name}</a>
										</li>
									</#list>
								</ul>
							</li>
	
							<li>
								<a href="#uiLocators">UI Locators</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
									<#list uiLocators as locator>
										<li>
											<a href="#locator-${locator.name}">${locator.name}</a>
										</li>
									</#list>
								</ul>
							</li>
						</ul>
					</div>
				</div>
				
				<div class="col-md-9 col-lg-10">
					<div id="steps" class="dataSection1">
						<h1>Steps and Assertions</h1>
						<p>
							Steps and assertions are basic building blocks of autoX. From the basic language (like if, loop, functions etc) to complex/custom
							functionality everything is done using steps/assertions.<br/>
							<b>Step</b> is a simple tag, which helps in execution of some functionality.<br/>
							<b>Assertion</b> is similar to step, which apart from execution of some functionality it also validates the state of current execution.
						</p>
						
						<#list activeGroups as group>
							<div id="grp-${group}" class="dataSection2">
								<h2>${group} related steps</h2>
								
								<#list getStepsWithGroup(group) as step>
									<div id="step-${group}-${step.name}" class="dataSection3">
										<h3>${step.type} ${step.name} - ${step.title}</h3>
										
										<@addContent step=step/>
									</div>
								</#list>
							</div>
						</#list>
					</div>

					<div id="fmarkerMethods" class="dataSection1">
						<h1>Free Marker Methods</h1>
						<p>Defines the steps of autox for all functionalities</p>
						
						<#list freeMarkerMethods as fmarker>
							<div id="fmarker-${fmarker.name}" class="dataSection2">
								<h2>Free Marker Method - ${fmarker.name}</h2>
								<@addDocumentation docInfo=fmarker/>
							</div>
						</#list>
					</div>
						
					<div id="parsers" class="dataSection1">
						<h1>Expression Types</h1>
						<p>Defines the steps of autox for all functionalities</p>
						
						<#list parsers as parser>
							<div id="parser-${parser.name}" class="dataSection2">
								<h2>Expression Type - ${parser.name}</h2>
								<@addDocumentation docInfo=parser/>
							</div>
						</#list>
					</div>

					<div id="uiLocators" class="dataSection1">
						<h1>UI Locators</h1>
						<p>Defines the steps of autox for all functionalities</p>
						
						<#list uiLocators as locator>
							<div id="locator-${locator.name}" class="dataSection2">
								<h2>Expression Type - ${locator.name}</h2>
								<@addDocumentation docInfo=locator/>
							</div>
						</#list>
					</div>
				</div>
			</div>
		</div>

	</body>
</html>


<#macro addContent step>
	<p>
		${step.description}
	</p>
	
	<table class="table table-striped table-hover table-bordered">
		<tr>
			<th colspan="3"  class="doc_mainHeading">
				Parameters
			</th>
		</tr>
		<tr>
			<th class="doc_heading"> Name </th>
			<th class="doc_heading"> Type </th>
			<th class="doc_heading"> Description </th>
		</tr>
		<#list step.params as param>
			<tr>
				<td>${param.name}</td>
				<td>${param.type}</td>
				<td>
					<#if param.sourceType == "CONDITION">
						<b><i>This is a "Condition" parameter. This is a freemarker expression that should result in boolean value.</i></b>
						<BR/>
					</#if>
					<#if param.sourceType == "EXPRESSION">
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>
					</#if>
					<#if param.sourceType == "EXPRESSION_PATH">
						<b><i>This is an expression-path. This is similar to <a href="#parsers">Expressions</a>, but are generally used to set the value at specified path.</i></b>
						<BR/>
					</#if>						
					<#if param.sourceType == "UI_LOCATOR">
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>
					</#if>						

					${param.description}
				</td>
			</tr>
		</#list>
		
		<#assign i = step.params?size>
		<#if i==0>
			<tr>
				<td colspan="3" style="font-weight: bold; ">
					<i>No parameters available.</i>
				</td>
			</tr>
		</#if>
	</table>
	
	<#if step.hasExamples()>
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
			<#list step.examples as ex>
				<tr>
					<td class="doc_heading" style="width: 30px;"> ${ex?index + 1} </td>
					<td>
						${ex.description}<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							${ex.escapedContent}
						</div>
						
					</td>
				</tr>
			</#list>
		</table>
	</#if>
	
</#macro>

<#macro addDocumentation docInfo>
	<p>
		${docInfo.description}
	</p>
	
	<#if docInfo.hasParameters()>
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="3"  class="doc_mainHeading">
					Parameters
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Name </th>
				<th class="doc_heading"> Type </th>
				<th class="doc_heading"> Description </th>
			</tr>
			
			<#list docInfo.parameters as param>
				<tr>
					<td>${param.name}</td>
					<td>${param.type}</td>
					<td>
						${param.description}
					</td>
				</tr>
			</#list>
		</table>
	</#if>

	<#if docInfo.hasReturnInfo()>
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>${docInfo.returnType}</td>
				
				<th class="doc_heading"> Description </th>
				<td>${docInfo.returnDescription}</td>
			</tr>
		</table>
	</#if>

	<#if docInfo.hasExamples()>
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
			<#list docInfo.examples as ex>
				<tr>
					<td class="doc_heading" style="width: 30px;"> ${ex?index + 1} </td>
					<td>
						${ex.description}<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							${ex.escapedContent}
						</div>
						
					</td>
				</tr>
			</#list>
		</table>
	</#if>
	
</#macro>
