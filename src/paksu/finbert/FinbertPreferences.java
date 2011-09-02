package paksu.finbert;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class FinbertPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		String nicknameSummary = PreferenceManager.getDefaultSharedPreferences(this).getString("nickname",
				getString(R.string.nickname_not_set));
		findPreference("nickname").setSummary(nicknameSummary);
		findPreference("nickname").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String newNickname = (String) newValue;
				if (!isValidName(newNickname)) {
					Toast.makeText(FinbertPreferences.this, getString(R.string.invalid_nickname), Toast.LENGTH_SHORT)
							.show();
					return false;
				}
				findPreference("nickname").setSummary(newNickname);
				return true;
			}
		});
	}

	private static boolean isValidName(String name) {
		if (name.length() == 0 || name.length() > 20) {
			return false;
		}
		if (name.toCharArray()[0] == ' ') {
			return false;
		}
		if (name.toCharArray()[name.length() - 1] == ' ') {
			return false;
		}

		return true;
	}
}
