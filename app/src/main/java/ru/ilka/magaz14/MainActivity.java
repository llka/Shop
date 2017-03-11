package ru.ilka.magaz14;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Product> products = new ArrayList<Product>();
    BoxAdapter boxAdapter;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_CHANGE_ID = 2;
    final String SAVED_TEXT = "saved_text";
    String newProduct_name;
    int  newProduct_price, newProduct_img, change_index;

    //сервисная хуйня
    final int TASK1_CODE = 1;
    final int TASK2_CODE = 2;
    public final static int STATUS_START = 100;
    public final static int STATUS_FINISH = 200;
    public final static String PARAM_TIME = "time";
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String DOLLAR = "dollar";
    public final static String EURO = "euro";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_EURO = "result_euro";
    public final static String PARAM_FLAG = "flag";
    int dollar = 20000, euro = 25000;
    boolean flag = true;
    //

    SharedPreferences sPref;

    Button btnLoad, btnSave;
    EditText etFileName;
    Button btnAdd, btnKurs;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // создаем адаптер
        fillData();
        boxAdapter = new BoxAdapter(this, products);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);
        registerForContextMenu(lvMain);
        lvMain.setLongClickable(true);

        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnKurs = (Button) findViewById(R.id.btnKurs);
        etFileName = (EditText)findViewById(R.id.etFileName);
        btnLoad.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnKurs.setOnClickListener(this);
        etFileName.setOnClickListener(this);
    }

    // генерируем данные для адаптера

    void fillData() {
        for (int i = 1; i <= 20; i++) {
            products.add(new Product("Product " + i, i * 1000, R.drawable.gary_loomis, false));
        }
    }

    // выводим информацию о корзине
    public void showResult(View v) {
        String result = "Товары в корзине:";
        int summ_cost = 0;
        for (Product p : boxAdapter.getBox()) {
            if (p.isInBox()) {
                result += "\n" + p.getName();
                summ_cost += p.getPrice();
            }
        }
        result += "\n" + "К оплате: " + summ_cost + " руб";

        Toast tost = Toast.makeText(this, result, Toast.LENGTH_LONG);
        tost.setGravity(Gravity.RIGHT,-50,300);
        tost.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                products.add(new Product("Колбаса",30000,R.drawable.sausage, false));
                        // уведомляем, что данные изменились
                boxAdapter.notifyDataSetChanged();
                break;
            case R.id.btnKurs:
                PendingIntent pi;
                Intent intent, intent1;
                intent1 = new Intent();

                pi = createPendingResult(TASK1_CODE, intent1, 0);
                intent = new Intent(this, MService.class).putExtra(PARAM_PINTENT, pi).putExtra(DOLLAR,dollar)
                        .putExtra(EURO,euro).putExtra(PARAM_FLAG,flag);
                startService(intent);
                flag = false;

                break;
            case R.id.btnSave:
                saveText();
                break;
            case R.id.btnLoad:
                String file_name = loadText();
                try {
                    readTextFile(getBaseContext(), file_name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //удаление на скилле
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить запись");
        menu.add(0,CM_CHANGE_ID,0,"Изменить запись");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {

            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            products.remove(acmi.position);
            // уведомляем, что данные изменились
            boxAdapter.notifyDataSetChanged();
            return true;
        }
        else if(item.getItemId() == CM_CHANGE_ID)
        {
            Intent intentik = new Intent(this, CreateProduct.class);
            startActivityForResult(intentik, 1);

            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            change_index = acmi.position;
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == STATUS_FINISH) {
            dollar = data.getIntExtra(PARAM_RESULT, 0);
            euro = data.getIntExtra(PARAM_EURO, 0);

            switch (requestCode) {
                case TASK1_CODE:
                    String res = "DOL = " + dollar + "\n" + "EU = " + euro;
                    Toast tost = Toast.makeText(this, res, Toast.LENGTH_LONG);
                    tost.setGravity(Gravity.CENTER, -50, 300);
                    tost.show();
                    break;
            }
        }
        else {
            if (data == null) {
                return;
            }

            newProduct_name = data.getStringExtra("_name");
            newProduct_img = data.getIntExtra("_img", R.drawable.putin);
            newProduct_price = data.getIntExtra("_price", 5000);

            Product cp = new Product(newProduct_name, newProduct_price, newProduct_img, false);
            products.set(change_index, cp);

            // уведомляем, что данные изменились
            boxAdapter.notifyDataSetChanged();
        }


    }

    //сохранение в Prefirence
    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, etFileName.getText().toString());
        ed.commit();
        Toast.makeText(this, "file name saved", Toast.LENGTH_SHORT).show();
    }

    //загрука из Prefirence
    String loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        String file_name = sPref.getString(SAVED_TEXT, "input.txt");

        Toast.makeText(this, "I've read file name", Toast.LENGTH_SHORT).show();
        return file_name;
    }

    //чёткое чтение файла из Assets
    public void readTextFile(Context context, String file_name) throws IOException {
        String _name = "";
        int _price = 0, _img = R.drawable.putin;
        try {
            AssetManager assetManager = context.getAssets();
            InputStreamReader istream = new InputStreamReader(assetManager.open(file_name));
            BufferedReader in = new BufferedReader(istream);

            products.clear();
            int n = Integer.parseInt(in.readLine());
            for(int i = 0; i < n; ++i){
                _name = in.readLine();
                _price = Integer.parseInt(in.readLine());
                Product p = new Product(_name, _price, _img, false);
                products.add(p);
            }
            // уведомляем, что данные изменились
            boxAdapter.notifyDataSetChanged();

            in.close();
        } catch (FileNotFoundException e) {
            // FileNotFoundExpeption
        } catch (IOException e) {
            // IOExeption
        }
    }


}
