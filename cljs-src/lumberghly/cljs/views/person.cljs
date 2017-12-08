(ns lumberghly.cljs.views.person
  (:require [reagent.core :as r]
            [lumberghly.cljs.utils :as utils]))

(defn project-card []
  [:div.m2.p2.border.rounded
   {:style {:width "400px"}}
   [:div  
    "Name"]
   [:div 
    "Activity"]])

(defn cont-act []
  [:div.m2.p2.border.rounded
   {:style {:width "600px"}}
   "Pretty graph here"])

(defn body []
  [:div.p2.justify-center.items-center
   [:h2 "Top Contributed Projects"]
   [:div.flex
    [project-card]
    [project-card]
    [project-card]]
   [:h2 "Contribution Activity over the last year"]
   [cont-act]])
