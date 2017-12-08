(ns lumberghly.cljs.core
  (:require [reagent.core :as r]
            [goog.dom :as dom]))

(enable-console-print!)

(defn body []
  [:div "Hello world"])

(defn -main [& args]
  (r/render-component [body]
                      (dom/getElement "app")))

(set! (.-onload js/window) -main)
