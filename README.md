# cuma

Extensible micro template engine for Clojure.

[![Build Status](https://travis-ci.org/liquidz/cuma.png?branch=master)](https://travis-ci.org/liquidz/cuma)

## Installation

Add following dependency to your `profject.clj`.
```clojure
[cuma "0.0.5"]
```

## Usage

require `[cure.core :refer [render]]`

### Replace Variable

```clojure
(render "hello $(x)" {:x "world"})
;=> hello world
```

Replace escaped variable.

```clojure
(render "$(x)" {:x "<h1>"})
;=> &lt;h1&gt;
```

Replace unescaped variable.

```clojure
(render "$(raw x)" {:x "<h1>"})
;=> <h1>
```

Include another template.

```clojure
(render "$(include tmpl)" {:tmpl "hello $(x)" :x "world"})
;=> hello world
```

Apply custom function to variable.
Function detail is explained at following.

```clojure
(render "$(upper x)" {:upper (fn [data s] (.toUpperCase s)) :x "hello")
;=> HELLO
```

Chain custom functions.

```clojure
(let [f (fn [_ arg] (str "foo " arg))
      g (fn [_ arg] (str "bar " arg))]
 (render "$(-> x g f)" {:f f :g g :x "baz"}))
;=> "foo bar baz"
```

### Replace Section
#### if section

```clojure
(render "@(if flg) foo @(end)" {:flg true})
;=> foo
(render "@(if flg) foo @(end)" {:flg false})
;=>
```

Implicit variable `$(.)` is binded in `if` section.

```clojure
(render "@(if x) $(.) @(end)" {:x "hello"})
;=> hello
```

Map data is expanded to variable in `if` section.

```clojure
(render "@(if m) $(n) @(end)" {:m {:n "foo"}})
;=> foo
```

#### for section

Implicit variable `$(.)` is binded in `for` section.

```clojure
(render "@(for arr) $(.) @(end)" {:arr [1 2 3]})
;=> 1 2 3
```

Map data is expanded to variable in `for` section.

```clojure
(render "@(for arr) $(n) @(end)" {:arr [{:n 1} {:n 2} {:n 3}]})
;=> 1 2 3

(render "@(for arr1) @(for arr2) $(a)$(b) @(end) @(end)"
        {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]})
;=> 13 14 23 24
```

#### let section

```clojure
(render "@(let :x \"foo\" :y 123) $(x) $(y) @(end)" {})
;=> foo 123
```

#### custom section
```clojure
(render "@(foo) world @(end)" {:foo (fn [data body] (str "hello " body))})
;=> hello world

(render "@(foo x) world @(end)"
        {:foo (fn [data body arg] (str arg " " body)) :x "hello"})
;=> hello world
```

### Dotted Variable

```clojure
(render "$(a.b.c)" {:a {:b {:c "hello"}}})
;=> hello
```

### Not Supporting Form
```clojure
; NOT SUPPORTED: nested variable
(render "$(f (g x))" {...})
```


## Extension

Replacing variable and section are allowd to use custom function,
and cuma allows you to make custon function as extension.

Cuma searches `cuma.extension.*` namespaces, and load all public functions as extension.

`raw`, `->`, `include`, `if`, `for` are also extension.
https://github.com/liquidz/cuma/blob/master/src/cuma/extension/core.clj

### Variable Extension

```clojure
(render "$(f x y z)" {:x 1 : y 2 :z 3 :foo "bar"})
```

```clojure
(ns cuma.extension.YOUR_EXTENSION_NAME)

(defn f
  "@data => {:x 1 :y 2 :z 3 :foo "bar" :render #'cuma.core/render, OTHER_EXTENSIONS}
   @args => [1 2 3]"
  [data & args]
  (apply + args))
```

### Section Extension

```clojure
(render "@(f x y z) world @(end)" {:x 1 :y 2 :z 3 :foo "bar"})
```

```clojure
(ns cuma.extension.YOUR_EXTENSION_NAME)

(defn f
  "@data => {:x 1 :y 2 :z 3 :foo "bar" :render #'cuma.core/render, OTHER_EXTENSIONS}
   @body => " hello "
   @args => [1 2 3]"
  [data body & args]
  ((:render data) (str "hello" body)))
```

## Performance
test code: https://gist.github.com/liquidz/5381090

<img src="https://docs.google.com/spreadsheet/oimg?key=0AtD8N5xmx-U-dENUYTNQdWJwSUxXWkhyQjFfOXJMUHc&oid=5&zx=p7l2q58ev5i3" />


## License

Copyright (C) 2014 Masashi Iizuka([@uochan](http://twitter.com))

Distributed under the Eclipse Public License, the same as Clojure.
