(function($, _, hash, undefined) {
    "use strict";
    var colors = [
        "scolor1",
        "scolor2",
        "scolor3",
        "scolor4",
        "scolor5",
        "scolor6"
    ],
    colorTemplate = _.template($("#colorPickerTemplate").html()),
    textareaTemplate = _.template($("#textareaTemplate").html()),
    textTemplate = _.template($("#textTemplate").html()),
    nbElm,
    nElm,
    sticklet = {
        get nbElm () {
            return nbElm;
        },
        get nElm () {
            return nElm;
        },
        init: function() {
            var _this = this;
            hash.registerHashChangeCallback(_this.hashChangeCallback, _this);
            sticklet.ajax({
            	url: "/pages",
            	success: function(r) {
            		nbElm = elm("notebook", null, $("#notebookTemplate").html(), Object.keys(r.notebook));
            		nElm = elm("note", null, $("#noteTemplate").html(), Object.keys(r.note));
            		_this.notebook.getAll();
            		_this.channel.open(r.token);
            	}
            });

            $("#mainWrapper").on("click touchend", "[data-sort-by][data-type]", function(ev) {
                var el = $(this),
                type = el.attr("data-type"),
                sb = el.attr("data-sort-by"),
                sd = el.attr("data-direction"),
                data = {};
                data[/notebook/i.test(type) ? "snb" : "sn"] = sb;
                data[/notebook/i.test(type) ? "snbd" : "snd"] = sd||"";
                var changed = hash.add(data);
                if (!changed) {
                	_this.sort();
                }
            }).on("click touchend", "[data-filter-by][data-type]", function(ev) {
                //TODO: finish this
                var el = $(this),
                type = el.attr("data-type"),
                fb = el.attr("data-filter-by"),
                id = el.attr("data-" + fb + "-id"),
                data = {};
                data[/notebook/i.test(type) ? "fnb" : "fn"] = fb;
                data["f" + fb + "id"] = id;
                hash.add(data);
            }).on("click touchend", ".createNotebook", function(ev) {
            	_this.notebook.create();
            }).on("click touchend", ".createNote", function(ev) {
            	_this.note.create();
            }).on("click touchend", ".note .color-select", function(ev) {
            	var el = $(this),
            	$n = el.closest(".note");
            	_this.note.save(nElm.getId($n), "color", parseInt(el.attr("data-color-id"), 10));
            }).on("click touchend", ".notebooks .color-select", function(ev) {
            	var el = $(this),
            	$nb = el.closest(".notebook");
            	_this.notebook.save(nbElm.getId($nb), "color", parseInt(el.attr("data-color-id"), 10));
            }).on("click touchend", ".deleteNotebook", function(ev) {
            	_this.notebook["delete"]($(this).closest(".notebook").attr("data-notebook-id"));
            }).on("click touchend", ".deleteNote", function(ev) {
            	_this.note["delete"]($(this).closest(".note").attr("data-note-id"));
            }).on("click touchend", ".logout", function(ev) {
            	window.location = "/pages/logout";
            }).on("click touchend", ".notebook-title-link", function(ev) {
            	var $nb = $(this).closest(".notebook"),
            	nbId = nbElm.getId($nb);
            	if (hash.get("nb") != nbId) {
            		//TODO: need first note?
            		hash.add({
            			nb: nbId,
            			n: "",
            			a: ""
            		});
            	}
            }).on("dblclick", ".notes .editable", function(ev) {
            	var el = $(this),
            	par = el.parent(),
            	temp = (el.hasClass("title") ? textTemplate : textareaTemplate),
            	nId = nElm.getId(el.closest(".note")),
            	text = $.trim(el.text());

            	//el.addClass("editing-text");
            	var newEl = temp({
            		text: text,
            		type: "note",
            		id: nId
            	});

            	hash.addOne("n", nId);

            	el.replaceWith(newEl);
            	par.find(".editing-text").focus().val(text).text(text);
            	par.find(".editing-text").get(0).setSelectionRange(text.length, text.length);
            }).on("blur", ".notes .editing-text", function(ev) {
            	var el = $(this),
            	$n = el.closest(".note"),
            	prop = el.attr("data-parent-type"),
            	val = el.val(),
            	classStr = "editable " + el.attr("data-type") + "-" + prop + "-inline" + 
            		(prop === "title" ? " title" : "");
            	if (/content/i.test(prop)) {
            		el.replaceWith("<p class='" + classStr + "' title='Double click to edit'>" + val + "</p>");
            	} else {
            		el.replaceWith("<span class='" + classStr + "' title='Double click to edit'>" + val + "</span>");
            	}
            	
            	_this.note.save(nElm.getId($n), prop, val);
            });
            
            $(window).on("resize", _.debounce(resizeWindow, 250));
            resizeWindow();
        },
        sort: function() {
        	var hashes = hash.get();
        	if (hashes["snb"]) {
        		var nbs = get$nb();
        		sort(nbs, nbElm, hashes["snb"], hashes["snbd"]);
        		$("#left-bar .current-sort").text(hashes["snb"] + " " + (hashes["snbd"]||""));
        	}
        	if (hashes["sn"]) {
        		var ns = get$n();
        		sort(ns, nElm, hashes["sn"], hashes["snd"]);
        		$("#right-bar .current-sort").text(hashes["sn"] + " " + (hashes["snd"]||""));
        	}
        },
        filter: function() {
        	var hashes = hash.get();
        	if (hashes["fnb"]) {

        	}
        	if (hashes["fn"]) {

        	}
        },
        hashChangeCallback: function(changes, oldHash, newHash) {
        	var _this = this;
        	if (oldHash["nb"] !== newHash["nb"] && newHash["nb"]) {
        		_this.notebook.get(newHash["nb"]);
        	} else if (newHash["n"] && (oldHash["n"] !== newHash["n"] || oldHash["a"] !== newHash["a"])) {
        		_this.note.load();
        	}

        	//sort, filter
        	if ((changes.indexOf("fn") + changes.indexOf("fnb")) !== -2) {
        		_this.filter();
        	}
        	if ((changes.indexOf("sn") + changes.indexOf("snb") + changes.indexOf("snbd") + changes.indexOf("snd")) !== -4) {
        		_this.sort();
        	}
        },
        ajax: function(obj) {
        	if (obj.data) {
        		obj.data = {
        			data: JSON.stringify(obj.data)
        		};
        	}
        	return $.ajax(obj);
        },
        getColorPicker: function() {
        	return colorTemplate({colors: colors});
        },
        note: {
        	load: function() {
        		var nId = hash.get("n");
        		nElm.elements.removeClass("current").removeClass("editing");
        		nElm.getById(nId).addClass("current").addClass(getEdit() ? "editing" : "");
        		scrollTo(nElm.getById(nId));
        	},
        	save: function(nId, prop, value) {
        		sticklet.ajax({
        			url: "/note/" + nId,
        			type: "put",
        			data: {
        				prop: prop,
        				value: value
        			}
        		});
        	},
        	create: function() {
        		sticklet.ajax({
        			url: "/note",
        			type: "post",
        			data: {
        				notebook: hash.get("nb")
        			}
        		});
        	},
        	"delete": function(nId) {
        		sticklet.ajax({
        			url: "/note/" + nId,
        			type: "delete"
        		});
        	},
        	created: function(note) {
        		var $notes = get$n();
        		if (hash.get("nb") == note.notebook) {
        			$notes.prepend(nElm.create(note));
        			//sticklet.sort();
        		}
        		updateNotebookNoteCount(note.notebook, true);
        		if (hash.get("n") == note.id) {
        			hash.addOne("n", $notes.find(".note:first").attr("data-note-id"));
        		}
        	},
        	updated: function(note) {
        		var el = nElm.getById(note.id);
        		if (el.length === 0) {
        			get$n().append(nElm.create(note));
        		} else {
        			//TODO: do this better
        			el.replaceWith(nElm.create(note));
        		}
        	},
        	deleted: function(note) {
        		nElm.getById(note.id).remove();
        		updateNotebookNoteCount(note.notebook, false);
        	}
        },
        notebook: {
        	getAll: function() {
        		var _this = this;
        		sticklet.ajax({
        			url: "/notebook",
        			type: "get",
        			success: function(r) {
        				var data = r.notebooks;
        				if (data && data.length) {
        					var nbId = hash.get("nb"),
        					$notebooks = get$nb().html("");
        					
        					_(data).each(function(notebook) {
        						$notebooks.append(nbElm.create(notebook));
        					});
        					
        					if (nbId) {
        						sticklet.notebook.get(nbId);
        					} else if (data.length > 0) {
        						hash.add({
        							nb: data[0].id,
        							n: "", //data[0].firstNote,
        							a: ""
        						});
        					}
        					sticklet.sort();
        				}
        			}
        		});
        	},
        	get: function(nbId) {
        		sticklet.ajax({
        			url: "/notebook/" + nbId,
        			type: "get",
        			success: function(r) {
        				sticklet.notebook.load(r.notebook);
        			}
        		});
        	},
        	load: function(notebook) {
        		if (typeof notebook === "object") {
        			var nbId = hash.get("nb"),
        			nid = hash.get("n"),
        			$notes = get$n().html("");

        			nbElm.elements.removeClass("current");
        			nbElm.getById(nbId).addClass("current");
        			scrollTo(nbElm.getById(nbId));

        			_(notebook.notes).each(function(note) {
        				$notes.append(nElm.create(note));
        			});

        			if (nid) {
        				sticklet.note.load();
        			} else if (notebook.notes.length > 0) {
        				hash.add({
        					nb: nbId,
        					n: notebook.notes[notebook.notes.length-1].id,
        					a: ""
        				});
        			} else {
        				hash.add({
        					nb: nbId,
        					n: "",
        					a: ""
        				});
        			}
        			sticklet.sort();
        		}
        	},
        	save: function(nbId, prop, value) {
        		sticklet.ajax({
        			url: "/notebook/" + nbId,
        			type: "put",
        			data: {
        				prop: prop,
        				value: value
        			}
        		});
        	},
        	create: function() {
        		sticklet.ajax({
        			url: "/notebook",
        			type: "post"
        		});
        	},
        	"delete": function(nbId) {
        		sticklet.ajax({
        			url: "/notebook/" + nbId,
        			type: "delete"
        		});
        	},
        	created: function(notebook) {
        		get$nb().prepend(nbElm.create(notebook));
        		//sticklet.sort();
        	},
        	updated: function(notebook) {
        		var el = nbElm.getById(notebook.id);
        		if (el.length === 0) {
        			get$nb().append(nbElm.create(notebook));
        		} else {
        			//TODO: do this better
        			el.replaceWith(nbElm.create(notebook));
        		}
        	},
        	deleted: function(notebook) {
        		nbElm.getById(notebook.id).remove();
        		nElm.getByNotebook(notebook.id).remove();
        	},
        },
        channel: {
        	connected: false,
        	channel: null,
        	socket: null,
        	open: function(token) {
        		var _this = this;
        		_this.channel = new goog.appengine.Channel(token);
        		_this.socket = _this.channel.open();
        		$.extend(_this.socket, _this.socketEvents);
        	},
        	sendMessage: function(message) {
        		
        	},
        	socketEvents: {
        		onopen: function() {
        			var _this = this;
        			_this.connected = true;
        		},
        		onmessage: function(m) {
        			var data = JSON.parse(m.data),
        			fn = getValue(data.callback, sticklet);
        			if (typeof fn === "function") {
        				fn.call(sticklet, data.data);
        			}
        		},
        		onclose: (function() {
        			var retry = 10,
        			retries = 0;
        			return function() {
        				console.log("bye");
        				if (retries < retry) {
        					sticklet.channel.open();
        					retries++;
        				}
        			};
        		}())
        	}
        }
    };

    function getEdit() {
        return /edit/i.test(hash.get("a"));
    }

    function scrollTo(child, parent) {
        child = $(child);
        parent = parent||child.scrollParent();
        parent = $(parent);
        parent.scrollTop(parent.scrollTop() + child.position().top);
    }

    function sort(parent, elm, attr, dir) {
        var arr = _(elm.elements).sortBy(function($el) {
            return parseInt(elm.getId($el), 10)||0;
        });

        arr = _(arr).sortBy(function($el) {
            return elm.getAttr($el, attr);
        });
        _(arr).each(function($el) {
            if (!dir || /desc/i.test(dir)) {
                parent.append($el);
            } else {
                parent.prepend($el);
            }
        });
    }

    function getDate(msecs) {
        var d = new Date(msecs);
        return d.getMonth() + "/" + d.getDate() + "/" + d.getYear();
    }
    
    function resizeWindow() {
    	var ca = $("#content-area");
    	ca.height($(window).height() - ca.offset().top - 
    			(parseInt(ca.children().first().css("margin-bottom"), 10)||0)); 
    }
    
    function getValue(name, context) {
        context = context || window;
        var i, namespaces = name.split("."),
        last = namespaces.pop();
        for (var i = 0; i < namespaces.length; i++) {
            if (context.hasOwnProperty(namespaces[i])) {
                context = context[namespaces[i]];
            } else {
                return void 0;
            }
        }
        return (context !== null && typeof context !== "undefined") ? context[last] : void 0;
    }
    
    function updateNotebookNoteCount(notebook, up) {
    	var $nb = nbElm.getById(notebook);
    	nbElm.setNoteCount($nb, parseInt(nbElm.getNoteCount($nb), 10) + (up ? 1 : -1));
    	$nb.find(".notebook-note-count-value").text(nbElm.getNoteCount($nb));
    }

    function get$n() {
    	return $("#mainWrapper .notes");
    }
    function get$nb() {
    	return $("#mainWrapper .notebooks");
    }
    
    window.sticklet = sticklet;
    sticklet.init();

}(jQuery, _, hash));
