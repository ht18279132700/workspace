function relatedSelect() {
	var b = arguments;
	relatedSelect.data = {};
	if (b.length <= 1) {
		return
	}
	for ( var c = 0; c < b.length; c++) {
		b[c].fnName = b[c].id + "callback";
		b[c].obj = g(b[c].id);
		b[c].initurl = b[c].url;
		b[c].obj.index = c;
		b[c].obj.options[0].selected = true;
		b[c].obj.oriText = b[c].obj.options[0].text;
		if (b[c].url && c === 0 && !b[c].load) {
			b[c].obj.onmouseover = function() {
				var k = this.index;
				b[k].url += "?callback=" + b[k].fnName;
				h(b[k]);
				this.onmouseover = this.onfocus = null
			}
		}
		if (c > 0) {
			b[c].obj.disabled = true
		}
		if (c < b.length - 1) {
			b[c].obj.onchange = function() {
				var q, m, p = this.options, k = p.length, t = this.index | 0;
				var s = p[p.selectedIndex];
				q = s.getAttribute("custom");
				m = q && q.indexOf("+") != -1;
				q = q !== window.undefined ? q : this.value;
				b[t].pointer = b[t].id + q;
				for ( var l = t + 1; l < b.length; l++) {
					if (!b[l].url || b[l].initurl.indexOf("=") == -1) {
						return
					}
					if (l == t + 1 && !m && q) {
						var r = b[l];
						if (r.loadTimer) {
							window.clearTimeout(r.loadTimer)
						}
						r.loadTimer = window.setTimeout(function() {
							r.obj.options[0].text = "数据加载中..."
						}, 200);
						b[l].url = b[l].initurl + q + "&callback="
								+ b[l].fnName;
						h(b[l], b[t].pointer)
					}
					d(b[l].obj);
					b[l].obj.disabled = true
				}
			}
		}
		if (b[c].url && b[c].load) {
			if (b[c].url.indexOf("=") > 0) {
				b[c].url += "&callback=" + b[c].fnName
			} else {
				b[c].url += "?callback=" + b[c].fnName
			}
			h(b[c])
		}
	}
	function e(o, m) {
		var n = document.createElement("option");
		n.setAttribute("value", o.value);
		n.appendChild(document.createTextNode(o.text));
		var k = o.attrs;
		for ( var l in k) {
			if (k.hasOwnProperty(l) && k[l] !== m) {
				if (l == "className") {
					n.className = k[l]
				} else {
					n.setAttribute(l, k[l])
				}
			}
		}
		return n
	}
	function a(l, i) {
		var k = document.createElement("optgroup");
		k.label = l.label;
		return k
	}
	function h(m, i, l) {
		var k = relatedSelect.data[i] !== l;
		window[m.fnName] = function(q) {
			if (m.loadTimer) {
				window.clearTimeout(m.loadTimer)
			}
			var o, r = m.node;
			for ( var p in q) {
				if (p == r.root) {
					o = q[p]
				}
			}
			if (!o || o === "" || o.length < 1) {
				return
			}
			window.setTimeout(function() {
				m.obj.removeAttribute("disabled");
				m.obj.disabled = false;
				var t = document.createDocumentFragment();
				var v;
				for ( var s in o) {
					var w, u, n, y = o[s][r.value], x = o[s][r.text];
					r.custom && (w = o[s][r.custom]);
					u = o[s]["disabled"];
					if (u !== window.undefined) {
						u = "disabled"
					}
					n = o[s]["caption"];
					if (!y || !x) {
						continue
					}
					if (n !== window.undefined) {
						t.appendChild(a({
							label : x
						}))
					} else {
						t.appendChild(e({
							value : y,
							text : x,
							attrs : {
								custom : w
							}
						}))
					}
				}
				m.obj.appendChild(t)
			}, 0);
			relatedSelect.data[i] = q
		};
		if (k) {
			window[m.fnName](relatedSelect.data[i])
		}
		j(k, m.url, function() {
			if (m.obj.oriText) {
				m.obj.options[0].text = m.obj.oriText
			}
			if (m.defaultValue) {
				setTimeout(function() {
					m.obj.value = m.defaultValue;
					m.defaultValue = null;
					f(m.obj, "change")
				}, 300)
			}
		})
	}
	function f(l, i) {
		var m = document;
		var k = m.createEventObject ? m.createEventObject() : m
				.createEvent("MouseEvents");
		if (!document.all) {
			k.initMouseEvent(i, true, true, document.defaultView, 0, 0, 0, 0,
					0, false, false, false, false, 0, null);
			l.dispatchEvent(k)
		} else {
			l.fireEvent("on" + i, k)
		}
	}
	function d(k) {
		k.style.visibility = "hidden";
		var i = k.options[0];
		k.innerHTML = "";
		k.options[0] = i;
		k.style.visibility = "visible"
	}
	function j(m, o, p) {
		p = p || function() {
		};
		if (m) {
			return p(false)
		}
		window.__needJS__ = [];
		var l = window.__needJS__ || (window.__needJS__ = []), n = l[o]
				|| (l[o] = {
					loaded : false,
					callbacks : []
				});
		
		var k = n.callbacks;
		if (k.push(p) == 1) {
			var i = document.createElement("script");
			i.onload = i.onreadystatechange = function() {
				var q = i.readyState;
				if (q && q != "loaded" && q != "complete") {
					return
				}
				i.onload = i.onreadystatechange = null;
				n.loaded = true;
				for ( var r = 0; r < k.length; r++) {
					k[r](true);
				}
			};
			i.src = o;
			document.getElementsByTagName("head")[0].appendChild(i)
		}
	}
	function g(i) {
		return document.getElementById(i)
	}
}
function attachSearchCar(b) {
	var c = b.form, g = b.carBtn, e = b.priceBtn, i = b.shangjiaBtn, a = b.imgBtn, d = b.byBtn, f;
	if (g) {
		g.onclick = function() {
			var l = c.bid && c.bid.value, j = c.sid && c.sid.value, k = c.mid
					&& c.mid.value;
			if (!l) {
				f = h("http://price.pcauto.com.cn/cars/")
			}
			if (l) {
				f = h("http://price.pcauto.com.cn/price/nb" + l + "/")
			}
			if (j) {
				if (/\+\d+/i.test(j)) {
					f = h("http://price.pcauto.com.cn/price/b"
							+ j.replace("+", "") + "/")
				} else {
					f = h("http://price.pcauto.com.cn/sg" + j + "/")
				}
			}
			if (l && k) {
				if (!(/\+\d+/i.test(k))) {
					f = h("http://price.pcauto.com.cn/m" + k + "/")
				} else {
					f = h("http://price.pcauto.com.cn/price/s"
							+ k.replace("+", "") + "/")
				}
			}
			f.run();
			return false
		}
	}
	if (e) {
		e.onclick = function() {
			var l = c.bid && c.bid.value, j = c.sid && c.sid.value, k = c.mid
					&& c.mid.value;
			if (!l) {
				f = h("http://price.pcauto.com.cn/")
			}
			if (l) {
				f = h("http://price.pcauto.com.cn/price/nb" + l + "/")
			}
			if (j) {
				if (/\+\d+/i.test(j)) {
					f = h("http://price.pcauto.com.cn/price/b"
							+ j.replace("+", "") + "/")
				} else {
					f = h("http://price.pcauto.com.cn/price/sg" + j + "/")
				}
			}
			if (l && k) {
				if (/\+\d+/i.test(k)) {
					f = h("http://price.pcauto.com.cn/price/s"
							+ k.replace("+", "") + "/")
				} else {
					f = h("http://price.pcauto.com.cn/m" + k + "/")
				}
			}
			f.run();
			return false
		}
	}
	if (i) {
		i.onclick = function() {
			var m = c.bid && c.bid.value, j = c.sid && c.sid.value, l = c.mid
					&& c.mid.value, n = c.rid && c.rid.value;
			n = n ? "/c" + n : "";
			var k;
			if (!m && !n) {
				f = h("http://price.pcauto.com.cn/shangjia/")
			}
			if (n) {
				k = "http://price.pcauto.com.cn/shangjia" + n + "/";
				if (m) {
					k = "http://price.pcauto.com.cn/shangjia" + n + "/nb" + m
							+ "/"
				}
				if (j) {
					if (/\+\d+/i.test(j)) {
						k = "http://price.pcauto.com.cn/shangjia" + n + "/b"
								+ j.replace("+", "") + "/"
					} else {
						k = "http://price.pcauto.com.cn/shangjia" + n + "/sg"
								+ j + "/"
					}
				}
				f = h(k)
			} else {
				if (m) {
					f = h("http://price.pcauto.com.cn/index_djump.jsp", {
						fip : 1,
						bid : m
					})
				}
				if (j) {
					if (/\+\d+/i.test(j)) {
						f = h("http://price.pcauto.com.cn/index_djump.jsp", {
							fip : 1,
							mfid : j.replace("+", "")
						})
					} else {
						f = h("http://price.pcauto.com.cn/index_djump.jsp", {
							fip : 1,
							sid : j
						})
					}
				}
			}
			f.run();
			return false
		}
	}
	if (a) {
		a.onclick = function() {
			var l = c.bid && c.bid.value, j = c.sid && c.sid.value, k = c.mid
					&& c.mid.value;
			if (!l) {
				f = h("http://price.pcauto.com.cn/cars/pic.html")
			}
			if (l) {
				f = h("http://price.pcauto.com.cn/cars/nb" + l + "/")
			}
			if (j) {
				if (/\+\d+/i.test(j)) {
					f = h("http://price.pcauto.com.cn/cars/b"
							+ j.replace("+", "") + "/")
				} else {
					f = h("http://price.pcauto.com.cn/cars/sg" + j + "/")
				}
			}
			if (l && k) {
				if (/\+\d+/i.test(k)) {
					f = h("http://price.pcauto.com.cn/cars/s"
							+ k.replace("+", "") + "-o1/")
				} else {
					f = h("http://price.pcauto.com.cn/cars/m" + k + "-o1/")
				}
			}
			f.run();
			return false
		}
	}
	if (d) {
		d.onclick = function() {
			var l = c.bid && c.bid.value, j = c.sid && c.sid.value, k = c.mid
					&& c.mid.value;
			if (!l) {
				alert("请选择品牌和车系");
				return false
			}
			if (!j) {
				alert("请选择车系");
				return false
			}
			if (j) {
				f = h("http://price.pcauto.com.cn/sg" + j + "/baoyang.html")
			}
			if (l && k) {
				f = h("http://price.pcauto.com.cn/m" + k + "/baoyang.html")
			}
			f.run();
			return false
		}
	}
	function h(o, n) {
		var j = document.body || document.documentElement;
		var m = document.createElement("form");
		m.style.display = "none";
		m.action = o;
		m.method = "GET";
		m.target = "_blank";
		if (n) {
			for ( var l in n) {
				var k = document.createElement("input");
				k.type = "text";
				k.name = l;
				k.value = n[l];
				m.appendChild(k)
			}
		}
		m = j.appendChild(m);
		m.run = function() {
			m.submit();
			window.setTimeout(function() {
				j.removeChild(m)
			}, 10)
		};
		return m
	}
};