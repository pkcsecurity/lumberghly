(ns lumberghly.cljs.model
  (:require [reagent.core :as r]))

(def app-state (r/atom :home))

(def user (r/atom {}))
