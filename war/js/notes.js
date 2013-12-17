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
    nbElm,
    nElm,
    sticklet = {
//        get colors () {
//            return colors;
//        },
        get nbElm () {
            return nbElm;
        },
        get nElm () {
            return nElm;
        },
        init: function() {
            var _this = this;
            hash.registerHashChangeCallback(_this.hashChangeCallback, _this);
            _this.getNotebooks();

            _this.sort();

            $("#mainWrapper").on("click touchend", "[data-sort-by][data-type]", function(ev) {
                var el = $(this),
                type = el.attr("data-type"),
                sb = el.attr("data-sort-by"),
                sd = el.attr("data-direction"),
                data = {};
                data[/notebook/i.test(type) ? "snb" : "sn"] = sb;
                data[/notebook/i.test(type) ? "snbd" : "snd"] = sd||"";
                hash.add(data);
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
            	createNotebook();
            }).on("click touchend", ".color-select", function(ev) {
            	var el = $(this),
            	$nb = el.closest(".notebook");
            	_this.saveNotebook(nbElm.getId($nb), "color", parseInt(el.attr("data-color-id"), 10));
            });
            
            $(window).on("resize", resizeWindow);
            resizeWindow();
        },
        getNotebooks: function() {
        	var _this = this;
        	_this.ajax({
        		url: "/notebook",
        		type: "get",
        		success: function(r) {
        			_this.loadNotebooks(r.notebooks);
        		}
        	});
        },
        loadNotebooks: function(data) {
        	if (data && data.length) {
        		var _this = this,
        		nbId = hash.get("nb"),
        		$notebooks = $("#mainWrapper .notebooks").html("");

        		nbElm = elm("notebook", null, $("#notebookTemplate").html(), Object.keys(data[0]));

        		_(data).each(function(notebook) {
        			$notebooks.append(nbElm.create(notebook));
        		});

        		if (nbId) {
        			_this.getNotebook(nbId);
        		} else if (data.length > 0) {
        			hash.add({
        				nb: data[0].id,
        				n: data[0].firstNote,
        				a: ""
        			});
        		}
        	}
        },
        getNotebook: function(nbId) {
        	var _this = this;
        	_this.ajax({
        		url: "/notebook/" + nbId,
        		type: "get",
        		success: function(r) {
        			_this.loadNotebook(r.notebook);
        		}
        	});
        },
        loadNotebook: function(notebook) {
        	if (typeof notebook === "object") {
        		var _this = this,
        		nbId = hash.get("nb"),
        		nid = hash.get("n"),
        		$notes = $("#mainWrapper .notes").html("");

        		nElm = elm("note", null, $("#noteTemplate").html(), Object.keys(notebook));

        		nbElm.elements.removeClass("current");
        		nbElm.getById(nbId).addClass("current");
        		scrollTo(nbElm.getById(nbId), $("#mainWrapper .notebooks"));

        		_(notebook.notes).each(function(note) {
        			$notes.append(nElm.create(note));
        		});

        		if (nid) {
        			_this.loadNote();
        		} else if (notebook.notes.length > 0) {
        			hash.add({
        				nb: nbId,
        				n: data[0].id,
        				a: ""
        			});
        		}
        	}
        },
        saveNotebook: function(nbId, prop, value) {
        	sticklet.ajax({
        		url: "/notebook/" + nbId,
        		type: "put",
        		data: {
        			prop: prop,
        			value: value
        		},
        		success: function(r) {
        			console.log(r);
        		}
        	})
        },
        loadNote: function() {
            var nId = hash.get("nid");
            nElm.elements.removeClass("current").removeClass("editing");
            nElm.getById(nId).addClass("current").addClass(getEdit() ? "editing" : "");
            scrollTo(nElm.getById(nId), $("#mainWrapper .notes"));
            console.log("scroll to and expand note, edit if necessary");
        },
        sort: function() {
            var hashes = hash.get();
            if (hashes["snb"]) {
                var nbs = $("#mainWrapper .notebooks");
                sort(nbs, nbElm, hashes["snb"], hashes["snbd"]);
                $("#left-bar .current-sort").text(hashes["snb"] + " " + (hashes["snbd"]||""));
            }
            if (hashes["sn"]) {
                var ns = $("#mainWrapper .notes");
                sort(ns, nElm, hashes["sn"], hashes["snd"]);
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
                _this.getNotebook(newHash["nb"]);
            } else if (newHash["n"] && (oldHash["n"] !== newHash["n"] || oldHash["a"] !== newHash["a"])) {
                _this.loadNote()
            }

            //sort, filter
            if ((changes.indexOf("fn") + changes.indexOf("fnb")) !== -2) {
                _this.filter();
            }
            if ((changes.indexOf("sn") + changes.indexOf("snb")) !== -2) {
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
        updateNote: function(note) {

        },
        updateNotebook: function(notebook) {

        }
    };
    
    function createNotebook() {
    	sticklet.ajax({
    		url: "/notebook",
    		type: "post",
    		success: function(r) {
    			$("#mainWrapper .notebooks").append(nbElm.create(r.notebook));
    		}
    	});
    }

    function getEdit() {
        return /edit/i.test(hash.get("a"));
    }

    function scrollTo(child, parent) {
        child = $(child);
        parent = $(parent);
        var pos = child.position()||{};
        parent.scrollTop(parent.scrollTop() + pos.top);
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

    window.sticklet = sticklet;
    sticklet.init();

}(jQuery, _, hash));
