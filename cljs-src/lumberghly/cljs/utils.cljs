(ns lumberghly.cljs.utils
  (:require [clojure.string :as str]
            [goog.dom :as dom]))

(defn uid [] (str (gensym)))

(defn uid->elem [uid]
  (dom/getElement uid))

(defn focus-uid [uid]
  (when-let [elem (uid->elem uid)]
    (.focus elem)))

(defn off [a]
  (fn [] (reset! a false)))

(defn on [a]
  (fn [] (reset! a true)))

(defn toggle [a]
  (fn [] (swap! a not)))

(def not-blank? (complement str/blank?))

(def blank? str/blank?)

(def trim str/trim)

(def nop (constantly nil))
