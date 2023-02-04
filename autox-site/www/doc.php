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
																			<li>
											<a href="#grp-Common">Common</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Common-assertDeepEquals">assertDeepEquals</a>
													</li>
													<li>
														<a href="#step-Common-assertEquals">assertEquals</a>
													</li>
													<li>
														<a href="#step-Common-assertFalse">assertFalse</a>
													</li>
													<li>
														<a href="#step-Common-assertFileExists">assertFileExists</a>
													</li>
													<li>
														<a href="#step-Common-assertNotEquals">assertNotEquals</a>
													</li>
													<li>
														<a href="#step-Common-assertNotNull">assertNotNull</a>
													</li>
													<li>
														<a href="#step-Common-assertNotSame">assertNotSame</a>
													</li>
													<li>
														<a href="#step-Common-assertNull">assertNull</a>
													</li>
													<li>
														<a href="#step-Common-assertSame">assertSame</a>
													</li>
													<li>
														<a href="#step-Common-assertTrue">assertTrue</a>
													</li>
													<li>
														<a href="#step-Common-collectionAdd">collectionAdd</a>
													</li>
													<li>
														<a href="#step-Common-collectionRemove">collectionRemove</a>
													</li>
													<li>
														<a href="#step-Common-createTempFile">createTempFile</a>
													</li>
													<li>
														<a href="#step-Common-executeCommand">executeCommand</a>
													</li>
													<li>
														<a href="#step-Common-invokeMethod">invokeMethod</a>
													</li>
													<li>
														<a href="#step-Common-log">log</a>
													</li>
													<li>
														<a href="#step-Common-mapPut">mapPut</a>
													</li>
													<li>
														<a href="#step-Common-mkdir">mkdir</a>
													</li>
													<li>
														<a href="#step-Common-remove">remove</a>
													</li>
													<li>
														<a href="#step-Common-sendMail">sendMail</a>
													</li>
													<li>
														<a href="#step-Common-set">set</a>
													</li>
													<li>
														<a href="#step-Common-sleep">sleep</a>
													</li>
													<li>
														<a href="#step-Common-startTimer">startTimer</a>
													</li>
													<li>
														<a href="#step-Common-stopTimer">stopTimer</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Lang">Lang</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Lang-break">break</a>
													</li>
													<li>
														<a href="#step-Lang-continue">continue</a>
													</li>
													<li>
														<a href="#step-Lang-execute">execute</a>
													</li>
													<li>
														<a href="#step-Lang-fail">fail</a>
													</li>
													<li>
														<a href="#step-Lang-for">for</a>
													</li>
													<li>
														<a href="#step-Lang-forEach">forEach</a>
													</li>
													<li>
														<a href="#step-Lang-function">function</a>
													</li>
													<li>
														<a href="#step-Lang-functionRef">functionRef</a>
													</li>
													<li>
														<a href="#step-Lang-if">if</a>
													</li>
													<li>
														<a href="#step-Lang-pollAndCheck">pollAndCheck</a>
													</li>
													<li>
														<a href="#step-Lang-return">return</a>
													</li>
													<li>
														<a href="#step-Lang-while">while</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Mock">Mock</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Mock-mockFetchRequest">mockFetchRequest</a>
													</li>
													<li>
														<a href="#step-Mock-mockResponse">mockResponse</a>
													</li>
													<li>
														<a href="#step-Mock-mockServerReset">mockServerReset</a>
													</li>
													<li>
														<a href="#step-Mock-mockServerStart">mockServerStart</a>
													</li>
													<li>
														<a href="#step-Mock-mockServerStop">mockServerStop</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Mongodb">Mongodb</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Mongodb-assertMongo">assertMongo</a>
													</li>
													<li>
														<a href="#step-Mongodb-mongoMultiQuery">mongoMultiQuery</a>
													</li>
													<li>
														<a href="#step-Mongodb-mongoQuery">mongoQuery</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Rdbms">Rdbms</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Rdbms-sqlAssert">sqlAssert</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlAssertValue">sqlAssertValue</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlDdlQuery">sqlDdlQuery</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlDmlQuery">sqlDmlQuery</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlFetchValueQuery">sqlFetchValueQuery</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlLoadQueryColumnList">sqlLoadQueryColumnList</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlLoadQueryMap">sqlLoadQueryMap</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlLoadQueryRowBean">sqlLoadQueryRowBean</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlLoadQueryRowMap">sqlLoadQueryRowMap</a>
													</li>
													<li>
														<a href="#step-Rdbms-sqlMultiDmlQuery">sqlMultiDmlQuery</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Rest_Api">Rest_Api</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Rest_Api-restInvokeDelete">restInvokeDelete</a>
													</li>
													<li>
														<a href="#step-Rest_Api-restInvokeGet">restInvokeGet</a>
													</li>
													<li>
														<a href="#step-Rest_Api-restInvokeGetFile">restInvokeGetFile</a>
													</li>
													<li>
														<a href="#step-Rest_Api-restInvokeMultipartPost">restInvokeMultipartPost</a>
													</li>
													<li>
														<a href="#step-Rest_Api-restInvokeMultipartPut">restInvokeMultipartPut</a>
													</li>
													<li>
														<a href="#step-Rest_Api-restInvokePost">restInvokePost</a>
													</li>
													<li>
														<a href="#step-Rest_Api-restInvokePut">restInvokePut</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Ssh">Ssh</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Ssh-sshCloseSession">sshCloseSession</a>
													</li>
													<li>
														<a href="#step-Ssh-sshExecuteCommand">sshExecuteCommand</a>
													</li>
													<li>
														<a href="#step-Ssh-sshStartSession">sshStartSession</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Store">Store</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Store-storeDelete">storeDelete</a>
													</li>
											</ul>
										</li>
										<li>
											<a href="#grp-Ui">Ui</a>
	
											<ul class="nav nav-stacked" style="margin-left: 10px">
													<li>
														<a href="#step-Ui-uiAssertFormFields">uiAssertFormFields</a>
													</li>
													<li>
														<a href="#step-Ui-uiAssertValue">uiAssertValue</a>
													</li>
													<li>
														<a href="#step-Ui-uiAssertVisibility">uiAssertVisibility</a>
													</li>
													<li>
														<a href="#step-Ui-uiClick">uiClick</a>
													</li>
													<li>
														<a href="#step-Ui-uiClickAndDownload">uiClickAndDownload</a>
													</li>
													<li>
														<a href="#step-Ui-uiCloseSession">uiCloseSession</a>
													</li>
													<li>
														<a href="#step-Ui-uiCloseWindow">uiCloseWindow</a>
													</li>
													<li>
														<a href="#step-Ui-uiDblClick">uiDblClick</a>
													</li>
													<li>
														<a href="#step-Ui-uiDragAndDrop">uiDragAndDrop</a>
													</li>
													<li>
														<a href="#step-Ui-uiExecuteJs">uiExecuteJs</a>
													</li>
													<li>
														<a href="#step-Ui-uiFillForm">uiFillForm</a>
													</li>
													<li>
														<a href="#step-Ui-uiGetElements">uiGetElements</a>
													</li>
													<li>
														<a href="#step-Ui-uiGetNewWindowHandle">uiGetNewWindowHandle</a>
													</li>
													<li>
														<a href="#step-Ui-uiGetValue">uiGetValue</a>
													</li>
													<li>
														<a href="#step-Ui-uiGotoPage">uiGotoPage</a>
													</li>
													<li>
														<a href="#step-Ui-uiGotoUrl">uiGotoUrl</a>
													</li>
													<li>
														<a href="#step-Ui-uiHandleAlert">uiHandleAlert</a>
													</li>
													<li>
														<a href="#step-Ui-uiHandleConfirm">uiHandleConfirm</a>
													</li>
													<li>
														<a href="#step-Ui-uiHandlePrompt">uiHandlePrompt</a>
													</li>
													<li>
														<a href="#step-Ui-uiIsVisible">uiIsVisible</a>
													</li>
													<li>
														<a href="#step-Ui-uiLoadCookies">uiLoadCookies</a>
													</li>
													<li>
														<a href="#step-Ui-uiLogScreenShot">uiLogScreenShot</a>
													</li>
													<li>
														<a href="#step-Ui-uiMoveMouse">uiMoveMouse</a>
													</li>
													<li>
														<a href="#step-Ui-uiMoveTo">uiMoveTo</a>
													</li>
													<li>
														<a href="#step-Ui-uiOpenWindow">uiOpenWindow</a>
													</li>
													<li>
														<a href="#step-Ui-uiQuitSession">uiQuitSession</a>
													</li>
													<li>
														<a href="#step-Ui-uiRefresh">uiRefresh</a>
													</li>
													<li>
														<a href="#step-Ui-uiResetSession">uiResetSession</a>
													</li>
													<li>
														<a href="#step-Ui-uiRightClick">uiRightClick</a>
													</li>
													<li>
														<a href="#step-Ui-uiSetStyle">uiSetStyle</a>
													</li>
													<li>
														<a href="#step-Ui-uiSetValue">uiSetValue</a>
													</li>
													<li>
														<a href="#step-Ui-uiStoreCookies">uiStoreCookies</a>
													</li>
													<li>
														<a href="#step-Ui-uiSwitchFrame">uiSwitchFrame</a>
													</li>
													<li>
														<a href="#step-Ui-uiSwitchWindow">uiSwitchWindow</a>
													</li>
													<li>
														<a href="#step-Ui-uiSyncWindowHandles">uiSyncWindowHandles</a>
													</li>
													<li>
														<a href="#step-Ui-uiWaitFor">uiWaitFor</a>
													</li>
											</ul>
										</li>
								</ul>
							</li>
							
							<li>
								<a href="#fmarkerMethods">Free Marker Methods</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
										<li>
											<a href="#fmarker-addDays">addDays</a>
										</li>
										<li>
											<a href="#fmarker-addHours">addHours</a>
										</li>
										<li>
											<a href="#fmarker-addMinutes">addMinutes</a>
										</li>
										<li>
											<a href="#fmarker-addSeconds">addSeconds</a>
										</li>
										<li>
											<a href="#fmarker-collectionToString">collectionToString</a>
										</li>
										<li>
											<a href="#fmarker-compare">compare</a>
										</li>
										<li>
											<a href="#fmarker-compareAndGet">compareAndGet</a>
										</li>
										<li>
											<a href="#fmarker-countOfXpath">countOfXpath</a>
										</li>
										<li>
											<a href="#fmarker-dateToStr">dateToStr</a>
										</li>
										<li>
											<a href="#fmarker-escape">escape</a>
										</li>
										<li>
											<a href="#fmarker-fullPath">fullPath</a>
										</li>
										<li>
											<a href="#fmarker-getValueByXpath">getValueByXpath</a>
										</li>
										<li>
											<a href="#fmarker-getValuesByXpath">getValuesByXpath</a>
										</li>
										<li>
											<a href="#fmarker-ifNull">ifNull</a>
										</li>
										<li>
											<a href="#fmarker-indexOf">indexOf</a>
										</li>
										<li>
											<a href="#fmarker-intToStr">intToStr</a>
										</li>
										<li>
											<a href="#fmarker-intersectionCount">intersectionCount</a>
										</li>
										<li>
											<a href="#fmarker-isEmpty">isEmpty</a>
										</li>
										<li>
											<a href="#fmarker-isNotEmpty">isNotEmpty</a>
										</li>
										<li>
											<a href="#fmarker-isSubmap">isSubmap</a>
										</li>
										<li>
											<a href="#fmarker-lastIndexOf">lastIndexOf</a>
										</li>
										<li>
											<a href="#fmarker-lower">lower</a>
										</li>
										<li>
											<a href="#fmarker-lstToSet">lstToSet</a>
										</li>
										<li>
											<a href="#fmarker-mapToString">mapToString</a>
										</li>
										<li>
											<a href="#fmarker-now">now</a>
										</li>
										<li>
											<a href="#fmarker-nvl">nvl</a>
										</li>
										<li>
											<a href="#fmarker-parseDate">parseDate</a>
										</li>
										<li>
											<a href="#fmarker-random">random</a>
										</li>
										<li>
											<a href="#fmarker-randomAlpha">randomAlpha</a>
										</li>
										<li>
											<a href="#fmarker-randomAlphaNumeric">randomAlphaNumeric</a>
										</li>
										<li>
											<a href="#fmarker-randomDouble">randomDouble</a>
										</li>
										<li>
											<a href="#fmarker-randomFloat">randomFloat</a>
										</li>
										<li>
											<a href="#fmarker-randomInt">randomInt</a>
										</li>
										<li>
											<a href="#fmarker-randomString">randomString</a>
										</li>
										<li>
											<a href="#fmarker-regexMatches">regexMatches</a>
										</li>
										<li>
											<a href="#fmarker-regexParse">regexParse</a>
										</li>
										<li>
											<a href="#fmarker-regexParseAll">regexParseAll</a>
										</li>
										<li>
											<a href="#fmarker-regexParseMatch">regexParseMatch</a>
										</li>
										<li>
											<a href="#fmarker-resBlob">resBlob</a>
										</li>
										<li>
											<a href="#fmarker-resClob">resClob</a>
										</li>
										<li>
											<a href="#fmarker-setAttr">setAttr</a>
										</li>
										<li>
											<a href="#fmarker-sizeOf">sizeOf</a>
										</li>
										<li>
											<a href="#fmarker-split">split</a>
										</li>
										<li>
											<a href="#fmarker-storeValue">storeValue</a>
										</li>
										<li>
											<a href="#fmarker-strToInt">strToInt</a>
										</li>
										<li>
											<a href="#fmarker-strToList">strToList</a>
										</li>
										<li>
											<a href="#fmarker-substr">substr</a>
										</li>
										<li>
											<a href="#fmarker-toBlob">toBlob</a>
										</li>
										<li>
											<a href="#fmarker-toClob">toClob</a>
										</li>
										<li>
											<a href="#fmarker-toJson">toJson</a>
										</li>
										<li>
											<a href="#fmarker-toReader">toReader</a>
										</li>
										<li>
											<a href="#fmarker-toStream">toStream</a>
										</li>
										<li>
											<a href="#fmarker-toText">toText</a>
										</li>
										<li>
											<a href="#fmarker-today">today</a>
										</li>
										<li>
											<a href="#fmarker-uiDisplayValue">uiDisplayValue</a>
										</li>
										<li>
											<a href="#fmarker-uiElemAttr">uiElemAttr</a>
										</li>
										<li>
											<a href="#fmarker-uiIsPresent">uiIsPresent</a>
										</li>
										<li>
											<a href="#fmarker-uiIsVisible">uiIsVisible</a>
										</li>
										<li>
											<a href="#fmarker-uiValue">uiValue</a>
										</li>
										<li>
											<a href="#fmarker-upper">upper</a>
										</li>
								</ul>
							</li>
							
							<li>
								<a href="#parsers">Expression Types</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
										<li>
											<a href="#parser-attr">attr</a>
										</li>
										<li>
											<a href="#parser-bfile">bfile</a>
										</li>
										<li>
											<a href="#parser-boolean">boolean</a>
										</li>
										<li>
											<a href="#parser-bres">bres</a>
										</li>
										<li>
											<a href="#parser-condition">condition</a>
										</li>
										<li>
											<a href="#parser-date">date</a>
										</li>
										<li>
											<a href="#parser-double">double</a>
										</li>
										<li>
											<a href="#parser-expr">expr</a>
										</li>
										<li>
											<a href="#parser-file">file</a>
										</li>
										<li>
											<a href="#parser-float">float</a>
										</li>
										<li>
											<a href="#parser-int">int</a>
										</li>
										<li>
											<a href="#parser-json">json</a>
										</li>
										<li>
											<a href="#parser-jsonWithType">jsonWithType</a>
										</li>
										<li>
											<a href="#parser-list">list</a>
										</li>
										<li>
											<a href="#parser-long">long</a>
										</li>
										<li>
											<a href="#parser-map">map</a>
										</li>
										<li>
											<a href="#parser-param">param</a>
										</li>
										<li>
											<a href="#parser-prop">prop</a>
										</li>
										<li>
											<a href="#parser-res">res</a>
										</li>
										<li>
											<a href="#parser-set">set</a>
										</li>
										<li>
											<a href="#parser-store">store</a>
										</li>
										<li>
											<a href="#parser-string">string</a>
										</li>
										<li>
											<a href="#parser-uival">uival</a>
										</li>
										<li>
											<a href="#parser-xpath">xpath</a>
										</li>
								</ul>
							</li>
	
							<li>
								<a href="#uiLocators">UI Locators</a>
								
								<ul class="nav nav-stacked" style="margin-left: 10px">
										<li>
											<a href="#locator-class">class</a>
										</li>
										<li>
											<a href="#locator-css">css</a>
										</li>
										<li>
											<a href="#locator-id">id</a>
										</li>
										<li>
											<a href="#locator-js">js</a>
										</li>
										<li>
											<a href="#locator-name">name</a>
										</li>
										<li>
											<a href="#locator-tag">tag</a>
										</li>
										<li>
											<a href="#locator-xpath">xpath</a>
										</li>
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
						
							<div id="grp-Common" class="dataSection2">
								<h2>Common related steps</h2>
								
									<div id="step-Common-assertDeepEquals" class="dataSection3">
										<h3>Assertion assertDeepEquals - assert deep equals</h3>
										
	<p>
		Compares specified values for deep equality. This will not compare the java types, but compares only the structure.
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
			<tr>
				<td>actual</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Actual value in comparison
				</td>
			</tr>
			<tr>
				<td>checkEquality</td>
				<td>boolean</td>
				<td>

					If false, instead of checking for equlity, check will be done for non equality. Default: true
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expected</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value in comparison.
				</td>
			</tr>
			<tr>
				<td>failedPathAttr</td>
				<td>java.lang.String</td>
				<td>

					Failed path, if any, will be set on context with this attribute. Default: failedPath
				</td>
			</tr>
			<tr>
				<td>ignoreExtraProperties</td>
				<td>boolean</td>
				<td>

					If true, extra properties in actual will be ignored and will only ensure expected structure is found in actual object. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-assertEquals" class="dataSection3">
										<h3>Assertion assertEquals - assert equals</h3>
										
	<p>
		Compares specified values for euqality.
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
			<tr>
				<td>actual</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Actual value in comparison
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expected</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value in comparison.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference can be used both for expected and actual<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-equals expected="string:value" actual="string:value" /&gt;<br/>&lt;ccg:assert-equals expected="int:10" actual="prop: attr.intAttr" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertFalse" class="dataSection3">
										<h3>Assertion assertFalse - assert false</h3>
										
	<p>
		Asserts given value is either boolean false or string 'false'
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value to be evaluated
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Using free marker condition in the value<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-false value="condition: attr.intAttr1 gt 5" /&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-false value="prop: attr.flag" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertFileExists" class="dataSection3">
										<h3>Assertion assertFileExists - assert file exists</h3>
										
	<p>
		Validates specified path exists.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>path</td>
				<td>java.lang.String</td>
				<td>

					Path of file to check.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Assert specified file exists<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-file-exists path="test.txt"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertNotEquals" class="dataSection3">
										<h3>Assertion assertNotEquals - assert not equals</h3>
										
	<p>
		Compares specified values for non euqality.
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
			<tr>
				<td>actual</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Actual value in comparison
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expected</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value in comparison.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-not-equals expected="int:11" actual="prop:attr.intAttr" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertNotNull" class="dataSection3">
										<h3>Assertion assertNotNull - assert not null</h3>
										
	<p>
		Asserts the specified value is not null.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value to check.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-not-null value="attr: intAttr" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertNotSame" class="dataSection3">
										<h3>Assertion assertNotSame - assert not same</h3>
										
	<p>
		Asserts specified values are not same reference.
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
			<tr>
				<td>actual</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Actual value in comparison
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expected</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value in comparison.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-not-same expected="prop:attr.intAttr" actual="prop:attr.intAttr1" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertNull" class="dataSection3">
										<h3>Assertion assertNull - assert null</h3>
										
	<p>
		Asserts the value to be null.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value to check.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-null value="prop:attr.intAttrXyz" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertSame" class="dataSection3">
										<h3>Assertion assertSame - assert same</h3>
										
	<p>
		Asserts specified values are same reference.
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
			<tr>
				<td>actual</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Actual value in comparison
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expected</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value in comparison.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-same expected="prop:attr.intAttr" actual="prop:attr.intAttr" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-assertTrue" class="dataSection3">
										<h3>Assertion assertTrue - assert true</h3>
										
	<p>
		Asserts given value is either boolean true or string 'true'
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value to be checked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Any object reference expression can be used<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-true value="condition: attr.intAttr1 gt 5" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-collectionAdd" class="dataSection3">
										<h3>Step collectionAdd - collection add</h3>
										
	<p>
		Adds the specified value to specified collection
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
			<tr>
				<td>collection</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Collection expression to which specified value needs to be added.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value expression which needs to be added to specified collection. Default: null (null will be added to specified collection)
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-collectionRemove" class="dataSection3">
										<h3>Step collectionRemove - collection remove</h3>
										
	<p>
		Removes the specified value / key from specified collection or map
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
			<tr>
				<td>collection</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Collection or expression from which specified value (or key) needs to be removed.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value (or key) expression which needs to be removed from specified collection. Default: null (null will be removed from specified collection)
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-createTempFile" class="dataSection3">
										<h3>Step createTempFile - create temp file</h3>
										
	<p>
		Creates temporary file with specified content.
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
			<tr>
				<td>content</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Content to be written to the file being created.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>pathAttr</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute to use to set the generated file path.
				</td>
			</tr>
			<tr>
				<td>prefix</td>
				<td>java.lang.String</td>
				<td>

					Prefix to be used for generated file. Default: temp
				</td>
			</tr>
			<tr>
				<td>suffix</td>
				<td>java.lang.String</td>
				<td>

					Suffix to be used for generated file. Default: .txt
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-executeCommand" class="dataSection3">
										<h3>Step executeCommand - execute command</h3>
										
	<p>
		Executes specified OS command.
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
			<tr>
				<td>command</td>
				<td>java.lang.String</td>
				<td>

					Command to be executed.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedExitCode</td>
				<td>java.lang.Integer</td>
				<td>

					Expected exit code of command. If specified, exit code will be validated with actual exit code.
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the command, which is used in generating log file. This name will be used to set context attribute with generate log file path.
				</td>
			</tr>
			<tr>
				<td>workingDirectory</td>
				<td>java.lang.String</td>
				<td>

					Directory in which command has to be executed.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-invokeMethod" class="dataSection3">
										<h3>Step invokeMethod - invoke method</h3>
										
	<p>
		Executes specified method on specified bean.
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
			<tr>
				<td>deepCloneObject</td>
				<td>boolean</td>
				<td>

					When set to false, object will not be deep cloned. Which means property expressions if any, will be processed only once.
