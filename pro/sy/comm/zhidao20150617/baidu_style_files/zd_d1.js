var Hunter = {
	ratio : {
		"7561" : 0.05
	},
	userConfig : [ {
		hid : (function() {
			var l = document.location, query = {}, dict = {
				"zhidao.baidu.com" : {
					"\/$" : "7559",
					"\/browse\/\\d+" : "7560",
					"\/question\/\\d+\\.html" : "7561"
				}
			}, key, success, result, ls;
			dict = (l.port === '') ? dict[l.hostname.toLowerCase()]
					: dict[l.hostname.toLowerCase() + ":" + l.port];
			l.search.toLowerCase().replace(/([^?&#]+)=([^?&#]*)/g,
					function(all, name, value) {
						query[name] = value
					});
			ls = l.pathname + l.search;
			for (key in dict) {
				r = new RegExp(key, 'ig');
				if (r.test(ls)) {
					result = dict[key]
				}
			}
			;
			return result
		})()
	} ]
};
void function() {
	window.Hunter = window.Hunter || {};
	var i = "toString", q = "getBoundingClientRect", r = "activeElement", S = "previousSibling", a = "nodeName", n = "innerWidth", B = "innerHeight", y = "documentElement", P = {
		grid : 7,
		pid : 240,
		hid : (function() {
			if (Hunter.userConfig) {
				for ( var ao = 0, ap = Hunter.userConfig.length; ao < ap; ao++) {
					if (Hunter.userConfig[ao].hid) {
						au = Hunter.userConfig[ao].hid
					}
				}
			}
			if (!au) {
				function p(av) {
					return String(av).replace(/\.html?(\?|$)/, "$1").replace(
							/\?.*/, "").toLowerCase()
				}
				var w = document.location, aq = {}, d = {
					"wenku.baidu.com" : {
						"\/topic\/xiaoxue\/" : "5342",
						"\/topic\/edu\/" : "5343",
						"\/room" : "7048",
						"\/course\/index" : "7075",
						"\/user\/task?tab=1" : "7553",
						"\/user\/task?tab=0" : "7554"
					}
				}, ar, at, au;
				d = d[w.hostname.toLowerCase()];
				if (!d) {
					return
				}
				w.search.toLowerCase().replace(/([^?&#]+)=([^?&#]*)/g,
						function(aw, av, ax) {
							aq[av] = ax
						});
				w = p(w.pathname);
				for (ar in d) {
					at = w == p(ar);
					at
							&& /\?/.test(ar)
							&& ar.toLowerCase().replace(
									/([^?&#]+)=([^?&#]*)/g,
									function(aw, av, ax) {
										if (aq[av] != ax.replace(
												/[^\x00-\xff]/g, function(ay) {
													return encodeURI(ay)
												}).toLowerCase()) {
											at = 0
										}
									});
					if (at) {
						au = d[ar]
					}
				}
			}
			if (Hunter.ratio && typeof (Hunter.ratio[au]) != "undifined") {
				if (Math.random() > Hunter.ratio[au]) {
					return
				}
			}
			return au
		})(),
		logPath : "http://nsclick.baidu.com/u.gif"
	}, v = {}, aj, ag = [], ab = window, am = document, G = am.body, E = am[y], l = am.defaultView, M = Math.max, k = encodeURIComponent, al = 100, j, N, H, t, I, u = ab.screen.width
			+ "*" + ab.screen.height, R, C, x, h = 110, z = 2060, D = 1000 * 60 * 30, m, f, s, o, r, c, ad = [
			[ "mousemove", "m" ], [ "mousedown", "d" ], [ "contextmenu", "r" ],
			[ "mouseup", "u" ], [ "click", "c" ], [ "dblclick", "l" ],
			[ "keydown", "k" ], [ "mousewheel", "w" ],
			[ "DOMMouseScroll", "w", ab ], [ "scroll", "s", ab ],
			[ "resize", "e", ab ], [ "beforeunload", "z", ab ],
			[ "unload", "z", ab ], [ "focusout", "o" ], [ "blur", "o", ab ],
			[ "focusin", "i" ], [ "focus", "i", ab ] ], ac, g = /gecko/i
			.test(navigator.userAgent), W, U;
	function K(ap, w, p, d, ao) {
		ap = am.getElementsByTagName(am.all ? "object" : "embed");
		for (w = 0; d = ap[w++];) {
			if (!d[c]) {
				d[c] = 1;
				for (p = 0; (ao = ad[p]) && p < 7; p++) {
					V(d, ao[0], (function(aq) {
						return function(ar) {
							aa(aq, ar)
						}
					})(ao[1]))
				}
			}
		}
	}
	function X(aq, d, ap, ao, p, au, w) {
		aq = document.getElementsByTagName("iframe");
		for (ap = 0; ap < aq.length; ap++) {
			w = aq[ap];
			try {
				var at = String(w.src).replace(/^\w+:\/\/([^\/]+).*$|.*/, "$1");
				if ((!at || at == document.location.hostname)
						&& !w.contentWindow[c]) {
					w.contentWindow[c] = 1;
					au = w.contentWindow.document;
					for (ao = 0; (p = ad[ao]) && ao < 7; ao++) {
						V(au, p[0], (function(av, ax, aw) {
							return function(ay) {
								if (g) {
									if ("d" == av) {
										U = b(ay)
									}
									if ("u" == av) {
										U = 0
									}
								}
								aa.call({
									path : ax,
									doc : aw,
									flag : c
								}, av, ay)
							}
						})(p[1], A(w), au))
					}
				}
			} catch (ar) {
			}
		}
	}
	function V(p, d, w) {
		if (!p) {
			return
		}
		if (p.addEventListener) {
			p.addEventListener(d, w, false)
		} else {
			p.attachEvent && p.attachEvent("on" + d, w)
		}
		ag.push([ p, d, w ])
	}
	function Y(w, d, ap, ao) {
		if (!w) {
			return
		}
		try {
			if (w.removeEventListener) {
				w.removeEventListener(d, ap, false)
			} else {
				w.detachEvent && w.detachEvent("on" + d, ap)
			}
			w[c] = ao
		} catch (p) {
		}
	}
	function b(d) {
		return d.which
				|| d.button
				&& (d.button & 1 ? 1 : (d.button & 2 ? 3 : (d.button & 4 ? 2
						: 0)))
	}
	function Q(d) {
		while (d = ag.pop()) {
			Y(d[0], d[1], d[2])
		}
	}
	function an() {
		return new Date - j
	}
	function ak(w, p, d) {
		w = w.slice();
		w[1] = w[1][i](36);
		if (/[mlducwrkfh]/.test(w[0])) {
			for (d = 2; d < w.length; d++) {
				if (("" + w[d]).length > 1) {
					if (w[d] === s[d]) {
						w[d] = "^"
					} else {
						s[d] = w[d]
					}
				}
			}
		}
		p = w.join("*").replace(/\*0\b/g, "*").replace(/^(.)\*|\*+$/g, "$1")
				+ "!";
		t += p.length;
		if (t > z) {
			ai({
				data : k(N.join("") + (P.group ? "@@" + H.join("") : ""))
			});
			t = h;
			N = [];
			H = []
		}
		N.push(p)
	}
	function aa(aq, ao, au, ap, d, p, ar, av, w) {
		ap = an();
		if (o) {
			clearTimeout(o[0]);
			if (ap - o[2] > 50) {
				o[1]()
			} else {
				o = 0
			}
		}
		var at = /^u/.test(typeof am[r]) ? 0 : am[r];
		if (at != r) {
			ak([ "f", ap, A(at) ]);
			r = at
		}
		if (aq === "j") {
			ak([ aq, ap ].concat(d));
			return
		}
		if (!ao) {
			ak([ aq, ap ])
		}
		if (ap > D) {
			J();
			return
		}
		if ("i" == aq && null !== o) {
			return
		}
		w = ao.target || ao.srcElement;
		while (w && w.nodeType != 1) {
			w = w.parentNode
		}
		if (f[0] == w) {
			au = f[1]
		} else {
			if (this.flag == c && this.doc) {
				au = this.path + "/" + A(w, this.doc)
			} else {
				au = A(w)
			}
		}
		f = [ w, au ];
		d = [ aq, ap, au ];
		if (/[mw]/.test(aq)) {
			if (m[0] == aq && ap - m[1] < al && m[2] == d[2]) {
				return
			}
			m = d.slice(0, 3)
		}
		if (w && !w[c] && /select/i.test(w.tagName)) {
			w[c] = 1;
			V(w, "change", function(aw) {
				aa("h", aw)
			})
		}
		if ("o" == aq) {
			o && clearTimeout(o[0]);
			o = function() {
				o = null;
				d[2] = +(Math.min(ab.screenTop || 0, ab.screenY || 0) < -22932);
				ak(d)
			};
			o = [ setTimeout(o, 1000), o, ap ]
		} else {
			if (/[se]/.test(aq)) {
				p = e();
				d[3] = p[[ 0, 2 ][+(aq == "e")]];
				d[2] = p[[ 1, 3 ][+(aq == "e")]]
			} else {
				if ("i" == aq) {
					d[2] = ""
				} else {
					if (w) {
						if (/[mlducwr]/.test(aq)) {
							ar = af(w, [ ao.clientX, ao.clientY ], P.grid);
							if (!ar) {
								return
							}
							d[3] = ar[0];
							d[4] = ar[1];
							if (/[cdul]/.test(aq)) {
								d[5] = b(ao)
							}
							if (aq == "m") {
								d[5] = g ? U : b(ao)
							}
							if (aq == "w") {
								d[5] = +((ao.wheelDelta || ao.detail) < 0)
							}
						} else {
							if ("k" == aq) {
								d[3] = /password/i.test(w.type) ? 1
										: ao.keyCode;
								d[4] = [ +ao.altKey || 0, +ao.ctrlKey || 0,
										+ao.shiftKey || 0, +ao.metaKey || 0 ]
										.join("")
							} else {
								if ("h" == aq) {
									d[3] = w.selectedIndex
								}
							}
						}
					}
				}
			}
			ak(d)
		}
		if (/[dcukio]/.test(aq)) {
			X();
			K();
			W && clearInterval(W);
			ac = 0;
			W = setInterval(function() {
				X();
				K();
				if (ac++ > 3) {
					W && clearInterval(W);
					ac = 0;
					W = 0
				}
			}, 1000)
		}
	}
	function J() {
		if (!j) {
			return
		}
		ai({
			cmd : "close",
			data : k(N.join("") + "z" + an()[i](36)
					+ (P.group ? "@@" + H.join("") : ""))
		});
		t = h;
		N = [];
		H = [];
		j = 0;
		Q()
	}
	function Z(w, d, ao) {
		d = [];
		for (ao in w) {
			if (typeof w[ao] != "undefined") {
				d.push(ao + "=" + decodeURIComponent(w[ao]))
			}
		}
		return d.join("&")
	}
	function O(p, ao, w) {
		if (!E || !E[q]) {
			return
		}
		if (window._hunter_sid) {
			return
		}
		if (j) {
			return
		}
		j = new Date;
		C = e();
		x = L();
		I = (+j)[i](36) + (+Math.random().toFixed(8).substr(2))[i](36);
		window._hunter_sid = I;
		c = "_e_" + I;
		R = 0;
		t = h;
		N = [];
		H = [];
		s = [];
		m = [];
		f = [];
		aj = [];
		var d = /^u/.test(typeof am[r]) ? 0 : am[r];
		ak([ "a", 0, C[0], C[1], C[2], C[3], A(d) ]);
		r = d;
		ai({
			cmd : "open",
			ref : k(k(am.referrer)),
			data : k(N.join(""))
		});
		for (p = 0; ao = ad[p++];) {
			if (/(focus.)|blur|focus/.test(ao[0]) && (!RegExp.$1 ^ !am.all)) {
				continue
			}
			V(ao[2] || am, ao[0], (function(ap) {
				return function(aq) {
					if (ap == "z") {
						J();
						w = an();
						while (an() - w < 100) {
						}
						return
					}
					if (g) {
						if ("d" == ap) {
							U = b(aq)
						}
						if ("u" == ap) {
							U = 0
						}
					}
					aa(ap, aq)
				}
			})(ao[1]))
		}
		K();
		X()
	}
	function A(ao) {
		if (!ao || ao.nodeType != 1 || /^(html|body)$/i.test(ao.tagName)) {
			return ao && /^html$/i.test(ao.tagName) ? "~html" : ""
		}
		var ap = "" + (ao.getAttribute && ao.getAttribute("id"));
		if (ap && ap.length < 11 && !(/tangram/i.test(ap))
				&& am.getElementById(ap) == ao) {
			return "." + ap.replace(/[!-\/\s~^]/g, function(aq) {
				return "%" + (256 + aq.charCodeAt())[i](16).substr(1)
			})
		}
		var w = 1, p = ao[S], d = "nodeName";
		while (p) {
			w += p[d] == ao[d];
			p = p[S]
		}
		return A(ao.parentNode) + "~" + (w < 2 ? "" : w) + ao[d].toLowerCase()
	}
	function F(d) {
		if (!d || d.nodeType != 1 || /^(html|body)$/i.test(d.tagName)) {
			return
		}
		var p = d.getAttribute && d.getAttribute("hgroup");
		if (p) {
			return d
		}
		return F(d.parentNode)
	}
	function af(w, aq, p) {
		var ao = w[q](), d = ah(w);
		p = p || 1;
		function ap(ar) {
			return String(+ar.toFixed(3)).replace(/^0\./g, ".")
		}
		return [ ap(~~((aq[0] - ao.left) / p) * p / d[0]),
				ap(~~((aq[1] - ao.top) / p) * p / d[1]) ]
	}
	function ah(d) {
		var p = d[q]();
		return [ ~~(p.right - p.left), ~~(p.bottom - p.top) ]
	}
	function L() {
		var p = ah(E), d = ah(G);
		return [ M(p[0], d[0], ab[n] || 0, E.scrollWidth || 0),
				M(p[1], d[1], ab[B] || 0, E.scrollHeight || 0) ]
	}
	function e() {
		return [
				M(E.scrollLeft || 0, G.scrollLeft || 0,
						(l && l.pageXOffset) || 0),
				M(E.scrollTop || 0, G.scrollTop || 0, (l && l.pageYOffset) || 0),
				ab[n] || E.clientWidth || G.clientWidth || 0,
				ab[B] || E.clientHeight || G.clientHeight || 0 ]
	}
	function ai(aq) {
		if (!aq) {
			return
		}
		var ap = Hunter.logPath || P.logPath;
		for ( var ao in v) {
			aq[ao] = v[ao]
		}
		var p = am.createElement("img"), d, w = am.getElementsByTagName("head")[0]
				|| bd;
		p.src = ap + "?" + Z({
			pid : P.pid,
			hid : P.hid,
			qid : ab.bdQid,
			gr : P.grid,
			sid : I,
			seq : R++,
			px : u,
			ps : x,
			vr : C,
			dv : 3
		}) + "&" + Z(aq);
		p.onerror = p.onload = p.onreadystatechange = function() {
			if (!d && /^(loaded|complete)$/.test(this.readyState)) {
				d = 1;
				w.removeChild(p);
				p = 0
			}
		};
		w.appendChild(p);
		C = e();
		x = L();
		s = []
	}
	function T(d, p) {
		if (!j) {
			return
		}
		ts = an();
		d = [ "g", ts ].concat(d);
		ak(d);
		if (p) {
			ai({
				data : k(N.join(""))
			})
		}
	}
	if (typeof Hunter.param == "object") {
		for ( var ae in Hunter.param) {
			v[ae] = Hunter.param[ae]
		}
	}
	if (P.hid) {
		O()
	}
	Hunter.start = function() {
		if (P.hid) {
			O()
		}
	};
	Hunter.stop = function() {
		J();
		window._hunter_sid = null
	};
	Hunter.setParam = function(d, p) {
		v[d] = p
	};
	Hunter.record = T
}();