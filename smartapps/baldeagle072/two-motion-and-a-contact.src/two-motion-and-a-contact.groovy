/**
 *  Two Motion and a Contact
 *  2/13/2015
 *
 *  Copyright 2015 Eric Roberts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Two Motion and a Contact",
    namespace: "baldeagle072",
    author: "Eric Roberts",
    description: "If there is motion on the first sensor and then the second sensor, turn on the lights. Only turn off if the motion stops and the door is open.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Sensors") {
        input "motion1", "capability.motionSensor", title: "Motion 1", required: true
        input "motion2", "capability.motionSensor", title: "Motion 1", required: true
        input "contact", "capability.contactSensor", title: "Contact", required: true
    }

    section("Lights") {
        input "lights", "capability.switch", title: "Lights to turn on"
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"

    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"

    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(motion1, "motion", "motion1Handler")
    subscribe(motion2, "motion", "motion2Handler")
}

def motion1Handler(evt) {
	log.debug("motion1 = $evt.value")
    if (evt.value == "active") {
        state.motion1on = true
    } else {
        state.motion1on = false
        checkOff()
    }
}

def motion2Handler(evt) {
	log.debug("motion2 = $evt.value : $state.motion1on")
    if (evt.value == "active" && state.motion1on) {
        lights.on()
    } else {
        checkOff()
    }
}

def checkOff() {
    def motion1Inactive = motion1.currentValue("motion") == "inactive"
    def motion2Inactive = motion2.currentValue("motion") == "inactive"
    def contactOpen = contact.currentValue("contact") == "open"
    log.debug("checkOff : $motion1Inactive : $motion2Inactive : $contactOpen :")
    if (motion1Inactive && motion2Inactive && contactOpen) {
        lights.off()
    }
}