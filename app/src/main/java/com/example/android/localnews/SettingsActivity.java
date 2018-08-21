package com.example.android.localnews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //obtain the settings preference so we can change the summary
            Preference searchTerm = findPreference(getString(R.string.settings_search_term_key));

            //bind the preference summary to a value
            bindPreferenceSummaryToValue(searchTerm);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            //update the displayed preference after it has been changed
            String stringValue = value.toString();
            preference.setSummary(stringValue);

            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            //set the preference listener to the current activity
            preference.setOnPreferenceChangeListener(this);

            //get the current value of the preference and update the summary
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