Default false
				</td>
			</tr>
			<tr>
				<td>deepCloneParams</td>
				<td>boolean</td>
				<td>

					When set to false, parameters will not be deep cloned. Which means property expressions if any, will be processed only once.
Default false
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>isStatic</td>
				<td>boolean</td>
				<td>

					Flag indicating if the method to be invoked is a static method or normal instance method. 
Defaults: false
				</td>
			</tr>
			<tr>
				<td>method</td>
				<td>java.lang.String</td>
				<td>

					Name of the method to be invoked.
				</td>
			</tr>
			<tr>
				<td>object</td>
				<td>java.lang.Object</td>
				<td>

					Object on which method needs to be invoked. For non-static method this is mandatory
				</td>
			</tr>
			<tr>
				<td>objectType</td>
				<td>java.lang.Class</td>
				<td>

					Object on which method needs to be invoked. For static method this is mandatory
				</td>
			</tr>
			<tr>
				<td>paramTypes</td>
				<td>java.lang.String</td>
				<td>

					List of method argument types delimited by comma. Needs to be used when particular method needs to be invoked.
If not specified, method which matches with specified arguments will be invoked.
				</td>
			</tr>
			<tr>
				<td>parameters</td>
				<td>java.util.List</td>
				<td>

					List of parameters to be passed to method.
				</td>
			</tr>
			<tr>
				<td>resultParameter</td>
				<td>java.lang.String</td>
				<td>

					Context parameter name to be used to set the result on context. 
