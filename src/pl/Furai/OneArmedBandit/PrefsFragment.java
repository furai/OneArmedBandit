package pl.Furai.OneArmedBandit;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PrefsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}


}
