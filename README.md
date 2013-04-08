# cuma

S-expression based micro template in clojure.

[![Build Status](https://travis-ci.org/liquidz/cuma.png?branch=master)](https://travis-ci.org/liquidz/cuma)

## Example

```clojure
(render "hello $(x)" {:x "world"})
;=> hello world

(render "$(escape x)" {:x "<h1>"})
;=> &lt;h1&gt;

(render "$(upper x)" {:upper (fn [data s] (.toUpperCase s)) :x "hello")
;=> HELLO

(render "@(if flg) foo @(/if)" {:flg true})
;=> foo
(render "@(if flg) foo @(/if)" {:flg false})
;=>
(render "@(if x) $(.) @(/if)" {:x "hello"})
;=> hello
(render "@(if m) $(n) @(/if)" {:m {:n "foo"}})
;=> foo

(render "@(for arr) $(.) @(/for)" {:arr [1 2 3]})
;=> 1 2 3
(render "@(for arr) $(n) @(/for)" {:arr [{:n 1} {:n 2} {:n 3}]})
;=> 1 2 3

(render "@(for arr1) @(for arr2) $(a)$(b) @(/for) @(/for)"
        {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]})
;=> 13 14 23 24

(render "@(foo) world @(/foo)" {:foo (fn [data body] (str "hello " body))})
;=> hello world

(render "@(foo x) world @(/foo)"
        {:foo (fn [data body arg] (str arg " " body)) :x "hello"})
;=> hello world

(render "$(include tmpl)" {:tmpl "hello $(x)" :x "world"})
;=> hello world
```

```clojur
; NOT SUPPORTED: nested variable
(render "$(f (g x))" {...})
```


## Extension


## Usage

## License

Copyright (C) 2013 Masashi Iizuka([@uochan](http://twitter.com))

Distributed under the Eclipse Public License, the same as Clojure.
