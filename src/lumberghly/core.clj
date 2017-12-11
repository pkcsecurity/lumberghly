(ns lumberghly.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]
            [clj-time.core :as t]
            [clj-time.coerce :as coerce])
  (:gen-class))

(def pg-db {:dbtype "postgresql"
            :dbname "lumberghly"
            :host "localhost"
            :user "lumberghly_dev"
            :password "changeme"})

(defn upsert!
  "Updates columns or inserts a new row in the specified table"
  [db table row where-clause]
  (jdbc/with-db-transaction [t-con db]
    (let [result (jdbc/update! t-con table row where-clause)]
      (if (zero? (first result))
        (jdbc/insert! t-con table row)
        result))))

(defn get-token [] (System/getenv "BC3_ACCESS_TOKEN"))

(defn jsread [s]
  (json/read-str s :key-fn #(keyword %)))

(defn get-tenants []
  (let [{:keys [body]} @(http/get "https://launchpad.37signals.com/authorization.json" {:oauth-token (get-token)})
        authorization (jsread body)
        accounts (:accounts authorization)]
    accounts))

(defn get-api-uri
  ([] (str (:href (first (get-tenants)))))
  ([section] (str (:href (first (get-tenants))) "/" section)))

(defn sanity-check []
  (jdbc/query pg-db ["SELECT * FROM actions"])

  )

(defn store-metadata [obj type]
  (let [email (get-in obj [:creator :email_address])
        {:keys [created_at updated_at] :as m} obj
        person-id (get-in obj [:creator :id])
        project-id (get-in obj [:bucket :id])]
    (upsert! pg-db :persons
             {:id person-id
              :name (get-in obj [:creator :name])
              :email email}
             ["id = ?" person-id])
    (upsert! pg-db :actions
             {:id (:id obj)
              :timestamp (max (coerce/to-long updated_at) (coerce/to-long created_at))
              :action (clojure.string/upper-case type)
              :project_id project-id
              :person_id person-id}
             ["id = ?" (:id obj)])
    (upsert! pg-db :personsprojects
             {:project_id project-id
              :person_id person-id}
             ["project_id = ? AND person_id = ?" project-id person-id])))

(defn crawl-comments [url]
  (let [comments (jsread (:body @(http/get url {:oauth-token (get-token)})))]
    (doseq [c comments]
      (let [{:keys [created_at updated_at] :as metadata} c]
        (println (str created_at " " updated_at))
        (println (type created_at))
        (store-metadata c "COMMENT")
        )
      )
    ))

(defn crawl-messages [url]
  (let [messages (jsread (:body @(http/get url {:oauth-token (get-token)})))]
    (doseq [m messages]
      (println m)
      (store-metadata m "MESSAGE")
      (crawl-comments (:comments_url m)))

    ))

(defn crawl-message-board [project]
  (let [board (first (filter (fn [x] (= (:name x) "message_board")) (:dock project))) ;; I thought projects only had one board...
        url (:url board)
        board-meta (jsread (:body @(http/get url {:oauth-token (get-token)})))]
    (crawl-messages (:messages_url board-meta))))

(defn insert-project [project]
  (upsert! pg-db :projects
           {:id (:id project)
            :name (:name project)
            :description (:description project)
            :url (:app_url project)}
           ["id = ?" (:id project)]))

(defn crawl-projects []
  (let [projects (jsread (:body @(http/get (get-api-uri "projects.json") {:oauth-token (get-token)})))]
    (doseq [p projects]
      (insert-project p)
      (crawl-message-board p))))

(defn get-n-days-actions [n]
  (let [now (t/now)
        then (t/minus (t/now) (t/days n))
        res (jdbc/query pg-db ["SELECT * from actions INNER JOIN persons on actions.person_id=persons.id INNER JOIN projects on actions.project_id=projects.id WHERE timestamp >= ?" (coerce/to-long then)])]
    (loop [[hd & tl] res
           acc {}]
      (let [k (:name hd)
            v hd]
        (if tl
          (recur tl (assoc acc k (conj (get acc k []) v)))
          acc)))))

(defn get-stats [m]
  (let [people (keys m)]
    (into
      {}
      (for [[person l] m]
        [person (count l)]))))

(defn -main [& args]
  #_(println (get-n-days-actions (int (first args))))
  (doseq [x (reverse (sort-by val (get-stats (get-n-days-actions (int (first args))))))]
    (println (str (first x) ":") (second x))))
