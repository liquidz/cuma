# cuma

S-expression based micro template in clojure.

[![Build Status](https://travis-ci.org/liquidz/cuma.png?branch=master)](https://travis-ci.org/liquidz/cuma)

## Example

```clojure
(render "hello $(x)" {:x "world"})
;=> hello world

(render "1 + 2 + 3 = $(+ 1 2 3)")
;=> 1 + 2 + 3 = 6

(render "$(if flag \"foo\" \"bar\")" {:flag true})
;=> foo

(render "@(for [x arr]) x = $(x) @(/for)" {:arr [1 2]})
;=> x = 1 x = 2

; test.txt:
;   @(if flag)
;      hello $(x)
;   @(/if)
(render (slurp "test.txt") {:flag true :x "world"})
;=> hello world

; test.txt:
;   @(for [x arr])
;      hello $(x)
;   @(/for)
(render (slurp "test.txt") {:arr ["foo" "bar"]})
;=> hello foo
;   hello bar


(render "hello $(upper \"world\")" {:upper #(.toUpperCase %)})
;=> hello WORLD
```

## Usage

## License

Copyright (C) 2013 Masashi Iizuka([@uochan](http://twitter.com))

Distributed under the Eclipse Public License, the same as Clojure.
