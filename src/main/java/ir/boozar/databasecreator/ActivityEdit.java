package ir.boozar.databasecreator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ActivityEdit extends ActionBarActivity {

    private DB db;

    private long currentID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            String file=extras.getString("DB");
            //Log.i("hz","db-f:"+file);
            db=new DB(this,file);
        }

        Button btn=(Button) findViewById(R.id.prev);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto(currentID-1);
            }
        });
        btn=(Button) findViewById(R.id.next);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoto(currentID+1);
            }
        });

        setPhoto(1);
    }
    private void setPhoto(final long id){
        DB.PhotoObj ph=db.getPhoto(id);
        Button btnP = (Button) findViewById(R.id.prev);
        Button btnN = (Button) findViewById(R.id.next);

        if(ph.fileData==null){
            if(currentID==-1){
                btnP.setVisibility(View.GONE);
                btnN.setVisibility(View.GONE);
            }else if(id<currentID) {
                btnP.setVisibility(View.GONE);
            }else{
                btnN.setVisibility(View.GONE);
            }
            return;
        }else{
            btnP.setVisibility(View.VISIBLE);
            btnN.setVisibility(View.VISIBLE);
        }

        DB.PhotoObj ph0=new DB.PhotoObj();
        ph0.id=currentID;
        EditText et=(EditText) findViewById(R.id.desc);
        ph0.desc=et.getText().toString();
        et.setText(ph.desc);
        et=(EditText) findViewById(R.id.tags);
        ph0.tags=et.getText().toString();
        et.setText(ph.tags);
        db.setPhotoInfo(ph0);

        currentID=id;
        ImageView iv=(ImageView) findViewById(R.id.image);
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)iv.getLayoutParams();
        Bitmap b=BitmapFactory.decodeByteArray(
                ph.fileData, 0, ph.fileData.length);
        int w=params.width;
        //Log.i("hz", "w:" + w);
        params.height=w*b.getHeight()/b.getWidth();
        iv.setLayoutParams(params);
        iv.setImageBitmap(b);
        TextView tv=(TextView) findViewById(R.id.filename);
        tv.setText(ph.fileName);
    }
}
