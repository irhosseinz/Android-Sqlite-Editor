package ir.boozar.databasecreator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class DB extends SQLiteOpenHelper {
    public static final String TABLE_PACKAGE="package";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_DATE="date";
    public static final String COLUMN_NAME="name";
    public static final String COLUMN_DATABASE="database";
    public static final String COLUMN_COUNT="count";
    public static final String COLUMN_BUY_DATE="buy_date";
    public static final String COLUMN_BUY="buy";

    public static final String TABLE_PHOTOS="photos";
    public static final String COLUMN_PACKAGE="package";
    public static final String COLUMN_FILE="file";
    public static final String COLUMN_FILE_NAME="fname";
    public static final String COLUMN_DESC="desc";
    public static final String COLUMN_TAGS="tags";
    public static final String COLUMN_LIKE="like";
    public static final String COLUMN_SHARE="share";

    private final String[] DATABASE_CREATE =new String[]{
            "create table "+TABLE_PHOTOS+" ( "+COLUMN_ID
                    + " integer primary key autoincrement, "
                    + COLUMN_PACKAGE+" integer, "
                    + COLUMN_FILE+" blob, "
                    + COLUMN_FILE_NAME+" text null, "
                    + COLUMN_DATE+" integer, "
                    + COLUMN_DESC+" text null, "
                    + COLUMN_TAGS+" text null, "
                    + COLUMN_LIKE+" integer default 0, "
                    + COLUMN_SHARE+" integer default 0 "
                    + " );"
    };

    public DB(Context context,String name) {
        super(context, name, null, 1);
        //Log.i("hz","openDB:"+name);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        for(String query:DATABASE_CREATE)
            db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV){

    }

    public int photosQuality=ActivityMain.photosQualityDefault;
    public void newPic(String path){
        Bitmap bmp = BitmapFactory.decodeFile(path);
        if(bmp==null)
            return;
        SQLiteStatement p = getWritableDatabase()
                .compileStatement("insert into photos(" +
                        COLUMN_FILE+"," +
                        COLUMN_FILE_NAME+"," +
                        COLUMN_DATE+") values(?,?,?)");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, photosQuality, stream);
        byte[] data = stream.toByteArray();
        p.bindBlob(1, data);
        p.bindString(2, new File(path).getName());
        p.bindLong(3, System.currentTimeMillis());
        p.executeInsert();
    }
    public PhotoObj getPhoto(long id){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(
                TABLE_PHOTOS
                ,null
                ,"id=?"
                ,new String[]{id+""},null,null,null
        );
        //Log.i("hz", "getPhoto:" + id + "," + c.getCount());
        PhotoObj o=new PhotoObj();
        if(c.moveToFirst()){
            o.id=c.getLong(0);
            o.fileData=c.getBlob(2);
            o.fileName=c.getString(3);
            o.date=c.getLong(4);
            o.desc=c.getString(5);
            o.tags=c.getString(6);

        }
        return o;
    }
    public void setPhotoInfo(PhotoObj ph){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_DESC,ph.desc);
        v.put(COLUMN_TAGS,ph.tags);
        db.update(
                TABLE_PHOTOS,v,"id=?"
                ,new String[]{ph.id+""});
    }
    public static class PhotoObj{
        public long id,date;
        public String fileName,desc,tags;
        public byte[] fileData;
    }
}