;(function (undefined) {
	
	"use strict";
	
	if (typeof(window.IECursor) === 'undefined') {
		window.IECursor = {
			fix: function() {
				var styleSheets = document.styleSheets;
				for (var s = 0; s < styleSheets.length; s++) {
					this.fixStyleSheet(styleSheets[s]);
				}
			},
	
			fixStyleSheet: function(styleSheet) {
				if (!styleSheet.rules) {
					// not IE
					return;
				}
		
				var href = styleSheet.href;
				if (!href) {
					return;
				}
	
				var path = this.getPath(href);
		
				var rules = styleSheet.rules;		
				for (var r = 0; r < rules.length; r++) {
					this.fixStyle(rules[r].style, path);
				}
			},
	
			fixStyle: function(style, path) {
				if (style.cursor) {
					style.cursor = this.absolutize(style.cursor, path);
				}
			},
	
			getPath: function(url) {
				var from = 0;
				var to = url.lastIndexOf("/") + 1;
		
				return url.substring(from, to);
			},
	
			absolutize: function(oldCursor, path) {
				var newCursor = "";
		
				var index = 0;
				while (true) {
					var from = oldCursor.indexOf("url(", index);
					if (from == -1) {
						break;
					}
					from += 4;
					var to = oldCursor.indexOf(")", from);
			
					newCursor += oldCursor.substring(index, from);
				
					var url = oldCursor.substring(from, to);
					if (!url.indexOf("/") == 0) {
						newCursor += path;
					}
					newCursor += url;
			
					index = to;
				}
		
				newCursor += oldCursor.substring(index);

				return newCursor;
			}
		};
	}		
})();
