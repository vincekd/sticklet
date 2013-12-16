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
    nbElm,
    nElm,
    sticklet = {
        get colors () {
            return colors;
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
            _this.getNotebooks();

            _this.sort();

            $("#mainWrapper").on("click", "[data-sort-by][data-type]", function(ev) {
                var el = $(this),
                type = el.attr("data-type"),
                sb = el.attr("data-sort-by"),
                sd = el.attr("data-direction"),
                data = {};
                data[/notebook/i.test(type) ? "snb" : "sn"] = sb;
                data[/notebook/i.test(type) ? "snbd" : "snd"] = sd||"";
                hash.add(data);
            }).on("click", "[data-filter-by][data-type]", function(ev) {
                //TODO: finish this
                var el = $(this),
                type = el.attr("data-type"),
                fb = el.attr("data-filter-by"),
                id = el.attr("data-" + fb + "-id"),
                data = {};
                data[/notebook/i.test(type) ? "fnb" : "fn"] = fb;
                data["f" + fb + "id"] = id;
                hash.add(data);
            });
        },
        getNotebooks: function() {
        	var _this = this;
        	$.ajax({
        		url: "/notebook",
        		type: "get",
        		success: function(r) {
        			console.log(r);
        			_this.loadNotebooks(r.notebooks);
        		}
        	});
        },
        loadNotebooks: function(data) {
            var _this = this,
            nbId = hash.get("nb"),
            $notebooks = $("#mainWrapper .notebooks").html("");


            nbElm = elm("notebook", null, $("#notebookTemplate").html(), Object.keys(data[0]||{}));

            if (data.length > 0) {
                if (!nbId) {
                    nbId = data[0].id;
                }
                nbElm = elm("notebook", null, $("#notebookTemplate").html(), Object.keys(data[0]));
            }

            _(data).each(function(notebook) {
                $notebooks.append(nbElm.create(notebook));
            });

            if (nbId) {
                _this.loadNotebook(nbId);
            } else if (data.length > 0) {
                hash.add({
                    nb: data[0].id,
                    n: data[0].firstNote,
                    a: ""
                });
            }
        },
        getNotebook: function(nbId) {
        	
        },
        loadNotebook: function(data) {
            var _this = this,
            nbId = hash.get("nb"),
            nid = hash.get("n"),
            $notes = $("#mainWrapper .notes").html("");

            nElm = elm("note", null, $("#noteTemplate").html(), Object.keys(data[0]||{}));

            nbElm.elements.removeClass("current");
            nbElm.getById(nbId).addClass("current");
            scrollTo(nbElm.getById(nbId), $("#mainWrapper .notebooks"));

            _(data).each(function(note) {
                $notes.append(nElm.create(note));
            });

            if (nid) {
                _this.loadNote();
            } else if (data.length > 0) {
                hash.add({
                    nb: nbId,
                    n: data[0].id,
                    a: ""
                });
            }
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
                _this.loadNotebook();
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
        updateNote: function(note) {

        },
        updateNotebook: function(notebook) {

        }
    };

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

    window.sticklet = sticklet;
    sticklet.init();

}(jQuery, _, hash));
