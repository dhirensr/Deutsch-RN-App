(ns quotes.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [quotes.handlers]
            [clojure.string :as string]
            [quotes.subs]
            [clojure.string :as str]))




(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def Alert (.-Alert ReactNative))
(defn alert [title]
  (.alert Alert title))
(defn rn-comp [name]
  (-> ReactNative
      (aget name)
      r/adapt-react-class))

(def text-input (rn-comp "TextInput"))
(defonce NativeBase (js/require "native-base"))
(defonce VectorIcon (js/require "@expo/vector-icons"))

(defn nb-comp [name]
  (-> NativeBase
      (aget name)
      r/adapt-react-class))


(defonce content (nb-comp "Content"))
(defonce header (nb-comp "Header"))
(defonce item (nb-comp "Item"))
(defonce content (nb-comp "Input"))
(defonce label (nb-comp "Label"))
(defonce container (nb-comp "Container"))
(defonce input (nb-comp "Input"))
(defonce picker (nb-comp "Picker"))
(defonce picker-item (r/adapt-react-class (.-Item (aget NativeBase "Picker"))))
(defonce button (nb-comp "Button"))
(def my-quotes ["When you find yourself in a hole,stop digging. — Will"
                "If you're waiting to have a good idea before you have any ideas, you won't have many ideas."
                "The most experienced planner in the world is your brain."
                "Don't just do something. Stand there. — Rochelle Myer"
                "People love to win. If you're not totally clear about the purpose of what you're doing, you have no chance of winning."
                "Fanaticism Consists of redoubling your efforts when you have forgotten your aim. — George"
                "Celebrate any progress. Don't wait to get perfect. — Ann McGee Cooper"
                "If you're not sure why you're doing something, you can never do enough of it."])
(def quotes-icon (js/require "./assets/images/quotes-icon.png"))
(def deutschland-logo (js/require "./assets/images/deutschland.jpg"))


(def verb-vector [["anrufen" "Dativ" "mit" "Ich rufe bei der Autofirma an."]
                  ["beginnen" "Dativ" "mit" "Beginnen Sie endlich mit Ihrer Arbeit!"]
                  ["bitten" "Akkusativ" "um" "Ich wollte dich um etwas Hilfe bitten."]
                  ["bleiben" "Dativ" "bei" "Ich bleibe bei meiner Meinung: Diese Entscheidung ist falsch."]
                  ["danken" "Dativ" "für" "Ich danke Ihnen für Ihr Geschenk."]
                  ["denken" "Akkusativ" "an" "Er denkt nur noch an seine Arbeit."]
                  ["arbeiten" "Dativ" "bei" "Ich arbeite bei Siemens."]
                  ["anfangen" "Dativ" "mit" "Ich fange mit der Übung an."]
                  ["antworten" "Akkusativ" "auf" "Bitte antworten Sie heute auf den Brief."]
                  ["aufhören (to stop)" "Dativ" "mit" "Er hört um 17.00 Uhr mit der Arbeit auf."]
                  ["aufpassen (to watch out)" "Akkusativ" "auf" "Ein Babysitter passt auf kleine Kinder auf."]
                  ["sich bedanken" "Dativ" "bei" "Ich bedanke mich herzlich bei dir."]
                  ["beschweren (to complain)" "Akkusativ" "über" "Er beschwerte sich über das kalte Essen. "]
                  ["beginnen" "Dativ" "mit" "Beginnen Sie endlich mit Ihrer Arbeit!"]
                  ["beschweren" "Dativ" "bei" "Sie beschwert sich bei dem Direktor."]
                  ["bitten" "Akkusativ" "um" "Ich wollte dich um etwas Hilfe bitten."]
                  ["debattieren / diskutieren" "Dativ" "mit" "Sie diskutiert mit ihm."]
                  ["debattieren / diskutieren" "Akkusativ" "über" "Seit Stunden diskutieren sie über diese Entscheidung."]
                  ["sich entschuldigen" "Dativ" "bei" "Er hat sich noch immer nicht bei mir entschuldigt."]
                  ["sich erinnern (to recall)" "Akkusativ" "mit" "Ich erinnere mich nicht an dieses Gespräch."]
                  ["freuen" "Akkusativ" "auf" "Ich freue mich auf das freie Wochenende."]
                  ["freuen (looking forward)" "Akkusativ" "über" "Ich freue mich sehr über dein Geschenk. "]
                  ["glauben" "Akkusativ" "an" "Sie glauben an Gott."]
                  ["gratulieren" "Dativ" "zu" "Ich gratuliere dir zum Geburtstag. "]
                  ["sich interessieren" "Akkusativ" "für" " Sie interessiert sich für französische Lyrik."]
                  ["sich kümmern (to take care of)" "Akkusativ" "um" "Sie kümmert sich darum, dass alles funktioniert."]
                  ["sprechen" "Dativ" "mit" "Er hat noch nicht mit mir gesprochen. "]
                  ["sprechen" "Akkusativ" "über" "Sie sprechen über die Reise."]
                  ["teilnehmen (to take part)" "Dativ" "an" "Sie hat schon dreimal an dem Kurs teilgenommen."]
                  ["telefonieren" "Dativ" "mit" "Hast du schon mit dem Arzt telefoniert?"]
                  ["träumen (to dream)" "Dativ" "von" "Er träumt von einem Luxusurlaub in der Karibik."]
                  ["sich treffen" "Dativ" "mit" "Die Kanzlerin (chancellor) trifft sich täglich mit ihrem Pressesprecher."]
                  ["sich unterhalten (to chat)" "Dativ" "mit" "Der Sänger unterhält sich mit dem Bassisten."]
                  ["antworten" "Akkusativ" "auf" "Niemand antwortete auf die Frage. "]
                  ["sich informieren" "Akkusativ" "über" "Man kann sich hier über alle Details informieren."]
                  ["fliegen" "Dativ" "mit" "Ich fliege nicht gern mit kleinen Flugzeugen."]
                  ["fahren" "Dativ" "mit" "Ich fahre immer mit meinem Freund."]
                  ["zurechtkommen (to cope)" "Dativ" "mit" "Ich komme mit meinen Kollegen gut zurecht."]
                  ["sich beeilen (to hurry)" "Dativ" "mit" "Sie beeilt sich mit der Arbeit."]
                  ["einsteigen" "Akkusativ" "in" "Ich steigen in den Bus ein."]
                  ["einziehen (to move in)" "Akkusativ" "in" "Ich ziehe am 1. Juni in die Wohnung ein."]])

