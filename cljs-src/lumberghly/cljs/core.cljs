(ns lumberghly.cljs.core
  (:require [reagent.core :as r]
            [goog.dom :as dom]
            [lumberghly.cljs.views.nav :as nav]
            [lumberghly.cljs.views.person :as person]))

(enable-console-print!)

(defn body []
  [:div.fixed.top-0.left-0.bottom-0.right-0
   [lumberghly.cljs.views.nav/body]
   [:div.m3
    [lumberghly.cljs.views.person/body]]])

(defn -main [& args]
  (r/render-component [body]
                      (dom/getElement "app")))

(set! (.-onload js/window) -main)
