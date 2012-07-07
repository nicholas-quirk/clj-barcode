(ns clj-barcode.core
  (:import [net.sourceforge.barbecue Barcode BarcodeFactory BarcodeImageHandler Modulo10]
           [net.sourceforge.barbecue.env DefaultEnvironment EnvironmentFactory]
           [net.sourceforge.barbecue.formatter SVGFormatter]
           [net.sourceforge.barbecue.linear.code128.ModuleFactory]
           [net.sourceforge.barbecue.linear.code39.ModuleFactory]
           [net.sourceforge.barbecue.linear.postnet.ModuleFactory]
           [net.sourceforge.barbecue.linear.upc.ModuleFactory]))

;; Barcode Factory
(defn create-barcode [barcode data & args]
  (let [args (apply hash-map args)]
    (condp = barcode
      :2of7 (BarcodeFactory/create2of7 data)
      :3of9 (BarcodeFactory/create3of9 data (:require-checksum args))
      :bookland (BarcodeFactory/createBookland data)
      :codabar (BarcodeFactory/createCodabar data)
      :code128 (BarcodeFactory/createCode128 data)
      :code128A (BarcodeFactory/createCode128A data)
      :code128B (BarcodeFactory/createCode128B data)
      :code128C (BarcodeFactory/createCode128C data)
      :code39 (BarcodeFactory/createCode39 data (:require-checksum args))
      :ean128 (BarcodeFactory/createEAN128 data)
      :ean13 (BarcodeFactory/createEAN13 data)
      :global-trade-item-number (BarcodeFactory/createGlobalTradeItemNumber data)
      :int2of5 (if (empty? args) (BarcodeFactory/createInt2of5 data) (BarcodeFactory/createInt2of5 data (:check-digit args)))
      :monarch (BarcodeFactory/createMonarch data)
      :nw7 (BarcodeFactory/createNW7 data)
      :pdf417 (BarcodeFactory/createPDF417 data)
      :postNet (BarcodeFactory/createPostNet data)
      :random-weight-upca (BarcodeFactory/createRandomWeightUPCA data)
      :scc14-shipping-code (BarcodeFactory/createSCC14ShippingCode data)
      :shipment-id-num (BarcodeFactory/createShipmentIdentificationNumber data)
      :sscc18 (BarcodeFactory/createSSCC18 data)
      :std2of5 (if (empty? args) (BarcodeFactory/createStd2of5 data) (BarcodeFactory/createStd2of5 data (:check-digit args)))
      :ucc128 (BarcodeFactory/createUCC128 data (:app-id args))
      :upca (BarcodeFactory/createUPCA data)
      :usd3 (BarcodeFactory/createUSD3 data (:require-checksum args))
      :usd4 (BarcodeFactory/createUSD4 data)
      :usps (BarcodeFactory/createUSPS data)
      :parseEAN128 (BarcodeFactory/parseEAN128 data)
  )))

(defn convert-to-awt-image [barcode]
  (BarcodeImageHandler/getImage barcode))

(defn output-barcode [barcode output-stream format]
  (condp = format
    :gif (BarcodeImageHandler/writeGIF barcode output-stream)
    :jpeg (BarcodeImageHandler/writeJPEG barcode output-stream)
    :png (BarcodeImageHandler/writePNG barcode output-stream)))

(defn list-image-formats []
  (apply hash-set (BarcodeImageHandler/getImageFormats)))

(defn save-to-file [barcode file format]
  (let [f (java.io.File. file) ]
	  (condp = format
	    :gif (BarcodeImageHandler/saveGIF barcode f)
      :jpeg (BarcodeImageHandler/saveJPEG barcode f)
      :png (BarcodeImageHandler/savePNG barcode f))))

;; Getters / Setters
(defn bar-height [barcode & args]
  (if (empty? args)
    (.getHeight barcode)
    (.setBarHeight barcode (first args))))

