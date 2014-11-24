;;; Functional Programming Patterns
;;; Mutual recursion example using a finite state machine

;; defs the supplied var names with no bindings, useful for making forward declarations.
(declare plasma vapor liquid solid)

(defn plasma [[transition & rest-transition]]
  (do (println "plasma now")
      ;; #(...) is shorthand for anonymous function. This whole function returns a closure
      #(case transition
         ;; If it ends, valid
         nil true
         ;; Only way out is deionization
         :deionization (vapor rest-transition)
         ;; Otherwise invalid
         (do (println (str transition " is invalid"))
             false))))

(defn vapor [[transition & rest-transition]]
  (do (println "vapor now")
      #(case transition
         nil true
         :ionization   (plasma rest-transition)
         :condensation (liquid rest-transition)
         :deposition   (solid  rest-transition)
         (do (println (str transition " is invalid"))
             false))))

(defn liquid [[transition & rest-transition]]
  (do (println "liquid now")
      #(case transition
         nil true
         :vaporization (vapor rest-transition)
         :freezing     (solid rest-transition)
         (do (println (str transition " is invalid"))
             false))))

(defn solid [[transition & rest-transition]]
  (do (println "solid now")
      #(case transition
         nil true
         :melting     (liquid rest-transition)
         :sublimation (vapor  rest-transition)
         (do (println (str transition " is invalid"))
             false))))

;; Define sequences to test
(def valid-seq [:melting :vaporization :ionization :deionization])
(def invalid-seq [:vaporization :ionization :deionization :freezing])

;; Actual call requires trampoline and initial state
(trampoline solid valid-seq)
;; solid now
;; liquid now
;; vapor now
;; plasma now
;; vapor now
;; true

(trampoline liquid invalid-seq)
;; liquid now
;; vapor now
;; plasma now
;; vapor now
;; :freezing is invalid
;; false

