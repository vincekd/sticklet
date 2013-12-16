(function($, _, w) {
    "use strict";

    function elm(name, domain, template, values) {
        var _this = this;
        _this.domain = domain||"body";
        _this.name = name.replace(/^[\.#]/g, "");
        _this.template = (typeof template === "string" ? _.template(template) : (typeof template === "function" ? template : null));
        _(values).each(function(v) {
            if (v) {
                var prop = ucfirst(v);
                _this["getBy" + prop] = function(val) { return _this.getBy(v, val); }; 
                _this["set" + prop] = function(el, val) { return _this.setAttr(el, v, val); }; 
                _this["get" + prop] = function(el) { return _this.getAttr(el, v); };
                _this["clear" + prop] = function(el) { return _this.clearAttr(el, v); };
            }
        });
    }

    elm.prototype = {
        get selector () {
            var _this = this;
            return $(_this.domain).find("." + _this.realName);
        },
        get elements () {
            var _this = this;
            return _this.selector;
        },
        get first () {
            var _this = this;
            return _this.selector.first();
        },
        getBy: function(key, val) {
            var _this = this;
            return $(_this.domain).find("." + _this.realName + "[" + _this.getAttrName(key) + "='" + val + "']");
        },
        getAttr: function(el, prop) {
            var _this = this;
            return $(el).attr(_this.getAttrName(prop));
        },
        setAttr: function(el, prop, val) {
            var _this = this;
            return $(el).attr(_this.getAttrName(prop), val);
        },
        clearAttr: function(el, prop) {
            var _this = this;
            return $(el).attr(_this.getAttrName(prop), "");
        },
        create: function(obj) {
            var _this = this;
            if (_this.template)
                return _this.template(obj);
        },
        get realName () {
            var _this = this;
            return _this.name.replace(/[A-Z]/g, function(str1, offset, whole) {
                if (offset === 0) {
                    return str1.toLowerCase();
                }
                return "-" + str1.toLowerCase();
            });
        },
        getAttrName: function(prop) {
            var _this = this;
            return "data-" + _this.realName + "-" + (prop||"").toLowerCase();
        }
    };

    function ucfirst(str) {
        return (str||"").replace(/^(.)(.*)$/, function(str, p1, p2, i) {
            return p1.toUpperCase() + p2;
        });
    }
    
    function trim(str) {
        return str.replace(/^\s+|\s+$/g, "");
    }
    
    window.elm = function(name, domain, template, values) {
        return new elm(name, domain, template, values);
    };
}(jQuery, _, window, undefined));
