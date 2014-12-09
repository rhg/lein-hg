(ns leiningen.vcs.hg
  (:require [leiningen.core.eval :as eval]
            [leiningen.core.main :as main]
            [leiningen.vcs :as vcs]))

(defmethod vcs/push :hg [project & args]
  (binding [eval/*dir* (:root project)]
    (apply eval/sh "hg" "push" args)))

(defmethod vcs/commit :hg [project]
  (binding [eval/*dir* (:root project)]
    (eval/sh "hg" "commit" "-m" (str "Version " (:version project)))))

(defmethod vcs/tag :hg [{:keys [root version]} & [prefix]]
  (binding [eval/*dir* root]
    (let [tag (if prefix
                (str prefix version)
                version)]
      (eval/sh "hg" "tag" tag "-m" (str "Release " version)))))

(defmethod vcs/assert-committed :hg [project]
  (binding [eval/*dir* (:root project)]
    (when (re-find #"."
                   (with-out-str (eval/sh "hg" "status")))
       (main/abort "Uncommitted changes in" (:root project) "directory."))))