Default: returnValue
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-log" class="dataSection3">
										<h3>Step log - log</h3>
										
	<p>
		Logs specified message. Multiple messages can be specified in single log statement. If non-string or non-primitive values are specified they are converted to json before printing.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>level</td>
				<td>com.yukthitech.autox.test.log.LogLevel</td>
				<td>

					Logging level. Default Value: DEBUG
				</td>
			</tr>
			<tr>
				<td>message</td>
				<td>java.util.List</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Message(s)/object(s) to log
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Logging simple message at default level (DEBUG)<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:log message="This message is from step group"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Logging multiple messages and by using expressions<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:log&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;message&gt;Invoking method using object from app config: &lt;/message&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;message&gt;prop: data.beanFromApp&lt;/message&gt;<br/>&lt;/ccg:log&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Logging at specific level<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:log level="SUMMARY" message="Time taken during test was: 100"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-mapPut" class="dataSection3">
										<h3>Step mapPut - map put</h3>
										
	<p>
		Adds the specified key-value entry to specified map.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>key</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Key expression which needs to be added to specified collection. Default: null (null will be added)
				</td>
			</tr>
			<tr>
				<td>map</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Map expression to which specified entry needs to be added.
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value expression which needs to be added to specified collection. Default: null (null will be added)
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-mkdir" class="dataSection3">
										<h3>Step mkdir - mkdir</h3>
										
	<p>
		Creates a directory with required parent folder as needed in work folder.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Context attribute to which result folder path will be set
				</td>
			</tr>
			<tr>
				<td>path</td>
				<td>java.lang.String</td>
				<td>

					Directory path to create.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Creating dir in work folder<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:mkdir path="tmp/test1" name="tmpDir"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-remove" class="dataSection3">
										<h3>Step remove - remove</h3>
										
	<p>
		Removes the specified context attribute or values matching with specified path.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expression</td>
				<td>java.lang.String</td>
				<td>

					Expression to be used to remove the values. Currently supported expressions: xpath, attr, store
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute to remove.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Removing context attribute<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:remove name="tmpDir"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Removing using xpath expression - key in a map.<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="bean"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;json:<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key1" : "value1",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key2" : "value2",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"arr": [1, 2, 3, 4, 5],<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"strArr": ["one", "two", "three"]<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/value&gt;<br/>&lt;/ccg:set&gt;<br/><br/>&lt;ccg:remove expression="xpath: /attr/bean/key2"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Removing using xpath expression - to remove element in a list using index. Note: In xpath idex starts with 1 (not zero).<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:remove expression="xpath: /attr/bean/arr[2]"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 4 </td>
					<td>
						Removing using xpath expression - to remove element in a list using value.<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:remove expression="xpath: /attr/bean/strArr[contains(., 'two')]"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-sendMail" class="dataSection3">
										<h3>Step sendMail - send mail</h3>
										
	<p>
		Step to send mail, useful to send specific notifications from test cases.
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
			<tr>
				<td>content</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Content to be used for sending mail.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>fromAddress</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Email-Id from which notification should be marked as sent.
				</td>
			</tr>
			<tr>
				<td>password</td>
				<td>java.lang.String</td>
				<td>

					Smtp password to be used for authentication. If not specified, authentication will not be done.
				</td>
			</tr>
			<tr>
				<td>smptpHost</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Smtp host to be used for sending mails
				</td>
			</tr>
			<tr>
				<td>smptpPort</td>
				<td>java.lang.Integer</td>
				<td>

					Smtp port to be used for sending mails.
				</td>
			</tr>
			<tr>
				<td>subject</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Subject to be used for sending mail.
				</td>
			</tr>
			<tr>
				<td>toAddressList</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Space separated address list to which notification should be sent.
				</td>
			</tr>
			<tr>
				<td>ttlsEnabled</td>
				<td>boolean</td>
				<td>

					Flag indicating ttls enabled or not.
				</td>
			</tr>
			<tr>
				<td>userName</td>
				<td>java.lang.String</td>
				<td>

					Smtp user name to be used for authentication. If not specified, authentication will not be done.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Common-set" class="dataSection3">
										<h3>Step set - set</h3>
										
	<p>
		Sets the specified value using specified expression
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expression</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an expression-path. This is similar to <a href="#parsers">Expressions</a>, but are generally used to set the value at specified path.</i></b>
						<BR/>

					Expression to be used to set the value.
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value of the attribute to set. Default: empty string
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Setting context attribute with type<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="intAttr" value="int: 10" /&gt;<br/>&lt;ccg:set expression="attr(int): intAttr1" value="10"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using piped expressions for setting the value<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="propAttr1" value="file:./src/test/resources/data/data1.json | prop: bean1.prop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Setting property (instead of default context attribute)<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="prop: attr.beanForTest.key1" value="newValue1" /&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 4 </td>
					<td>
						Setting property using json<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="beanForTest"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;json:<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key1" : "value1",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key2" : "value2"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/value&gt;<br/>&lt;/ccg:set&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-sleep" class="dataSection3">
										<h3>Step sleep - sleep</h3>
										
	<p>
		Sleeps for specified amount of time.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>time</td>
				<td>java.lang.Long</td>
				<td>

					Time to sleep.
				</td>
			</tr>
			<tr>
				<td>timeUnit</td>
				<td>java.util.concurrent.TimeUnit</td>
				<td>

					Units of time specified. Default: millis
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Sleep for specified number of millis (default time units)<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:sleep time="5000"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Sleep using non-default time units<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:sleep time="10" timeUnit="SECONDS"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-startTimer" class="dataSection3">
										<h3>Step startTimer - start timer</h3>
										
	<p>
		Starts time tracking with specified name. Stopping timer would keep elaspsed time on context which can used for logging.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the timer.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Starting/stopped timer with specified name<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:start-timer name="timeTaken"/&gt;<br/>&lt;ccg:sleep time="10" timeUnit="SECONDS"/&gt;<br/>&lt;ccg:stop-timer name="timeTaken"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Common-stopTimer" class="dataSection3">
										<h3>Step stopTimer - stop timer</h3>
										
	<p>
		Stops the timer and keeps the elapsed time on context.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the timer.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Starting/stopped timer with specified name<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:start-timer name="timeTaken"/&gt;<br/>&lt;ccg:sleep time="10" timeUnit="SECONDS"/&gt;<br/>&lt;ccg:stop-timer name="timeTaken"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
							</div>
							<div id="grp-Lang" class="dataSection2">
								<h2>Lang related steps</h2>
								
									<div id="step-Lang-break" class="dataSection3">
										<h3>Step break - break</h3>
										
	<p>
		Breaks current loop
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Breaking the loop<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:for start="1" end="20"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:if condition="attr.loopVar % 2 != 0"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:continue/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/ccg:if&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="res" value="${attr.res}|${attr.loopVar}"/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:if condition="attr.loopVar gte 10"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:break/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/ccg:if&gt;<br/>&lt;/ccg:for&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-continue" class="dataSection3">
										<h3>Step continue - continue</h3>
										
	<p>
		Continues current loop
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Lang-execute" class="dataSection3">
										<h3>Step execute - execute</h3>
										
	<p>
		Execute specified expression.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enclose</td>
				<td>boolean</td>
				<td>

					If true, encloses the specified expression in ${}. Default: true
				</td>
			</tr>
			<tr>
				<td>expression</td>
				<td>java.lang.String</td>
				<td>

					Expression to execute.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Executing the expression<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:execute expression="${attr.val + 1}"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-fail" class="dataSection3">
										<h3>Step fail - fail</h3>
										
	<p>
		Fails the current test case by throwing fail exception.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>message</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Message ot the fail exception to be thrown
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Failing the test case<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:if condition="attr.loopVar % 2 != 0"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:fail/&gt;<br/>&lt;/ccg:if&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-for" class="dataSection3">
										<h3>Step for - for</h3>
										
	<p>
		Loops through specified range of values and for each iteration executed underlying steps
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>end</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Inclusive end of range.
				</td>
			</tr>
			<tr>
				<td>loopVar</td>
				<td>java.lang.String</td>
				<td>

					Loop variable that will be used to set loop iteration object on context. Default: loopVar
				</td>
			</tr>
			<tr>
				<td>start</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Inclusive start of range.
				</td>
			</tr>
			<tr>
				<td>steps</td>
				<td>com.yukthitech.autox.test.Function</td>
				<td>

					Group of steps/validations to be executed in loop.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Looping through range of numbers<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:for start="1" end="20"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:if condition="attr.loopVar % 2 != 0"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:continue/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/ccg:if&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="res" value="${attr.res}|${attr.loopVar}"/&gt;<br/>&lt;/ccg:for&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-forEach" class="dataSection3">
										<h3>Step forEach - for each</h3>
										
	<p>
		Loops through specified collection, map or string tokens and for each iteration executed underlying steps
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
			<tr>
				<td>delimiter</td>
				<td>java.lang.String</td>
				<td>

					If expression evaluated to string, delimiter to be used to split the string. Default Value: comma (\s*\,\s*)
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expression</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expression which will be evaluated to collection or map or String
				</td>
			</tr>
			<tr>
				<td>ignoreError</td>
				<td>boolean</td>
				<td>

					Ignores error during iteration and continues to next iteration.
				</td>
			</tr>
			<tr>
				<td>loopIdxVar</td>
				<td>java.lang.String</td>
				<td>

					Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar
				</td>
			</tr>
			<tr>
				<td>loopVar</td>
				<td>java.lang.String</td>
				<td>

					Loop variable that will be used to set loop iteration object on context. Default: loopVar
				</td>
			</tr>
			<tr>
				<td>steps</td>
				<td>com.yukthitech.autox.test.Function</td>
				<td>

					Group of steps/validations to be executed in loop.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Looping through list elements<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:for-each expression="attr: checkBoxes" loopVar="cbox"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="values" value="${attr.values},${uiElemAttr('value', attr.cbox, null)}"/&gt;<br/>&lt;/ccg:for-each&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Looping through string tokens using delimter<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:for-each expression="string: a,b,c,d, e,f" delimiter="\s*\,\s*"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:if condition="attr.loopVar == 'b'"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:continue/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/ccg:if&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="res" value="${attr.res}|${attr.loopVar}"/&gt;<br/>&lt;/ccg:for-each&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-function" class="dataSection3">
										<h3>Step function - function</h3>
										
	<p>
		Creates custom function for reusability which can be invoked from other places.
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
			<tr>
				<td>description</td>
				<td>java.lang.String</td>
				<td>

					Description of the function
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the function
				</td>
			</tr>
			<tr>
				<td>returnDescription</td>
				<td>java.lang.String</td>
				<td>

					Return description of the function. If not specified, function is assumed will not return any value
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Lang-functionRef" class="dataSection3">
										<h3>Step functionRef - function ref</h3>
										
	<p>
		Reference step to execute target function with specified parameters.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the function to execute.
				</td>
			</tr>
			<tr>
				<td>params</td>
				<td>java.util.Map</td>
				<td>

					Parameters to be passed to function.
				</td>
			</tr>
			<tr>
				<td>returnAttr</td>
				<td>java.lang.String</td>
				<td>

					Attribute name to be used to specify return value. If not specified, return value will be ignored. Default: null
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Lang-if" class="dataSection3">
										<h3>Step if - if</h3>
										
	<p>
		Evaluates specified condition and if evaluates to true execute 'then' otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
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
			<tr>
				<td>condition</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a "Condition" parameter. This is a freemarker expression that should result in boolean value.</i></b>
						<BR/>

					Freemarker condition to be evaluated.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>else</td>
				<td>com.yukthitech.autox.test.Function</td>
				<td>

					Group of steps/validations to be executed when condition evaluated to be false.
				</td>
			</tr>
			<tr>
				<td>then</td>
				<td>com.yukthitech.autox.test.Function</td>
				<td>

					Group of steps/validations to be executed when condition evaluated to be true.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						If to check condition and execute steps<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:if condition="attr.loopVar % 2 != 0"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:continue/&gt;<br/>&lt;/ccg:if&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						If with else block<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:if condition="attr.flag == 1"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;then&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="ifExec" value="if-then"/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/then&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;else&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="ifExec" value="if-else"/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/else&gt;<br/>&lt;/ccg:if&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-pollAndCheck" class="dataSection3">
										<h3>Assertion pollAndCheck - poll and check</h3>
										
	<p>
		Used to execute polling steps till check condition is met with specified interval gap. Validation will fail if required condition is not met or exceeds timeout.
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
			<tr>
				<td>checkCondition</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a "Condition" parameter. This is a freemarker expression that should result in boolean value.</i></b>
						<BR/>

					Check Freemarker condition to be evaluated.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>poll</td>
				<td>com.yukthitech.autox.test.Function</td>
				<td>

					Group of steps/validations to be executed as part of polling.
				</td>
			</tr>
			<tr>
				<td>pollingInterval</td>
				<td>java.lang.Long</td>
				<td>

					Polling interval duration.
				</td>
			</tr>
			<tr>
				<td>pollingIntervalUnit</td>
				<td>java.util.concurrent.TimeUnit</td>
				<td>

					Polling interval time unit. Defaults to millis.
				</td>
			</tr>
			<tr>
				<td>timeOut</td>
				<td>java.lang.Long</td>
				<td>

					Timout till which check condition will be tried. After this time, this validation will fail.
				</td>
			</tr>
			<tr>
				<td>timeOutUnit</td>
				<td>java.util.concurrent.TimeUnit</td>
				<td>

					Time out time unit. Defaults to millis.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Polling and checking for condition<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:pollAndCheck checkCondition="attr.checkVar gte 5" timeOut="20" timeOutUnit="SECONDS" pollingInterval="500"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;poll&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="checkVar" value="int: ${attr.checkVar + 1}"/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/poll&gt;<br/>&lt;/ccg:pollAndCheck&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-return" class="dataSection3">
										<h3>Step return - return</h3>
										
	<p>
		Returns from current execution. Currently this is supported only in step-group.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Value to be returned.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Returning from a step group<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;step-group name="condSimpleGroup"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:if condition="attr.returnFlag??"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="ifExec"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;string: returnFlag: ${attr.returnFlag}&lt;/value&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/ccg:set&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:return/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/ccg:if&gt;<br/>&lt;/step-group&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Lang-while" class="dataSection3">
										<h3>Step while - while</h3>
										
	<p>
		Loops till specified condition is evaluated to true executed underlying steps
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
			<tr>
				<td>condition</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a "Condition" parameter. This is a freemarker expression that should result in boolean value.</i></b>
						<BR/>

					Freemarker condition to be evaluated.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>steps</td>
				<td>com.yukthitech.autox.test.Function</td>
				<td>

					Group of steps/validations to be executed in loop.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Looping till condition is meeting<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:while condition="attr.i lt 5"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="res" value="${attr.res}|${attr.i}"/&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="i" value="int: ${attr.i + 1}"/&gt;<br/>&lt;/ccg:while&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
							</div>
							<div id="grp-Mock" class="dataSection2">
								<h2>Mock related steps</h2>
								
									<div id="step-Mock-mockFetchRequest" class="dataSection3">
										<h3>Step mockFetchRequest - mock fetch request</h3>
										
	<p>
		Fetches mock request details with specified filter details
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
			<tr>
				<td>attributeName</td>
				<td>java.lang.String</td>
				<td>

					Attribute name to be used to store filtered-request on context.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>methodFilter</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Method filter to be used to fetch mock request.
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the server.
				</td>
			</tr>
			<tr>
				<td>uriFilter</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Uri filter to be used to fetch mock request.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Fetching mock requests received on specified mock-server (from last reser or start)
				with specified request filter criteria.
				
				Below example fetches POST requests from "testMockServer" for uri  "/test/job"<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;s:mock-fetch-request name="testMockServer" attributeName="mockRequests" uriFilter="/test/job" methodFilter="POST"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Mock-mockResponse" class="dataSection3">
										<h3>Step mockResponse - mock response</h3>
										
	<p>
		Mocks the specified request (url + method) with specified response.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>method</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Http method of the request to be mocked
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Name of the server where mocking should be done.
				</td>
			</tr>
			<tr>
				<td>responseBody</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Body of the mocked response
				</td>
			</tr>
			<tr>
				<td>responseHeader</td>
				<td>java.util.Map</td>
				<td>

					Headers to be added to the mock response
				</td>
			</tr>
			<tr>
				<td>responseStatusCode</td>
				<td>java.lang.Integer</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Status code to be sent as part of mock response
				</td>
			</tr>
			<tr>
				<td>times</td>
				<td>int</td>
				<td>

					Number of times for which response should be available for given request. Default: Integer max value
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Request uri to be mocked
				</td>
			</tr>
			<tr>
				<td>waitConfig</td>
				<td>com.yukthitech.autox.test.proxy.steps.WaitConfig</td>
				<td>

					Wait configuration to be used before sending response.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Mocking a response for a POST api<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;s:mock-response method="POST" name="testMockServer" responseStatusCode="200" uri="/test/job"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;response-header name="Content-Type"&gt;application/json&lt;/response-header&gt;<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;responseBody&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"code" : 0,<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"message": "some test message from response"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/responseBody&gt;<br/>&lt;/s:mock-response&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Mock-mockServerReset" class="dataSection3">
										<h3>Step mockServerReset - mock server reset</h3>
										
	<p>
		Resets specified mock server, that is cleaning up all mocked responses and requests..
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Name of the server.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Reseting the mock server, that is cleaning up all mocked responses and requests.<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;s:mock-server-reset name="testMockServer"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Mock-mockServerStart" class="dataSection3">
										<h3>Step mockServerStart - mock server start</h3>
										
	<p>
		Starts Mock Server with specified name and port
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Name of the server.
				</td>
			</tr>
			<tr>
				<td>port</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Port Number on which mock server has to start
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Starting mock server with specified name at specified port<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;s:mock-server-start name="testMockServer" port="9944"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Mock-mockServerStop" class="dataSection3">
										<h3>Step mockServerStop - mock server stop</h3>
										
	<p>
		Stops specified mock server.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Name of the server.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Stopping specified mock server.<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;s:mock-server-stop name="testMockServer"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
							</div>
							<div id="grp-Mongodb" class="dataSection2">
								<h2>Mongodb related steps</h2>
								
									<div id="step-Mongodb-assertMongo" class="dataSection3">
										<h3>Assertion assertMongo - assert mongo</h3>
										
	<p>
		Executes specified mongo Query on specified mongo resource. Syntax of queries can be found at https://docs.mongodb.com/manual/reference/command. And the result is deep-compared with specified expecte object. Extra properties from result are ignored.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expected</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value in comparison.
				</td>
			</tr>
			<tr>
				<td>mongoResourceName</td>
				<td>java.lang.String</td>
				<td>

					Mongo Resource to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Mongodb-mongoMultiQuery" class="dataSection3">
										<h3>Step mongoMultiQuery - mongo multi query</h3>
										
	<p>
		Executes specified multiple mongo Query on specified mongo resource. Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>mongoResourceName</td>
				<td>java.lang.String</td>
				<td>

					Mongo Resource to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>queries</td>
				<td>java.util.List</td>
				<td>

					Query(ies) to execute.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Mongodb-mongoQuery" class="dataSection3">
										<h3>Step mongoQuery - mongo query</h3>
										
	<p>
		Executes specified mongo Query on specified mongo resource. Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>mongoResourceName</td>
				<td>java.lang.String</td>
				<td>

					Mongo Resource to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute.
				</td>
			</tr>
			<tr>
				<td>resultAttribute</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute to be used to set the result.
				</td>
			</tr>
		
	</table>
	
	
									</div>
							</div>
							<div id="grp-Rdbms" class="dataSection2">
								<h2>Rdbms related steps</h2>
								
									<div id="step-Rdbms-sqlAssert" class="dataSection3">
										<h3>Assertion sqlAssert - sql assert</h3>
										
	<p>
		Executes specified query and validates expected data is returned
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
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expectedRows</td>
				<td>java.util.List</td>
				<td>

					Expected rows of values from query result. Each row will have list of column (name-value pairs)
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute whose results needs to be validated.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlAssertValue" class="dataSection3">
										<h3>Assertion sqlAssertValue - sql assert value</h3>
										
	<p>
		Executes specified query and validates only single value (first row - first column value)
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
			<tr>
				<td>convertExpression</td>
				<td>java.lang.String</td>
				<td>

					Expression to be used on query result, before comparision. Default: null
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>expectedValue</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Expected value. Which will compared with value from db.
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute whose results needs to be validated.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlDdlQuery" class="dataSection3">
										<h3>Step sqlDdlQuery - sql ddl query</h3>
										
	<p>
		Executes specified DDL query on specified data source.
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
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Data source to be used for sql execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					DDL query to execute
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlDmlQuery" class="dataSection3">
										<h3>Step sqlDmlQuery - sql dml query</h3>
										
	<p>
		Executes specified DML Query on specified data source.
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
			<tr>
				<td>commitAtEnd</td>
				<td>boolean</td>
				<td>

					If true, calls commit at end. Default: true
				</td>
			</tr>
			<tr>
				<td>countAttribute</td>
				<td>java.lang.String</td>
				<td>

					If specified, number of rows affected will be set on the context
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Data source to be used for sql execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>failOnNoUpdate</td>
				<td>boolean</td>
				<td>

					If true, error will be thrown if no rows are affected by specified DML.
