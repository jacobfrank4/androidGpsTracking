package bcit.androidgpstracking;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class contactListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				"has_phone_number = 1", null, null);

		if (cursor.moveToFirst()) {
			ArrayList<String> contactList = new ArrayList<>();
			contactList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			while (cursor.moveToNext()) {
				contactList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			}
			//Set adapter here
			ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, contactList);
			setListAdapter(adapter);
		} else {
			throw new UnsupportedOperationException("You have no contacts with phone numbers");
		}
	}
}