(defn bar-width [barcode & args]
  (if (empty? args)
    (.getWidth barcode)
    (.setBarWidth barcode (first args))))

(defn bar-res [barcode & args]
  (if (empty? args)
    (.setResolution barcode (first args))))

(defn bar-svg [barcode & args]
  (if (empty? args)
    (.getSVG barcode)
    (let [args (apply hash-map args)]
      (.setSVGScalar barcode (:scalar args) (:units args)))))

(defn bar-data [barcode]
  (.getData barcode))

(defn drawing-text? [barcode & args]
  (if (empty? args)
    (.isDrawingText barcode)
    (.setDrawingText barcode (first args))))

;; Modulo Calculation
(defn mod-10-check-digit [data & args]
  (let [args-num (/ (count args) 2) args (apply hash-map args)]
    (condp = args-num
      1 (Modulo10/getMod10CheckDigit data (:weight-odd args))
      2 (Modulo10/getMod10CheckDigit data (:weight-even args) (:weight-odd args))
      3 (Modulo10/getMod10CheckDigit data (:weight-even args) (:weight-odd args) (:begin-even? args)))))

;; Environment
(defn env []
  (EnvironmentFactory/getEnvironment))

(defn env-font []
  (.getDefaultFont (env)))

(defn env-resolution []
    (.getResolution (env)))

(defn use-env-mode [mode & args]
  (condp = mode
    :default-env (EnvironmentFactory/setDefaultMode)
    :headless (EnvironmentFactory/setHeadlessMode)
    :non-awt (if (empty? args) (EnvironmentFactory/setNonAWTMode) (EnvironmentFactory/setNonAWTMode (first args)))))

;; Module Factory Methods
(defn module-index [bar-type key & args]
  (let [args (apply hash-map args)]
	  (condp = bar-type
	    :code128 (net.sourceforge.barbecue.linear.code128.ModuleFactory/getIndex key (:mode args))
	    :code39 (net.sourceforge.barbecue.linear.code39.ModuleFactory/getIndex key)
      :postnet (net.sourceforge.barbecue.linear.postnet.ModuleFactory/getIndex key))))

(defn module [bar-type key & args]
  (let [args (apply hash-map args)]
    (condp = bar-type
      :code128 (net.sourceforge.barbecue.linear.code128.ModuleFactory/getModule key (:mode args))
      :code39 (net.sourceforge.barbecue.linear.code39.ModuleFactory/getModule key)
      :postnet (net.sourceforge.barbecue.linear.postnet.ModuleFactory/getModule key)
      :upc (net.sourceforge.barbecue.linear.upc.ModuleFactory/getModule key (:position args)))))

(defn module-for-index [bar-type & args]
  (let [args (apply hash-map args)]
    (condp = bar-type
      :code128 (net.sourceforge.barbecue.linear.code128.ModuleFactory/getModuleForIndex (:index args) (:mode args))
      :code39 (net.sourceforge.barbecue.linear.code39.ModuleFactory/getModuleForIndex (:index args))
      :postnet (net.sourceforge.barbecue.linear.postnet.ModuleFactory/getModuleForIndex (:index args))
      :upc (net.sourceforge.barbecue.linear.upc.ModuleFactory/getModuleForIndex (:index args)))))

(defn has-module? [bar-type key & args]
  (let [args (apply hash-map args)]
    (condp = bar-type
      :code39 (net.sourceforge.barbecue.linear.code39.ModuleFactory/hasModule key (:extended-mode? args))
      :upc (net.sourceforge.barbecue.linear.upc.ModuleFactory/hasModule key))))

;; Barcode Specific
(defn valid-upc? [key]
  (net.sourceforge.barbecue.linear.upc.ModuleFactory/isValid key))

(defn code39-extended-character [c]
  (net.sourceforge.barbecue.linear.code39.ModuleFactory/getExtendedCharacter c))
                                                                              