;(function (undefined) {
	
	"use strict";
	
	if (typeof(window.wicketdnd) === 'undefined') {
		window.wicketdnd = {
	
			MARGIN: 5,
		
			THRESHOLD: 4,
		
			OFFSET: 16,
		
			DELAY: 1000,

			LINK : 16,

			COPY : 17,
		
			dragSource: function(id, componentPath, operations, types, selectors) {
				var element = Wicket.$(id);

				$(element).on('mousedown', selectors.initiate, function(event) {
					if ($(event.target).is('input,select,option,button,textarea')) {
						return;
					}

					var closest = $(this).closest(selectors.select).get(0);
					if (closest.id) {
						event.preventDefault();
						event.stopPropagation();

						gesture(closest.id, wicketdnd.position(event));
					} else {
						Wicket.Log.error('wicket-dnd: drag matched selector but does not have markup id');
					}
				});

				function gesture(id, startPosition) {
					$(document).on('mousemove.wicketdnd', function(event) {
						event.preventDefault();
						event.stopPropagation();

						var distance = wicketdnd.distance(wicketdnd.position(event), startPosition);
						if (distance >= wicketdnd.THRESHOLD) {
							$(document).off('.wicketdnd');
	
							transfer(id);
						}
					});

					$(document).on('mouseup.wicketdnd', function(event) {
						$(document).off('.wicketdnd');
					});
				};

				function mark(id) {
					$('#' + id).addClass("dnd-drag");
				};

				function unmark(id) {
					$('#' + id).removeClass("dnd-drag");
				};

				function transfer(id) {
					var link = false;
					var copy = false;

					var hover = createHover(id);
					$('body').append(hover);

					mark(id);

					var target = undefined;
					var location = wicketdnd.locationNone;
					var type = undefined;
					var operation = wicketdnd.operation('NONE');
					operation.mark();

					var timeout = undefined;
					
					$(document).on('mousemove.wicketdnd', function(event) {
						event.preventDefault();
						event.stopPropagation();

						hover.css({'left' : (event.pageX + wicketdnd.OFFSET) + 'px', 'top' : (event.pageY + wicketdnd.OFFSET) + 'px'});
						
						if ($(event.target).hasClass('dnd-hover-cover') ||
						    $(event.target).hasClass('dnd-drop-top') ||
						    $(event.target).hasClass('dnd-drop-bottom') ||
						    $(event.target).hasClass('dnd-drop-left') ||
						    $(event.target).hasClass('dnd-drop-right')) {
							return;
						}

						target = wicketdnd.findTarget(event);

						updateLocation(target, event);

						type = wicketdnd.findType(types, location.types);

						updateOperation();
					});

					$(document).on('mouseup.wicketdnd', function(event) {
						event.preventDefault();
						event.stopPropagation();

						hover.remove();

						unmark(id);

						$(document).off('.wicketdnd');

						operation.unmark();

						if (operation.name != 'NONE') {
							target.notify('drop', operation, componentPath, id, location, undefined);
							target = undefined;
						}

						id = undefined;

						setLocation(wicketdnd.locationNone);
					});

					function keyUpOrDown(event) {
						if (event.which == wicketdnd.LINK) {
							link = event.data;
						}
						if (event.which == wicketdnd.COPY) {
							copy = event.data;
						}
						updateOperation();
					};

					function updateOperation() {
						var newOperation = wicketdnd.findOperation(link, copy, type, operations, location.operations);
						if (newOperation.name != operation.name) {
							operation.unmark();
							operation = newOperation;
							operation.mark();
						}
					};

					function updateLocation(target, event) {
						var newLocation;
						if (target === undefined) {
							newLocation = wicketdnd.locationNone;
						} else {
							newLocation = target.findLocation(event);
						}
						if (newLocation.id != location.id || newLocation.anchor != location.anchor) {
							setLocation(newLocation);
						}
					};

					function setLocation(newLocation) {
						location.unmark();
						location = newLocation;
						location.mark();

						if (timeout) {
							clearTimeout(timeout);
						}
						timeout = undefined;
						if (newLocation != wicketdnd.locationNone) {
							timeout = setTimeout(
								function() {
									target.notify(
										'drag',
										operation,
										componentPath,
										id,
										location,
										function() {
											mark(id);
											location.mark();
										}
									);
									timeout = undefined;
								},
								wicketdnd.DELAY
							);
						}
					};

					$(document).on('keydown.wicketdnd', true, keyUpOrDown);
					$(document).on('keyup.wicketdnd', false, keyUpOrDown);
				};

				function createHover(id) {
					var original = $('#' + id);
					if (!original.is(selectors.clone)) {
						original = original.find(selectors.clone);
					}

					var clone = original.clone();
					clone.addClass('dnd-clone');

					if (clone.is('td')) {
						var tr = $('<tr>');
						tr.addClass('dnd-hover-tr');
						tr.append(clone);
						clone = tr;
					}
					if (clone.is('tr')) {
						var tbody = $('<tbody>');
						tbody.addClass('dnd-hover-tbody');
						tbody.append(clone);
						clone = tbody;
					}
					if (clone.is('tbody')) {
						var table = $('<table>');
						table.addClass('dnd-hover-table');
						table.append(clone);
						clone = table;
					}

					clone.css({ 'width' : original.outerWidth() + 'px', 'height' : original.outerHeight() + 'px' });

					var hover = $('<div>');
					hover.addClass('dnd-hover');
					hover.append(clone);		

					var cover = $('<div>');
					cover.addClass('dnd-hover-cover');
					hover.append(cover);

					return hover;
				};
			},
		
			dropTarget: function(id, callbackUrl, operations, types, selectors) {
				var element = Wicket.$(id);

				$(element).data('drop-target', {
					'callbackUrl' : callbackUrl,
					'operations' : operations,
					'types' : types,
					'selectors' : selectors,
					'findLocation' : function(event) {
						var candidate = event.target;
						var position = wicketdnd.position(event);
						var location = wicketdnd.locationNone;

						do {
							location = findLocation(position, candidate, location);

							if (location != wicketdnd.locationNone && location.anchor != 'CENTER') {
								break;
							}

							if (candidate == element) {
								break;
							}
							candidate = candidate.parentNode;
						} while (candidate);


						if (location != wicketdnd.locationNone && !location.id) {
							Wicket.Log.error('wicket-dnd: drop ' + location.anchor + ' matched selector but does not have markup id');
							location = wicketdnd.locationNone;
						}

						return location;
					},
					'notify' : function(phase, operation, componentPath, id, location, success) {
						var attrs = {
							'u': callbackUrl,
							'ep': {},
							'sh': [success]
						};
						attrs.ep['phase'] = phase;
						attrs.ep['operation'] = operation.name;
						attrs.ep['source'] = componentPath;
						attrs.ep['drag'] = id;
						attrs.ep['component'] = location.id;
						attrs.ep['anchor'] = location.anchor;
						Wicket.Ajax.ajax(attrs);
					}
				});

				function findLocation(position, candidate, location) {
					
					if (location == wicketdnd.locationNone && $(candidate).is(selectors.center)) {
						location = {
							'id' : candidate.id,
							'operations' : operations,
							'types' : types,
							'anchor' : 'CENTER',
							'mark' : function() {
								$('#' + candidate.id).addClass('dnd-drop-center');
							},
							'unmark' : function() {
								$('#' + candidate.id).removeClass('dnd-drop-center');
							}
						};
					}

					var topMargin = wicketdnd.MARGIN;
					var bottomMargin = wicketdnd.MARGIN;
					var leftMargin = wicketdnd.MARGIN;
					var rightMargin = wicketdnd.MARGIN;

					var base = $(element).offset();
					var offset = $(candidate).offset();
					var width = $(candidate).outerWidth();
					var height = $(candidate).outerHeight();

					if (location == wicketdnd.locationNone) {
						// no location yet thus using full bounds
						topMargin = height / 2;
						bottomMargin = height / 2;
						leftMargin = width / 2;
						rightMargin = width / 2;
					}
			
					if ($(candidate).is(selectors.top) && (position.top <= offset.top + topMargin)) {
						var _div = $('<div>').addClass('dnd-drop-top');
						location = {
							'id' : candidate.id,
							'operations' : operations,
							'types' : types,
							'anchor' : 'TOP',
							'mark' : function() {
								$(element).append(_div);
								_div.css({ 'left' : (offset.left - base.left) + 'px', 'top' : (offset.top - base.top - _div.outerHeight()/2) + 'px', 'width' : width + 'px'});
							},
							'unmark' : function() {
								_div.remove();
							}
						};
					} else if ($(candidate).is(selectors.bottom) && (position.top >= offset.top + height - bottomMargin)) {
						var _div = $('<div>').addClass('dnd-drop-bottom');
						location = {
							'id' : candidate.id,
							'operations' : operations,
							'types' : types,
							'anchor' : 'BOTTOM',
							'mark' : function() {
								$(element).append(_div);
								_div.css({ 'left' : (offset.left - base.left)  + 'px', 'top' : (offset.top - base.top + height - _div.outerHeight()/2) + 'px', 'width' : width + 'px'});
							},
							'unmark' : function() {
								_div.remove();
							}
						};
					} else if ($(candidate).is(selectors.left) && (position.left <= offset.left + leftMargin)) {
						var _div = $('<div>').addClass('dnd-drop-left');
						location = {
							'id' : candidate.id,
							'operations' : operations,
							'types' : types,
							'anchor' : 'LEFT',
							'mark' : function() {
								$(element).append(_div);
								_div.css({ 'left' : (offset.left - base.left - _div.outerWidth()/2) + 'px', 'top' : (offset.top - base.top) + 'px', 'height' : height + 'px'});
							},
							'unmark' : function() {
								_div.remove();
							}
						};
					} else if ($(candidate).is(selectors.right) && (position.left >= offset.left + width - rightMargin)) {
						var _div = $('<div>').addClass('dnd-drop-right');
						location = {
							'id' : candidate.id,
							'operations' : operations,
							'types' : types,
							'anchor' : 'RIGHT',
							'mark' : function() {
								$(element).append(_div);
								_div.css({ 'left' : (offset.left - base.left + width - _div.outerWidth()/2) + 'px', 'top' : (offset.top - base.top)  + 'px', 'height' : height + 'px'});
							},
							'unmark' : function() {
								_div.remove();
							}
						};
					}
					
					return location;
				};
			},

			findTarget: function(event) {
				var candidate = event.target;
				while (candidate) {
					var data = $(candidate).data('drop-target');
					if (data) {
						return data;
					}

					candidate = candidate.parentNode;
				}

				return undefined;
			},

			position: function(event) {
				return {'left': event.pageX, 'top': event.pageY}
			},

			distance: function(position1, position2) {
				var deltaLeft = position1.left - position2.left;
				var deltaTop = position1.top - position2.top;
		
				return Math.abs(deltaLeft) + Math.abs(deltaTop);
			},

			locationNone: {
				'operations' : [],
				'types' : [],
				'mark' : function() {
				},
				'unmark' : function() {
				}
			},

			operation: function(name) {
				return {
					'name' : name,
					'mark' : function() {
						$('body').addClass('dnd-' + name);
					},
					'unmark' : function() {
						$('body').removeClass('dnd-' + name);
					}
				};
			},

			findType: function(sourceTypes, targetTypes) {
				for (var index = 0; index < sourceTypes.length; index++) {
					var type = sourceTypes[index];
			
					if ($.inArray(type, targetTypes) != -1) {
						return type;
					}
				}
				return undefined;
			},

			findOperation: function(link, copy, type, sourceOperations, targetOperations) {

				function allowed(operation) {
					return $.inArray(operation, sourceOperations) != -1 && 
					       $.inArray(operation, targetOperations) != -1;
				};

				if (type != undefined) {
					if (link) {
						if (allowed('LINK')) {
							return wicketdnd.operation('LINK');
						}
					} else if (copy) {
						if (allowed('COPY')) {
							return wicketdnd.operation('COPY');
						}
					} else {
						if (allowed('MOVE')) {
							return wicketdnd.operation('MOVE');
						} else if (allowed('COPY')) {
							return wicketdnd.operation('COPY');
						} else if (allowed('LINK')) {
							return wicketdnd.operation('LINK');
						}
					}
				}
				return wicketdnd.operation('NONE');
			}
		};
	}		
})();
