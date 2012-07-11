# clj-barcode

Clojure wrapper around the Apache Barbecue 1.5 beta library.

## Why?
This is for smaller businesses that are integrating Clojure into their core base. This wrapper provides a light weight solution to an existing library already in use. The goal was to provide a more idomatic way to generate barcodes in Clojure. 1.5 beta was chosen because the last stable release was in 2005. This release as at least 2007.

## What?
This wrapper currently only contains the functionality to generate barcode objects and image files (PNG, JPEG, GIF). There are GUI API's for Swing components, but in the interest in keeping this small I've left those out.

## Usage

Unfortunately, the Barbecue library is not currently in a Leiningen or Maven repository. The source and binaries can be downloaded from [here](http://sourceforge.net/projects/barbecue/files/barbecue/1.0.6d/). So download the jar and link it to your projects build path.

## Sample Code

Most of the function are called the same way. A keyword of the barcode you want to generate and a string of the data is usually required. Any functions that have variable arguments are passed using key value pairs.

To create a new barcode:
```clojure
(create-barcode :int2of5 "12345" :check-digit true)
```

Write barcode as image file to disk:
```clojure
(save-to-file (create-barcode :2of7 "12345") "/home/user/barcode.png" :png)
```

A getter and setter functions are one in the same. If a value is passed with a barcode, then the value will be set for that barcode.
```clojure
(bar-width (create-barcode :bookland "12345")) ;; Getter
(bar-width (create-barcode :bookland "12345") 100) ;; Setter
```
Some utility functions are included:
```clojure
(mod-10-check-digit "12345" :weight-odd 10)

(valid-upc? "12345")

(code39-extended-character \x)
```