Default Value: false
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlFetchValueQuery" class="dataSection3">
										<h3>Step sqlFetchValueQuery - sql fetch value query</h3>
										
	<p>
		Fetches single value (first row, first column value) from the results of specified query.
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
			<tr>
				<td>contextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute which should be used to keep the result value.
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute, the result's first column will be used to create list.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlLoadQueryColumnList" class="dataSection3">
										<h3>Step sqlLoadQueryColumnList - sql load query column list</h3>
										
	<p>
		Executes specified query and loads the result first column values as list on the context. 
In case of zero results empty list will be kept on context.
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
			<tr>
				<td>contextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute which should be used to keep the result map on the context.
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute, the result's first column will be used to create list.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlLoadQueryMap" class="dataSection3">
										<h3>Step sqlLoadQueryMap - sql load query map</h3>
										
	<p>
		Executes specified query and loads the results as map on context. 
In case of zero results empty map will be kept on context. 
Per row new entry will be added.
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
			<tr>
				<td>contextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute which should be used to keep the result map on the context.
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>keyColumn</td>
				<td>java.lang.String</td>
				<td>

					Results column name whose values should be used as key in result map.
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute, the results will be used to create map.
				</td>
			</tr>
			<tr>
				<td>valueColumn</td>
				<td>java.lang.String</td>
				<td>

					Results column name whose values should be used as value in result map.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlLoadQueryRowBean" class="dataSection3">
										<h3>Step sqlLoadQueryRowBean - sql load query row bean</h3>
										
	<p>
		Executes specified query and loads the results as bean(s) on context. 
In case of zero results empty map will be kept on context. 
Per row new bean will be created.
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
			<tr>
				<td>beanType</td>
				<td>java.lang.Class</td>
				<td>

					Type of bean to which rows should be converted.
				</td>
			</tr>
			<tr>
				<td>contextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute which should be used to keep the result bean on the context.
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>processAllRows</td>
				<td>boolean</td>
				<td>

					If false, only first row will be processed into bean. If true, per row new map will be created and loads of this beans into context.
Default: true
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute, the results will be used to create bean.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlLoadQueryRowMap" class="dataSection3">
										<h3>Step sqlLoadQueryRowMap - sql load query row map</h3>
										
	<p>
		Executes specified query and loads the results as map(s) on context. 
In case of zero results empty map will be kept on context. 
Per row new map will be created.
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
			<tr>
				<td>contextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute which should be used to keep the result map on the context.
				</td>
			</tr>
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Name of the data source to be used for query execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>processAllRows</td>
				<td>boolean</td>
				<td>

					If false, only first row will be processed into map. If true, per row new map will be created and loads of this maps into context.
Default: true
				</td>
			</tr>
			<tr>
				<td>query</td>
				<td>java.lang.String</td>
				<td>

					Query to execute, the results will be used to create map.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Rdbms-sqlMultiDmlQuery" class="dataSection3">
										<h3>Step sqlMultiDmlQuery - sql multi dml query</h3>
										
	<p>
		Executes specified multiple DML queries in single transaction
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
			<tr>
				<td>dataSourceName</td>
				<td>java.lang.String</td>
				<td>

					Data source to be used for sql execution.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>failOnNoUpdate</td>
				<td>boolean</td>
				<td>

					If true, error will be thrown if no rows are affected by specified DML.
