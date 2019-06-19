package ir.boozar.databasecreator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteProgram;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.Executor;


public class ActivityMain extends ActionBarActivity{

    private static final int INTENT_SELECT_FOLDER=1;
    private static final int INTENT_SELECT_DB=2;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        Button btn=(Button) findViewById(R.id.new_db);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("file/*");
                intent = Intent.createChooser(chooseFile, "انتخاب فلدر");
                startActivityForResult(intent, INTENT_SELECT_FOLDER);
            }
        });
        btn=(Button) findViewById(R.id.select_db);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("file/*");
                intent = Intent.createChooser(chooseFile, "انتخاب فلدر");
                startActivityForResult(intent, INTENT_SELECT_DB);
            }
        });
    }

    private boolean isImage(String path){
        String[] ex=new String[] {"jpg", "png"};
        boolean isImage=false;
        for(String e:ex){
            if(path.endsWith(e)){
                isImage=true;
                break;
            }
        }
        return isImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case INTENT_SELECT_FOLDER:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();

                    String fileDB=Environment.getExternalStorageDirectory()
                            + File.separator
                            + "Boozar" + File.separator
                            + "db_temp";
                    File fDB=new File(fileDB);
                    fDB.mkdirs();
                    fileDB+= File.separator
                            +System.currentTimeMillis();
                    DB db=new DB(context,fileDB);
                    db.photosQuality=photosQuality;

                    String p = uri.getPath();
                    File file=new File(p);
                    file=new File(file.getParent());
                    File[] l=file.listFiles();
                    for(File f:l){
                        if(f.isFile() && isImage(f.getPath())){
                            db.newPic(f.getPath());
                        }
                    }
                    db.close();
                    Intent i=new Intent(context,ActivityEdit.class);
                    //Log.i("hz","db-f0:"+fileDB);
                    i.putExtra("DB",fileDB);
                    startActivity(i);
                }
                break;
            case INTENT_SELECT_DB:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    Intent i=new Intent(context,ActivityEdit.class);
                    i.putExtra("DB",uri.getPath());
                    startActivity(i);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public static final int photosQualityDefault=60;
    private int photosQuality=photosQualityDefault;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.set_quality) {
            AlertDialog.Builder d=new AlertDialog.Builder(context);
            d.setTitle("تعیین کیفیت عکسها");
            final EditText et=new EditText(context);
            et.setText(photosQuality+"");
            et.setHint("یک عدد بین ۱ تا ۱۰۰");
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            d.setView(et);
            d.setPositiveButton("تعیین",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String text=et.getText().toString();
                    if(text.equals(""))
                        return;
                    int q=Integer.parseInt(text);
                    if(q<1 || q>100)
                        q=photosQualityDefault;
                    photosQuality=q;
                }
            });
            d.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
