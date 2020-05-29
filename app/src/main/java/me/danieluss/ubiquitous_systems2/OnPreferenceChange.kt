package me.danieluss.ubiquitous_systems2

import androidx.preference.Preference

class OnPreferenceChange: Preference.OnPreferenceChangeListener {
    override fun onPreferenceChange(
        preference: Preference,
        newValue: Any
    ): Boolean {
        preference.summary = newValue as String
        return true
    }
}