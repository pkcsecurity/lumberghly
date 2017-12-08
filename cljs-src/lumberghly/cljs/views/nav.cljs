(ns lumberghly.cljs.views.nav
  (:require [reagent.core :as r]
            [lumberghly.cljs.utils :as utils]
            [lumberghly.cljs.model :as m]
            [lumberghly.cljs.utils :as utils]))

(def nav-height "50px")

(defn avatar [hover?]
  [:div.circle.transition
   {:style {:background-image "url('img/user.png')"
            :background-size :cover
            :opacity (if hover? 0.5 1)
            :height "35px"
            :width "35px"}}])

(defn nav-right []
  [:div.p2.flex.justify-center.items-center.dark-gray.right
   [:div.mx1
    [avatar false]]])

(defn body []
  [:div.s1.bg-white.flex.justify-between.items-center.dark-gray.z1
   {:style {:height nav-height}}
   [:div.p2.left ]
   [nav-right]])
