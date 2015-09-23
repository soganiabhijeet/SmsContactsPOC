package grocerylist.india.com.smscontactspoc;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/*This class fetches sms and calls
* from content resolver*/
public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button call;
    private Button sms;
    private TextView text;
    private TextView missedView;
    private TextView incomingView;
    private TextView outgoingView;
    private ScrollView contactsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.displaySms);
        call = (Button) findViewById(R.id.call);
        sms = (Button) findViewById(R.id.sms);
        missedView = (TextView) findViewById(R.id.missedCalls);
        incomingView = (TextView) findViewById(R.id.incomingCalls);
        outgoingView = (TextView) findViewById(R.id.dialledCalls);
        contactsView = (ScrollView) findViewById(R.id.scrollView);
        call.setOnClickListener(this);
        sms.setOnClickListener(this);
        text.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.call) {
            getCallDetails();

        } else if (v.getId() == R.id.sms) {
            getSMS();
        }

    }

    private void getCallDetails() {
        Date lastWeekDate=getLastWeekDate();
        //contactsView.setVisibility(View.VISIBLE);
        text.setVisibility(View.INVISIBLE);
        StringBuffer sb = new StringBuffer();
        StringBuffer outgoing = new StringBuffer();
        StringBuffer incoming = new StringBuffer();
        StringBuffer missed = new StringBuffer();
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
        outgoing.append("\tOUTGOING CALLS");
        incoming.append("\tINCOMING CALLS");
        missed.append("\tMISSED CALLS");

        int count = 0;
        int missedCalls=0;
        int incomingCalls=0;
        int outgoingCalls=0;
        managedCursor.moveToLast();
        while (managedCursor.moveToPrevious() && count < 100) {
            count++;
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            if(callDayTime.before(lastWeekDate))
                continue;
            String callDuration = managedCursor.getString(duration);
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    outgoingCalls++;
                    outgoing.append("\nCall id: "+outgoingCalls+"\nPhone Number:--- " + phNumber + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                    outgoing.append("\n----------------------------------");
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    incomingCalls++;
                    incoming.append("\nCall id: "+incomingCalls+"\nPhone Number:--- " + phNumber + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                    incoming.append("\n----------------------------------");
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    missedCalls++;
                    missed.append("\nCall id: "+missedCalls+"\nPhone Number:--- " + phNumber + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                    missed.append("\n----------------------------------");
                    break;
            }

        }
        managedCursor.close();
        outgoing.append("TOTAL OUTGOING CALLS LAST WEEK " + outgoingCalls);
        incoming.append("TOTAL INCOMING CALLS LAST WEEK "+incomingCalls);
        missed.append("TOTAL MISSED CALLS LAST WEEK "+missedCalls);
        outgoingView.setText(outgoing);
        incomingView.setText(incoming);
        missedView.setText(missed);
        contactsView.setVisibility(View.VISIBLE);
        outgoingView.setVisibility(View.VISIBLE);
        incomingView.setVisibility(View.VISIBLE);
        missedView.setVisibility(View.VISIBLE);
    }


    private void getSMS() {
        contactsView.setVisibility(View.INVISIBLE);
        int count = 0;
        Uri inboxURI = Uri.parse("content://sms/inbox");
        StringBuffer sb = new StringBuffer();
        // List required columns
        String[] reqCols = new String[]{"address", "body"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor smsInboxCursor = cr.query(inboxURI, reqCols, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        while (smsInboxCursor.moveToNext() && count < 10) {
            count++;
            sb.append("SMS From: " + smsInboxCursor.getString(indexAddress) + "\n" + smsInboxCursor.getString(indexBody) + "\n");
            sb.append("\n----------------------------------\n");
        }
        smsInboxCursor.close();
        text.setText(sb);
        text.setVisibility(View.VISIBLE);

    }

    private Date getLastWeekDate() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        return c.getTime();
        /*c.add(Calendar.DATE, 6);
        Date end = c.getTime();
        System.out.println(start + " - " + end);*/
    }

}
