(ns env.index
  (:require [env.dev :as dev]))

;; undo main.js goog preamble hack
(set! js/window.goog js/undefined)

(-> (js/require "figwheel-bridge")
    (.withModules #js {"./assets/icons/loading.png" (js/require "../../../assets/icons/loading.png"), "./assets/images/quotes-icon.png" (js/require "../../../assets/images/quotes-icon.png"), "expo" (js/require "expo"), "./assets/images/deutschland.jpg" (js/require "../../../assets/images/deutschland.jpg"), "./assets/images/cljs.png" (js/require "../../../assets/images/cljs.png"), "./assets/icons/app.png" (js/require "../../../assets/icons/app.png"), "native-base" (js/require "native-base"), "react-native" (js/require "react-native"), "react" (js/require "react"), "create-react-class" (js/require "create-react-class"), "@expo/vector-icons" (js/require "@expo/vector-icons")}
)
    (.start "main" "expo" "192.168.0.102"))
