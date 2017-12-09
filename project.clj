(defproject lumberghly "0.1.0-SNAPSHOT"
  :description "A tool for empowering middle management to make small decisions on large problems."
  :url "https://lumbergh.ly"
  :main lumberghly.core
  :plugins [[lein-cljsbuild "LATEST"]
            [lein-cljfmt "LATEST"]]
  :cljfmt {:indents {#".*" [[:inner 0]]}}
  :profiles {:uberjar {:main lumberghly.core, :aot :all}}
  :uberjar-name "lumberghly.org-standalone.jar"
  :min-lein-version "2.8.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.postgresql/postgresql "42.1.4"]
                 [clj-time "0.14.2"]
                 ; cljx deps
                 [org.clojure/clojurescript "LATEST"]
                 [hiccup "LATEST"]
                 [reagent "LATEST"]]
  :clean-targets ["static/development/js"
                  "static/release/js"
                  "static/development/index.js"
                  "static/development/index.js.map"
                  "out"
                  "target"]
  :cljsbuild {:builds [{:id "dev"
                       :source-paths ["cljs-src"]
                       :compiler {:output-to "static/development/index.js"
                                  :source-map true
                                  :output-dir "static/development/js"
                                  :optimizations :none
                                  :main lumberghly.cljs.core
                                  :asset-path "development/js"
                                  :cache-analysis true
                                  :pretty-print true}}
                      {:id "release"
                       :source-paths ["cljs-src"]
                       :compiler {:output-to "static/release/index.js"
                                  :source-map "static/release/index.js.map"
                                  :externs []
                                  :main lumberghly.cljs.core
                                  :output-dir "static/release/js"
                                  :optimizations :advanced
                                  :pseudo-names false}}]})
  
