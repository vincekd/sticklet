(function(window, undefined) {
    "use strict";

    var hashChangeCallbacks = [],
    fromHash = function(str) {
        if (str === "" || window.location.hash.length <= 1) {
            return {};
        } else if (!str) {
            str = window.location.hash.substr(1);
        }

        var params = str.split("&")||[],
        paramsObject = {};

        for(var i = 0; i < params.length; i++) {
            var a = params[i].split("=");
            if (typeof a[1] !== "undefined" && a[1] !== null && a[1] !== "") {
                paramsObject[a[0]] =  decodeURIComponent(a[1]);
            }
        }
        return paramsObject;
    },
    toHash = function(params) {
        //only update if it's actually different so we don't break hash history
        var hashes = fromHash(),
        same = true,
        str = [];

        for (var h in hashes) {
            if (!params[h] || params[h] !== hashes[h]) {
                same = false
                break
            }
        }

        for(var p in params) {
            if (same && (!hashes[p] || hashes[p] !== params[p])) 
                same = false;
            if (p !== "")
                str.push(p + "=" + encodeURIComponent(params[p]));
        }
        if (!same) {
            window.location.hash = str.join("&");
        }
    },
    hashChange = function(ev) {
        var oldURL = ev.oldURL,
        oldHash = (/#/.test(oldURL) ? hash.utils.fromHash(oldURL.replace(/^[^#]*#/,'')) : {}),
        newHash = hash.get(),
        changes = [];

        _(oldHash).each(function(val, key) {
            if (val !== newHash[key]) {
                changes.push(key);
            }
        });

        _(newHash).each(function(val, key) {
            if (val !== oldHash[key] && changes.indexOf(key) === -1) {
                changes.push(key);
            }
        });

        _(hashChangeCallbacks).each(function(callback) {
            callback(changes, oldHash, newHash);
        });
    },
    hash = {
        init: function() {
            window.onhashchange = hashChange;
        },
        registerHashChangeCallback: function(callback, domain) {
            if (typeof callback === "function") {
                hashChangeCallbacks.push(function(changes, old, n) {
                    callback.call(domain, changes, old, n);
                });
            }
        },
        get: function(param) {
            var params = fromHash();
            if (param) {
                return params[param];
            } else {
                return params;
            }
        },
        addOne: function(str, val) {
            var obj = {};
            obj[str] = val;
            hash.add(obj);
        },
        add: function(newParams) {
            if (Object.keys(newParams).length <= 0) return;
            var params = fromHash();
            for (var p in newParams) {
                params[p] = newParams[p];
            }
            toHash(params);
        },
        addRemove: function(toAdd, toRemove) {
            var params = fromHash();
            if (typeof toAdd === "object") {
                for (var a  in toAdd) {
                    if (toAdd.hasOwnProperty(a) && !!a) {
                        params[a] = toAdd[a];
                    }
                }
            }
            if (typeof toRemove === "object" && toRemove.constructor.name === "Array") {
                for (var i = 0; i < toRemove.length; i++) {
                    if (!!toRemove[i])
                        delete params[toRemove[i]];
                }
            } else if (!!toRemove) {
                delete params[toRemove];
            }
            toHash(params);
        },
        remove: function(removeParams) {
            removeParams = (typeof removeParams === "string") ? [removeParams] : removeParams;
            var params = fromHash();
            for (var i = 0; i < removeParams.length; i++) {
                delete params[removeParams[i]];
            }
            toHash(params);
        },
        clear: function() {
            toHash({});
        },
        set: toHash,
        utils: {
            fromHash: fromHash,
            toHash: toHash
        }
    };

    window.hash = hash;

    hash.init();
})(window);