Default Value: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
							</div>
							<div id="grp-Rest_Api" class="dataSection2">
								<h2>Rest_Api related steps</h2>
								
									<div id="step-Rest_Api-restInvokeDelete" class="dataSection3">
										<h3>Step restInvokeDelete - rest invoke delete</h3>
										
	<p>
		Used to invoke DELETE api.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking delete api<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-delete uri="/emp/delete/{id}"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;pathVariable name="id"&gt;${attr.emp2_id}&lt;/pathVariable&gt;<br/>&lt;/ccg:rest-invoke-delete&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Rest_Api-restInvokeGet" class="dataSection3">
										<h3>Step restInvokeGet - rest invoke get</h3>
										
	<p>
		Used to invoke GET api.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking get api<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-get uri="/emp/get/{id}"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;pathVariable name="id"&gt;${attr.emp2_id}&lt;/pathVariable&gt;<br/>&lt;/ccg:rest-invoke-get&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Rest_Api-restInvokeGetFile" class="dataSection3">
										<h3>Step restInvokeGetFile - rest invoke get file</h3>
										
	<p>
		Used to invoke GET api and save response as file.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>outputFile</td>
				<td>java.lang.String</td>
				<td>

					Output file where response content should be stored. If not specified, temp file will be used. The output file path will be set response attribute
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking get api to download file<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-get-file uri="/emp/getFile/{id}"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;pathVariable name="id"&gt;${attr.emp3_id}&lt;/pathVariable&gt;<br/>&lt;/ccg:rest-invoke-get-file&gt;<br/><br/>&lt;ccg:set expression="outputContent" value="file(text=true): ${attr.response}"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Rest_Api-restInvokeMultipartPost" class="dataSection3">
										<h3>Step restInvokeMultipartPost - rest invoke multipart post</h3>
										
	<p>
		Used to invoke Multipart POST api.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking post api with attachment<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-multipart-post uri="/emp/saveWithFile"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;part name="details"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name" : "Emp3",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "some address"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/value&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/part&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;attachment name="file" file="file:./src/test/resources/testFile.txt"/&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>&lt;/ccg:rest-invoke-multipart-post&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Rest_Api-restInvokeMultipartPut" class="dataSection3">
										<h3>Step restInvokeMultipartPut - rest invoke multipart put</h3>
										
	<p>
		Used to invoke Multipart PUT api.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking put api with attachment<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-multipart-put uri="/emp/updateWithFile"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;part name="details"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name" : "Emp3",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "some address"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/value&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/part&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;attachment name="file" file="file:./src/test/resources/testFile.txt"/&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>&lt;/ccg:rest-invoke-multipart-put&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Rest_Api-restInvokePost" class="dataSection3">
										<h3>Step restInvokePost - rest invoke post</h3>
										
	<p>
		Used to invoke POST api.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>body</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking post api<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-post uri="/emp/save"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;body&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name" : "Emp2",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "some address"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/body&gt;<br/>&lt;/ccg:rest-invoke-post&gt;<br/><br/>&lt;ccg:set expression="emp2_id" value="attr: response | xpath: //id"/&gt;<br/>&lt;s:assert-equals actual="prop: attr.result.statusCode" expected="int: 200"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Invoking post api as a Form<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-post uri="/emp/saveForm"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;param name="name"&gt;Emp1&lt;/param&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;param name="address"&gt;some address&lt;/param&gt;<br/>&lt;/ccg:rest-invoke-post&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Rest_Api-restInvokePut" class="dataSection3">
										<h3>Step restInvokePut - rest invoke put</h3>
										
	<p>
		Used to invoke PUT api.
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
			<tr>
				<td>baseUrl</td>
				<td>java.lang.String</td>
				<td>

					Base url to be used. If specified, this will be used instead of using base url from plugin.
				</td>
			</tr>
			<tr>
				<td>body</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
				</td>
			</tr>
			<tr>
				<td>contentType</td>
				<td>java.lang.String</td>
				<td>

					Request content type to be used. default: application/json
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedResponseType</td>
				<td>java.lang.Class</td>
				<td>

					Expected response type. Default: java.lang.Object
				</td>
			</tr>
			<tr>
				<td>responseContextAttribure</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which the actaul rest response object will be placed. default: response
				</td>
			</tr>
			<tr>
				<td>resultContextAttribute</td>
				<td>java.lang.String</td>
				<td>

					Context attribute name on which result object will be placed, which can be used to fetch status code. Result will have following properties: <br/>1. statusCode (int) - Http status code obtained from the rest call<br/>2. value (object) - the response object (this can be accessed using 'responseContextAttribure' directly3. headers (Map<String, List<String>>) - Response header. Note: a header can have multiple values.<br/>default: result
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					Uri to be invoked.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Invoking put api<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:rest-invoke-put uri="/emp/update"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;body&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name" : "Emp2",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "some address"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/body&gt;<br/>&lt;/ccg:rest-invoke-put&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
							</div>
							<div id="grp-Ssh" class="dataSection2">
								<h2>Ssh related steps</h2>
								
									<div id="step-Ssh-sshCloseSession" class="dataSection3">
										<h3>Step sshCloseSession - ssh close session</h3>
										
	<p>
		Closes the specified remote session.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>session</td>
				<td>java.lang.String</td>
				<td>

					Name of the session to be closed.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ssh-sshExecuteCommand" class="dataSection3">
										<h3>Step sshExecuteCommand - ssh execute command</h3>
										
	<p>
		Executes specified command on specified session and stores the output on context.
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
			<tr>
				<td>command</td>
				<td>java.lang.String</td>
				<td>

					Command to be executed.
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>exitStatusVar</td>
				<td>java.lang.String</td>
				<td>

					Name of the context attribute on which exit status to be set. Default: exitStatus
				</td>
			</tr>
			<tr>
				<td>outputVar</td>
				<td>java.lang.String</td>
				<td>

					Name of the output context attribute on which output needs to be set. Default: output
				</td>
			</tr>
			<tr>
				<td>session</td>
				<td>java.lang.String</td>
				<td>

					Name of the session on which command needs to be executed.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ssh-sshStartSession" class="dataSection3">
										<h3>Step sshStartSession - ssh start session</h3>
										
	<p>
		Starts a new session with specified details. The session can be accessed in other ssh steps with specified name.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>host</td>
				<td>java.lang.String</td>
				<td>

					Remote host to be connected.
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name for the session being started.
				</td>
			</tr>
			<tr>
				<td>password</td>
				<td>java.lang.String</td>
				<td>

					Password for login. Either of password or private-key is mandatory. If both are provided, password will be given higher preference.
				</td>
			</tr>
			<tr>
				<td>port</td>
				<td>int</td>
				<td>

					Remote host's ssh port. Default: 22
				</td>
			</tr>
			<tr>
				<td>privateKeyPath</td>
				<td>java.lang.String</td>
				<td>

					Private key to be used for login. Either of password or private-key is mandatory. If both are provided, password will be given higher preference.
				</td>
			</tr>
			<tr>
				<td>user</td>
				<td>java.lang.String</td>
				<td>

					User name for login.
				</td>
			</tr>
		
	</table>
	
	
									</div>
							</div>
							<div id="grp-Store" class="dataSection2">
								<h2>Store related steps</h2>
								
									<div id="step-Store-storeDelete" class="dataSection3">
										<h3>Step storeDelete - store delete</h3>
										
	<p>
		Deletes value from store for specified key.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>key</td>
				<td>java.lang.String</td>
				<td>

					Key to be deleted.
				</td>
			</tr>
		
	</table>
	
	
									</div>
							</div>
							<div id="grp-Ui" class="dataSection2">
								<h2>Ui related steps</h2>
								
									<div id="step-Ui-uiAssertFormFields" class="dataSection3">
										<h3>Assertion uiAssertFormFields - ui assert form fields</h3>
										
	<p>
		Validates specified form fields are present
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the form to be validated.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiAssertValue" class="dataSection3">
										<h3>Assertion uiAssertValue - ui assert value</h3>
										
	<p>
		Validates specified element has specified value/text
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to be validated.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.String</td>
				<td>

					Expected value of the element.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiAssertVisibility" class="dataSection3">
										<h3>Assertion uiAssertVisibility - ui assert visibility</h3>
										
	<p>
		Validates specified element is visible/hidden
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>enabled</td>
				<td>java.lang.String</td>
				<td>

					Enables/disables current validation.
Default: true
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to validate
				</td>
			</tr>
			<tr>
				<td>message</td>
				<td>java.lang.String</td>
				<td>

					Message expected in the target element.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>visible</td>
				<td>java.lang.String</td>
				<td>

					Flag indicating if the validation is for visibility or invisibility.
Default: true
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Asserts the visibility of ui element<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-assert-visibility locator="id: downloadLink"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiClick" class="dataSection3">
										<h3>Step uiClick - ui click</h3>
										
	<p>
		Clicks the specified target
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to be clicked. Out of located elements, first element will be clicked.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>postHideLocator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis.
				</td>
			</tr>
			<tr>
				<td>postVerificationDelay</td>
				<td>int</td>
				<td>

					Time to wait to perform post verification in millis. Default: 2000
				</td>
			</tr>
			<tr>
				<td>postVisibilityLocator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Click the element using locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-click locator="id: button"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiClickAndDownload" class="dataSection3">
										<h3>Step uiClickAndDownload - ui click and download</h3>
										
	<p>
		Clicks the specified target and download the result file. If no  file is downloaded, this will throw exception.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>downloadWaitTime</td>
				<td>long</td>
				<td>

					Time to wait for download to complete in millis. Default: 30000
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to be triggered. Out of located elements, first element will be clicked.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>pathName</td>
				<td>java.lang.String</td>
				<td>

					Attribute name which would be set with the downloaded file path.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Clicks a link in ui to download file<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-click-and-download locator="id: downloadLink" pathName="filePath"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiCloseSession" class="dataSection3">
										<h3>Step uiCloseSession - ui close session</h3>
										
	<p>
		Closes the current browser window.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiCloseWindow" class="dataSection3">
										<h3>Step uiCloseWindow - ui close window</h3>
										
	<p>
		Closes the current window.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Click the element using locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-open-window url="/index.html" name="Index"/&gt;<br/>&lt;ccg:ui-switch-window locator="Index"/&gt;<br/>&lt;ccg:ui-is-visible locator="id: button" name="visibFlag1"/&gt;<br/>&lt;ccg:ui-close-window /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiDblClick" class="dataSection3">
										<h3>Step uiDblClick - ui dbl click</h3>
										
	<p>
		Double Clicks the specified target
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to be double-cicked.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>postHideLocator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis.
				</td>
			</tr>
			<tr>
				<td>postVerificationDelay</td>
				<td>int</td>
				<td>

					Time to wait to perform post verification in millis. Default: 2000
				</td>
			</tr>
			<tr>
				<td>postVisibilityLocator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiDragAndDrop" class="dataSection3">
										<h3>Step uiDragAndDrop - ui drag and drop</h3>
										
	<p>
		Drags the specified element to specified target
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
			<tr>
				<td>destination</td>
				<td>java.lang.String</td>
				<td>

					Locator of element on which source element should be dropped
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>source</td>
				<td>java.lang.String</td>
				<td>

					Locator of element which needs to be dragged
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiExecuteJs" class="dataSection3">
										<h3>Step uiExecuteJs - ui execute js</h3>
										
	<p>
		Can be used to execute js code
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>resultAttribute</td>
				<td>java.lang.String</td>
				<td>

					If specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null
				</td>
			</tr>
			<tr>
				<td>script</td>
				<td>java.lang.String</td>
				<td>

					Script to execute
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiFillForm" class="dataSection3">
										<h3>Step uiFillForm - ui fill form</h3>
										
	<p>
		Fills the form with specified data
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
			<tr>
				<td>data</td>
				<td>java.lang.Object</td>
				<td>
						<b><i>This is an <a href="#parsers">Expression param</a></i></b>
						<BR/>

					Data to populate in the form
				</td>
			</tr>
			<tr>
				<td>delay</td>
				<td>int</td>
				<td>

					Delay in millis time between field to field filling. Useful in forms, in which field options are fetched based on other field values. Default: 10
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Html locator of the form or container (like DIV) enclosing the input elements
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiGetElements" class="dataSection3">
										<h3>Step uiGetElements - ui get elements</h3>
										
	<p>
		Fetches value of specified ui element
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the elements to be fetched
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute to set.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Getting elements using locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-get-elements locator="xpath: //input[@type='checkbox']" name="checkBoxes"/&gt;<br/><br/>&lt;ccg:forEach expression="attr.checkBoxes" loopVar="cbox"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;ccg:set expression="values" value="${attr.values},${uiElemAttr('value', attr.cbox, null)}"/&gt;<br/>&lt;/ccg:forEach&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiGetNewWindowHandle" class="dataSection3">
										<h3>Step uiGetNewWindowHandle - ui get new window handle</h3>
										
	<p>
		Used to fetch newly opened window handle.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name by which window can be accessed.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiGetValue" class="dataSection3">
										<h3>Step uiGetValue - ui get value</h3>
										
	<p>
		Fetches value of specified ui element
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>displayValue</td>
				<td>boolean</td>
				<td>

					If set to true, instead of value display value will be fetched (currently non-select fields will return value itself).
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element for which value needs to be fetched
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute to set.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Getting value of specified locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-get-value locator="xpath: //input[@name='statusFld']" name="fldValue"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiGotoPage" class="dataSection3">
										<h3>Step uiGotoPage - ui goto page</h3>
										
	<p>
		Loads page with specified uri
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>uri</td>
				<td>java.lang.String</td>
				<td>

					URI of the page to load
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Filling html form<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-goto-page uri="/index.html" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiGotoUrl" class="dataSection3">
										<h3>Step uiGotoUrl - ui goto url</h3>
										
	<p>
		Loads page with specified url
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>url</td>
				<td>java.lang.String</td>
				<td>

					URL of the page to load
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiHandleAlert" class="dataSection3">
										<h3>Step uiHandleAlert - ui handle alert</h3>
										
	<p>
		Used to validate and click ok of alert prompt.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedMessage</td>
				<td>java.lang.String</td>
				<td>

					Messaged expected in alert. If specified, alert message will be validated with this message.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiHandleConfirm" class="dataSection3">
										<h3>Step uiHandleConfirm - ui handle confirm</h3>
										
	<p>
		Used to validate and click ok/cancel of confirm prompt.
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
			<tr>
				<td>accept</td>
				<td>boolean</td>
				<td>

					Flag used to accept or cancel confirm box. Default: true
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedMessage</td>
				<td>java.lang.String</td>
				<td>

					Messaged expected in alert. If specified, alert message will be validated with this message.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiHandlePrompt" class="dataSection3">
										<h3>Step uiHandlePrompt - ui handle prompt</h3>
										
	<p>
		Used to validate, feed and accept/cancel prompt.
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
			<tr>
				<td>accept</td>
				<td>boolean</td>
				<td>

					Flag used to accept or cancel confirm box. Default: true
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>expectedMessage</td>
				<td>java.lang.String</td>
				<td>

					Messaged expected in alert. If specified, alert message will be validated with this message.
				</td>
			</tr>
			<tr>
				<td>text</td>
				<td>java.lang.String</td>
				<td>

					If specified, feeds the specified text to the prompt
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiIsVisible" class="dataSection3">
										<h3>Step uiIsVisible - ui is visible</h3>
										
	<p>
		Fetches flag indicating if target element is visible or not
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element for which value needs to be fetched
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the attribute to set.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Checking for visibility of element<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-is-visible locator="id: testLayer" name="visibFlag1"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiLoadCookies" class="dataSection3">
										<h3>Step uiLoadCookies - ui load cookies</h3>
										
	<p>
		Loads cookies from specified file into current session cookies.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>path</td>
				<td>java.lang.String</td>
				<td>

					Path of the file where cookies should be loaded from. Default: ./cookies.ser
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiLogScreenShot" class="dataSection3">
										<h3>Step uiLogScreenShot - ui log screen shot</h3>
										
	<p>
		Takes current screen snapshot and adds to the log
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>level</td>
				<td>com.yukthitech.autox.test.log.LogLevel</td>
				<td>

					Logging level. Default Value: DEBUG
				</td>
			</tr>
			<tr>
				<td>message</td>
				<td>java.lang.String</td>
				<td>

					Message to be logged along with image
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the screenshot image file to be created
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Taking screen shot of the screen<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-log-screen-shot name="test.png" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiMoveMouse" class="dataSection3">
										<h3>Step uiMoveMouse - ui move mouse</h3>
										
	<p>
		Moves the mouse to specified target and optionally clicks the element.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>xoffset</td>
				<td>int</td>
				<td>

					Mouse mouse in x-direction by specified amount.
				</td>
			</tr>
			<tr>
				<td>yoffset</td>
				<td>int</td>
				<td>

					Mouse mouse in y-direction by specified amount.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiMoveTo" class="dataSection3">
										<h3>Step uiMoveTo - ui move to</h3>
										
	<p>
		Moves the mouse to specified target and optionally clicks the element.
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
			<tr>
				<td>alignToTop</td>
				<td>boolean</td>
				<td>

					Before moving the element, the element will be aligned to document. This flag indicates if alignment should be to top or bottom. Default: true
				</td>
			</tr>
			<tr>
				<td>click</td>
				<td>boolean</td>
				<td>

					If true, moves the mouse to target and clicks the element. Default: false
				</td>
			</tr>
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to which mouse needs to be moved. Out of located elements, first element will be clicked.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>timeGap</td>
				<td>long</td>
				<td>

					Time gap (in millis) which will be used before clicking and after moving the mouse over the element. Default: 10
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiOpenWindow" class="dataSection3">
										<h3>Step uiOpenWindow - ui open window</h3>
										
	<p>
		Opens new window with specifie name and url.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>name</td>
				<td>java.lang.String</td>
				<td>

					Name of the window being opened.
				</td>
			</tr>
			<tr>
				<td>url</td>
				<td>java.lang.String</td>
				<td>

					Url to be opened.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Opening a new window<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-open-window url="/index.html" name="Index"/&gt;<br/>&lt;ccg:ui-switch-window locator="Index"/&gt;<br/>&lt;ccg:ui-is-visible locator="id: button" name="visibFlag1"/&gt;<br/>&lt;ccg:ui-close-window /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiQuitSession" class="dataSection3">
										<h3>Step uiQuitSession - ui quit session</h3>
										
	<p>
		Quits the driver. In order to user driver again it has to be initialized.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiRefresh" class="dataSection3">
										<h3>Step uiRefresh - ui refresh</h3>
										
	<p>
		Refreshes the current page.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>postHideLocator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis.
				</td>
			</tr>
			<tr>
				<td>postVerificationDelay</td>
				<td>int</td>
				<td>

					Time to wait to perform post verification in millis. Default: 2000
				</td>
			</tr>
			<tr>
				<td>postVisibilityLocator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. Note: Polling for visibility will be done every 100 millis.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiResetSession" class="dataSection3">
										<h3>Step uiResetSession - ui reset session</h3>
										
	<p>
		Resets the driver for usage.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiRightClick" class="dataSection3">
										<h3>Step uiRightClick - ui right click</h3>
										
	<p>
		Right clicks the specified target
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to be triggered. Out of located elements, first element will be clicked.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiSetStyle" class="dataSection3">
										<h3>Step uiSetStyle - ui set style</h3>
										
	<p>
		Used to manipulate the style of the element.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element whose style needs to be modified.
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>styles</td>
				<td>java.util.Map</td>
				<td>

					Styles to be modified.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiSetValue" class="dataSection3">
										<h3>Step uiSetValue - ui set value</h3>
										
	<p>
		Populates specified field with specified value
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator of the element to be populated
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>pressEnterAtEnd</td>
				<td>boolean</td>
				<td>

					If true, an enter-key press will be simulated on target element after populating value. Default: false
				</td>
			</tr>
			<tr>
				<td>value</td>
				<td>java.lang.String</td>
				<td>

					Value to be filled with. Defaults to empty string.
				</td>
			</tr>
		
	</table>
	
		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Setting value of element with specified locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-set-value locator="xpath: //input[@name='statusFld']" value="OPEN"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
									</div>
									<div id="step-Ui-uiStoreCookies" class="dataSection3">
										<h3>Step uiStoreCookies - ui store cookies</h3>
										
	<p>
		Stores the current session cookies into specified file.
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>path</td>
				<td>java.lang.String</td>
				<td>

					Path of the file where cookies should be persisted. Default: ./cookies.ser
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiSwitchFrame" class="dataSection3">
										<h3>Step uiSwitchFrame - ui switch frame</h3>
										
	<p>
		Helps in switching the frames
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>index</td>
				<td>java.lang.Integer</td>
				<td>

					Index of the frame. Either locator or index is mandatory.
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>

					Locator of the frame. Either locator or index is mandatory.
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiSwitchWindow" class="dataSection3">
										<h3>Step uiSwitchWindow - ui switch window</h3>
										
	<p>
		Helps in switching between windows
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>locator</td>
				<td>java.lang.String</td>
				<td>

					Locator of the window. If none is specified, main window will be selected.
				</td>
			</tr>
			<tr>
				<td>newWindow</td>
				<td>boolean</td>
				<td>

					If no locator is specified and this flag is true, switch would be done to newly opened window. New window is determined based on previous SyncWindowHandles invocation (or window other than main window). If no new window is found, an exception would be thrown. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiSyncWindowHandles" class="dataSection3">
										<h3>Step uiSyncWindowHandles - ui sync window handles</h3>
										
	<p>
		Syncs the current open window handles to context. Which can be used to identify 
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
		
	</table>
	
	
									</div>
									<div id="step-Ui-uiWaitFor" class="dataSection3">
										<h3>Step uiWaitFor - ui wait for</h3>
										
	<p>
		Waits for (at least one) specified element to become visible/hidden
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
			<tr>
				<td>disableLogging</td>
				<td>boolean</td>
				<td>

					Flag indicating if logging has to be disabled for current step. Default: false
				</td>
			</tr>
			<tr>
				<td>gapTime</td>
				<td>int</td>
				<td>

					Gap time in millis to wait for between each check. Default: 1000
				</td>
			</tr>
			<tr>
				<td>hidden</td>
				<td>java.lang.String</td>
				<td>

					If true, this step waits for element with specified locator gets removed or hidden.
Default: false
				</td>
			</tr>
			<tr>
				<td>locators</td>
				<td>java.util.List</td>
				<td>
						<b><i>This is a <a href="#uiLocators">UI locator param.</a></i></b>
						<BR/>

					Locator(s) of the element to be waited for
				</td>
			</tr>
			<tr>
				<td>parentElement</td>
				<td>java.lang.String</td>
				<td>

					Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
				</td>
			</tr>
			<tr>
				<td>waitTime</td>
				<td>int</td>
				<td>

					Total wait time in millis for element to become visible or hidden. Default: 60000
				</td>
			</tr>
		
	</table>
	
	
									</div>
							</div>
					</div>

					<div id="fmarkerMethods" class="dataSection1">
						<h1>Free Marker Methods</h1>
						<p>Defines the steps of autox for all functionalities</p>
						
							<div id="fmarker-addDays" class="dataSection2">
								<h2>Free Marker Method - addDays</h2>
	<p>
		Adds specified number of days to specified date
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
			
				<tr>
					<td>date</td>
					<td>java.util.Date</td>
					<td>
						Date to which days should be added
					</td>
				</tr>
				<tr>
					<td>days</td>
					<td>int</td>
					<td>
						Days to be added.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Resultant date after addition of specified days</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-addHours" class="dataSection2">
								<h2>Free Marker Method - addHours</h2>
	<p>
		Adds specified number of hours to specified date
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
			
				<tr>
					<td>date</td>
					<td>java.util.Date</td>
					<td>
						Date to which hours should be added
					</td>
				</tr>
				<tr>
					<td>hours</td>
					<td>int</td>
					<td>
						Hours to be added.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Resultant date after addition of specified hours</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-addMinutes" class="dataSection2">
								<h2>Free Marker Method - addMinutes</h2>
	<p>
		Adds specified number of minutes to specified date
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
			
				<tr>
					<td>date</td>
					<td>java.util.Date</td>
					<td>
						Date to which minutes should be added
					</td>
				</tr>
				<tr>
					<td>minutes</td>
					<td>int</td>
					<td>
						Minutes to be added.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Resultant date after addition of specified minutes</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-addSeconds" class="dataSection2">
								<h2>Free Marker Method - addSeconds</h2>
	<p>
		Adds specified number of seconds to specified date
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
			
				<tr>
					<td>date</td>
					<td>java.util.Date</td>
					<td>
						Date to which seconds should be added
					</td>
				</tr>
				<tr>
					<td>seconds</td>
					<td>int</td>
					<td>
						Seconds to be added.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Resultant date after addition of specified seconds</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-collectionToString" class="dataSection2">
								<h2>Free Marker Method - collectionToString</h2>
	<p>
		Converts collection of objects into string.
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
			
				<tr>
					<td>lst</td>
					<td>java.util.Collection</td>
					<td>
						Collection to be converted
					</td>
				</tr>
				<tr>
					<td>prefix</td>
					<td>java.lang.String</td>
					<td>
						Prefix to be used at start of coverted string
					</td>
				</tr>
				<tr>
					<td>delimiter</td>
					<td>java.lang.String</td>
					<td>
						Delimiter to be used between the collection elements
					</td>
				</tr>
				<tr>
					<td>suffix</td>
					<td>java.lang.String</td>
					<td>
						Suffix to be used at end of converted string
					</td>
				</tr>
				<tr>
					<td>emptyString</td>
					<td>java.lang.String</td>
					<td>
						String to be used when input list is null or empty
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted string</td>
			</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						collectionToString(lst, '[', ' | ', ']', '')<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							[a | b | c]
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						collectionToString(null, '[', ' | ', ']', '<empty>')<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;empty&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="fmarker-compare" class="dataSection2">
								<h2>Free Marker Method - compare</h2>
	<p>
		Compares the specified values and returns the comparision result as int.
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
			
				<tr>
					<td>value1</td>
					<td>java.lang.Object</td>
					<td>
						Value1 to compare
					</td>
				</tr>
				<tr>
					<td>value2</td>
					<td>java.lang.Object</td>
					<td>
						Value2 to compare
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>Comparision result.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-compareAndGet" class="dataSection2">
								<h2>Free Marker Method - compareAndGet</h2>
	<p>
		Used to compare specified attribute with specified value and return appropiate result.
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
			
				<tr>
					<td>name</td>
					<td>java.lang.String</td>
					<td>
						Name of the attribute to check
					</td>
				</tr>
				<tr>
					<td>value</td>
					<td>java.lang.String</td>
					<td>
						Expected value of the attribute
					</td>
				</tr>
				<tr>
					<td>trueVal</td>
					<td>java.lang.String</td>
					<td>
						Value to be returned when the attribute value match with specified value
					</td>
				</tr>
				<tr>
					<td>falseVal</td>
					<td>java.lang.String</td>
					<td>
						Value to be returned when the attribute value DOES NOT match with specified value
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>True or false value based on match.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-countOfXpath" class="dataSection2">
								<h2>Free Marker Method - countOfXpath</h2>
	<p>
		Fetches the count of values matching with specified xpath from specified source object.
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
			
				<tr>
					<td>source</td>
					<td>java.lang.Object</td>
					<td>
						Source object on which xpath should be evaluated
					</td>
				</tr>
				<tr>
					<td>xpath</td>
					<td>java.lang.String</td>
					<td>
						Xpath to be evaluated
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>Number of values matching with specified xpath</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-dateToStr" class="dataSection2">
								<h2>Free Marker Method - dateToStr</h2>
	<p>
		Converts specified date into string in specified format.
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
			
				<tr>
					<td>date</td>
					<td>java.util.Date</td>
					<td>
						Date to be converted
					</td>
				</tr>
				<tr>
					<td>format</td>
					<td>java.lang.String</td>
					<td>
						Date format to which date should be converted
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Fromated date string.</td>
			</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						dateToStr(date, 'MM/dd/yyy')<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							20/20/2018
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="fmarker-escape" class="dataSection2">
								<h2>Free Marker Method - escape</h2>
	<p>
		Removes special characters and coverts result into json string (enclosed in double quotes)
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
			
				<tr>
					<td>str</td>
					<td>java.lang.String</td>
					<td>
						String to be converted
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted string</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-fullPath" class="dataSection2">
								<h2>Free Marker Method - fullPath</h2>
	<p>
		Converts input file path (Can be relative, partial path) to full canonical path.
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
			
				<tr>
					<td>path</td>
					<td>java.lang.String</td>
					<td>
						Path to be converted
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Canonical path of the specified path</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-getValueByXpath" class="dataSection2">
								<h2>Free Marker Method - getValueByXpath</h2>
	<p>
		Fetches the value for specified xpath from specified source object.
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
			
				<tr>
					<td>source</td>
					<td>java.lang.Object</td>
					<td>
						Source object on which xpath needs to be evaluated
					</td>
				</tr>
				<tr>
					<td>xpath</td>
					<td>java.lang.String</td>
					<td>
						Xpath to be evaluated
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.Object</td>
				
				<th class="doc_heading"> Description </th>
				<td>Value of specified xpath</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-getValuesByXpath" class="dataSection2">
								<h2>Free Marker Method - getValuesByXpath</h2>
	<p>
		Fetches the value(s) list for specified xpath from specified source object.
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
			
				<tr>
					<td>source</td>
					<td>java.lang.Object</td>
					<td>
						Source object on which xpath needs to be evaluated
					</td>
				</tr>
				<tr>
					<td>xpath</td>
					<td>java.lang.String</td>
					<td>
						Xpath to be evaluated
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.List</td>
				
				<th class="doc_heading"> Description </th>
				<td>Value of specified xpath</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-ifNull" class="dataSection2">
								<h2>Free Marker Method - ifNull</h2>
	<p>
		If 'nullCheck' is null, 'ifNull' will be returned otherwise 'ifNotNull' will be returned.
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
			
				<tr>
					<td>nullCheck</td>
					<td>java.lang.Object</td>
					<td>
						object to be checked for null
					</td>
				</tr>
				<tr>
					<td>ifNull</td>
					<td>java.lang.Object</td>
					<td>
						object to be returned if null
					</td>
				</tr>
				<tr>
					<td>ifNotNull</td>
					<td>java.lang.Object</td>
					<td>
						object to be returned if not null
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.Object</td>
				
				<th class="doc_heading"> Description </th>
				<td>ifNull or ifNotNull based on nullCheck.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-indexOf" class="dataSection2">
								<h2>Free Marker Method - indexOf</h2>
	<p>
		Finds the first index of specified substring in specified string.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String in which substring needs to be searched
					</td>
				</tr>
				<tr>
					<td>substr</td>
					<td>java.lang.String</td>
					<td>
						Substring that needs to be searched
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>index of subbstring. If not found -1.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-intToStr" class="dataSection2">
								<h2>Free Marker Method - intToStr</h2>
	<p>
		Converts specified int value to string using specified radix.
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
			
				<tr>
					<td>value</td>
					<td>int</td>
					<td>
						Int value to be converted
					</td>
				</tr>
				<tr>
					<td>radix</td>
					<td>int</td>
					<td>
						Radix to be used for conversion
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Result substring.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-intersectionCount" class="dataSection2">
								<h2>Free Marker Method - intersectionCount</h2>
	<p>
		Evaluates the intersection size of specified collections.
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
			
				<tr>
					<td>collection1</td>
					<td>java.util.Collection</td>
					<td>
						Collection one to be checked
					</td>
				</tr>
				<tr>
					<td>collection2</td>
					<td>java.util.Collection</td>
					<td>
						Collection two to be checked
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>intersection size of collections</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-isEmpty" class="dataSection2">
								<h2>Free Marker Method - isEmpty</h2>
	<p>
		Used to check if specified value is empty. For collection, map and string, along with null this will check for empty value.
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
			
				<tr>
					<td>value</td>
					<td>java.lang.Object</td>
					<td>
						Value to be checked for empty
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>boolean</td>
				
				<th class="doc_heading"> Description </th>
				<td>True if value is empty.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-isNotEmpty" class="dataSection2">
								<h2>Free Marker Method - isNotEmpty</h2>
	<p>
		Used to check if specified value is not empty. For collection, map and string, along with non-null this will check for non-empty value.
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
			
				<tr>
					<td>value</td>
					<td>java.lang.Object</td>
					<td>
						Value to be checked for empty
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>boolean</td>
				
				<th class="doc_heading"> Description </th>
				<td>True if value is empty.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-isSubmap" class="dataSection2">
								<h2>Free Marker Method - isSubmap</h2>
	<p>
		Checks if specified submap is submap of supermap
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
			
				<tr>
					<td>superMap</td>
					<td>java.util.Map</td>
					<td>
						Super-set map in which submap has to be checked
					</td>
				</tr>
				<tr>
					<td>superMap</td>
					<td>java.util.Map</td>
					<td>
						Sub-set map which needs to be checked
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>boolean</td>
				
				<th class="doc_heading"> Description </th>
				<td>true if comparision is susccessful.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-lastIndexOf" class="dataSection2">
								<h2>Free Marker Method - lastIndexOf</h2>
	<p>
		Finds the last index of specified substring in specified string.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String in which substring needs to be searched
					</td>
				</tr>
				<tr>
					<td>substr</td>
					<td>java.lang.String</td>
					<td>
						Substring that needs to be searched
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>index of subbstring. If not found -1.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-lower" class="dataSection2">
								<h2>Free Marker Method - lower</h2>
	<p>
		Converts the specified string to lower case.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String to be converted
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Lower cased substring.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-lstToSet" class="dataSection2">
								<h2>Free Marker Method - lstToSet</h2>
	<p>
		Converts specified list into set.
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
			
				<tr>
					<td>list</td>
					<td>java.util.List</td>
					<td>
						List to be converted
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Set</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted set.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-mapToString" class="dataSection2">
								<h2>Free Marker Method - mapToString</h2>
	<p>
		Converts map of objects into string.
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
			
				<tr>
					<td>map</td>
					<td>java.util.Map</td>
					<td>
						Prefix to be used at start of coverted string
					</td>
				</tr>
				<tr>
					<td>template</td>
					<td>java.lang.String</td>
					<td>
						Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders)
					</td>
				</tr>
				<tr>
					<td>prefix</td>
					<td>java.lang.String</td>
					<td>
						Prefix to be used at start of coverted string
					</td>
				</tr>
				<tr>
					<td>delimiter</td>
					<td>java.lang.String</td>
					<td>
						Delimiter to be used between elements.
					</td>
				</tr>
				<tr>
					<td>suffix</td>
					<td>java.lang.String</td>
					<td>
						Suffix to be used at end of string.
					</td>
				</tr>
				<tr>
					<td>emptyString</td>
					<td>java.lang.String</td>
					<td>
						String that will be returned if input map is null or empty.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted string</td>
			</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						mapToString(map, '#key=#value', '[', ' | ', ']', '')<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							[a=1 | b=2 | c=3]
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;empty&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="fmarker-now" class="dataSection2">
								<h2>Free Marker Method - now</h2>
	<p>
		Returns the current date object
	</p>
	

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Current date and time</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-nvl" class="dataSection2">
								<h2>Free Marker Method - nvl</h2>
	<p>
		Used to check if specified value is null and return approp value when null and when non-null.
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
			
				<tr>
					<td>value</td>
					<td>java.lang.Object</td>
					<td>
						Value to be checked for empty
					</td>
				</tr>
				<tr>
					<td>nullValue</td>
					<td>java.lang.Object</td>
					<td>
						Value to be returned when value is null
					</td>
				</tr>
				<tr>
					<td>nonNullValue</td>
					<td>java.lang.Object</td>
					<td>
						Value to be returned when value is non-null
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.Object</td>
				
				<th class="doc_heading"> Description </th>
				<td>Specified null-condition-value or non-null-condition-value.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-parseDate" class="dataSection2">
								<h2>Free Marker Method - parseDate</h2>
	<p>
		Parses string to date using specified format.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String to parse
					</td>
				</tr>
				<tr>
					<td>format</td>
					<td>java.lang.String</td>
					<td>
						Format to be used for parsing
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Parsed date object</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-random" class="dataSection2">
								<h2>Free Marker Method - random</h2>
	<p>
		Generates random int.
	</p>
	

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random number</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-randomAlpha" class="dataSection2">
								<h2>Free Marker Method - randomAlpha</h2>
	<p>
		Generates random alpha string with specified prefix.
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
			
				<tr>
					<td>prefix</td>
					<td>java.lang.String</td>
					<td>
						Prefix that will added to generated random string
					</td>
				</tr>
				<tr>
					<td>length</td>
					<td>int</td>
					<td>
						Expected length of resulting random string
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random string</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-randomAlphaNumeric" class="dataSection2">
								<h2>Free Marker Method - randomAlphaNumeric</h2>
	<p>
		Generates random alpha-numeric string with specified prefix.
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
			
				<tr>
					<td>prefix</td>
					<td>java.lang.String</td>
					<td>
						Prefix that will added to generated random string
					</td>
				</tr>
				<tr>
					<td>length</td>
					<td>int</td>
					<td>
						Expected length of resulting random string
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random string</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-randomDouble" class="dataSection2">
								<h2>Free Marker Method - randomDouble</h2>
	<p>
		Generates random double in given range.
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
			
				<tr>
					<td>min</td>
					<td>double</td>
					<td>
						Min value of the expected range
					</td>
				</tr>
				<tr>
					<td>max</td>
					<td>double</td>
					<td>
						Max value of the expected range
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>double</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random number</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-randomFloat" class="dataSection2">
								<h2>Free Marker Method - randomFloat</h2>
	<p>
		Generates random float in given range.
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
			
				<tr>
					<td>min</td>
					<td>float</td>
					<td>
						Min value of the expected range
					</td>
				</tr>
				<tr>
					<td>max</td>
					<td>float</td>
					<td>
						Max value of the expected range
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>float</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random number</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-randomInt" class="dataSection2">
								<h2>Free Marker Method - randomInt</h2>
	<p>
		Generates random int in given range.
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
			
				<tr>
					<td>min</td>
					<td>int</td>
					<td>
						Min value of the expected range
					</td>
				</tr>
				<tr>
					<td>max</td>
					<td>int</td>
					<td>
						Max value of the expected range
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random number</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-randomString" class="dataSection2">
								<h2>Free Marker Method - randomString</h2>
	<p>
		Generates random string with specified prefix (based on timestamp).
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
			
				<tr>
					<td>prefix</td>
					<td>java.lang.String</td>
					<td>
						Prefix that will added to generated random string
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Random string</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-regexMatches" class="dataSection2">
								<h2>Free Marker Method - regexMatches</h2>
	<p>
		Checks wether specified content is matching with specified regex.
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
			
				<tr>
					<td>content</td>
					<td>java.lang.String</td>
					<td>
						String which needs to be evaluated aganist regex
					</td>
				</tr>
				<tr>
					<td>regex</td>
					<td>java.lang.String</td>
					<td>
						Regex to be used
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>boolean</td>
				
				<th class="doc_heading"> Description </th>
				<td>True if specified content is matching with specified regex</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-regexParse" class="dataSection2">
								<h2>Free Marker Method - regexParse</h2>
	<p>
		Using specified regex tries to find fist match in given content, from that extracts the groups in specified regex.
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
			
				<tr>
					<td>content</td>
					<td>java.lang.String</td>
					<td>
						String in which specified regex match needs to be found
					</td>
				</tr>
				<tr>
					<td>regex</td>
					<td>java.lang.String</td>
					<td>
						Regex with group names to be extracted from match
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Map</td>
				
				<th class="doc_heading"> Description </th>
				<td>From the first match map using group name in regex as key and group value from the match as value</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-regexParseAll" class="dataSection2">
								<h2>Free Marker Method - regexParseAll</h2>
	<p>
		Using specified regex finds all matches for given content, from that extracts the groups in specified regex.
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
			
				<tr>
					<td>content</td>
					<td>java.lang.String</td>
					<td>
						String in which specified regex match needs to be found
					</td>
				</tr>
				<tr>
					<td>regex</td>
					<td>java.lang.String</td>
					<td>
						Regex with group names to be extracted from match
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.List</td>
				
				<th class="doc_heading"> Description </th>
				<td>From all matches list of maps are returned. Map uses group name in regex as key and group value from the match as value</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-regexParseMatch" class="dataSection2">
								<h2>Free Marker Method - regexParseMatch</h2>
	<p>
		Using specified regex tries to check if content is matched, from that extracts the groups in specified regex.
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
			
				<tr>
					<td>content</td>
					<td>java.lang.String</td>
					<td>
						String in which specified regex match needs to be found
					</td>
				</tr>
				<tr>
					<td>regex</td>
					<td>java.lang.String</td>
					<td>
						Regex with group names to be extracted from match
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Map</td>
				
				<th class="doc_heading"> Description </th>
				<td>From the match map using group name in regex as key and group value from the match as value</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-resBlob" class="dataSection2">
								<h2>Free Marker Method - resBlob</h2>
	<p>
		Used to load specified resource as blob object.
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
			
				<tr>
					<td>res</td>
					<td>java.lang.String</td>
					<td>
						Resource that needs to be converted to blob
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.sql.Blob</td>
				
				<th class="doc_heading"> Description </th>
				<td>Loaded blob object.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-resClob" class="dataSection2">
								<h2>Free Marker Method - resClob</h2>
	<p>
		Used to load specified resource as clob object.
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
			
				<tr>
					<td>res</td>
					<td>java.lang.String</td>
					<td>
						Resource to be converted into clob
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.sql.Clob</td>
				
				<th class="doc_heading"> Description </th>
				<td>Loaded blob object.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-setAttr" class="dataSection2">
								<h2>Free Marker Method - setAttr</h2>
	<p>
		Used to set value as content attribute. This function will always return empty string.
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
			
				<tr>
					<td>arg0</td>
					<td>java.lang.String</td>
					<td>
						
					</td>
				</tr>
				<tr>
					<td>arg1</td>
					<td>java.lang.Object</td>
					<td>
						
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Always empty string.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-sizeOf" class="dataSection2">
								<h2>Free Marker Method - sizeOf</h2>
	<p>
		Used to fetch size of specified value. If string length of string is returned, if collection size of collection is returned, if null zero will be returned. Otherwise 1 will be returned.
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
			
				<tr>
					<td>value</td>
					<td>java.lang.Object</td>
					<td>
						Value whose size to be determined
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>Size of specified object.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-split" class="dataSection2">
								<h2>Free Marker Method - split</h2>
	<p>
		Splits the given string into list of strings using specified delimiter.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String to parse
					</td>
				</tr>
				<tr>
					<td>delimiter</td>
					<td>java.lang.String</td>
					<td>
						Delimiter to be used for spliting
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.List</td>
				
				<th class="doc_heading"> Description </th>
				<td>List of string resulted from spliting.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-storeValue" class="dataSection2">
								<h2>Free Marker Method - storeValue</h2>
	<p>
		Fetches the value of specified key from the store.
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
			
				<tr>
					<td>key</td>
					<td>java.lang.String</td>
					<td>
						Key of value to be fetched
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.Object</td>
				
				<th class="doc_heading"> Description </th>
				<td>Matched value</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-strToInt" class="dataSection2">
								<h2>Free Marker Method - strToInt</h2>
	<p>
		Converts specified string to int using specified radix.
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
			
				<tr>
					<td>value</td>
					<td>java.lang.String</td>
					<td>
						String value to be converted
					</td>
				</tr>
				<tr>
					<td>radix</td>
					<td>int</td>
					<td>
						Radix to be used for conversion
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>int</td>
				
				<th class="doc_heading"> Description </th>
				<td>Result int value.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-strToList" class="dataSection2">
								<h2>Free Marker Method - strToList</h2>
	<p>
		Converts specified string into list by splitting it using specified delimiter.
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
			
				<tr>
					<td>str</td>
					<td>java.lang.String</td>
					<td>
						String to be converted
					</td>
				</tr>
				<tr>
					<td>delim</td>
					<td>java.lang.String</td>
					<td>
						Delimiter to be used
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.List</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted list.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-substr" class="dataSection2">
								<h2>Free Marker Method - substr</h2>
	<p>
		Substring of speicifed string with specified range.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String from which substring needs to be extracted
					</td>
				</tr>
				<tr>
					<td>start</td>
					<td>int</td>
					<td>
						Start from which substring
					</td>
				</tr>
				<tr>
					<td>string</td>
					<td>int</td>
					<td>
						End index of substring. If negative value is specified, this will be not be used.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Result substring.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-toBlob" class="dataSection2">
								<h2>Free Marker Method - toBlob</h2>
	<p>
		Used to convert specified data into a blob object. Supported parameter types - CharSequence, byte[], Serializable, InputStream.
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
			
				<tr>
					<td>input</td>
					<td>java.lang.Object</td>
					<td>
						Input that needs to be converted to blob
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.sql.Blob</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted blob object.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-toClob" class="dataSection2">
								<h2>Free Marker Method - toClob</h2>
	<p>
		Used to convert specified data into a clob. Supported parameter types - CharSequence, byte[], InputStream, Reader.
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
			
				<tr>
					<td>input</td>
					<td>java.lang.Object</td>
					<td>
						Input that needs to be converted to clob
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.sql.Clob</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted input stream.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-toJson" class="dataSection2">
								<h2>Free Marker Method - toJson</h2>
	<p>
		Used to convert specified object into json string.
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
			
				<tr>
					<td>value</td>
					<td>java.lang.Object</td>
					<td>
						Value to be converted into json string.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted json string.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-toReader" class="dataSection2">
								<h2>Free Marker Method - toReader</h2>
	<p>
		Used to convert specified data into a reader. Supported parameter types - CharSequence, byte[].
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
			
				<tr>
					<td>input</td>
					<td>java.lang.Object</td>
					<td>
						Input that needs to be converted to reader
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.io.Reader</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted input stream.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-toStream" class="dataSection2">
								<h2>Free Marker Method - toStream</h2>
	<p>
		Used to convert specified data into a input stream. Supported parameter types - CharSequence, byte[].
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
			
				<tr>
					<td>input</td>
					<td>java.lang.Object</td>
					<td>
						Input that needs to be converted to input stream
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.io.InputStream</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted input stream.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-toText" class="dataSection2">
								<h2>Free Marker Method - toText</h2>
	<p>
		Used to convert specified object into string. toString() will be invoked on input object to convert
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
			
				<tr>
					<td>value</td>
					<td>java.lang.Object</td>
					<td>
						Value to be converted into string.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Converted string. If null, 'null' will be returned.</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-today" class="dataSection2">
								<h2>Free Marker Method - today</h2>
	<p>
		Returns the current date object
	</p>
	

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.util.Date</td>
				
				<th class="doc_heading"> Description </th>
				<td>Current date</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-uiDisplayValue" class="dataSection2">
								<h2>Free Marker Method - uiDisplayValue</h2>
	<p>
		Fetches display value of specified locator. For select, option label will be fetched. If element is not Select, its ui value will be fetched.
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
			
				<tr>
					<td>locator</td>
					<td>java.lang.Object</td>
					<td>
						Locator of the ui element whose display value needs to be fetched.
					</td>
				</tr>
				<tr>
					<td>parent</td>
					<td>java.lang.String</td>
					<td>
						Optional. Context attribute name which should hold parent web element.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Value of the ui element</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-uiElemAttr" class="dataSection2">
								<h2>Free Marker Method - uiElemAttr</h2>
	<p>
		Fetches attribute value of specified locator.
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
			
				<tr>
					<td>attrName</td>
					<td>java.lang.String</td>
					<td>
						Name of the attribute whose value to be fetched.
					</td>
				</tr>
				<tr>
					<td>locator</td>
					<td>java.lang.Object</td>
					<td>
						Locator of the ui element whose attribute value needs to be fetched.
					</td>
				</tr>
				<tr>
					<td>parent</td>
					<td>java.lang.String</td>
					<td>
						Optional. Context attribute name which should hold parent web element.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Value of the ui element attribute</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-uiIsPresent" class="dataSection2">
								<h2>Free Marker Method - uiIsPresent</h2>
	<p>
		Checks if specified element is present or not (need not be visible).
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
			
				<tr>
					<td>locator</td>
					<td>java.lang.String</td>
					<td>
						Locator of the ui element whose attribute value needs to be fetched.
					</td>
				</tr>
				<tr>
					<td>parent</td>
					<td>java.lang.String</td>
					<td>
						Optional. Context attribute name which should hold parent web element.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>boolean</td>
				
				<th class="doc_heading"> Description </th>
				<td>True if element is available (need not be visible)</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-uiIsVisible" class="dataSection2">
								<h2>Free Marker Method - uiIsVisible</h2>
	<p>
		Checks if specified element is visible or not.
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
			
				<tr>
					<td>locator</td>
					<td>java.lang.Object</td>
					<td>
						Locator of the ui element whose attribute value needs to be fetched.
					</td>
				</tr>
				<tr>
					<td>parent</td>
					<td>java.lang.String</td>
					<td>
						Optional. Context attribute name which should hold parent web element.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>boolean</td>
				
				<th class="doc_heading"> Description </th>
				<td>True if element is visible</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-uiValue" class="dataSection2">
								<h2>Free Marker Method - uiValue</h2>
	<p>
		Fetches value of specified locator. If element is text/textarea, its ui value will be fetched.
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
			
				<tr>
					<td>locator</td>
					<td>java.lang.Object</td>
					<td>
						Locator of the ui element whose element needs to be fetched.
					</td>
				</tr>
				<tr>
					<td>parent</td>
					<td>java.lang.String</td>
					<td>
						Optional. Context attribute name which should hold parent web element.
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Value of the ui element</td>
			</tr>
		</table>

	
							</div>
							<div id="fmarker-upper" class="dataSection2">
								<h2>Free Marker Method - upper</h2>
	<p>
		Converts the specified string to upper case.
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
			
				<tr>
					<td>string</td>
					<td>java.lang.String</td>
					<td>
						String to be converted
					</td>
				</tr>
		</table>

		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="4"  class="doc_mainHeading">
					Return Details
				</th>
			</tr>
			<tr>
				<th class="doc_heading"> Type </th>
				<td>java.lang.String</td>
				
				<th class="doc_heading"> Description </th>
				<td>Lower cased substring.</td>
			</tr>
		</table>

	
							</div>
					</div>
						
					<div id="parsers" class="dataSection1">
						<h1>Expression Types</h1>
						<p>Defines the steps of autox for all functionalities</p>
						
							<div id="parser-attr" class="dataSection2">
								<h2>Expression Type - attr</h2>
	<p>
		Parses specified expression as context attribute.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							attr: attrName
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using attr with type while setting attribute value. Below example sets value 10 (integer) on context with name intAttr1<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="attr(int): intAttr1" value="10"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using attr to access context attribute value<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-not-null value="attr: intAttr" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-bfile" class="dataSection2">
								<h2>Expression Type - bfile</h2>
	<p>
		Parses specified expression as file path and loads it as binary data (byte array).
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							bfile: /tmp/data
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-boolean" class="dataSection2">
								<h2>Expression Type - boolean</h2>
	<p>
		Parses specified expression into boolean. If expression value is true (case insensitive), then result will be true.  In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							boolean: True
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using to convert string into boolean and set Boolean object on context<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="booleanAttr" value="boolean: true"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using to convert string into boolean and set Boolean object on context<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="booleanAttr" value="boolean: true"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-bres" class="dataSection2">
								<h2>Expression Type - bres</h2>
	<p>
		Parses specified expression as resource path and loads it as binary data (byte array).
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							res: /tmp/data.json
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-condition" class="dataSection2">
								<h2>Expression Type - condition</h2>
	<p>
		Evaluates specified expression as condition and resultant boolean value will be returned
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							condition: (attr.flag == true)
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using condition in assertion to check simple conditions<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-true value="condition: attr.intAttr1 gt 5" /&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using condition in assertion to check simple conditions<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-true value="condition: attr.intAttr1 gt 5" /&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-date" class="dataSection2">
								<h2>Expression Type - date</h2>
	<p>
		Parses specified expression into date. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							date: 21/3/2018, date(format=MM/dd/yyy): 3/21/2018
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-double" class="dataSection2">
								<h2>Expression Type - double</h2>
	<p>
		Parses specified expression into double. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							double: 10.2
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-expr" class="dataSection2">
								<h2>Expression Type - expr</h2>
	<p>
		Evaluates specified expression as freemarker expression and resultant value will be returned
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							expr: today()
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-file" class="dataSection2">
								<h2>Expression Type - file</h2>
	<p>
		Parses specified expression as file path and loads it as object. Supported file types: xml, json, properties
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							file: /tmp/data.json
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using file to load file as object and extracting xpath value out of it<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr1" value="file:./src/test/resources/data/data1.json | xpath: //bean1/prop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Loading file as simple text (instead of loading as object)<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="outputContent" value="file(text=true): ${attr.response}"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 4 </td>
					<td>
						Using file to load file as object and extracting xpath value out of it<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr1" value="file:./src/test/resources/data/data1.json | xpath: //bean1/prop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 5 </td>
					<td>
						Loading file as simple text (instead of loading as object)<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="outputContent" value="file(text=true): ${attr.response}"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-float" class="dataSection2">
								<h2>Expression Type - float</h2>
	<p>
		Parses specified expression into float. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							float: 10.2
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-int" class="dataSection2">
								<h2>Expression Type - int</h2>
	<p>
		Parses specified expression into int. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							int: 10
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-json" class="dataSection2">
								<h2>Expression Type - json</h2>
	<p>
		Parses specified expression as json string and loads it as object. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							json: {"a": 2, "b": 3}
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Converting json into object<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="beanForTest"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;json:<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key1" : "value1",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key2" : "value2"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/value&gt;<br/>&lt;/ccg:set&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Converting json into object and setting it on context<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="beanForTest"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;json:<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key1" : "value1",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key2" : "value2"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/value&gt;<br/>&lt;/ccg:set&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-jsonWithType" class="dataSection2">
								<h2>Expression Type - jsonWithType</h2>
	<p>
		Parses specified expression as json (with types) string and loads it as object. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							jsonWithType: {"a": 2, "b": 3}
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Converting json with type definitions into object (of specified type)<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;s:set expression="attrJson"&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;[<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"java.util.HashSet",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value1",<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value2"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]<br/>&nbsp;&nbsp;&nbsp;&nbsp;]&lt;/value&gt;<br/>&lt;/s:set&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&lt;s:assert-equals expected="jsonWithType: ${attr.attrJson}" actual="set: value1, value2"&gt;<br/>&lt;/s:assert-equals&gt;<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>&lt;s:assert-equals expected="attr: attrJson | jsonWithType: $" actual="set: value1, value2"&gt;<br/>&lt;/s:assert-equals&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-list" class="dataSection2">
								<h2>Expression Type - list</h2>
	<p>
		Parses specified expression into list of strings (using comma as delimiter). If type specified, strings will be converted to specified type. In case of '$', current value's string value will be parsed. If current value is collection, it will converted to list directly.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							list: val1, val2, val3
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Converting delimited string of values into list and asserting them<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-equals actual="attr: empNames" expected="list: employee1, employee2"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Converting delimited string of values into list and asserting them<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-equals actual="attr: empNames" expected="list: employee1, employee2"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-long" class="dataSection2">
								<h2>Expression Type - long</h2>
	<p>
		Parses specified expression into long. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							long: 10
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-map" class="dataSection2">
								<h2>Expression Type - map</h2>
	<p>
		Parses specified expression into map of strings (using comma as delimiter and = as delimiter for key and value). If types specified, strings will be converted to specified type. In case of '$', current value's string value will be parsed.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							map: key1 = val1, key2=val2, key3=val3
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-param" class="dataSection2">
								<h2>Expression Type - param</h2>
	<p>
		Parses specified expression as parameter.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							param: paramName
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-prop" class="dataSection2">
								<h2>Expression Type - prop</h2>
	<p>
		Parses specified expression as bean property on effective-context (context or current object in case of piping).
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							prop: attr.bean.value1
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using prop to access nested properties<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="propAttr1" value="file:./src/test/resources/data/data1.json | prop: bean1.prop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using prop to access nested properties<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="propAttr1" value="file:./src/test/resources/data/data1.json | prop: bean1.prop1"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-res" class="dataSection2">
								<h2>Expression Type - res</h2>
	<p>
		Parses specified expression as resource path and loads it as object. Supported file types: xml, json, properties
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							res: /tmp/data.json
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using res to load resource as object and extracting xpath value out of it<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr2" value="res:/data/data1.json | xpath: //bean1/subbean1/sprop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Parsing resource as template file and then converting result into object<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="jsonObj" value="res(template=true):/data/data.json"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 4 </td>
					<td>
						Using res to load resource as object and extracting xpath value out of it<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr2" value="res:/data/data1.json | xpath: //bean1/subbean1/sprop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 5 </td>
					<td>
						Parsing resource as template file and then converting result into object<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="jsonObj" value="res(template=true):/data/data.json"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-set" class="dataSection2">
								<h2>Expression Type - set</h2>
	<p>
		Parses specified expression into set of strings (using comma as delimiter). If type specified, strings will be converted to specified type. In case of '$', current value's string value will be parsed. If current value is collection, it will converted to set directly.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							set: val1, val2, val3
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-store" class="dataSection2">
								<h2>Expression Type - store</h2>
	<p>
		Parses specified expression as value on/from store.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							store: key1
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using store to set the value into the store<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="store: testStoreKey" value="value1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using store to fetch the value of the specified key from store<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="ctxAttrKey" value="store: testStoreKey"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 4 </td>
					<td>
						Using store to set the value into the store<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="store: testStoreKey" value="value1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 5 </td>
					<td>
						Using store to fetch the value of the specified key from store<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="ctxAttrKey" value="store: testStoreKey"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-string" class="dataSection2">
								<h2>Expression Type - string</h2>
	<p>
		Returns specified expression as stirng value after triming. In case of '$', current value will be converted to string. In case input object Blob/Clob, string value will be extracted from it.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							string: str
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using string to escape expression formats<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-equals actual="attr: returnValue" expected="string: beanFromApp:someName"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using string to escape expression formats<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:assert-equals actual="attr: returnValue" expected="string: beanFromApp:someName"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-uival" class="dataSection2">
								<h2>Expression Type - uival</h2>
	<p>
		Parses provided exprssion as ui locator and fetches/sets its value.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							uival: id:name
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="parser-xpath" class="dataSection2">
								<h2>Expression Type - xpath</h2>
	<p>
		Parses specified expression as xpath on effective-context (context or current object in case of piping).
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Default<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							xpath: /attr/bean/value1
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 2 </td>
					<td>
						Using xpath to access single property value<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr1" value="file:./src/test/resources/data/data1.json | xpath: //bean1/prop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 3 </td>
					<td>
						Using xpath to access multiple values matching specified path<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr1" value="xpath(multi=true): /attr/bean1//prop1"/&gt;
						</div>
						
					</td>
				</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 4 </td>
					<td>
						Using xpath to access properties using xpath<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:set expression="xpathAttr1" value="file:./src/test/resources/data/data1.json | xpath: //bean1/prop1"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
					</div>

					<div id="uiLocators" class="dataSection1">
						<h1>UI Locators</h1>
						<p>Defines the steps of autox for all functionalities</p>
						
							<div id="locator-class" class="dataSection2">
								<h2>Expression Type - class</h2>
	<p>
		Used to find ui elements using css class
	</p>
	


	
							</div>
							<div id="locator-css" class="dataSection2">
								<h2>Expression Type - css</h2>
	<p>
		Used to find ui elements using css locator.
	</p>
	


	
							</div>
							<div id="locator-id" class="dataSection2">
								<h2>Expression Type - id</h2>
	<p>
		Used to find ui elements using id.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Accessing ui element with id locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-click locator="id: button"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
							<div id="locator-js" class="dataSection2">
								<h2>Expression Type - js</h2>
	<p>
		Used to find ui elements using js expression.
	</p>
	


	
							</div>
							<div id="locator-name" class="dataSection2">
								<h2>Expression Type - name</h2>
	<p>
		Used to find ui elements using name of the element
	</p>
	


	
							</div>
							<div id="locator-tag" class="dataSection2">
								<h2>Expression Type - tag</h2>
	<p>
		Used to find ui elements using tag name
	</p>
	


	
							</div>
							<div id="locator-xpath" class="dataSection2">
								<h2>Expression Type - xpath</h2>
	<p>
		Used to find ui elements using xpath.
	</p>
	


		<table class="table table-striped table-hover table-bordered">
			<tr>
				<th colspan="2"  class="doc_mainHeading">
					Examples
				</th>
			</tr>
				<tr>
					<td class="doc_heading" style="width: 30px;"> 1 </td>
					<td>
						Accessing ui element with xpath locator<br/>

						<div style="margin-left: 20px; font-weight: bold; font-style: italic;">
							&lt;ccg:ui-get-value locator="xpath: //input[@name='statusFld']" name="fldValue"/&gt;
						</div>
						
					</td>
				</tr>
		</table>
	
							</div>
					</div>
				</div>
			</div>
		</div>

	</body>
</html>


