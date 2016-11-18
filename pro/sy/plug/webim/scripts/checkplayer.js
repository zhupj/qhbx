/*	CheckPlayer 1.0.2 <http://checkplayer.flensed.com/>
	Copyright (c) 2008 Kyle Simpson, Getify Solutions, Inc.
	This software is released under the MIT License <http://www.opensource.org/licenses/mit-license.php>

	====================================================================================================
	Portions of this code were extracted and/or derived from:

	SWFObject v2.1 & 2.2a8 <http://code.google.com/p/swfobject/>
	Copyright (c) 2007-2008 Geoff Stearns, Michael Williams, and Bobby van der Sluis
	This software is released under the MIT License <http://www.opensource.org/licenses/mit-license.php>
 */
(function(R) {
	var E = R, V = R.document, N = "undefined", G = true, X = false, W = "", H = "object", O = "function", T = "string", M = "div", D = "onunload", J = "none", U = null, P = null, I = null, L = null, K = "flensed.js", F = "checkplayer.js", B = "swfobject.js", C = R.setTimeout, A = R.clearTimeout, S = R.setInterval, Q = R.clearInterval;
	if (typeof R.flensed === N) {
		R.flensed = {}
	}
	if (typeof R.flensed.checkplayer !== N) {
		return
	}
	P = R.flensed;
	C(function() {
		var Y = X, i = V.getElementsByTagName("script"), d = i.length;
		try {
			P.base_path.toLowerCase();
			Y = G
		} catch (b) {
			P.base_path = ""
		}
		function g(o, n, p) {
			for ( var m = 0; m < d; m++) {
				if (typeof i[m].src !== N) {
					if (i[m].src.indexOf(o) >= 0) {
						break
					}
				}
			}
			var l = V.createElement("script");
			l.setAttribute("src", P.base_path + o);
			if (typeof n !== N) {
				l.setAttribute("type", n)
			}
			if (typeof p !== N) {
				l.setAttribute("language", p)
			}
			V.getElementsByTagName("head")[0].appendChild(l)
		}
		if ((typeof i !== N) && (i !== null)) {
			if (!Y) {
				var j = 0;
				for ( var c = 0; c < d; c++) {
					if (typeof i[c].src !== N) {
						if (((j = i[c].src.indexOf(K)) >= 0)
								|| ((j = i[c].src.indexOf(F)) >= 0)) {
							P.base_path = i[c].src.substr(0, j);
							break
						}
					}
				}
			}
		}
		try {
			R.swfobject.getObjectById("a")
		} catch (h) {
			g(B, "text/javascript")
		}
		try {
			P.ua.pv.join(".")
		} catch (f) {
			g(K, "text/javascript")
		}
		function Z() {
			A(a);
			try {
				E.detachEvent(D, arguments.callee)
			} catch (k) {
			}
		}
		try {
			E.attachEvent(D, Z)
		} catch (e) {
		}
		var a = C(function() {
			Z();
			try {
				R.swfobject.getObjectById("a");
				P.ua.pv.join(".")
			} catch (k) {
				throw new R.Error("CheckPlayer dependencies failed to load.")
			}
		}, 20000)
	}, 0);
	P.checkplayer = function(x, AI, o, AB) {
		if (typeof I._ins !== N) {
			if (I._ins.ready()) {
				setTimeout(function() {
					AI(I._ins)
				}, 0)
			}
			return I._ins
		}
		var a = "6.0.65", z = [], i = null, f = X, g = null, AK = null, s = W, d = X, l = null, b = [], r = {}, AA = [], e = null, AG = null, AF = null, m = null, h = X, AH = null, k = X, t = X, p = X, AE = null;
		var Z = function() {
			if ((typeof x !== N) && (x !== null) && (x !== X)) {
				AG = x + W
			} else {
				AG = "0.0.0"
			}
			if (typeof AI === O) {
				AF = AI
			}
			if (typeof o !== N) {
				h = !(!o)
			}
			if (typeof AB === O) {
				m = AB
			}
			function AM() {
				A(g);
				try {
					E.detachEvent(D, AM)
				} catch (AP) {
				}
			}
			try {
				E.attachEvent(D, AM)
			} catch (AN) {
			}
			(function AO() {
				try {
					P.bindEvent(E, D, y)
				} catch (AP) {
					g = C(arguments.callee, 25);
					return
				}
				AM();
				AH = P.ua.pv.join(".");
				g = C(AD, 1)
			})()
		}();
		function AD() {
			try {
				e = V.getElementsByTagName("body")[0]
			} catch (AN) {
			}
			if ((typeof e === N) || (e === null)) {
				g = C(AD, 25);
				return
			}
			try {
				R.swfobject.getObjectById("a");
				L = R.swfobject
			} catch (AM) {
				g = C(AD, 25);
				return
			}
			t = L.hasFlashPlayerVersion(a);
			k = L.hasFlashPlayerVersion(AG);
			AJ();
			if (typeof AF === O) {
				AF(j)
			}
			d = G;
			if (k) {
				u()
			} else {
				if (h && !f) {
					v()
				}
			}
		}
		function y() {
			if (typeof E.detachEvent !== N) {
				E.detachEvent(D, y)
			}
			I._ins = null;
			if ((typeof l !== N) && (l !== null)) {
				try {
					l.updateSWFCallback = null;
					AC = null
				} catch (AP) {
				}
				l = null
			}
			try {
				for ( var AO in j) {
					if (j[AO] !== Object.prototype[AO]) {
						try {
							j[AO] = null
						} catch (AN) {
						}
					}
				}
			} catch (AM) {
			}
			j = null;
			e = null;
			Y();
			AA = null;
			AF = null;
			m = null;
			try {
				for ( var AS in I) {
					if (I[AS] !== Object.prototype[AS]) {
						try {
							I[AS] = null
						} catch (AR) {
						}
					}
				}
			} catch (AQ) {
			}
			I = null;
			P.checkplayer = null;
			P = null;
			R = null
		}
		function AL(AN, AO, AM) {
			AA[AA.length] = {
				func : AN,
				funcName : AO,
				args : AM
			}
		}
		function u() {
			if (!d) {
				i = C(u, 25);
				return
			}
			var AO = 0;
			try {
				AO = AA.length
			} catch (AP) {
			}
			for ( var AN = 0; AN < AO; AN++) {
				try {
					AA[AN].func.apply(j, AA[AN].args);
					AA[AN] = X
				} catch (AM) {
					k = X;
					AJ();
					if (typeof AF === O) {
						AF(j)
					} else {
						throw new R.Error("checkplayer::" + AA[AN].funcName
								+ "() call failed.")
					}
				}
			}
			AA = null
		}
		function Y() {
			A(g);
			g = null;
			A(i);
			i = null;
			for ( var AN in r) {
				if (r[AN] !== Object.prototype[AN]) {
					Q(r[AN]);
					r[AN] = X
				}
			}
			for ( var AM in z) {
				if (z[AM] !== Object.prototype[AM]) {
					A(z[AM]);
					z[AM] = X
				}
			}
		}
		function AJ() {
			try {
				j.playerVersionDetected = AH;
				j.checkPassed = k;
				j.updateable = t;
				j.updateStatus = p;
				j.updateControlsContainer = AE
			} catch (AM) {
			}
		}
		function n(AS, AN) {
			var AP = AN ? "visible" : "hidden";
			var AR = P.getObjectById(AS);
			try {
				if (AR !== null && (typeof AR.style !== N)
						&& (AR.style !== null)) {
					AR.style.visibility = AP
				} else {
					try {
						P.createCSS("#" + AS, "visibility:" + AP)
					} catch (AQ) {
					}
				}
			} catch (AO) {
				try {
					P.createCSS("#" + AS, "visibility:" + AP)
				} catch (AM) {
				}
			}
		}
		function v() {
			var AR = e;
			if ((typeof AR === N) || (AR === null)) {
				z[z.length] = C(v, 25);
				return
			}
			try {
				L.getObjectById("a")
			} catch (AQ) {
				z[z.length] = C(v, 25);
				return
			}
			if (!f) {
				f = G;
				Y();
				if (t) {
					s = "CheckPlayerUpdate";
					AK = s + "SWF";
					P
							.createCSS(
									"#" + s,
									"width:221px;height:145px;position:absolute;left:5px;top:5px;border:none;background-color:#000000;display:block;");
					P
							.createCSS("#" + AK,
									"display:inline;position:absolute;left:1px;top:1px;");
					AE = V.createElement(M);
					AE.id = s;
					AR.appendChild(AE);
					n(AE.id, X);
					AJ();
					var AT = null;
					try {
						AT = E.top.location.toString()
					} catch (AM) {
						AT = E.location.toString()
					}
					var AO = {
						swfId : AK,
						MMredirectURL : AT.replace(/&/g, "%26"),
						MMplayerType : (P.ua.ie && P.ua.win ? "ActiveX"
								: "PlugIn"),
						MMdoctitle : V.title.slice(0, 47)
								+ " - Flash Player Installation"
					};
					var AS = {
						allowScriptAccess : "always"
					};
					var AP = {
						id : AK,
						name : AK
					};
					try {
						q(P.base_path + "updateplayer.swf", {
							appendToId : s
						}, "219", "143", AO, AS, AP, {
							swfTimeout : 3000,
							swfCB : c
						}, G)
					} catch (AN) {
						w();
						return
					}
				} else {
					w()
				}
			}
		}
		function w(AM) {
			if (typeof AM === N) {
				AM = "Flash Player not detected or not updateable."
			}
			p = I.UPDATE_FAILED;
			AJ();
			if (typeof m === O) {
				m(j)
			} else {
				throw new R.Error("checkplayer::UpdatePlayer(): " + AM)
			}
		}
		function c(AM) {
			if (AM.status === I.SWF_LOADED) {
				A(r["continueUpdate_" + AK]);
				r["continueUpdate_" + AK] = X;
				l = AM.srcElem;
				l.updateSWFCallback = AC;
				p = I.UPDATE_INIT;
				AJ();
				if (typeof m === O) {
					m(j)
				}
				n(AE.id, G)
			} else {
				if (AM.status === I.SWF_FAILED || AM.status === I.SWF_TIMEOUT) {
					w()
				}
			}
		}
		function AC(AN) {
			try {
				if (AN === 0) {
					p = I.UPDATE_SUCCESSFUL;
					AE.style.display = J;
					try {
						E.open(W, "_self", W);
						E.close();
						R.self.opener = E;
						R.self.close()
					} catch (AO) {
					}
				} else {
					if (AN === 1) {
						p = I.UPDATE_CANCELED;
						AE.style.display = J
					} else {
						if (AN === 2) {
							AE.style.display = J;
							w("The Flash Player update failed.");
							return
						} else {
							if (AN === 3) {
								AE.style.display = J;
								w("The Flash Player update timed out.");
								return
							}
						}
					}
				}
			} catch (AM) {
			}
			AJ();
			if (typeof m === O) {
				m(j)
			}
		}
		function q(Af, AS, AU, AN, AP, AR, AW, Ad, Ab) {
			if (AS !== null && (typeof AS === T || typeof AS.replaceId === T)) {
				n(((typeof AS === T) ? AS : AS.replaceId), X)
			}
			if (!d && !Ab) {
				AL(q, "DoSWF", arguments);
				return
			}
			if (k || Ab) {
				AU += W;
				AN += W;
				var AZ = (typeof AW === H) ? AW : {};
				AZ.data = Af;
				AZ.width = AU;
				AZ.height = AN;
				var AY = (typeof AR === H) ? AR : {};
				if (typeof AP === H) {
					for ( var Ac in AP) {
						if (AP[Ac] !== Object.prototype[Ac]) {
							if (typeof AY.flashvars !== N) {
								AY.flashvars += "&" + Ac + "=" + AP[Ac]
							} else {
								AY.flashvars = Ac + "=" + AP[Ac]
							}
						}
					}
				}
				var Ae = null;
				if (typeof AW.id !== N) {
					Ae = AW.id
				} else {
					if (AS !== null
							&& (typeof AS === T || typeof AS.replaceId === T)) {
						Ae = ((typeof AS === T) ? AS : AS.replaceId)
					} else {
						Ae = "swf_" + b.length
					}
				}
				var Ag = null;
				if (AS === null || AS === X || typeof AS.appendToId === T) {
					var AO = null;
					if (AS !== null && AS !== X && typeof AS.appendToId === T) {
						AO = P.getObjectById(AS.appendToId)
					} else {
						AO = e
					}
					var AT = V.createElement(M);
					Ag = (AT.id = Ae);
					AO.appendChild(AT)
				} else {
					Ag = ((typeof AS.replaceId === T) ? AS.replaceId : AS)
				}
				var AX = function() {
				}, Aa = 0, AM = W, AV = null;
				if (typeof Ad !== N && Ad !== null) {
					if (typeof Ad === H) {
						if (typeof Ad.swfCB !== N && Ad.swfCB !== null) {
							AX = Ad.swfCB
						}
						if (typeof Ad.swfTimeout !== N
								&& (R.parseInt(Ad.swfTimeout, 10) > 0)) {
							Aa = Ad.swfTimeout
						}
						if (typeof Ad.swfEICheck !== N
								&& Ad.swfEICheck !== null
								&& Ad.swfEICheck !== W) {
							AM = Ad.swfEICheck
						}
					} else {
						if (typeof Ad === O) {
							AX = Ad
						}
					}
				}
				try {
					AV = L.createSWF(AZ, AY, Ag)
				} catch (AQ) {
				}
				if (AV !== null) {
					b[b.length] = Ae;
					if (typeof AX === O) {
						AX({
							status : I.SWF_INIT,
							srcId : Ae,
							srcElem : AV
						});
						r[Ae] = S(
								function() {
									var Ai = P.getObjectById(Ae);
									if ((typeof Ai !== N)
											&& (Ai !== null)
											&& (Ai.nodeName === "OBJECT" || Ai.nodeName === "EMBED")) {
										var Ah = 0;
										try {
											Ah = Ai.PercentLoaded()
										} catch (Aj) {
										}
										if (Ah > 0) {
											if (Aa > 0) {
												A(r["DoSWFtimeout_" + Ae]);
												r["DoSWFtimeout_" + Ae] = X
											}
											if (Ah < 100) {
												C(function() {
													AX({
														status : I.SWF_LOADING,
														srcId : Ae,
														srcElem : Ai
													})
												}, 1)
											} else {
												Q(r[Ae]);
												r[Ae] = X;
												C(function() {
													AX({
														status : I.SWF_LOADED,
														srcId : Ae,
														srcElem : Ai
													})
												}, 1);
												if (AM !== W) {
													var Ak = X;
													r[Ae] = S(
															function() {
																if (!Ak
																		&& typeof Ai[AM] === O) {
																	Ak = G;
																	try {
																		Ai[AM]
																				();
																		Q(r[Ae]);
																		r[Ae] = X;
																		AX({
																			status : I.SWF_EI_READY,
																			srcId : Ae,
																			srcElem : Ai
																		})
																	} catch (Al) {
																	}
																	Ak = X
																}
															}, 25)
												}
											}
										}
									}
								}, 50);
						if (Aa > 0) {
							r["DoSWFtimeout_" + Ae] = C(
									function() {
										var Ai = P.getObjectById(Ae);
										if ((typeof Ai !== N)
												&& (Ai !== null)
												&& (Ai.nodeName === "OBJECT" || Ai.nodeName === "EMBED")) {
											var Ah = 0;
											try {
												Ah = Ai.PercentLoaded()
											} catch (Aj) {
											}
											if (Ah <= 0) {
												Q(r[Ae]);
												r[Ae] = X;
												if (P.ua.ie && P.ua.win
														&& Ai.readyState !== 4) {
													Ai.id = "removeSWF_"
															+ Ai.id;
													Ai.style.display = J;
													r[Ai.id] = S(
															function() {
																if (Ai.readyState === 4) {
																	Q(r[Ai.id]);
																	r[Ai.id] = X;
																	L
																			.removeSWF(Ai.id)
																}
															}, 500)
												} else {
													L.removeSWF(Ai.id)
												}
												r[Ae] = X;
												r["DoSWFtimeout_" + Ae] = X;
												AX({
													status : I.SWF_TIMEOUT,
													srcId : Ae,
													srcElem : Ai
												})
											}
										}
									}, Aa)
						}
					}
				} else {
					if (typeof AX === O) {
						AX({
							status : I.SWF_FAILED,
							srcId : Ae,
							srcElem : null
						})
					} else {
						throw new R.Error(
								"checkplayer::DoSWF(): SWF could not be loaded.")
					}
				}
			} else {
				if (typeof AX === O) {
					AX({
						status : I.SWF_FAILED,
						srcId : Ae,
						srcElem : null
					})
				} else {
					throw new R.Error(
							"checkplayer::DoSWF(): Minimum Flash Version not detected.")
				}
			}
		}
		var j = {
			playerVersionDetected : AH,
			versionChecked : AG,
			checkPassed : k,
			UpdatePlayer : v,
			DoSWF : function(AR, AS, AP, AQ, AN, AM, AO, AT) {
				q(AR, AS, AP, AQ, AN, AM, AO, AT, X)
			},
			ready : function() {
				return d
			},
			updateable : t,
			updateStatus : p,
			updateControlsContainer : AE
		};
		I._ins = j;
		return j
	};
	I = P.checkplayer;
	I.UPDATE_INIT = 1;
	I.UPDATE_SUCCESSFUL = 2;
	I.UPDATE_CANCELED = 3;
	I.UPDATE_FAILED = 4;
	I.SWF_INIT = 5;
	I.SWF_LOADING = 6;
	I.SWF_LOADED = 7;
	I.SWF_FAILED = 8;
	I.SWF_TIMEOUT = 9;
	I.SWF_EI_READY = 10;
	I.module_ready = function() {
	}
})(window);