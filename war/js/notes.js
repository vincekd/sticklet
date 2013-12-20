(function($, _, hash, undefined) {
    "use strict";
    var debug = false,
    log = function() {
    	if (debug)
    		console.log.apply(console, (Array.prototype.slice.apply(arguments)));
    },
    colors = [
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
    contentTemplate = _.template($("#noteContentTemplate").html()),
    titleTemplate = _.template($("#noteTitleTemplate").html()),
    notebookTitleTemplate = _.template($("#notebookTitleTemplate").html()),
    noteNotebookTemp = _.template($("#noteNotebookTemplate").html()),
    nbElm,
    nElm,
    sticklet = {
    	get notebookTitleTemp () {
    		return notebookTitleTemplate;
    	},
    	get noteContentTemplate () {
    		return contentTemplate;
    	},
    	get noteTitleTemplate () {
    		return titleTemplate;
    	},
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
            get$n().removeClass("tile-notes stack-notes").addClass((hash.get("no")||"stack") + "-notes");

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
            }).on("click touchend", ".editNotebookTitle", function(ev) {
            	var el = $(this),
            	$nb = el.closest(".notebook"),
            	title = $nb.find(".notebook-title-link");
            	title.replaceWith(textTemplate({
            		text: title.find(".default-content").length > 0 ? "" : title.text(),
            		type: "notebook",
            		id: nbElm.getId($nb)
            	}));
            	$nb.find(".notebook-title input.textbox").focus();
            }).on("click touchend", ".logout", function(ev) {
            	window.location = "/pages/logout";
            }).on("click touchend", ".notes-organization button", function(ev) {
            	hash.add({
            		no: $(this).attr("data-type")
            	});
            }).on("click touchend", ".notebook-title-link", function(ev) {
            	var $nb = $(this).closest(".notebook"),
            	nbId = nbElm.getId($nb);
            	if (hash.get("nb") != nbId) {
            		hash.add({
            			nb: nbId,
            			n: "",
            			a: ""
            		});
            	}
            }).on("click", ".note", function(ev) {
            	var el = $(this);
            	if ($(ev.target).closest(".note-context-menu").length === 0) {
            		hash.add({
            			n: $(this).attr("data-note-id")
            		});
            	}
            }).on("mousedown", ".notes .editable", function(ev) {
            	//TODO: prevent text highlighting on dblclick
            	//ev.preventDefault();
            }).on("dblclick", ".notes .editable", function(ev) {
            	ev.preventDefault();
            	ev.stopPropagation();
            	var el = $(this),
            	par = el.parent(),
            	text = $.trim(el.text()),
            	temp = (el.hasClass("title") ? textTemplate : textareaTemplate),
            	nId = nElm.getId(el.closest(".note"));

            	if (el.find(".default-content").length > 0) {
            		text = "";
            	}

            	var newEl = temp({
            		text: text,
            		type: "note",
            		id: nId
            	});

            	hash.addOne("n", nId);

            	el.replaceWith(newEl);

            	var $t = par.find(".editing-text").focus().val(text).text(text);
            	$t.get(0).setSelectionRange(text.length, text.length);
            	resizeTextarea($t);

            	return false;
            }).on("blur", ".editing-text", function(ev) {
            	var el = $(this),
            	changed = this.defaultValue !== this.value,
            	type = el.attr("data-type"),
            	$n = el.closest("." + type),
            	elm = type === "note" ? nElm : nbElm,
            	prop = el.attr("data-parent-type"),
            	val = el.val();

            	if (this.parentNode) {
            		$(this.parentNode).remove(".editing-text-hidden");
            		if (/content/i.test(prop)) {
            			el.replaceWith(contentTemplate({content: val}));
            		} else {
            			if (/note/i.test(type)) {
            				el.replaceWith(titleTemplate({title: val}));
            			} else {
            				el.replaceWith(notebookTitleTemplate({title: val}));
            			}
            		}
            	}

            	if (changed) {
            		_this[type].save(elm.getId($n), prop, val);
            	}
            }).on("keyup keydown keypress", ".editing-text.autoresize", function(ev) {
            	var el = $(this);
            	resizeTextarea(el);
            }).on("keydown keypress keyup", ".editing-text", _.debounce(function(ev) {
            	//makes sure hasn't been blurred
            	if (this.parentNode) {
            		var el = $(this),
            		type = el.attr("data-type");
            		_this[type].save(
            				(/note/i.test(type) ? nElm : nbElm).getId(el.closest("." + type)),
            				el.attr("data-parent-type"), //prop
            				el.val());
            	}
            }, 2000)).on("mouseenter", ".note-edit-notebook", function(ev) {
            	$(this).find("ul.dropdown-menu").html(noteNotebookTemp({
            		notebooks: nbElm.elements,
            		curNotebook: nElm.getNotebook($(this).closest(".note"))
            	}));
            }).on("click touchend", ".note-notebook-select", function(ev) {
            	var el = $(this),
            	$n = el.closest(".note");
            	sticklet.note.save(nElm.getId($n), "notebook", el.attr("data-notebook-id"));
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
        	
        	if (changes.indexOf("no") !== -1) {
        		get$n().removeClass("tile-notes stack-notes").addClass(newHash["no"] + "-notes");
        	}
        },
        ajax: function(obj) {
        	if (obj.data) {
        		obj.data = {
        			data: JSON.stringify(obj.data)
        		};
        	}
        	var err = obj.error;
        	obj.error = function(ev) {
        		console.error(ev);
        		if (/put|delete/i.test(type)) {
        			sticklet.alert("Failed to " (/put/i.test(type) ? "save" : "delete") + " note/notebook", true);
        		}
        		if (typeof err === "function") {
        			err.call(sticklet);
        		} 
        	};
        	return $.ajax(obj);
        },
        alert: function(msg, err, time) {
        	alert(msg);
        },
        getColorPicker: function() {
        	return colorTemplate({colors: colors});
        },
        note: {
        	load: function() {
        		var nId = hash.get("n");
        		nElm.elements.removeClass("current").removeClass("editing");
        		scrollTo(nElm.getById(nId).addClass("current"));
        	},
        	save: function(nId, prop, value) {
        		log("saving note " + nId + ":", prop + "=" + value);
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
        		if (confirm("Are you sure you want to delete this note?")) {
        			log("deleting note " + nId);
        			sticklet.ajax({
        				url: "/note/" + nId,
        				type: "delete"
        			});
        		}
        	},
        	created: function(note) {
        		var $notes = get$n(),
        		nb = hash.get("nb");
        		if (nb == note.notebook) {
        			$notes.prepend(nElm.create(note));
        			sticklet.sort();
        			hash.addOne("n", note.id);
        		} 
        		updateNotebookNoteCount(note.notebook, true);
        	},
        	updated: function(note) {
        		var el = nElm.getById(note.id),
        		hashes = hash.get();
        		if (note.notebook == hashes["nb"]) {
        			if (el.length === 0) {
        				get$n().append(nElm.create(note));
        				sticklet.sort();
        			} else {
        				if (el.find(".editing-text").length === 0) {
        					el.replaceWith(nElm.create(note));
        					if (hashes["n"] == note.id) {
        						nElm.getById(note.id).addClass("current");
        					}
        				} else {
        					log("editing...");
        				}
        			}
        		} else if (el.length > 0) {
        			el.remove();
        			updateNotebookNoteCount(hashes["nb"], false);
        			updateNotebookNoteCount(note.notebook, true);
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

        					sticklet.sort();

        					if (nbId) {
        						sticklet.notebook.get(nbId);
        					} else if (data.length > 0) {
        						hash.add({
        							nb: data[0].id,
        							n: "", //data[0].firstNote,
        							a: ""
        						});
        					}
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
        			
        			sticklet.sort();

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
        		}
        	},
        	save: function(nbId, prop, value) {
        		log("saving notebook " + nbId + ":", prop + "=" + value);
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
        		if (confirm("Are you sure you want to delete this notebook? " +
        				"It will delete the notebook and all the notes therein.")) {
        			log("deleting note", nbId);
        			sticklet.ajax({
        				url: "/notebook/" + nbId,
        				type: "delete"
        			});
        		}
        	},
        	created: function(notebook) {
        		get$nb().prepend(nbElm.create(notebook));
        		var nb = hash.get("nb");
        		if (!nb || nbElm.getById(nb).length === 0 || nb == notebook.id) {
        			hash.add({
        				nb: notebook.id
        			});
        		} 
        		//sticklet.sort();
        	},
        	updated: function(notebook) {
        		var el = nbElm.getById(notebook.id);
        		if (el.length === 0) {
        			get$nb().append(nbElm.create(notebook));
        		} else {
        			el.replaceWith(nbElm.create(notebook));
        			if (hash.get("nb") == notebook.id) {
        				nbElm.getById(notebook.id).addClass("current");
        			}
        		}
        	},
        	deleted: function(notebook) {
        		nbElm.getById(notebook.id).remove();
        		nElm.getByNotebook(notebook.id).remove();
        		if (hash.get("nb") == notebook.id) {
        			hash.add({
        				nb: (nbElm.getId(nbElm.first)||"")
        			});
        		}
        	},
        },
        channel: {
        	connected: false,
        	channel: null,
        	socket: null,
        	retries: 5,
        	retry: 0,
        	getToken: function() {
        		sticklet.ajax({
        			url: "/pages",
        			type: "get",
        			success: function(r) {
        				sticklet.channel.open(r.token);
        			}
        		});
        	},
        	open: function(token) {
        		var _this = this;
        		log("token:", token);
        		_this.channel = new goog.appengine.Channel(token);
        		_this.socket = _this.channel.open();
        		$.extend(_this.socket, _this.socketEvents);
        	},
        	sendMessage: function(message) {
        		
        	},
        	socketEvents: {
        		onopen: function() {
        			log("channel opened");
        			var _this = this;
        			_this.connected = true;
        			sticklet.channel.retry = 0;
        		},
        		onmessage: function(m) {
        			var data = JSON.parse(m.data),
        			fn = getValue(data.callback, sticklet);
        			if (typeof fn === "function") {
        				fn.call(sticklet, data.data);
        			}
        		},
        		onclose: function() {
        			log("channel closed");
        			if (sticklet.channel.retries > sticklet.channel.retry) {
        				sticklet.channel.retry++;
        				sticklet.channel.getToken();
        			}
        		},
        		onerror: function() {
        			log(arguments);
        		}
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
    function resizeTextarea(el) {
    	el = $(el);
    	el.height(el.prev(".editing-text-hidden").text(el.val()).height());
    }
    
    window.sticklet = sticklet;
    sticklet.init();

}(jQuery, _, hash));