#_(defn quote-main []
  (let [data (r/atom {})]
    (fn []
      [container
       [item {:regular true
              :floatingLabel true}
        [label "Enter Max. score that can be attained in your course (GPA/Percentage)"]
        [input {:on-change-text #(swap! data assoc :max-score %)
                :keyboard-type "numeric"
                :value (or (:max-score @data)  "")
                :editable true
                :max-length 10}]]
       [item {:regular true
              :floatingLabel true}
        [label "Enter Min. score that can be attained in your course (GPA/Percentage)"]
        [input {:on-change-text #(swap! data assoc :min-score %)
                :keyboard-type "numeric"
                :value (or (:min-score @data) "")
                :editable true
                :max-length 10}]]
       [item {:regular true
              :floatingLabel true}
        [label "Enter Score that you obtained in the course (GPA/Percentage)"]
        [input {:on-change-text #(swap! data assoc :score-obtained %)
                :keyboard-type "numeric"
                :value (or (:score-obtained @data)  "")
                :editable true
                :max-length 10}]]
       [text {:style {:font-size 20 :font-weight "bold" :font-style "italic"
                      :text-align "center" :margin-top 20}}
        (if-not (every? false? [(:max-score @data) (:min-score @data) (:score-obtained @data)])
          (str (+ 1 (* 3 (/ (-  (js/parseFloat (:max-score @data))
                                (js/parseFloat (:score-obtained @data)))
                            (-  (js/parseFloat (:max-score @data))
                                (js/parseFloat (:min-score @data)))))))
          "")]])))

(defn deutsch-main []
  (let [data (r/atom {:verb (rand-nth verb-vector)
                      :preposition ""
                      :verb-selected "Akkusativ"})]
    (fn []
      (let [verb (:verb @data)
            verb-name (first verb)
            verb-type (second verb)
            preposition (nth verb 2)
            help (nth verb 3)]
        [view {:style {:padding-top 12}}
         [item {:regular true
                :floatinglabel true

                :style {:border-width 1}}
          [label {:style {:padding-top 12
                          :padding-bottom 20
                          :margin-top 10
                          :padding-left 14}} "Welcome to Verben mit Präpositionen Game"]]
         [item {:regular true
                :floatinglabel true}
          [label {:style {:padding-top 12
                          :padding-bottom 20
                          :margin-top 12
                          :padding-left 10}} (str "Verb : " verb-name)]]
         [item {:regular true
                :floatinglabel true}
          [label {:style {:padding-top 12}} "Enter preposition"]
          [input {:on-change-text #(swap! data assoc :preposition %)
                  :value (:preposition @data)
                  :editable true
                  :style {:margin-left 20
                          :margin-right 20
                          :margin-top 20
                          :height 55}
                  :max-length 10}]]
         [picker {:selectedValue (:verb-selected @data)
                  :on-value-change (fn [val idx]
                                     (swap! data assoc :verb-selected val))}
          (doall (map-indexed (fn [idx verb-name]
                                ^{:key idx}[picker-item {:label verb-name
                                                         :value verb-name}])
                              ["Akkusativ" "Dativ"]))]
         [item {:regular true
                :floatinglabel true
                :style {:border-width 1}}
          [text {:style {:font-size 20 :font-weight "bold" :font-style "italic"
                         :text-align "center" :margin-top 20}}
           (if (= (:answer? @data) "Falsch!")
             (str "Hilfe : " help)
             "")]]
         [item {:regular true
                :floatinglabel true}
          [text {:style {:font-size 20 :font-weight "bold" :font-style "italic"
                         :text-align "center" :margin-top 20}}
           (or (str (:answer? @data)) "")]]
         [item {:regular true
                :floatinglabel true}
          [button {:on-press #(if (and
                                   (= preposition (string/trim (:preposition @data)))
                                   (= (:verb-selected @data) verb-type))
                                (swap! data assoc :answer? "Richtig!")
                                (swap! data assoc :answer? "Falsch!"))
                   :style {
                           :align-items "center"
                           :width "50%"}}
           [text {:style {:text-align "center"
                          :font-size 20}} "Submit"]]]
         [item {:regular true
                :floatinglabel true
                :style {:margin-bottom 40}}
          [button {:on-press #(swap! data assoc :verb (rand-nth verb-vector)
                                     :preposition ""
                                     :verb-selected "Akkusativ"
                                     :answer? nil)
                   :style {:margin-bottom 40
                           :align-items "center"
                           :width "50%"}}
           [text {:style {:text-align "center"
                          :font-size 20}} "Next"]]]]))))

(defn og-app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [image {:source (js/require "./assets/images/cljs.png")
               :style {:width 200
                       :height 200}}]
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press"]]])))

(defn app-root []
  (fn []
    [deutsch-main]))


(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component deutsch-main)))
