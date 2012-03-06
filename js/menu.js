(function ($) {
	$.fn.calcSubWidth = function () {
		var rowWidth = 0;
		$(this).find("ul").each(function() {
			rowWidth += $(this).width();
		});
		return rowWidth;
	}

	function megaHoverOver() {
		var rowWidth;
		$(this).find(".sub").stop().fadeTo('fast', 1).show();

		rowWidth = $(this).calcSubWidth();
		$(this).find(".sub").css({'width': rowWidth});
	}

	function megaHoverOut() {
		$(this).find(".sub").stop().fadeTo('fast', 0, function () {
			$(this).hide();
		});
	}

	var config = {
		sensitivity: 2,
		interval: 100,
		over: megaHoverOver,
		timeout: 500,
		out: megaHoverOut
	};

	$(document).ready(function () {
		$("ul.tabbed li .sub").css({'opacity': '0'});
		$("ul.tabbed li").hoverIntent(config);
	});
})(jQuery);
