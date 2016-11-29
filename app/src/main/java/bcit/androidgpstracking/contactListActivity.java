package bcit.androidgpstracking;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class contactListActivity extends ListActivity {

	HashMap<String, String> contactNames = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				"has_phone_number = 1", null, "display_name");

		if (cursor.moveToFirst()) {
			ArrayList<String> contactList = new ArrayList<>();
			contactList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			while (cursor.moveToNext()) {
				contactList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, contactList);
			setListAdapter(adapter);
		} else {
			throw new UnsupportedOperationException("You have no contacts with phone numbers");
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// ListView Clicked item index
		int itemPosition = position;

		// ListView Clicked item value
		String itemValue = (String) l.getItemAtPosition(position);

		if (contactNames.containsKey(itemValue)) {
			contactNames.remove(itemValue);
			return;
		}

		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, "DISPLAY_NAME = '" + itemValue + "'", null, null);
		cursor.moveToFirst();
		String contactId =
				cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
		phones.moveToFirst();
		String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		contactNames.put(itemValue, number);
		Toast.makeText(this, number, Toast.LENGTH_LONG).show();
	}

	public void returnNumbers(final View view) {
		Intent tent = new Intent();
		ArrayList<String> numbers = new ArrayList<>();
		for (Map.Entry<String, String> elem : contactNames.entrySet()) {
			numbers.add(elem.getValue());
		}
		tent.putExtra("numbers", numbers);
		setResult(Activity.RESULT_OK, tent);
		finish();
	}
}
