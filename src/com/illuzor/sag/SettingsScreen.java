package com.illuzor.sag;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author illuzor
 * экран настроек вибрации и типа управления
 */

public class SettingsScreen extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
