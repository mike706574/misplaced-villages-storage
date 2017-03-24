(ns misplaced-villages-storage.repo-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [misplaced-villages.game :as game]
            [misplaced-villages-storage.repo :as repo]))

(defn dummy-game
  [player-1 player-2]
  {::game/players (vector player-1 player-2)})

(defn test-repo
  [repo]
  (let [mike-and-abby-1 (dummy-game "Mike" "Abby")
        mike-and-abby-2 (dummy-game "Abby" "Mike")
        bob-and-mike-1 (dummy-game "Bob" "Mike")]
    (repo/remove-games repo)
    (is (empty? (repo/games repo)))
    (let [id-1 (repo/insert-game repo mike-and-abby-1)]
      (is (= (assoc mike-and-abby-1 ::game/id id-1)
             (repo/game repo id-1)))
      (is (= #{(assoc mike-and-abby-1 ::game/id id-1)}
             (set (repo/games repo))))
      (is (= (assoc mike-and-abby-1 ::game/id id-1)
             (repo/game repo id-1)))
      (let [id-2 (repo/insert-game repo mike-and-abby-2)]

        (is (= #{(assoc mike-and-abby-1 ::game/id id-1)
                 (assoc mike-and-abby-2 ::game/id id-2)
                 (set (repo/games repo))}))
        (is (= (assoc mike-and-abby-1 ::game/id id-1)
               (repo/game repo id-1)))
        (is (= (assoc mike-and-abby-2 ::game/id id-2)
               (repo/game repo id-2)))
        (let [id-3 (repo/insert-game repo bob-and-mike-1)]
          (is (= #{(assoc mike-and-abby-1 ::game/id id-1)
                   (assoc mike-and-abby-2 ::game/id id-2)
                   (assoc bob-and-mike-1 ::game/id id-3)}
                 (set (repo/games repo))))
          (is (= #{(assoc bob-and-mike-1 ::game/id id-3)}
                 (set (repo/games-for repo "Bob"))))
          (is (= #{(assoc mike-and-abby-1 ::game/id id-1)
                   (assoc mike-and-abby-2 ::game/id id-2)}
                 (set (repo/games-for repo "Abby"))))
          (is (= #{(assoc mike-and-abby-1 ::game/id id-1)
                   (assoc mike-and-abby-2 ::game/id id-2)}
                 (set (repo/games-between repo ["Abby" "Mike"]))))
          (repo/remove-games repo)
          (is (empty? (repo/games repo))))))))

(deftest atom-repo
  (test-repo (repo/atom-repo)))

(deftest mongo-repo
  (test-repo (repo/mongo-repo {:host "localhost"
                               :port 27017
                               :database "misplaced-villages-unit"})))
