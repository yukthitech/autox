$('body').scrollspy({
    target: '.bs-docs-sidebar',
    offset: 40
});

$("#sidebar").affix({
    offset: {
      top: 60
    }
});


$(document).ready(function(){
	var winHeight = $(window).height();
	var y = $("#myScrollspy").position().top;
	
	var h = winHeight - (2 * y);
	
	$("#myScrollspy").height( h );
	$("#myScrollspy").width( $($("#myScrollspy").parent()).width() );

	$('#myScrollspy').on('activate.bs.scrollspy', function () {
		var lastActiv = $("#myScrollspy li.active").last();
		lastActiv = $(lastActiv);
		
		var activY = lastActiv.position().top;
		var scrollY = $("#myScrollspy").position().top;
		
		var midPos = scrollY + ($("#myScrollspy").height() / 2);
		
		var toScroll = activY - midPos;
		$("#myScrollspy").scrollTop(toScroll);
	});
});

