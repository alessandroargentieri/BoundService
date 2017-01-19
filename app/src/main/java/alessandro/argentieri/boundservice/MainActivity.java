package alessandro.argentieri.boundservice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    BoundService mBoundService;
    boolean isServiceBound = false;

    TextView timestampText;
    TextView serviceText;

    Button startservicebutton;
    Button startthreadbutton;
    Button stopservicebutton;
    Button printgreetingsbutton;


    //CALLBACK METHODS///////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timestampText = (TextView) findViewById(R.id.timestamp_text);
        serviceText = (TextView) findViewById(R.id.service_state);
        //dichiaro i bottoni solo per abilitarli/disabilitarli
        startservicebutton = (Button)findViewById(R.id.start_service);
        stopservicebutton = (Button)findViewById(R.id.stop_service);
        startthreadbutton = (Button)findViewById(R.id.start_thread);
        printgreetingsbutton = (Button)findViewById(R.id.print_timestamp);

    }

    @Override
    protected void onStart() {
        super.onStart();
        stopservicebutton.setEnabled(false);
        startthreadbutton.setEnabled(false);
        printgreetingsbutton.setEnabled(false);
        //Intent intent = new Intent(this, BoundService.class);
        //startService(intent);
        //bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //IL SERVICE CONTINUA ANCHE SE L'ACTIVITY CORRENTE SI BLOCCA
        //if (isServiceBound) {
        //    unbindService(mServiceConnection);
        //    isServiceBound = false;
        //}
    }



    ///////////////BOTTONI CHE INTERAGISCONO CON IL SERVICE//////////////////////////////////////////


    public void StartService(View v){
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);

        //to effectively bind the service to the activity
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void StartThread(View v){
        if(isServiceBound){
            mBoundService.startTask();
        }
    }

    public void StopService(View v){
        if (isServiceBound) {
            unbindService(mServiceConnection);
            isServiceBound = false;
        }
        Intent intent = new Intent(MainActivity.this, BoundService.class);
        stopService(intent);
        stopservicebutton.setEnabled(false);
        startthreadbutton.setEnabled(false);
        printgreetingsbutton.setEnabled(false);
        startservicebutton.setEnabled(true);
    }

    public void PrintGreetings(View v){
        if (isServiceBound) {
            timestampText.setText(mBoundService.getGreetings());
        }
    }


    /////////////FUNZIONI RICHIAMATE DAL SERVICE/////////////////////////////////////////////////////


    public void setCountText(int count){
        timestampText.setText("Dato dal Service:  " + count + "!");
    }

    public void setServiceText(String serviceState){
        serviceText.setText("Service " + serviceState);
       // Toast.makeText(MainActivity.this, "serviceState: " + serviceState + "!", Toast.LENGTH_LONG).show();
    }


   //////////////////////////////////////////////////////////////////////////////////////////////////

    //non è la dichiarazione di una classe, ma la creazione di un'istanza che estendo ServiceConnection.
    //come se fosse dichiarata prima di tutti i metodi sotto la definizione della classe
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
            mBoundService = myBinder.getService();
            //to let the Service understand which Activity to call for the results
            mBoundService.setActivity(MainActivity.this);
           // Toast.makeText(MainActivity.this, "Activity sat!", Toast.LENGTH_LONG).show();
            MainActivity.this.setServiceText(" Bound");
            isServiceBound = true;
            stopservicebutton.setEnabled(true);
            startthreadbutton.setEnabled(true);
            printgreetingsbutton.setEnabled(true);
            startservicebutton.setEnabled(false);
        }
    };
   ///////////////////////////////////////////////////////////////////////////////////////////////////



}