(ns misplaced-villages-storage.repo
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [monger.core :as mg]
            [monger.collection :as mc]
            [misplaced-villages.game :as game]
            [monger.operators :refer :all])
   (:import org.bson.types.ObjectId))

(defprotocol Repo
  (insert-game [this game])
  (remove-game [this id])
  (remove-games [this])
  (game [this id])
  (games [this])
  (games-between [this players])
  (games-for [this player]))

(defrecord AtomRepo [state]
  Repo
  (insert-game [this game]
    (swap! state
           (fn [{:keys [last-id games]}]
             (let [id (inc last-id)]
             {:last-id id
              :games (conj games (assoc game ::game/id (str id)))})))
    (str (:last-id @state)))
  (remove-games [this]
    (swap! state (constantly {:last-id 0 :games []})))
  (game [this id]
    (first (filter #(= (::game/id %) id) (:games @state))))
  (games [this]
    (:games @state))
  (games-between [this players]
    (filter #(= (set (::game/players %)) (set players)) (:games @state)))
  (games-for [this player]
    (filter #(contains? (set (::game/players %)) player) (:games @state))))

(defn atom-repo
  [& _]
  (AtomRepo. (atom {:last-id 0 :games []})))

(defn prepare
  [game]
  (-> game
      (assoc ::game/id (str (:_id game)))
      (dissoc :_id)))

(defrecord MongoRepo [db]
  Repo
  (insert-game [this game]
    (str (:_id (mc/insert-and-return db "games" game))))
  (remove-games [this]
    (mc/remove db "games"))
  (game [this id]
    (when-let [game (mc/find-one-as-map db "games" {:_id (ObjectId. id)})]
      (-> game (assoc ::game/id id) (dissoc :_id))))
  (games [this]
    (map prepare (mc/find-maps db "games")))
  (games-between [this players]
    (map prepare (seq (mc/find-maps db "games" {::game/players {$all players}}))))
  (games-for [this player]
    (map prepare (seq (mc/find-maps db "games" {::game/players player})))))

(defn mongo-repo
  [{:keys [host port database]}]
  (MongoRepo. (-> (mg/connect {:host host :port port})
                  (mg/get-db database))))
